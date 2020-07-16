package traceModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import agents.Agent;
import documents.Document;
import documents.DocumentManager;
import documents.DocumentManagerStaticFactory;
import documents.Query;
import links.Link;
import preprocessing.PreProcCode;
import preprocessing.PreProcessor;
import retrieval.Result;
import retrieval.ResultObj;

public class TraceFunctions {
	private final static String stopWordsFile = "resources/stopwords.txt";
	private static PreProcCode preProcCode = new PreProcCode(stopWordsFile);
	private static String preProcContent = "";
	
	public static ArrayList<Query> getSourceArtifacts(HashMap<String, String> map, PreProcessor preProc) {
		ArrayList<Query> allQueries = new ArrayList<Query>();
		
		for(Entry<String, String> entry: map.entrySet()) {
			String id = entry.getKey();
			String content = entry.getValue();
			if((id != null && !id.isEmpty()) && (content != null && !content.isEmpty())) {
				if(preProc != null)
					preProcContent = preProc.process(content).trim();
				else
					preProcContent = content.trim();
				Query query = new Query(id, content, preProcContent);
				allQueries.add(query);
			}
			else System.out.println(id + " is missing information, so will not be used");
		}
		return allQueries;
	}
	
	public static DocumentManager getTargetArtifacts(HashMap<String, String> map, PreProcessor preProc){
		if(preProc == null) preProc = preProcCode;
		DocumentManager docMgr = DocumentManagerStaticFactory.getNewDocumentManager(preProc);
		
		for(Entry<String, String> entry: map.entrySet()) {
			String id = entry.getKey();
			String content = entry.getValue();
			if((id != null && !id.isEmpty()) && (content != null && !content.isEmpty())) {
				//remove unnecessary spacing
				content = content.replaceAll("[\\t\\n\\r]+", " ");
				docMgr.addDocument(id, content);
			}
			else System.out.println(id + " is missing information, so will not be used");
		}
		return docMgr;
	}
	
	public static HashSet<String> getLinks(HashMap<String, String> map) {
		HashSet<String> links = new HashSet<String>();
		
		for(Entry<String, String> entry: map.entrySet()) {
			String source = entry.getKey();
			String target = entry.getValue();
			links.add(source + "\t" + target);
		}
		return links;
	}
	
	public static List<Result> makeFinalResults(Query query, DocumentManager artifactTo, List<Result> results) {
		ArrayList<Document> documents = new ArrayList<Document>(artifactTo.getDocuments());
		if(results.size() == 0)
			return results;
		String queryId = query.getId();
		for(Document doc : documents){
			String docId = doc.getId();
			ResultObj res = new ResultObj(docId, 0, false);
			results.add(res);
			/*int index = results.indexOf(res);
			if(index == -1){
				if(answerSet.contains(queryId + "\t" + docId) || answerSet.contains(docId + "\t" + queryId))
					results.add(new ResultObj(docId, 0, true));
				else
					results.add(new ResultObj(docId, 0, false));
			}*/
		}
		return results;
	}
	
	public static HashMap<Link, Double> runExperiment(Agent agent, ArrayList<Query> source, DocumentManager target) throws Exception {
		HashMap<Link, Double> finalResults = new HashMap<Link, Double>();

		for(Query query: source) {
			agent.setQuery(query);
			agent.run();
			List<Result> results = agent.getResults();
			
			for(Result result: results) {
				double score = result.getRanking();
				if(Double.isNaN(score)) score = 0.0;
				Link link = new Link(query.getId(), result.getDocId());
				finalResults.put(link,  score);
				/*if(result.isCorrect()) {
					Link link = new Link(query.getId(), result.getDocId());
					finalResults.put(link, result.getRanking());
				}
				else {
					Link link = new Link(query.getId(), result.getDocId());
					finalResults.put(link, result.getRanking());
				}*/
			}
		}
		return finalResults;
	}
	
	public static String getSimilarity(int value) {
		String similarity = null;
		if(value == 0) similarity = "Cosine";
		if(value == 1) similarity = "Jaccard";
		return similarity;
	}
	
	public static HashMap<String, String> processData(HashMap<String, String> data, PreProcessor preProc){
		if(preProc != null) {
			for(Entry<String, String> entry: data.entrySet()) {
				entry.setValue(preProc.process(entry.getValue()));
			}
		}
		return data;
	}
	
	public static void jsonToMap(String t) {

        HashMap<String, String> map = new HashMap<String, String>();
		try {
			JSONObject jObject = new JSONObject(t);
			Iterator<?> keys = jObject.keys();

	        while( keys.hasNext() ){
	            String key = (String)keys.next();
	            String value = jObject.getString(key); 
	            System.out.println(key+","+value);
	        }
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
}
