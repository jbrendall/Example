package retrieval;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import dictionaries.Dictionary;
import dictionaries.Posting;
import dictionaries.TermEntry;
import documents.Document;
import documents.DocumentManager;
import documents.Query;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Carlos Castro, major changes done by Marek Gibiec  to support functionality needed by Rocchio
 * 
 * Class that implements the Searcher Interface.
 * This is the basic search engine.  
 * It calculates the dot product of the two vectors and delegates the actual scores to the similarity metric object
 * The idea is that more sophisticated searchers can extend this one.
 * This searcher also assumes that the query has already been pre-processed.
 *
 */
public class SearcherBasic implements Searcher{
	// Variables
	SimilarityMetric _similarityMetric;
	
	// Constructor
	SearcherBasic(SimilarityMetric similarityMetric) {
		_similarityMetric = similarityMetric;
	}
	
	
        
//    public void calculateWeights(Query queryDoc, Dictionary dict, HashSet<String> traceMatrix) {
//
//        HashMap<String, Double> queryTermFrequency;
//        HashMap<String, Double> queryTermWeight;
//
//
//        intermediateResults = new HashMap<String, Double>();	// Where the intermediate results of the query are kept.
//        queryTermFrequency = queryDoc.getTermFrequencies();	// Keeps track of term frequencies
//        queryTermWeight = new HashMap<String, Double>();	// Keeps track of term weights
//
//        queryVectorLength = 0.0;				// The document vector length of the query
//
//        String[] s = queryTermFrequency.keySet().toArray(new String[0]);
//
//        for (String qterm : s) {
//            if (!dict.containsTermEntry(qterm)) {
//                queryTermFrequency.remove(qterm);
//            }
//        }
//        // Iterates over the resulting query terms to compute their weights and the dot product of the query terms x the documents terms
//
//
//
//
//    }

    public List<Result> search(Query queryDoc, Dictionary dict, DocumentManager docMgr) {
        HashMap<String, Double> intermediateResults = new HashMap<String, Double>();
        double queryVectorLength = queryDoc.getQueryVectorLength();
        // Variables
        String queryId = queryDoc.getId();
        ArrayList<Result> results;

        // Initializes the data structures
        results = new ArrayList<Result>();	// Result array
        HashMap<String, Double> queryTermWeight = queryDoc.getTermWeights();
        HashMap<String, Double> queryTermFrequency = queryDoc.getTermFrequencies();

        String[] s = queryTermFrequency.keySet().toArray(new String[0]);

        for (String qterm : s) {
            if (!dict.containsTermEntry(qterm)) {
                queryTermFrequency.remove(qterm);
            }
        }
        
        for (String qterm : queryTermFrequency.keySet()) {
            TermEntry term = dict.getTermEntry(qterm);
            for (Posting posting : term.getPostings()) {
                String docId = posting.getDocId();

                // Calculates the product of the query times the posting for this particular term
                double r = queryTermWeight.get(qterm) * posting.getWeight();
                if (intermediateResults.containsKey(docId)) {
                    r += intermediateResults.get(docId);
                }
                intermediateResults.put(docId, r);
            }
        }
        
        // Add any document pair that is has no common terms with score of zero
        for(Document d: docMgr.getDocuments()) {
        	if(!intermediateResults.containsKey(d.getId())) {
        		intermediateResults.put(d.getId(), 0.0);
        	}
        }

        // The document vector length for the query is the square root of the sum of the squares of the term weights
        
        // It iterates over the intermediate results to create the final array that is returned to the user
        for (String docId : intermediateResults.keySet()) {
            // Result r = new ResultObj(docId, intermediateResults.get(docId));
            boolean ifCorrect = false;
            /*if (traceMatrix.contains(queryId + "\t" + docId) || traceMatrix.contains(docId + "\t" + queryId)) {
                ifCorrect = true;                
            }*/

            double similarity = _similarityMetric.computeSimilarity(intermediateResults.get(docId), queryVectorLength, dict.getDocumentVectorWeight(docId));
            Result r = new ResultObj(docId, similarity, ifCorrect);
            results.add(r);
        }

        Collections.sort(results);
        return results;
    }
}
