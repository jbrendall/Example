package documents;

import java.util.HashMap;

import opennlp.tools.ngram.NGramModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.StringList;
import preprocessing.PreProcessor;
/**
 * @author Jin Guo
 * 
 * Class that implements the Query with NGram support.
 *
 */
public class OueryAugmentedWithNGram extends Query {

	static private PreProcessor preProc;
	
	public OueryAugmentedWithNGram(String id, String originalText,
			String processedText) {
		 this.id = id;
	     this.originalText = originalText;
	     this.processedText = processedText;
	        
	     calculateTermFrequencies();
	}
	
	public static void setTextPreProcessor(PreProcessor preProc) {
		OueryAugmentedWithNGram.preProc = preProc;
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
        
        try {
			StringList tokens = new StringList(
					WhitespaceTokenizer.INSTANCE.tokenize(processedText));
			NGramModel nGramModel = new NGramModel();
	        nGramModel.add(tokens, 2, 3);
	        for (StringList ngram : nGramModel) {
	        	String ngramString = ngram.toString();
	        	if (!queryTermFrequency.containsKey(ngramString)) {
	                // First time the query word is encountered
	                queryTermFrequency.put(ngramString, (double) nGramModel.getCount(ngram));
	            } else {
	                // The query word is already there, so the frequency gets increased
	                queryTermFrequency.put(ngramString, 
	                		queryTermFrequency.get(ngramString) + nGramModel.getCount(ngram));
	            }
	        }
        } catch (IllegalArgumentException e) {
        	System.out.println("Argument invalid for tokenizer.");
		}
		
    }

}
