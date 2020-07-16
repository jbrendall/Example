package voter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import caching.Cache;
import links.Link;

public class MajorityRules implements Voter	{
	private HashMap<String, String> results;
	private HashMap<String, Double> thresholds;
	
	private final static Logger logger = LoggerFactory.getLogger(MajorityRules.class);
	
	public MajorityRules(HashMap<String, String> results2, HashMap<String, Double> thresholds) {
		this.results = results2;
		this.thresholds = thresholds;
	}
	
	@Override
	public HashMap<Link, Integer> run() {
		logger.info("MajorityRules voter starting...");
		
		HashMap<Link, Integer> finalResults = new HashMap<Link, Integer>();
		int numMethods = results.size();
		
		for(Entry<String, String> entry: results.entrySet()) {
			HashMap<Link, Double> map = null;
			try {
				map = Cache.loadMapLinks(entry.getValue());
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(finalResults.isEmpty()) {
				for(Link key: map.keySet()) {
					finalResults.put(key, 0);
				}
			}
			
			double threshold = thresholds.get(entry.getKey());
			
			for(Entry<Link, Double> e: map.entrySet()) {
				int update = 0;
				Link link = e.getKey();
				double score = e.getValue();
				if(score >= threshold)
					update = 1;
				
				/*if(finalResults.get(link) == null)
					finalResults.put(link, 0);*/
				
				update += finalResults.get(link);				
				
				finalResults.put(link, update);
			}
		}
		
		for(Entry<Link, Integer> entry: finalResults.entrySet()) {
			if(numMethods % 2 == 0) {
				if(entry.getValue() >= numMethods/2) finalResults.put(entry.getKey(), 1);
				else finalResults.put(entry.getKey(), 0);
			}
			else
				if(entry.getValue() >= (numMethods/2 + 1)) finalResults.put(entry.getKey(), 1);
				else finalResults.put(entry.getKey(), 0);
		}
		logger.info("MajorityRules voter finished...");
		return finalResults;
	}
}
