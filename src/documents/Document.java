package documents;

/**
 * @author Carlos Castro
 * 
 * This interface exposes the functionality of a single documents
 *
 */
public interface Document extends Comparable<Document>{
	public String getId();
	public String getOriginalText();
	public String getText();
        public void setText(String preprocessedText);
}
