package documents;

import java.util.HashMap;

import dictionaries.Dictionary;
import dictionaries.TermEntry;

/**
 *
 * @author Marek
 */
public class Query implements Comparable<Query> {

	//Default Constructor.
	public Query() {}
	
    public Query(String id, String originalText, String processedText) {
        this.id = id;
        this.originalText = originalText;
        this.processedText = processedText;
        
        calculateTermFrequencies();
    }

    public String getId() {
        return id;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getText() {
        return processedText;
    }

    public void setText(String preprocessedText) {
        processedText = preprocessedText;
        calculateTermFrequencies();
    }

    // The comparable interface allows a collection of Postings to be ordered
    public int compareTo(Query e) {
        if (id.compareTo(e.getId()) < 0) {
            return -1;
        } else if (id.equals(e.getId())) {
            return 0;
        } else {
            return 1;
        }
    }

    public HashMap<String, Double> getTermFrequencies() {
        return queryTermFrequency;
    }
    public HashMap<String, Double> getTermWeights() {
        return queryTermWeight;
    }
    public double getQueryVectorLength(){
        return queryVectorLength;
    }
    public void calculateTermWeights(Dictionary dict) {
        this.queryTermWeight = new HashMap<String, Double>();

        String[] s = queryTermFrequency.keySet().toArray(new String[0]);

        for (String qterm : s) {
            if (!dict.containsTermEntry(qterm)) {
                queryTermFrequency.remove(qterm);
            }
        }

        for (String qterm : queryTermFrequency.keySet()) {

            // Gets the Term from the dictionary
            TermEntry term = dict.getTermEntry(qterm);

            // It computes the weight of a term -  IE the frequency TIMES the term's specificity.
            // Note: the specifity of the term is stored in the weight.
            // 		For the basic dictionary this is just 1
            //		For the tf-idf dictionary this is the idf
            // 		For the signal-noise this is the signal
            double weight = queryTermFrequency.get(qterm) * term.getWeight();
            queryTermWeight.put(qterm, weight);

            // Updates the document vector length of the query
            queryVectorLength += Math.pow(weight, 2);
        }
        queryVectorLength = Math.sqrt(queryVectorLength);
    }

    public void calculcateVectorLength(){
        queryVectorLength = 0.0;
         for (String qterm : queryTermFrequency.keySet()) {
            double weight = queryTermWeight.get(qterm);
            queryVectorLength += Math.pow(weight, 2);
        }
        queryVectorLength = Math.sqrt(queryVectorLength);
    }

    private void calculateTermFrequencies() {
        this.queryTermFrequency = new HashMap<String, Double>();
        
        // The query is broken down into tokens
        String[] queryTerms = processedText.split(" ");

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
    }
    
    // Changed the visibility of members from private to protected by Jin
    protected String id;
    protected String originalText;
    protected String processedText;
    protected double queryVectorLength;
    protected HashMap<String, Double> queryTermFrequency;
    protected HashMap<String, Double> queryTermWeight;
}
