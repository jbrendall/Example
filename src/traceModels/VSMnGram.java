package traceModels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.Agent;
import agents.VSMWithNGramAgent;
import caching.Cache;
import dictionaries.DictBuilderNGram;
import dictionaries.Dictionary;
import dictionaries.DictionaryStaticFactory;
import documents.DocumentManager;
import documents.Query;
import links.Link;
import preprocessing.PreProcessor;
import traceDynamo.TraceDynamo;

public class VSMnGram implements TraceModel{

	private HashMap<String, String> source;
	private HashMap<String, String> target;
	private HashMap<String, String> links;
	private String similarity;
	private PreProcessor preProc;
	private int ngram_max;
	
	private DocumentManager documentManager;
	private Dictionary dictionary;
	private String path;
	
	private HashMap<Link, Double> results;
	
	private final static Logger logger = LoggerFactory.getLogger(VSMnGram.class);

	public VSMnGram(HashMap<String, String> source, HashMap<String, String> target, int similarityType, 
			PreProcessor p, int max, String directory, String preProcFile) {
		logger.info("Starting VSMnGram analysis...");
		this.source = source;
		this.target = target;
		this.similarity = TraceFunctions.getSimilarity(similarityType);
		this.preProc = p;
		this.ngram_max = max;
		path = directory+preProcFile+"_"+ngram_max+".dict";
	}
	
	@Override
	public HashMap<Link, Double> run() {
		ArrayList<Query> allQueries = TraceFunctions.getSourceArtifacts(source, preProc);
		documentManager = TraceFunctions.getTargetArtifacts(target, preProc);
		dictionary = buildDictionary(path);
		
		Agent agent = new VSMWithNGramAgent(documentManager, dictionary, similarity, preProc);
		
		try {
			 results =  TraceFunctions.runExperiment(agent, allQueries, documentManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("VSMnGram finished...");
		return results;
	}

	public Dictionary buildDictionary(String path) {
		Dictionary dict = null;
        
        try {
        	File file = new File(path);
        	if(file.exists()) {
        		logger.info("Dictionary already exists...loading dictionary");
        		dict = Cache.readDictionary(path);
        		//TraceDynamo.runtime += Cache.timeMap.get(path);
        	}
        	else {
        		long startTime = System.nanoTime();
        		String type = "NGram";
        		DictBuilderNGram builder = (DictBuilderNGram) DictionaryStaticFactory.getDictionaryBuilder(type);
        		builder.setTextPreProcessor(preProc);
                ((DictBuilderNGram)builder).setNgram_max(ngram_max);
                ((DictBuilderNGram)builder).setNgram_min(2);
        		dict = builder.build(type, documentManager);
        		double time = (double)(System.nanoTime() - startTime)/1_000_000_000.0;
        		//TraceDynamo.runtime += time;
        		
        		Cache.saveDictionary(dict, path);
        		Cache.timeMap.put(path, time);
        	}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return dict;
	}

}
