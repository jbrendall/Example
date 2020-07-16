package dictionaries;

import java.util.HashMap;
import java.util.Map;

public class DocVector {
	String docId;
	Map<Integer, Double> vector = new HashMap<Integer, Double>();
	double norm;
	
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public double getNorm() {
		return norm;
	}
	
	public void setNorm(double norm) {
		this.norm = norm;
	}
	public Map<Integer, Double> getVector() {
		return vector;
	}
	public void setVector(Map<Integer, Double> vector) {
		this.vector = vector;
	}
	public int getDimension() {
		return vector.size();
	}
	
}
