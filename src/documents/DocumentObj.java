package documents;

/**
 * @author Carlos Castro
 * 
 * Class that implements the Document Interface.
 *
 */
public final class DocumentObj implements Document{
	// Variables
	String _id; 
	String _originalText;
	String _processedText;

	// Constructor - Package Private
	public DocumentObj(String id, String text, String processedText) {
		// Sets the local variables
		_id=id;
		_originalText=text;
		_processedText = processedText;
	}

	// Getters
	public String getId() {return _id;}
	public String getOriginalText() {return _originalText;}
	public String getText() {return _processedText;}
        public void setText(String preprocessedText){
            _processedText = preprocessedText;
        }

	// The comparable interface allows a collection of Postings to be ordered
	public int compareTo(Document e) {
		if (_id.compareTo(e.getId()) < 0) {
			return -1;
		} else if (_id.equals(e.getId())) {
			return 0;
		} else {
			return 1;
		}
	}		
}
