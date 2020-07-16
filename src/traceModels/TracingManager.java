package traceModels;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import caching.Cache;
import links.Link;
import preprocessing.PreProcessor;
import traceDynamo.TraceDynamo;

public class TracingManager {	
	//private HashMap<String, HashMap<Link, Double>> similarityResults = new HashMap<String, HashMap<Link, Double>>();
	private HashMap<String, String> similarityResults = new HashMap<String, String>();
	
	private final static Logger logger = LoggerFactory.getLogger(TracingManager.class);
	
	public TracingManager(int vsm, 
			int vsm_ngram, int ngram_max, 
			int lda, int lsi,
			int num_topics, int passes, int chunk_size, double alpha, double beta,
			int similarity,
			HashMap<String, String> source, HashMap<String, String> target, 
			PreProcessor p, String directory, String preProcFile) throws IOException, ClassNotFoundException {
		
		String path = directory+"TracingModels/";
		Cache.createFolder(path);
		
		HashMap<Link, Double> map = null;
		String mapFile = null;
		if(vsm == 1) {
			String vsm_path = path + "VSM/";
			Cache.createFolder(vsm_path);
			mapFile = vsm_path+preProcFile+"_"+similarity+".map";
			File f = new File(mapFile);
			if(f.exists()) {
				logger.info("VSM results previously done...loading");
				//map = Cache.loadMapLinks(mapFile);
				TraceDynamo.runtime += Cache.timeMap.get(mapFile);
			}
			else {
				long startTime = System.nanoTime();
				TraceModel model = new VSM(source, target, similarity, p, vsm_path, preProcFile);
				map = model.run();
				double time = (double)(System.nanoTime() - startTime)/1_000_000_000.0;
				TraceDynamo.runtime += time;
				
				Cache.saveMapLinks(map, mapFile);
				Cache.timeMap.put(mapFile, time);
			}
			//similarityResults.put("VSM", map);
			similarityResults.put("VSM", mapFile);
		}
		if(vsm_ngram == 1) {
			String vsmngram_path = path+"VSMnGram/";
			Cache.createFolder(vsmngram_path);
			mapFile = vsmngram_path+preProcFile+"_"+ngram_max+"_"+similarity+".map";
			File f = new File(mapFile);
			if(f.exists()) {
				logger.info("VSMnGram results previously done...loading");
				//map = Cache.loadMapLinks(mapFile);
				TraceDynamo.runtime += Cache.timeMap.get(mapFile);
			}
			else {
				long startTime = System.nanoTime();
				TraceModel model = new VSMnGram(source, target, similarity, p, ngram_max, vsmngram_path, preProcFile);
				map = model.run();
				double time = (double)(System.nanoTime() - startTime)/1_000_000_000.0;
				TraceDynamo.runtime += time;
				
				Cache.saveMapLinks(map, mapFile);
				Cache.timeMap.put(mapFile, time);
			}
			//similarityResults.put("VSMnGram", map);
			similarityResults.put("VSMnGram", mapFile);
		}
		if(lda == 1) {
			String lda_path = path+"LDA/";
			Cache.createFolder(lda_path);
			String lda_att = num_topics+"_"+passes+"_"+chunk_size+"_"+alpha+"_"+beta;
			mapFile = lda_path+preProcFile+"_"+lda_att+"_"+similarity+".map";
			File f = new File(mapFile);
			if(f.exists()) {
				logger.info("LDA results previously done...loading");
				//map = Cache.loadMapLinks(mapFile);
				TraceDynamo.runtime += Cache.timeMap.get(mapFile);
			}
			else {
				long startTime = System.nanoTime();
				TraceModel model = new LDA(source, target, similarity, p, 4444, num_topics, passes, chunk_size, alpha, beta, lda_path, directory, preProcFile);
				map = model.run();
				double time = (double)(System.nanoTime() - startTime)/1_000_000_000.0;
				TraceDynamo.runtime += time;
				
				Cache.saveMapLinks(map, mapFile);
				Cache.timeMap.put(mapFile, time);
			}
			//similarityResults.put("LDA", map);
			similarityResults.put("LDA", mapFile);
		}
		if(lsi == 1) {
		 	String lsi_path = path+"LSI/";
			Cache.createFolder(lsi_path);
			String lsi_att = num_topics+"_"+chunk_size;
			mapFile = lsi_path+preProcFile+"_"+lsi_att+"_"+similarity+".map";
			File f = new File(mapFile);
			if(f.exists()) {
				logger.info("LSI results previously done...loading");
				//map = Cache.loadMapLinks(mapFile);
				TraceDynamo.runtime += Cache.timeMap.get(mapFile);
			}
			else {
				long startTime = System.nanoTime();
				TraceModel model = new LSI(source, target, similarity, p, 4444, num_topics, chunk_size, lsi_path, directory, preProcFile);
				map = model.run();
				double time = (double)(System.nanoTime() - startTime)/1_000_000_000.0;
				TraceDynamo.runtime += time;
				
				Cache.saveMapLinks(map, mapFile);
				Cache.timeMap.put(mapFile, time);
			}
			//similarityResults.put("LSI", map);
			similarityResults.put("LSI", mapFile);
		}
	}
	
	/*public HashMap<String, HashMap<Link, Double>> getSimilarityResults() {
		return similarityResults;
	}*/
	public HashMap<String, String> getSimilarityResults(){
		return similarityResults;
	}
}
