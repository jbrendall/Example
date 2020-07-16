package evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import links.Link;

public class MapCalculator {
	private int validLinkCount;
	
	public MapCalculator() {
		this.validLinkCount = 0;
	}
	
	//the set of potential links should already be in order based on similarity score
	//if not, do so before using evaluate
	public double evaluate(HashMap<Link, Integer> potentialLinks, HashMap<String, List<String>> links) {
		double MAP = 0;
		
		Map<String, List<Link>> resultPerSource = this.setEvaluationResult(potentialLinks, links);
	    MAP = this.calculateMAP(resultPerSource);
		
		return MAP;
	}
	
	private Map<String, List<Link>> setEvaluationResult(HashMap<Link, Integer> potentialLinks, HashMap<String, List<String>> links) {
		validLinkCount = 0;
		Map<String, List<Link>> resultPerSource = new HashMap<String, List<Link>>();
		
		List<Link> linksList = new ArrayList<Link>();
		for(Entry<String, List<String>> e: links.entrySet()) {
			for(String s: e.getValue()) {
				Link trueLink = new Link(e.getKey(), s);
				linksList.add(trueLink);
			}
		}
		
		for(Entry<Link, Integer> entry: potentialLinks.entrySet()) {
			Link link = entry.getKey();
			String sourceId = link.getSource();
			
			if(linksList.contains(link)) {
				//System.out.println(link.getSource()+":"+link.getTarget());
				link.setValid(true);
				validLinkCount++;
			}
			else
				link.setValid(false);
			
			if(resultPerSource.containsKey(sourceId))
				resultPerSource.get(sourceId).add(link);
			else {
				List<Link> list = new ArrayList<Link>();
				list.add(link);
				resultPerSource.put(sourceId, list);
			}
		}
		
		return resultPerSource;
	}
	
	private double calculateMAP(Map<String, List<Link>> resultPerSource) {
		double map = 0;
		double sourceWithValidlinkCount  =  0;
		for(String sourceId : resultPerSource.keySet()) {
			double ap = calculateAP(resultPerSource.get(sourceId));
			if(ap>0) {
				map = map + ap;
				sourceWithValidlinkCount++;
			}
		}
		map = map/sourceWithValidlinkCount;
		//System.out.println(map);
		return map;
	}
	
	private double calculateAP(List<Link> allLinksForSource) {
		int true_count = 0;
		int total_count = 0;
		double precision_sum = 0;
		for(int i = 0; i < allLinksForSource.size(); i++) {
			total_count++;
			Link link = allLinksForSource.get(i);
			//System.out.println(link.getSource()+","+link.getTarget());
			if(allLinksForSource.get(i).isValid()) {
				true_count++;
				//System.out.println(true_count+":"+total_count);
				precision_sum = precision_sum + (double)true_count/total_count;
			}
		}
		
		if(true_count > 0) {
			double ap = precision_sum/true_count;
			//System.out.println(ap);
			return ap;
		} else 
			return 0;
	}
	
}
