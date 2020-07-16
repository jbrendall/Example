package dictionaries;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Carlos Castro
 * 
 * This Interface exposes the main functions of a dictionary object
 *
 */
public interface Dictionary extends Serializable{
	public String getName();
	public TermEntry addTermEntry(String term, int numOfDocs, int totalFrequency, double weight);
	public boolean containsTermEntry(String term);
	public TermEntry getTermEntry (String term);
	public int getNumberOfTermEntries();
	public Collection<TermEntry> getTermEntries();
	public double getDocumentVectorWeight(String docId);
	public void setDocumentVectorWeight (String docId, double weight);
	public boolean containsDocumentVectorWeight(String docId);
}
