package documents;

import java.util.HashMap;

import dictionaries.Dictionary;
import dictionaries.TermEntry;
import preprocessing.PreProcStemmer;
import preprocessing.PreProcessor;

public class QueryFeatureVectorSpace extends Query{
	PreProcessor stemmer = new PreProcStemmer();
	public QueryFeatureVectorSpace(String id, String originalText, String text) {
		super(id, originalText, text);
	}
	@Override
    public void calculateTermWeights(Dictionary dict) {
        this.queryTermWeight = new HashMap<String, Double>();

        for (String qterm : queryTermFrequency.keySet()) {

            // Gets the Term from the dictionary
        	String stemFrom = stemmer.process(qterm).trim();
        	double weight=0;
        	if(!dict.containsTermEntry(stemFrom)) {
        		weight = queryTermFrequency.get(qterm);
        	} else {
	            TermEntry term = dict.getTermEntry(stemFrom);
	
	            // It computes the weight of a term -  IE the frequency TIMES the term's specificity.
	            // Note: the specifity of the term is stored in the weight.
	            // 		For the basic dictionary this is just 1
	            //		For the tf-idf dictionary this is the idf
	            // 		For the signal-noise this is the signal
	            weight = queryTermFrequency.get(qterm) * term.getWeight();
        	}
            queryTermWeight.put(qterm, weight);

            // Updates the document vector length of the query
            queryVectorLength += Math.pow(weight, 2);
        }
        queryVectorLength = Math.sqrt(queryVectorLength);
    }
}
