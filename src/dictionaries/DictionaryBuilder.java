package dictionaries;

import documents.DocumentManager;

/**
 * @author Carlos Castro
 * 
 * This Interface exposes the main functions of a Dictionary builder.
 * 
 * This is set up as a Builder design pattern, where the responsibility and details of building a dictionary 
 * are delegated to objects that implement this interface. 
 * 
 */
public interface DictionaryBuilder {
	public Dictionary build(String name, DocumentManager docMgr);
}
