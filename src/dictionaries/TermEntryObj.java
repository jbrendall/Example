package dictionaries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Carlos Castro
 * 
 * Class that implements the TermEntry Interface.
 *
 */
public class TermEntryObj implements TermEntry{
	// Variables
	String _term; 
	int _numOfDocs;
	int _totalFrequency;
	double _weight;
	// Data Structure that contains the collection of Postings
	HashMap<String, Posting> _postings;
	
	// Constructor - Package Private
	TermEntryObj(String term, int numOfDocs, int totalFrequency, double weight) {
		// Sets the local variables
		_term=term;
		_numOfDocs=numOfDocs;
		_totalFrequency = totalFrequency;
		_weight = weight;
		
		// Initializes the data structure for the postings
		_postings = new HashMap<String, Posting>();
	}
	
	// Getters
	public String getTerm() {return _term;}
	public int getNumOfDocs() {return _numOfDocs;}
	public int getTotalFrequency() {return _totalFrequency;}
	public double getWeight() {return _weight;}

	// Setters
	public void updateNumOfDocs(int numOfDocs){
		// Integrity checks
		if (numOfDocs<=0)
			throw new IllegalArgumentException("Number of documents has to be greater than 0.");
		_numOfDocs = numOfDocs;		
	}
	public void updateTotalFrequency(int totalFrequency){
		// Integrity checks
		if (totalFrequency<=0)
			throw new IllegalArgumentException("Total frequency has to be greater than 0.");
		_totalFrequency = totalFrequency;		
	}
	public void updateWeight(double weight){
		// TODO Integrity check of weight > 1 removed - possible in Signal Noise, but needs more investigation
		// Integrity checks 
		//if (weight<0)
		//	throw new IllegalArgumentException("Weight can't be negative");
		_weight = weight;		
	}

	// Methods that deal with the collection of postings
	public Posting addPosting(String docId, int frequency, double weight){
		// Integrity checks
		if (docId=="")
			throw new IllegalArgumentException("The docId can't be null");
		if (frequency<=0)
			throw new IllegalArgumentException("Frequency has to be greater than 0.");
		if (weight<0.0)
			throw new IllegalArgumentException("Weight should not be negative.");
		//if (!_documents.containsKey(id))
		//	throw new IllegalArgumentException("The document collection does not contain that id");

		// Creates a new posting and stores it.
		Posting p = new PostingObj(docId, frequency, weight);
		_postings.put(docId, p);
		
		return p;
	}
	
	public boolean containsPosting(String docId){
		// Integrity checks
		if (docId=="")
			throw new IllegalArgumentException("The docId can't be null");
		return _postings.containsKey(docId);
	}
	
	public Posting getPosting (String docId){
		// Integrity checks
		if (docId=="")
			throw new IllegalArgumentException("The docId can't be null");
		if (!_postings.containsKey(docId))
			throw new IllegalArgumentException("The posting collection doesn't contain that docId");

		return _postings.get(docId);		
	}
	
	public int getNumberOfPostings(){
		return _postings.size();
	}
	public Collection<Posting> getPostings(){
		// Returns a sorted and unmodifiable collection, so that no change can be made to it from outsite this object
		List<Posting> posts = new ArrayList<Posting>(_postings.values());
		Collections.sort(posts);
		return Collections.unmodifiableCollection(posts);
	}
	
	// The comparable interface allows a collection of TermEntries to be ordered
	public int compareTo(TermEntry e) {
		if (_term.compareTo(e.getTerm()) < 0) {
			return -1;
		} else if (_term.equals(e.getTerm())) {
			return 0;
		} else {
			return 1;
		}
	}		
}
