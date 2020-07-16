package dictionaries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Carlos Castro
 * 
 * Class that implements the Dictionary Interface.
 *
 */
public class DictionaryObj implements Dictionary{
	// Variables
	String _name; 
	// Data Structure that contains the collection of Terms
	HashMap<String, TermEntry> _terms;
	// Stores the vector lenght of each document - later used for normalization purposes
	// The vector length is the square root of the sum of the squares of all the term weights.	
	HashMap<String, Double> _documentVectorLength;	
	
	// Constructor - Package Private
	DictionaryObj(String name) {
		// Sets the local variables
		_name=name;
		
		// Initializes the data structure for the postings
		_terms = new HashMap<String, TermEntry>();
		// And the data structure for the document vector length
		_documentVectorLength = new HashMap<String, Double>();
	}
		
	public String getName() {return _name;}
	
	public TermEntry addTermEntry(String term, int numOfDocs, int totalFrequency, double weight) {
		// Integrity checks
		if (term=="")
			throw new IllegalArgumentException("The term can't be null");
		// Temporarily changed by Jin. For adding all ontology entries.
		if (numOfDocs<0) //if (numOfDocs<=0)
			throw new IllegalArgumentException("NumOfDocs has to be greater than 0.");
		if (totalFrequency<0) // if (numOfDocs<=0)
			throw new IllegalArgumentException("TotalFrequency has to be greater than 0.");
		if (weight<0)
			throw new IllegalArgumentException("Weight can't be negative");
		if (_terms.containsKey(term))
			throw new IllegalArgumentException("The dictionary already contains that term");

		// Creates a new term entry and stores it.
		TermEntry t = new TermEntryObj(term, numOfDocs, totalFrequency, weight);
		_terms.put(term, t);
		
		return t;		
	}
	
	public boolean containsTermEntry(String term){
		// Integrity checks
		if (term=="")
			throw new IllegalArgumentException("The term can't be null");
		return _terms.containsKey(term);
	}
	
	public TermEntry getTermEntry (String term){
		// Integrity checks
		if (term=="")
			throw new IllegalArgumentException("The term can't be null");
		if (!_terms.containsKey(term))
			throw new IllegalArgumentException("The dictionary does not contain that term");
		return _terms.get(term);
	}
	
	public int getNumberOfTermEntries() {
		return _terms.size();
	}
	
	public Collection<TermEntry> getTermEntries() {
		// Returns a sorted and unmodifiable collection, so that no change can be made to it from outsite this object
		List<TermEntry> terms = new ArrayList<TermEntry>(_terms.values());
		Collections.sort(terms);
		return Collections.unmodifiableCollection(terms);
	}
	
	public double getDocumentVectorWeight(String docId){
		// Integrity checks
		if (docId=="")
			throw new IllegalArgumentException("The docId can't be null");
		if (!_documentVectorLength.containsKey(docId))
			throw new IllegalArgumentException("There is no length associated with this document");
		return _documentVectorLength.get(docId);
	}
	
	public void setDocumentVectorWeight (String docId, double weight){
		// Integrity checks
		if (docId=="")
			throw new IllegalArgumentException("The docId can't be null");
		if (weight<0)
			throw new IllegalArgumentException("Weight can't be negative");
		_documentVectorLength.put(docId, weight);
	}
	
	public boolean containsDocumentVectorWeight(String docId){
		// Integrity checks
		if (docId=="")
			throw new IllegalArgumentException("The docId can't be null");
		return _documentVectorLength.containsKey(docId);
	}

}
