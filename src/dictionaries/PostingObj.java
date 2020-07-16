package dictionaries;

/**
 * @author Carlos Castro
 * 
 * Class that implements the Posting Interface.
 *
 */
final class PostingObj implements Posting{
	// Variables
	String _docId; 
	int _frequency;
	double _weight;

	// Constructor - Package Private
	PostingObj(String docId, int frequency, double weight) {
		// Sets the local variables
		_docId=docId;
		_frequency=frequency;
		_weight = weight;
	}
	
	// Getters
	public String getDocId() {return _docId;}
	public int getFrequency() {return _frequency;}
	public double getWeight() {return _weight;}
	
	// Setters
	public void updateFrequency(int frequency) {
		// Integrity checks
		if (frequency<=0)
			throw new IllegalArgumentException("Frequency has to be greater than 0.");
		_frequency = frequency;
	}
	public void updateWeight(double weight) {
		// TODO Integrity check of weight > 1 removed - possible in Signal Noise, but needs more investigation
		// Integrity checks
		//if (weight<0.0)
		//	throw new IllegalArgumentException("Weight should not be negative.");
		_weight = weight;
	}
	
	// The comparable interface allows a collection of Postings to be ordered
	public int compareTo(Posting e) {
		if (_docId.compareTo(e.getDocId()) < 0) {
			return -1;
		} else if (_docId.equals(e.getDocId())) {
			return 0;
		} else {
			return 1;
		}
	}		
}
