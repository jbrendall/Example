package lc;

import org.junit.Assert;
import org.junit.Test;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class EvaluationTest {

    private static Instances loadDefaultDataset() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get("test_data/apache_pig.arff"));
        ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
        return arff.getData();
    }

    private static Instances loadTestDataset() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get("test_data/apache_pig_test.arff"));
        ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
        return arff.getData();
    }

    @Test
    public void balanceTest() throws Exception {
        Instances dataset = loadDefaultDataset();

        // // // Attributes
        Assert.assertEquals(26, dataset.numAttributes());

        Attribute a0 = dataset.attribute(0);
        Assert.assertEquals("sample_id", a0.name());

        Attribute classAtt = dataset.attribute("class");
        dataset.setClass(classAtt);

        Assert.assertEquals(6, dataset.numInstances());

        Instances outputDataset = Evaluation.balanceDataset(dataset, 1);
        // FIXME: the dataset is already balanced, so nothing happens :-(
        Assert.assertEquals(6, outputDataset.numInstances());
    }

    @Test
    public void attributeModels() throws Exception {
        Instances dataset = loadDefaultDataset();

        Attribute classAtt = dataset.attribute("class");
        dataset.setClass(classAtt);

        Assert.assertEquals(6, dataset.numInstances());

        for (Evaluation.AttributeModel m : Evaluation.AttributeModel.values()) {
            Instances result = Evaluation.applyAttributeModel(m, dataset);

            Assert.assertEquals("instances are not filtered", 6, result.numInstances());
        }
    }

    @Test
    public void algorithmModels() throws Exception {
        Instances dataset = loadDefaultDataset();

        Attribute classAtt = dataset.attribute("class");
        dataset.setClass(classAtt);

        Assert.assertEquals(6, dataset.numInstances());

        Instances preparedDataset = Evaluation.applyAttributeModel(Evaluation.AttributeModel.OnlyIR, dataset);

        for (Evaluation.Algorithm a : Evaluation.Algorithm.values()) {
            Classifier cls = Evaluation.trainClassifier(a, preparedDataset);
            System.out.println(cls);
        }
    }

    @Test
    public void persistance() throws Exception {
        Instances dataset = loadDefaultDataset();

        Attribute classAtt = dataset.attribute("class");
        dataset.setClass(classAtt);

        Assert.assertEquals(6, dataset.numInstances());

        Instances preparedDataset = Evaluation.applyAttributeModel(Evaluation.AttributeModel.OnlyIR, dataset);

        Classifier cls = Evaluation.trainClassifier(Evaluation.Algorithm.ZeroR, preparedDataset);
        final Path fileName = Paths.get("test_data/zeroR.pmml");

        Evaluation.saveClassifier(cls, fileName);
        Classifier loaded = Evaluation.loadClassifier(fileName);
    }

    @Test
    public void predict() throws Exception {
        Instances trainDataset = loadDefaultDataset();

        Attribute classAtt = trainDataset.attribute("class");
        trainDataset.setClass(classAtt);
        Assert.assertEquals(6, trainDataset.numInstances());

        Instances preparedDataset = Evaluation.applyAttributeModel(
                Evaluation.AttributeModel.OnlyStruct,
                trainDataset);

        Classifier cls = Evaluation.trainClassifier(Evaluation.Algorithm.RandomForest, preparedDataset);
        Instances testDataset = loadTestDataset();
        classAtt = testDataset.attribute("class");
        testDataset.setClass(classAtt);

        final Instances predictionDataset = Evaluation.predict(cls, testDataset, "prediction");
        Assert.assertEquals(preparedDataset.numInstances(), predictionDataset.numInstances());
    }
}