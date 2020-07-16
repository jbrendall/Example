package dictionaries;

import java.util.HashMap;

import documents.Document;
import documents.DocumentManager;;

/**
 * @author Carlos Castro
 * 
 * Class that implements the Dictionary Builder Interface and extends the basic builder.
 * This particular builder will create a dictionary using the tf-idf formulas.
 * It utilizes the basic frequency counts that get computed in the basic builder.
 * The term weights are normalized 
 *
 */
public class DictBuilderTFIDF extends DictBuilderBasic implements DictionaryBuilder{
	
	public Dictionary build(String name, DocumentManager docMgr){
		// Variables
		int N = docMgr.getNumOfDocuments(); // Total Number of Documents
		double idf;
		String docId; 
		double vectorLength;
		// Stores the vector length of each document - this is used to normalize the term weights
		// The vector length is the square root of the sum of the squares of all the term weights.
		HashMap<String, Double> documentVectorLength = new HashMap<String, Double>();	
		
		// It starts off by calling the parent method, which will calculate the basic frequencies
		Dictionary dict = super.build(name, docMgr);
		
		// Iterates over all the terms
		for (TermEntry term : dict.getTermEntries()) {
			// Calculates the idf for each term - and stores this in the weight of the term - for weighing queries later
			idf = Math.log10(N/((double) term.getNumOfDocs()));
			term.updateWeight(idf);
			
			// Iterates over all the postings
			for (Posting posting : term.getPostings()) {
				// Multiplies each term frequency by the idf
				double newWeight = posting.getFrequency() * idf;
				posting.updateWeight(newWeight);
			
				// Updates the document vector length
				docId = posting.getDocId();
				vectorLength = Math.pow(newWeight, 2);
				if (documentVectorLength.containsKey(docId)) {
					// The document has other terms
					vectorLength += documentVectorLength.get(docId);
				}
				documentVectorLength.put(docId, vectorLength);
			}
		}
		
		// Now, we need to get the square root of all entries in the document vector length
		for (Document d : docMgr.getDocuments()) {
			docId = d.getId();
			vectorLength = Math.sqrt(documentVectorLength.get(docId));
			documentVectorLength.put(docId, vectorLength);
			// Here we update the document vector length of the dictionary - not the internal structure anymore
			dict.setDocumentVectorWeight(docId,vectorLength);
		}
		
		// Lastly, we normalize all the term weights
		// This was removed, to standarize all the dictionaries - none are normalized, 
		// Note that the document vector lengths are available in case normalization is needed - some similarity metrics need this
		/*
		for (TermEntry term : dict.getTermEntries()) {
			for (Posting posting : term.getPostings()) {
				vectorLength = documentVectorLength.get(posting.getDocId());
				posting.updateWeight(posting.getWeight() / vectorLength);
			}
		}
		*/
		
		return dict;
	}

}
