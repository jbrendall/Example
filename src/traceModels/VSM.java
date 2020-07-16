package traceModels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.Agent;
import agents.VSMAgent;
import caching.Cache;
import dictionaries.Dictionary;
import dictionaries.DictionaryBuilder;
import dictionaries.DictionaryStaticFactory;
import documents.DocumentManager;
import documents.Query;
import links.Link;
import preprocessing.PreProcessor;
import traceDynamo.TraceDynamo;

public class VSM implements TraceModel{
	private HashMap<String, String> source;
	private HashMap<String, String> target;
	private String similarity;
	private PreProcessor preProc;
	
	private DocumentManager documentManager;
	private Dictionary dictionary;
	private String path;
	
	private HashMap<Link, Double> results;
	
	private final static Logger logger = LoggerFactory.getLogger(VSM.class);

	public VSM(HashMap<String, String> source, HashMap<String, String> target, int similarityType, 
			PreProcessor p, String directory, String preProcFile) { 
		logger.info("Starting VSM analysis...");
		this.source = source;
		this.target = target;
		this.similarity = TraceFunctions.getSimilarity(similarityType);
		this.preProc = p;
		this.path = directory+preProcFile+".dict";
	}
	
	@Override
	public HashMap<Link, Double> run() {
		ArrayList<Query> allQueries = TraceFunctions.getSourceArtifacts(source, preProc);
		documentManager = TraceFunctions.getTargetArtifacts(target, preProc);
		dictionary = buildDictionary(path);
		
		Agent agent = new VSMAgent(documentManager, dictionary, similarity);
		
		try {
			 results =  TraceFunctions.runExperiment(agent, allQueries, documentManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("VSM finished...");
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
        		String type = "TFIDF";
        		DictionaryBuilder builder = DictionaryStaticFactory.getDictionaryBuilder(type);
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
