package documents;

import java.util.HashMap;
import java.util.List;

import preprocessing.PreProcCode;
/**
 * @author Jin Guo
 * 
 * Class that implements the Query with Camel Case Weighting support.
 *
 */
public class QueryWithCamelCaseWeighting extends Query {
	static private PreProcCode preProc = new PreProcCode("./stopwords.txt");
	static private double camelCaseTokenWeight = 1;
	
	public static double getCamelCaseTokenWeight() {
		return camelCaseTokenWeight;
	}
	public static void setCamelCaseTokenWeight(double camelCaseTokenWeight) {
		QueryWithCamelCaseWeighting.camelCaseTokenWeight = camelCaseTokenWeight;
	}


	public QueryWithCamelCaseWeighting(String id, String originalText,
			String processedText) {
		 this.id = id;
	     this.originalText = originalText;
	     this.processedText = processedText;
	     preProc.setPreserveCamelCaseWord(false);
	     calculateTermFrequencies();
	}
	

	private void calculateTermFrequencies() {
        this.queryTermFrequency = new HashMap<String, Double>();
        
        // The query is broken down into tokens
        String[] queryTerms = preProc.process(originalText).split(" ");

        // Iterates over each query term to compute the term frequency
        for (String qterm : queryTerms) {
            // It only cares about those words that are in the dictionary

            if(qterm.length() == 0)
                continue;
            
            if (!queryTermFrequency.containsKey(qterm)) {
                // First time the query word is encountered
                queryTermFrequency.put(qterm, 1.0);
            } else {
                // The query word is already there, so the frequency gets increased
                queryTermFrequency.put(qterm, queryTermFrequency.get(qterm) + 1.0);
            }
        }
        
        //Add the camel case tokens
        List<String> camelCaseTokens = preProc.getCamelCaseTokens(originalText);
        for(String t:camelCaseTokens) {
        	if (!queryTermFrequency.containsKey(t)) {
                // First time the query word is encountered
                queryTermFrequency.put(t, camelCaseTokenWeight);
            } else {
                // The query word is already there, so the frequency gets increased
                queryTermFrequency.put(t, queryTermFrequency.get(t) + camelCaseTokenWeight);
            }
        }
        
		
    }

}
