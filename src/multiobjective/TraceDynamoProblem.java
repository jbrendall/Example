package multiobjective;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import caching.Cache;
import chromosome.Chromosome;
import evaluation.MapCalculator;
import links.Link;
import traceDynamo.TraceDynamo;

public class TraceDynamoProblem extends AbstractDoubleProblem{
	private HashMap<String, String> source;
	private HashMap<String, String> target;
	private HashMap<String, List<String>> links;
	private String directory;
	
	private int numPreprocessors;
	private int num1TraceMethods;
	private int ngram_max;
	private int num2TraceMethods;
	private int pyAttributes;
	private int similarity;
	private int voter;
	
	//private HashMap<Link, Integer> results;
	
	public TraceDynamoProblem(HashMap<String, String> source, HashMap<String, String> target, HashMap<String, List<String>> links, String directory) {
		this(source, target, links, directory, 5, 2, 1, 2, 5, 1, 1);
	}
	
	public TraceDynamoProblem(HashMap<String, String> source, HashMap<String, String> target, HashMap<String, List<String>> links, String directory,
			int preprocessors, int firstMethods, int ngram_max, int secondMethods, int pyAtt, int sim, int vote) {
		this.source = source;
		this.target = target;
		this.links = links;
		this.directory = directory;
		
		this.numPreprocessors = preprocessors;
		this.num1TraceMethods = firstMethods;
		this.ngram_max = ngram_max;
		this.num2TraceMethods = secondMethods;
		this.pyAttributes = pyAtt;
		this.similarity = sim;
		this.voter = vote;
		
		setNumberOfVariables(this.numPreprocessors+this.num1TraceMethods+this.ngram_max+this.num2TraceMethods+this.pyAttributes+this.similarity+this.voter);
		setNumberOfObjectives(2);
		setName("TraceProblem");
		
		List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
	    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());
	    
	    int break1 = numPreprocessors+num1TraceMethods;
	    
	    //set preprocessor and vsm/ngram trace methods value range from 0 to 1
	    for(int i = 0; i < break1; i++) {
	    	lowerLimit.add((double) 0);
	    	upperLimit.add((double) 1);
	    }
	    
	    lowerLimit.add((double) 3);
	    upperLimit.add((double) 5.99);
	    
	    int break2 = break1+1+num2TraceMethods;
	    for(int i = break1 + 1; i < break2; i++) {
	    	lowerLimit.add((double) 0);
	    	upperLimit.add((double) 1);
	    }
	    
	    int break3 = break2 + pyAttributes;
	    for(int i = break2; i < break3; i++){ 
	    	lowerLimit.add((double) 0);
	    	upperLimit.add(4.99);
	    }
	    
	    /*for(int i = break3; i < getNumberOfVariables()-1; i++) {
	    	lowerLimit.add((double) 0);
	    	upperLimit.add((double) 1);
	    }*/
	    
	    //for now, similarity and voter set to 0 only
	    lowerLimit.add((double) 0);
	    upperLimit.add((double) 0);
	    
	    lowerLimit.add((double) 0);
	    upperLimit.add((double) 0);
	    /*
	    //set max value for ngram model
	    lowerLimit.add((double) 3);
	    upperLimit.add((double) 5);
	    
	    //set lda/lsi trace methods value range from 0 to 1
	    for(int i = 0; i < this.numTraceMethods; i++) {
	    	lowerLimit.add((double) 0);
	    	upperLimit.add((double) 1);
	    }
	    
	    //set range for all attributes associated with lda/lsi models
	    //num_topics
	    lowerLimit.add((double) 2); upperLimit.add((double) 10000);
	    //passes
	    lowerLimit.add((double) 2); upperLimit.add((double) 10000);
	    //chunksize
	    lowerLimit.add((double) 10); upperLimit.add((double) 10000);
	    //alpha and beta
	    for(int i = 0; i < 2; i++) {
	    	lowerLimit.add((double) 0);
	    	upperLimit.add((double) 1);
	    }
	     
	    //set range for similarity selection
	    lowerLimit.add((double) 0); upperLimit.add((double) 1);
	    //set range for voter selection
	    lowerLimit.add((double) 0); upperLimit.add((double) 0);
	   */
	    setLowerLimit(lowerLimit);
	    setUpperLimit(upperLimit);
	}
	
	@Override
	public void evaluate(DoubleSolution solution) {
		double time = 0;
	    double MAP = 0;
	    HashMap<Link, Integer> results = new HashMap<Link, Integer>();
	    
	    int numUpToPyAttributes = numPreprocessors+num1TraceMethods+ngram_max+num2TraceMethods;
	    int numIncludePyAttributes = numUpToPyAttributes + pyAttributes;
	    
	    List<Double> numTopics = setChoices(5, 10, 25, 50, 100);
	    List<Double> passes = setChoices(10, 25, 50, 100, 200);
	    List<Double> chunksize = setChoices(100, 500, 1000, 2000, 5000);
	    List<Double> alpha = setChoices(0.01, 0.1, 1, 5, 10);
	    List<Double> beta = setChoices(0.01, 0.1, 1, 5, 10);
	    
	    List<List<Double>> pyList = new ArrayList<List<Double>>();
	    pyList.add(numTopics); pyList.add(passes); pyList.add(chunksize); pyList.add(alpha); pyList.add(beta);
	    
	    List<Integer> pyIndices = new ArrayList<Integer>();
	    for(int i = numUpToPyAttributes; i < numIncludePyAttributes; i++) {
	    	int index = (int) Math.floor(solution.getVariableValue(i));
	    	pyIndices.add(index);
	    }
	    
	    //preprocessor options
	    int stopwords = (int) Math.round(solution.getVariableValue(0));
	    int cleanup = (int) Math.round(solution.getVariableValue(1));
	    int stemmer = (int) Math.round(solution.getVariableValue(2));
	    int code = (int) Math.round(solution.getVariableValue(3));
	    int ngram = (int) Math.round(solution.getVariableValue(4));
	    
	    //tracing options
		int vsm = (int) Math.round(solution.getVariableValue(5));
		int vsm_ngram = (int) Math.round(solution.getVariableValue(6));
		int ngram_max = (int) Math.floor(solution.getVariableValue(7));
		int lda = (int) Math.round(solution.getVariableValue(8));
		int lsi = (int) Math.round(solution.getVariableValue(9));
		
		//lda & lsi options
		int num_topics = (int) getValue(pyIndices.get(0), pyList.get(0));
		int pass = (int) getValue(pyIndices.get(1), pyList.get(1));
		int chunk_size = (int) getValue(pyIndices.get(2), pyList.get(2)); 
		double a = getValue(pyIndices.get(3), pyList.get(3));
		double b = getValue(pyIndices.get(4), pyList.get(4));
		
		int similarity = (int) Math.round(solution.getVariableValue(15));
	    
	    TraceDynamo tD = new TraceDynamo(source, target, directory);	    
	    try {
	    	Chromosome c = new Chromosome(stopwords, cleanup, stemmer, code, ngram, vsm, vsm_ngram, ngram_max, lda, lsi, num_topics, pass, chunk_size, a, b, similarity);
	    	tD.run(c);
	    	/*tD.run((int) Math.round(solution.getVariableValue(0)), (int) Math.round(solution.getVariableValue(1)), (int) Math.round(solution.getVariableValue(2)), (int) Math.round(solution.getVariableValue(3)), (int) Math.round(solution.getVariableValue(4)),
	    			(int) Math.round(solution.getVariableValue(5)), (int) Math.round(solution.getVariableValue(6)), (int) Math.floor(solution.getVariableValue(7)), (int) Math.round(solution.getVariableValue(8)), (int) Math.round(solution.getVariableValue(9)),
					(int) getValue(pyIndices.get(0), pyList.get(0)), (int) getValue(pyIndices.get(1), pyList.get(1)), (int) getValue(pyIndices.get(2), pyList.get(2)), 
					getValue(pyIndices.get(3), pyList.get(3)), getValue(pyIndices.get(4), pyList.get(4)), (int) Math.round(solution.getVariableValue(15)));*/
			if(tD.getSimilarityResults() != null) {
				time = Cache.getTime(Cache.timeMap, c, directory);
				tD.performVoting((int) Math.round(solution.getVariableValue(16)));
				if(tD.getVotingResults() != null)
					results = tD.getVotingResults();
			}
			
				
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    if(results.isEmpty()) {
	    	MAP = 0;
	    }
	    else {
	    	MapCalculator calc = new MapCalculator();
	    	MAP = calc.evaluate(results, links);
	    	if(Double.isNaN(MAP)) {		
	    		MAP = 0;
	    	}
	    	else {
	    		if(MAP != 0)
	    			MAP = -MAP;
	    	}
		}
	    
	    solution.setObjective(0, time);
	    solution.setObjective(1,  MAP);
	}
	
	private List<Double> setChoices(double ... a) {
		List<Double> choices = new ArrayList<Double>();
		for(double i: a) {
			choices.add(i);
		}
		return choices;
	}
	
	private double getValue(int index, List<Double> list) {
		return (double) list.get(index);
	}
}
