package dictionaries;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Carlos Castro
 * 
 * This Interface exposes the main functions of a term entry object
 *
 */
public interface TermEntry extends Comparable<TermEntry>, Serializable{
	public String getTerm();
	public int getNumOfDocs();
	public int getTotalFrequency();
	public double getWeight();
	
	public void updateNumOfDocs(int numOfDocs);
	public void updateTotalFrequency(int totalFrequency);
	public void updateWeight(double weight);
	
	public Posting addPosting(String docId, int frequency, double weight);
	public boolean containsPosting(String docId);
	public Posting getPosting (String docId);
	public int getNumberOfPostings();
	public Collection<Posting> getPostings();
}
