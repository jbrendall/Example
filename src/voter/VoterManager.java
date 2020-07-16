package voter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import caching.Cache;
import links.Link;
import traceDynamo.TraceDynamo;

public class VoterManager {
	private int voterIndex;
	private HashMap<String, String> results; 
	private HashMap<String, Double> thresholds;
	private String scoreOrderingKey;
	private HashMap<Link, Integer> finalResults;
	private Voter voter;
	private String path;
	
	private final static Logger logger = LoggerFactory.getLogger(VoterManager.class);
	
	public VoterManager(int voterIndex, HashMap<String, String> similarityResults, 
			HashMap<String, Double> thresholds, String path) {
		this.voterIndex = voterIndex;
		this.results = similarityResults;
		this.thresholds = thresholds;
		this.path = path;
		
		this.run();
	}
	
	//if you want to arrange results by a specific method, set this key using this constructor before running
	public VoterManager(int voterIndex, HashMap<String, String> results, 
			HashMap<String, Double> thresholds, String scoreOrderingKey, String path) {
		this.voterIndex = voterIndex;
		this.results = results;
		this.thresholds = thresholds;
		this.scoreOrderingKey = scoreOrderingKey;
		this.path = path+".results";
		
		this.run();
	}
	private void run() {
		
		logger.info("Setting up voter...");
		long startTime = System.nanoTime();
		if(voterIndex == 0) {
			voter = new MajorityRules(results, thresholds);
			finalResults = voter.run();
		}
				
		double time = (double)(System.nanoTime() - startTime)/1_000_000_000.0;
		TraceDynamo.runtime += time;
		
		finalResults = (HashMap<Link, Integer>) orderResults(finalResults, results);
		
		/*try {
			Cache.saveMapResults(finalResults, path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		logger.info("Voting completed...");
	}
	
	public HashMap<Link, Integer> getResults(){
		return finalResults;
	}
	
	private Map<Link, Integer> orderResults(HashMap<Link, Integer> results, HashMap<String, String> results2) {
		
		 HashMap<Link, ValueState> map = getRecord(results, results2);
		
		List<Map.Entry<Link, ValueState>> list = new ArrayList<Map.Entry<Link, ValueState>>(map.entrySet());
		
		Collections.sort(list,
				Map.Entry.comparingByValue(
						Comparator
							.comparingDouble(ValueState::getLinkState).thenComparingDouble(ValueState::getSimState).reversed()));

	    Map<Link, Integer> sortedMap = new LinkedHashMap<>();
	    for(Entry<Link, ValueState> entry : list) {
	    	sortedMap.put(entry.getKey(), entry.getValue().getLinkState().intValue());
	    }
	    
	    return sortedMap;
	}
	
	private HashMap<Link, ValueState> getRecord(HashMap<Link, Integer> results, HashMap<String, String> simResults) {
		HashMap<Link, ValueState> map = new HashMap<Link, ValueState>();
		HashMap<Link, Double> simMap = null;
		
		if(scoreOrderingKey != null) {
			try {
				simMap = Cache.loadMapLinks(simResults.get(scoreOrderingKey));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			Entry<String, String> entry = simResults.entrySet().iterator().next();
			try {
				simMap = Cache.loadMapLinks(entry.getValue());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			scoreOrderingKey = entry.getKey();
		}
		
		logger.info("Voter using {} for score ordering..,.", scoreOrderingKey);
		
		if(results.isEmpty()) {
			logger.info("Missing results............");
		}
		
		for(Entry<Link, Integer> result: results.entrySet()) {
			Link link = result.getKey();
			
			double linkValue = (double) result.getValue();
			double simValue = simMap.get(link);
			
			ValueState values = new ValueState(linkValue, simValue);
			
			map.put(link, values);
		}
		
		return map;
	}
	
	public class ValueState{
		private double linkState;
		private double simState;
		
		public ValueState(double linkValue, double simValue) {
			this.linkState = linkValue;
			this.simState = simValue;
		}
		
		public Double getLinkState() {
			return linkState;
		}
		
		public Double getSimState() {
			return simState;
		}
	}
}
