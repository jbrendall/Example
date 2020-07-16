package dictionaries;

import java.io.Serializable;

/**
 * @author Carlos Castro
 * 
 * This Interface exposes the main functions of a posting object
 *
 */
public interface Posting extends Comparable<Posting>, Serializable{
	public String getDocId();
	public int getFrequency();
	public double getWeight();
	public void updateFrequency(int frequency);
	public void updateWeight(double weight);
}
