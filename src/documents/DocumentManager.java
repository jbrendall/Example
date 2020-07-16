package documents;

import java.util.Collection;

/**
 * @author Carlos Castro
 * 
 * This Interface exposes the main functions to access the document objects in the IR System
 *
 */
public interface DocumentManager {
	
	// Methods related to the collection of documents
	public void addDocument(String id, String text);
	public Document getDocument(String id);	
	public int getNumOfDocuments();
	public Collection<Document> getDocuments();

	
}
