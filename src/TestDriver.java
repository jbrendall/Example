import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;

import caching.Cache;
import chromosome.Chromosome;
import evaluation.MapCalculator;
import links.Link;
import multiobjective.TraceDynamoExperiment;
import traceDynamo.TraceDynamo;

public class TestDriver {
	private final static String stopWordsFile = "resources/stopwords.txt";	
	public static void main(String[] args) throws IOException, ClassNotFoundException, JSONException {
		System.out.println("Testing TraceDynamo...");
		
		int runs = 1;
		int numEvals = 5;
		int popSize = 50;
		
		String source = "easyClinicUC.csv";
		String target = "easyClinicCC.csv";
		String knownLinks = "easyClinicLinks.csv";
		String directory = "easyClinicResults";
		
		
		if(args.length != 0) {
			runs = Integer.parseInt(args[0]);
			numEvals = Integer.parseInt(args[1]);
			popSize = Integer.parseInt(args[2]);
			
			source = args[3];
			target = args[4];
			knownLinks = args[5];
			directory = args[6];
			
		}
		
		//String dir2 = "C:/Users/jrendal1/Desktop/";
		
		HashMap<String, String> map = readCSV(source);
		HashMap<String, String> map2 = readCSV(target);
		
		HashMap<String, List<String>> links = readCSVLinks(knownLinks);
		
		String dir = directory +"/";
		Cache.createFolder(dir);
		
		String timeMapPath = dir+"timeMap.map";
		File f = new File(timeMapPath);
		if(f.exists())
			Cache.timeMap = Cache.loadMapTime(timeMapPath);
		else
			Cache.timeMap = new ConcurrentHashMap<String, Double>();
		
		TraceDynamoExperiment tde = new TraceDynamoExperiment(map, map2, links, dir, runs, numEvals, popSize);
		/*TraceDynamo tD = new TraceDynamo(map, map2, dir);
		
		Chromosome c = new Chromosome(1, 0, 1, 1, 1, 1, 1, 3, 1, 1, 25, 50, 500, 0.1, 5, 0);
		
		tD.run(c);
		if(tD.getSimilarityResults() != null) {
			if(tD.getSimilarityResults().containsKey("VSM")) {
				writeCSV(Cache.loadMapLinks(tD.getSimilarityResults().get("VSM")), dir+"VSM.csv");
			}
			if(tD.getSimilarityResults().containsKey("VSMnGram")) {
				writeCSV(Cache.loadMapLinks(tD.getSimilarityResults().get("VSMnGram")), dir+"VSMnGram.csv");
			}
			if(tD.getSimilarityResults().containsKey("LDA")) {
				writeCSV(Cache.loadMapLinks(tD.getSimilarityResults().get("LDA")), dir+"LDA.csv");
			}
			if(tD.getSimilarityResults().containsKey("LSI")) {
				writeCSV(Cache.loadMapLinks(tD.getSimilarityResults().get("LSI")), dir+"LSI.csv");
			}
			
			tD.performVoting(0);
			if(tD.getVotingResults() != null) {
				MapCalculator calc = new MapCalculator();
				double MAP = calc.evaluate(tD.getVotingResults(), links);
				System.out.println("Time is: "+Cache.getTime(Cache.timeMap, c, dir));
				System.out.println("MAP is: "+MAP);
			}
		}
		else
			System.out.println("Execution could not be completed");
		writeTimeCSV(Cache.timeMap, dir+"times.csv");*/
		Cache.saveMapTime(Cache.timeMap, timeMapPath);
	}
	
	private static HashMap<String, String> readCSV(String filepath) throws IOException {
		HashMap<String, String> results = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] temp = line.split(",");
			String id = temp[0];
			String value = temp[1];

			results.put(id, value);
				
		}
		return results;
	}
	
	private static HashMap<String, List<String>> readCSVLinks(String filepath) throws IOException {
		HashMap<String, List<String>> results = new HashMap<String, List<String>>();
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line = null;
		while((line = reader.readLine()) != null) {
			List<String> values = null;
			String[] temp = line.split(",");
			String id = temp[0];
			String value = temp[1];
			
			if(results.containsKey(id)) {
				values = results.get(id);
				values.add(value);
				results.put(id, values);
			}
			else {
				values = new ArrayList<String>();
				values.add(value);
				results.put(id, values);
			}
			
		}
		return results;
	}
	
	private static void writeCSV(HashMap<Link, Double> results, String outDir) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(outDir));
		String names = "id,id,score\n";
	    pw.write(names);
	    for(Entry<Link, Double> entry: results.entrySet()) {
	    	Link link = entry.getKey();
	    	String score = Double.toString(entry.getValue());
	    	String e = link.getSource()+","+link.getTarget()+","+score+"\n";
	    	pw.write(e);
	    }
	    pw.close();
	}
	
	private static void writeTimeCSV(ConcurrentHashMap<String, Double> timeMap, String outDir) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(outDir));
		String names = "id,time\n";
	    pw.write(names);
	    for(Entry<String, Double> entry: timeMap.entrySet()) {
	    	String link = entry.getKey();
	    	String score = Double.toString(entry.getValue());
	    	String e = link+","+score+"\n";
	    	pw.write(e);
	    }
	    pw.close();
	}
	
	private static void writeIntCSV(HashMap<Link, Integer> results, String outDir) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(outDir));
		String names = "id,id,score\n";
	    pw.write(names);
	    for(Entry<Link, Integer> entry: results.entrySet()) {
	    	Link link = entry.getKey();
	    	String score = Double.toString(entry.getValue());
	    	String e = link.getSource()+","+link.getTarget()+","+score+"\n";
	    	pw.write(e);
	    }
	    pw.close();
	}
	
	private static void writeConcate(HashMap<Link, Integer> votingResults, HashMap<String, String> results, String outDir) throws ClassNotFoundException, IOException {
		PrintWriter pw = new PrintWriter(new File(outDir));
		StringBuilder names = new StringBuilder();
		names.append("id,id,score");
	    for(Entry<String, String> e: results.entrySet()) {
			names.append(","+e.getKey());
		}
	    names.append("\n");
	    pw.write(names.toString());
		
		for(Entry<Link, Integer> entry: votingResults.entrySet()) {
			StringBuilder value = new StringBuilder();
			Link link = entry.getKey();
			int simScore = entry.getValue();
			value.append(link.getSource()+","+link.getTarget()+","+Double.toString(simScore));
			for(Entry<String, String> e: results.entrySet()) {
				HashMap<Link,Double> map = Cache.loadMapLinks(e.getValue());
				if(map.containsKey(link))
					value.append(","+Double.toString(map.get(link)));
				else
					value.append(", DNE");
			}
			value.append("\n");
			pw.write(value.toString());
		}
	    pw.close();
	}
}
