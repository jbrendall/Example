package lc;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.attribute.Remove;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Data preparation, training, testing and evaluating models in weka
 */
class Evaluation {

    /**
     * Balance the classes (i.e. linked/non_linked) of a dataset
     *
     * @param inputDataset input
     * @param randomSeed balancing uses randomization, and that's the seed for it
     * @return balanced data set
     * @throws Exception
     */
    static Instances balanceDataset(Instances inputDataset, int randomSeed) throws Exception {
        SpreadSubsample filter = new SpreadSubsample();
        String[] options = {
                "-M", "1.0",    // -> uniform distribution
                "-X", "0.0",    // -> no limit on class size
                "-S", Integer.toString(randomSeed)};

        filter.setOptions(options);
        filter.setInputFormat(inputDataset);

        return Filter.useFilter(inputDataset, filter);
    }

    /**
     * Predefined attribute models
     */
    enum AttributeModel {
        /**
         * only include if issue and commit existed at the same time
         * and the two text similarity metrics (VGRAM)
         */
        OnlyIR,

        /**
         * only structure, without any text and trace similarity
         */
        OnlyStruct,

        /**
         * all features, without LSI and trace similarity
         */
        All,

        /**
         * - remove LSI and trace similarity
         * - parameters for best attribute selection
         */
        Auto
    }

    /**
     * Apply options and 'swallow' exceptions.
     *
     * 'Save' means, we know what we are doing. All options are hard coded (no real user data) and fool proof.
     * So exceptions are _impossible_.
     *
     * @param obj object
     * @return object with options applied
     */
    private static <T extends OptionHandler> T saveApplyOptions(T obj, final String[] options, String name) {
        try {
            obj.setOptions(options.clone());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Invalid Options %s for %s",
                            String.join(" ", options),
                            name), e);
        }

        return obj;
    }

    private static Filter constructFilter(AttributeModel model) {
        switch (model) {
            case OnlyIR: {
                Remove filter = new Remove();
                String[] options = {
                        "-R", "7,23,24,26", // -> list of columns to delete
                        "-V"                // -> Invert matching sense (i.e. only keep specified columns)
                };
                return saveApplyOptions(filter, options, model.name());
            }

            case OnlyStruct: {
                Remove filter = new Remove();
                String[] options = {
                        "-R", "1-3,22-25"   // -> list of columns to delete

                };
                return saveApplyOptions(filter, options, model.name());
            }

            case All: {
                Remove filter = new Remove();
                String[] options = {
                        "-R", "1-3,22,25"   // -> list of columns to delete
                };
                return saveApplyOptions(filter, options, model.name());
            }

            case Auto: {
                Remove rmFilt = new Remove();
                String[] rmOpts = {
                        "-R", "1-3,22,25"   // -> list of columns to delete
                };

                CfsSubsetEval eval = new CfsSubsetEval();
                String[] evalOpts = {};     // -> keep defaults

                BestFirst search = new BestFirst();
                String[] searchOpts = {};   // -> keep defaults

                AttributeSelection attSel = new AttributeSelection();
                attSel.setEvaluator(saveApplyOptions(eval, evalOpts, "CfsSubSetEval " + model.name()));
                attSel.setSearch(saveApplyOptions(search, searchOpts, "BestFirst " + model.name()));

                MultiFilter mf = new MultiFilter();
                mf.setFilters(new Filter[]{
                        saveApplyOptions(rmFilt, rmOpts, "RemoveFilter " + model.name()),
                        attSel});

                return mf;
            }
            default:
                throw new IllegalArgumentException(
                        String.format("Cannot construct filter for %s", model));
        }
    }

    /**
     * Apply a predefined attribute model to a dataset
     *
     * NOTE: balancing should be done _BEFORE_ applying attribute selection
     *
     * @param model model to apply
     * @param dataset the (balanced) dataset
     *
     * @return dataset with subset of attributes
     * @throws Exception
     */
    static Instances applyAttributeModel(AttributeModel model, Instances dataset) throws Exception {
        Filter filter = constructFilter(model);
        filter.setInputFormat(dataset);
        return Filter.useFilter(dataset, filter);
    }

    /**
     * Predefined Algorithms
     */
    enum Algorithm {
        ZeroR,
        NaiveBayes,
        J48,
        RandomForest
    }

    private static AbstractClassifier constructClassifier(Algorithm algorithm) {
        switch (algorithm) {
            case ZeroR: return new ZeroR();
            case NaiveBayes: return new NaiveBayes();
            case J48: {
                J48 j48 = new J48();
                String[] opts = {
                        "-C", "0.25",   // -> Set confidence threshold for pruning
                        "-M", "2"       // -> Set minimum number of instances per leaf
                };
                return saveApplyOptions(j48, opts, algorithm.name());
            }
            case RandomForest: {
                RandomForest rf = new RandomForest();
                String[] opts = {
                        "-P", "100",
                        "-I", "100",
                        "-num-slots", "1",
                        "-K", "0",
                        "-M", "1.0",
                        "-V", "0.001",
                        "-S", "1"
                };
                return saveApplyOptions(rf, opts, algorithm.name());
            }
            default:
                throw new IllegalArgumentException(
                        String.format("Cannot construct classifier for %s", algorithm));
        }
    }

    /**
     * Train predefined classifier
     *
     * @param algorithm algorithm to use
     * @param dataset training set
     * @return trained classifier
     * @throws Exception
     */
    static Classifier trainClassifier(Algorithm algorithm, Instances dataset) throws Exception {
        Classifier classifier = constructClassifier(algorithm);
        classifier.buildClassifier(dataset);
        return classifier;
    }

    /**
     * Persist trained classifier to disc
     *
     * @param cls classifier to persist
     * @param destinationFileName destination file path, should end with '*.pmml'
     * @throws Exception if serialization fails
     */
    static void saveClassifier(final Classifier cls, final Path destinationFileName) throws Exception {
        weka.core.SerializationHelper.write(destinationFileName.toString(), cls);
    }

    /**
     * Load classifier
     *
     * @param sourceFileName source file path, usually ends with '*.pmml'
     * @return classifier
     * @throws Exception if deserialization fails
     */
    static Classifier loadClassifier(final Path sourceFileName) throws Exception {
        return (Classifier) SerializationHelper.read(sourceFileName.toString());
    }

    private static List<String> nominalValues(final Attribute att) {
        List<String> result = new ArrayList<>();
        final Enumeration<Object> enu = att.enumerateValues();
        while (enu.hasMoreElements()) {
            result.add((String) enu.nextElement());
        }
        return result;
    }

    /**
     * Prepare an empty prediction dataset containing the relevant columns
     */
    private static Instances emptyPredictionDataset(final String relationName,
                                                    final List<String> classLabels) {
        final ArrayList<Attribute> attributes = new ArrayList<Attribute>(Arrays.asList(
                new Attribute("sample_id", false),
                new Attribute("commit_hash", true),
                new Attribute("issue_id", true),
                // the actual class label (if available)
                new Attribute("actual", classLabels),
                // the predicted class label
                new Attribute("predicted", classLabels),
                // the probability of the prediction
                new Attribute("prediction", false)
        ));

        return new Instances(relationName, attributes, 50);
    }

    private static void addPrediction(final Instances dataset,
                                      final double sampleId,
                                      final String commitHash,
                                      final String issueId,
                                      final double actualClassIdx,
                                      final double[] classDistribution
    ) {
        Instance instance = new DenseInstance(dataset.numAttributes());
        instance.setDataset(dataset);

        double predValue = 0;
        if (Utils.sum(classDistribution) == 0) {
            predValue = Utils.missingValue();
        } else {
            predValue = Utils.maxIndex(classDistribution);
        }

        instance.setValue(0, sampleId);
        instance.setValue(1, commitHash);
        instance.setValue(2, issueId);

        // actual class
        if (!Utils.isMissingValue(actualClassIdx)) {
            instance.setValue(3, (int) actualClassIdx);
        }

        // predicted class & prediction
        if (!Utils.isMissingValue(predValue)) {
            instance.setValue(4, (int) predValue);

            instance.setValue(5, classDistribution[(int) predValue]);
        }

        dataset.add(instance);
    }

    /**
     * Predict the classes for (unlabeled) instances
     *
     * @param cls trained classifier to use for prediction
     * @param dataset dataset to operate on
     * @param relationName name of the resulting dataset
     * @return dataset holding the prediction results, i.e. predicted class and its probability
     * @throws Exception
     */
    static Instances predict(final Classifier cls,
                             final Instances dataset,
                             final String relationName) throws Exception {
        final Attribute sampleIdAtt = dataset.attribute("sample_id");
        final Attribute commitHashAtt = dataset.attribute("commit_hash");
        final Attribute issueIdAtt = dataset.attribute("issue_id");
        final Attribute classAtt = dataset.attribute("class");

        final Instances predictionDataset = emptyPredictionDataset(
                relationName, nominalValues(classAtt));

        for (int i = 0; i < dataset.numInstances(); ++i) {
            final Instance inst = dataset.instance(i);
            final double[] dist = cls.distributionForInstance(inst);

            addPrediction(predictionDataset,
                    inst.value(sampleIdAtt.index()),
                    inst.stringValue(commitHashAtt.index()),
                    inst.stringValue(issueIdAtt.index()),
                    inst.value(classAtt.index()),
                    dist);
        }

        return predictionDataset;
    }
}
