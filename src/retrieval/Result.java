package retrieval;

/**
 * @author Carlos Castro
 * 
 * This Interface exposes the main functions of a result object
 *
 */
public interface Result extends Comparable<Result> {
	public String getDocId();
	public double getRanking();
    public boolean isCorrect();
    public void setCorrect(boolean _correct);
	public void setRanking(double ranking);
	public void setDocId(String docId);
}
