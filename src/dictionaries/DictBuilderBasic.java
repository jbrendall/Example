package dictionaries;

import java.util.Collection;
import java.util.HashMap;

import documents.Document;
import documents.DocumentManager;

/**
 * @author Carlos Castro
 * 
 * Class that implements the Dictionary Builder Interface.
 * This particular builder will create a dictionary that contains the basic accumulated term frequencies.
 * The main idea is for more complex builders to inherit from this one.
 * Note, the accumulated term frequencies are not normalized; however, the document vector length has the values needed to normalize them
 *
 */
public class DictBuilderBasic implements DictionaryBuilder{

	public Dictionary build(String name, DocumentManager docMgr){
		// Variables
		TermEntry term;
		Posting posting;
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
			docId = d.getId();
			
			String[] terms = d.getText().split(" ");
			
			// Iterates over all the terms
			for (String t : terms) {
				if(t.isEmpty())
					continue;
				
				// Checks if that term has already a posting
				if(!dict.containsTermEntry(t)) {
					// New term
					term = dict.addTermEntry(t, 1, 1, 1.0);
					posting = term.addPosting(d.getId(), 1, 1.0);
					
				} else {
					// Existing term
					term = dict.getTermEntry(t);
					term.updateTotalFrequency(term.getTotalFrequency() + 1);
					//term.updateWeight(term.getWeight() + 1.0);
					term.updateWeight(1.0);
					
					// Checks if there is already a posting for this document
					if (!term.containsPosting(d.getId())) {
						// New posting
						term.updateNumOfDocs(term.getNumOfDocs() + 1);
						posting = term.addPosting(d.getId(), 1, 1.0);
						
					} else {
						// Existing posting
						posting = term.getPosting(d.getId());
						posting.updateFrequency(posting.getFrequency() + 1);
						posting.updateWeight(posting.getWeight() + 1.0);
					}
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
			if(documentVectorLength.containsKey(docId)) {
				vectorLength = Math.sqrt(documentVectorLength.get(docId));
				// Here we update the document vector length of the dictionary - not the internal structure any more
				dict.setDocumentVectorWeight(docId, vectorLength);
			} else {
				System.out.println("Problem with content in doc: " + docId);
			}
		}		
		
		return dict;
	}
}
