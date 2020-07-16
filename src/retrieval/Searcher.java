package retrieval;

import java.util.List;

import dictionaries.*;
import documents.DocumentManager;
import documents.Query;

import java.util.HashSet;

/**
 * @author Carlos Castro
 * 
 * This Interface exposes the main functions of a searcher object
 *
 */
public interface Searcher {
	public List<Result> search(Query queryDoc, Dictionary dict, DocumentManager docMgr);
}
