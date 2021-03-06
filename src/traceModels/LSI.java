package traceModels;

import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import caching.Cache;
import links.Link;
import preprocessing.PreProcessor;
import pyext.PythonExtension;
import traceDynamo.TraceDynamo;

public class LSI implements TraceModel{
	private HashMap<String, String> source;
	private HashMap<String, String> target;
	private HashSet<String> links;
	private int similarity;
	private PreProcessor preProc;
	private int port;
	private String path;
	private String dir;
	private String dictionary;
	
	private int num_topics;
	private int chunk_size;
	
	private HashMap<Link, Double> results;
	
	private final static Logger logger = LoggerFactory.getLogger(LSI.class);
	
	public LSI(HashMap<String, String> source, HashMap<String, String> target, int similarityType, PreProcessor p,
			int port, int num_topics, int chunk_size, 
			String lsi_path, String directory, String preProcFile) {
		this.source = source;
		this.target = target;
		//this.links = links;
		this.similarity = similarityType;
		this.preProc = p;
		this.port = port;
		String lsi_att = num_topics+"_"+chunk_size;
		this.path = lsi_path+preProcFile+"_"+lsi_att+".model";
		this.dir = directory;
		this.dictionary = lsi_path + preProcFile +".dict";
		
		this.num_topics = num_topics;
		this.chunk_size = chunk_size;
	}
	
	@Override
	public HashMap<Link, Double> run() {
		logger.info("Starting LSI analysis...");
		HashMap<Link, Double> results = new HashMap<Link, Double>();

		JSONObject message;
		try {
			JSONObject result = PythonExtension.run("LSI",
								getAttributes(),
								buildData(),
								port, dir);
			results = parseResults(result);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("LSI finished...");
		return results;
	}
	
	private JSONObject buildData() throws JSONException {
		source = TraceFunctions.processData(source, preProc);
		target = TraceFunctions.processData(target, preProc);
		
		JSONObject data = new JSONObject()
				.put("source", source)
				.put("target", target);
		
		return data;
	}
	
	private JSONObject getAttributes() throws JSONException {
		JSONObject attributes = new JSONObject()
				.put("path", path)
				.put("dict", dictionary)
				.put("similarity", similarity)
				.put("num_topics", num_topics)
				.put("chunk_size", chunk_size);
				
		return attributes;
	}
	
	private HashMap<Link, Double> parseResults(JSONObject data) throws JSONException {
		HashMap<Link, Double> results = new HashMap<Link, Double>();
		
		JSONArray jList = data.getJSONArray("lsi_result");
		
		for(int i = 0; i < jList.length(); i++) {
			String value = jList.get(i).toString();
			
			value = value.replaceAll(" ", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
			String[] output = value.split(",");
			
			Link link = new Link(output[0], output[1]);
			double score = Double.parseDouble(output[2]);
			
			results.put(link, score);
		}
		
		double model_time = data.getDouble("model_time");
		double dict_time = data.getDouble("dict_time");
		
		if(model_time != -1) {
			Cache.timeMap.put(path, model_time);
		}
		
		if(dict_time != -1) {
			Cache.timeMap.put(dictionary, dict_time);
		}
		
		return results;
	}

}
