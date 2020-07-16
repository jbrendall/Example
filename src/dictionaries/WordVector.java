package dictionaries;

import java.util.HashMap;
import java.util.Map;

public class WordVector {
	String token;
	Map<Integer, Double> vector = new HashMap<Integer, Double>();
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
