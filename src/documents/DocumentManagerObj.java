package documents;

import java.util.List;

import preprocessing.PreProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Carlos Castro
 * 
 * Class that implements the DataManager Interface.
 *
 */
final class DocumentManagerObj implements DocumentManager{
	// Variables
	String _id; 
	String _name;
	// Data Structure that contains the collection of documents
	HashMap<String, Document> _documents;
	// PreProcessor that modifies the text of the documents (cleaning, removing stop words, stemming, etc)
	PreProcessor _preProcessor;

	// Constructor - Package Private
	DocumentManagerObj(PreProcessor preProcessor) {
		// Integrity checks
		if (preProcessor==null)
			throw new IllegalArgumentException("The preProcessor can't be null");
		
		// Sets the pre-processor object
		_preProcessor = preProcessor;
		
		// Initializes the data structures
		_documents = new HashMap<String, Document>();
	}

	public void addDocument(String id, String text){
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The id can't be null");
		if (text=="")
			throw new IllegalArgumentException("The text can't be null");
		if (_documents.containsKey(id))
			throw new IllegalArgumentException("The document collection already contains id " + id);
		
		// PreProcesses the text
		String processedText = _preProcessor.process(text);
		
		if(id.equals("temp")) 
			System.out.println("FOUND IT!");
		// Creates a new document and stores it
		if(processedText != null && !processedText.isEmpty()) {
			Document d = new DocumentObj(id, text, processedText);
			_documents.put(id, d);
		}
	}
	
	public Document getDocument(String id) {
		// Integrity checks
		if (id=="")
			throw new IllegalArgumentException("The id can't be null");
		if (!_documents.containsKey(id))
			throw new IllegalArgumentException("The document collection doesn't contain that id");

		return _documents.get(id);
	}
	
	public int getNumOfDocuments(){
		return _documents.size();
	}
	
	public Collection<Document> getDocuments(){
		// Returns a sorted and unmodifiable collection, so that no change can be made to it from outsite this object
		List<Document> docs = new ArrayList<Document>(_documents.values());
		Collections.sort(docs);
		return Collections.unmodifiableCollection(docs);
		//return Collections.unmodifiableCollection(_documents.values());
	}
}
