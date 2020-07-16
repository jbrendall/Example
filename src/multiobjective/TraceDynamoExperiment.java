package multiobjective;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoSetAndFrontFromDoubleSolutions;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

public class TraceDynamoExperiment {
	private static int INDEPENDENT_RUNS = 8;
	
	private static int numberEvaluations;
	private static int populationSize;
	
	public TraceDynamoExperiment(HashMap<String, String> source, HashMap<String, String> target, HashMap<String, List<String>> links, String directory, 
			int runs, int evaluations, int size) throws IOException {
		
		INDEPENDENT_RUNS = runs;
		numberEvaluations = evaluations;
		populationSize = size;
		
		List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
		problemList.add(new ExperimentProblem<>(new TraceDynamoProblem(source, target, links, directory)));
		
		List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
				configureAlgorithmList(problemList);
		
		Experiment<DoubleSolution, List<DoubleSolution>> experiment =
	            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("MOGA")
	                    .setAlgorithmList(algorithmList)
	                    .setProblemList(problemList)
	                    .setExperimentBaseDirectory(directory)
	                    .setOutputParetoFrontFileName("FUN")
	                    .setOutputParetoSetFileName("VAR")
	                    .setReferenceFrontDirectory(directory + "/MOGA/referenceFronts")
	                    .setIndicatorList(Arrays.asList(
	                            new Epsilon<DoubleSolution>(),
	                            new Spread<DoubleSolution>(),
	                            new GenerationalDistance<DoubleSolution>(),
	                            new PISAHypervolume<DoubleSolution>(),
	                            new InvertedGenerationalDistance<DoubleSolution>(),
	                            new InvertedGenerationalDistancePlus<DoubleSolution>()))
	                    .setIndependentRuns(INDEPENDENT_RUNS)
	                    .setNumberOfCores(8)
	                    .build();
		
	    new ExecuteAlgorithms<>(experiment).run();
	    new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
	    /*new ComputeQualityIndicators<>(experiment).run();
	    new GenerateLatexTablesWithStatistics(experiment).run();
	    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
	    new GenerateFriedmanTestTables<>(experiment).run();
	    new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).run();*/
	}

	private static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
			List<ExperimentProblem<DoubleSolution>> problemList) {
		List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
		
		//int numberEvaluations = 1;
		//int populationSize = 5;
		//int numberEvaluations = 5;
		//int populationSize = 5;
		for(int run = 0; run < INDEPENDENT_RUNS; run++) {
			for(int i = 0; i< problemList.size(); i++) {
				Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(
						problemList.get(i).getProblem(),
						//new SBXCrossover(1.0,20),
						new SBXCrossover(0.8,20),
						new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0))
						.setMaxEvaluations(numberEvaluations).setPopulationSize(populationSize).build();
						//.setMaxEvaluations(25000).setPopulationSize(100).build();
				algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i), run));
			}
			
			/*for (int i = 0; i < problemList.size(); i++) {
		        Algorithm<List<DoubleSolution>> algorithm = new SPEA2Builder<DoubleSolution>(
		            problemList.get(i).getProblem(),
		            //new SBXCrossover(1.0, 10.0),
		            new SBXCrossover(0.8,20),
		            new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(),
		                20.0))
		        	.setMaxIterations(numberEvaluations).setPopulationSize(populationSize)
		        	//.setMaxIterations(250)
		        	//.setPopulationSize(100)
		            .build();
		        algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i), run));
	    	}*/
			
			/*for (int i = 0; i < problemList.size(); i++) {
		        double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
		        double mutationDistributionIndex = 20.0;
		        Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder(
		            (DoubleProblem) problemList.get(i).getProblem(),
		            new CrowdingDistanceArchive<DoubleSolution>(100))
		            .setMutation(new PolynomialMutation(mutationProbability, mutationDistributionIndex))
		            .setMaxIterations(numberEvaluations).setSwarmSize(populationSize)
		            //.setMaxIterations(250)
		            //.setSwarmSize(100)
		            .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
		            .build();
		        algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i), run));
		    }*/
		}
		return algorithms;
	}
}
