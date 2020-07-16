package documents;

import preprocessing.PreProcessor;

/**
 * @author Carlos Castro
 *
 * Factory class that returns the document manager 
 */
public class DocumentManagerStaticFactory {
	// A static class
	private DocumentManagerStaticFactory() {}
	
	public static DocumentManager getNewDocumentManager(PreProcessor preProcessor) {
		return new DocumentManagerObj(preProcessor);
	}
}
