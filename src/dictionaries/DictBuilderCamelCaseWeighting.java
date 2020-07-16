package dictionaries;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import documents.Document;
import documents.DocumentManager;
import preprocessing.PreProcCode;
import preprocessing.PreProcessor;
/**
 * @author Jin Guo
 * 
 * Build the dictionary with document corpus using the tf-idf schema. 
 * The dictionary is enhanced with identified camel case tokens.
 */
public class DictBuilderCamelCaseWeighting implements DictionaryBuilder{
	private PreProcessor preProc;

	@Override
	public Dictionary build(String name, DocumentManager docMgr) {
		// Variables
		String docId;
		double vectorLength;
		// Stores the vector length of each document - this is used to normalize the term weights
		// The vector length is the square root of the sum of the squares of all the term weights.
		HashMap<String, Double> documentVectorLength = new HashMap<String, Double>();	
		
		// Creates the dictionary
		Dictionary dict = new DictionaryObj(name);
		
		// Gets all the documents in the collection
		Collection <Document> docs = docMgr.getDocuments();
		
		// Iterates over all the documents
		for (Document d : docs) {
			String docText = preProc.process(d.getOriginalText());
			String[] terms = docText.split(" ");
			
			// Iterates over all the terms
			for (String t : terms) {
				buildingEachEntry(dict, d, t, 1, 1);
			}
			
			if(preProc instanceof PreProcCode) {
				List<String> camelCaseTokens = ((PreProcCode) preProc).getCamelCaseTokens(d.getOriginalText());
				// Iterates over all the camel case tokens
				for (String t : camelCaseTokens) {
					buildingEachEntry(dict, d, t, 1, 1);
				}		
			}
		}
		
		// Now that all the counts are in, it calculates the document vector weights
		for (TermEntry t : dict.getTermEntries()) {
			for (Posting p : t.getPostings()) {
				docId = p.getDocId();
				vectorLength = Math.pow(p.getFrequency(), 2);
				if (documentVectorLength.containsKey(docId)) {
					// The document has other terms
					vectorLength += documentVectorLength.get(docId);
				}
				documentVectorLength.put(docId, vectorLength);
			}
		}
		// Finally, we need to get the square root of all entries in the document vector length
		for (Document d : docMgr.getDocuments()) {
			docId = d.getId();
			vectorLength = Math.sqrt(documentVectorLength.get(docId));
			// Here we update the document vector length of the dictionary - not the internal structure any more
			dict.setDocumentVectorWeight(docId, vectorLength);
		}		
		
		// Start for calculating idf
		// Variables
		int N = docMgr.getNumOfDocuments(); // Total Number of Documents
		double idf;
		HashMap<String, Double> documentVectorLength1 = new HashMap<String, Double>();	
		// Iterates over all the terms
		for (TermEntry term : dict.getTermEntries()) {
			// Calculates the idf for each term - and stores this in the weight of the term - for weighing queries later
			if(term.getNumOfDocs() > 0) {
				idf = Math.log10(N/((double) term.getNumOfDocs()));
			} else {
				continue;
			}
			term.updateWeight(idf);
			
			// Iterates over all the postings
			for (Posting posting : term.getPostings()) {
				// Multiplies each term weight by the idf
				double newWeight = posting.getFrequency() * idf;
				posting.updateWeight(newWeight);
			
				// Updates the document vector length
				docId = posting.getDocId();
				vectorLength = Math.pow(newWeight, 2);
				if (documentVectorLength1.containsKey(docId)) {
					// The document has other terms
					vectorLength += documentVectorLength1.get(docId);
				}
				documentVectorLength1.put(docId, vectorLength);
			}
		}
		
		// Now, we need to get the square root of all entries in the document vector length
		for (Document d : docMgr.getDocuments()) {
			docId = d.getId();
			vectorLength = Math.sqrt(documentVectorLength1.get(docId));
			documentVectorLength1.put(docId, vectorLength);
			// Here we update the document vector length of the dictionary - not the internal structure anymore
			dict.setDocumentVectorWeight(docId,vectorLength);
		}	

		return dict;
	}
	

	private void buildingEachEntry(Dictionary dict, Document d, String t, int count, double weight) {
		TermEntry term;
		Posting posting;
		// Checks if that term has already a posting
		if(!dict.containsTermEntry(t)) {
			// New term
			term = dict.addTermEntry(t, 1, count, weight);
			posting = term.addPosting(d.getId(), count, weight);
			
		} else {
			// Existing term
			term = dict.getTermEntry(t);
			term.updateTotalFrequency(term.getTotalFrequency() + count);
			term.updateWeight(weight);
			
			// Checks if there is already a posting for this document
			if (!term.containsPosting(d.getId())) {
				// New posting
				term.updateNumOfDocs(term.getNumOfDocs() + 1);
				posting = term.addPosting(d.getId(), count, weight);
				
			} else {
				// Existing posting
				posting = term.getPosting(d.getId());
				posting.updateFrequency(posting.getFrequency() + count);
				posting.updateWeight(posting.getWeight() + weight);
			}
		}
	}
	
	public void setTextPreProcessor(PreProcessor preProc) {
		this.preProc = preProc;
	}


}

