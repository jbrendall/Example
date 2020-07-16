package retrieval;

/**
 * @author Carlos Castro
 * 
 * Class that implements the Result Interface.
 *
 */
public final class ResultObj implements Result {
	// Variables
	String _docId;
	double _ranking;
    boolean _correct;

    public void setCorrect(boolean _correct) {
        this._correct = _correct;
    }

    public void setRanking(double _ranking) {
        this._ranking = _ranking;
    }
	
	public void setDocId(String docId) {
		this._docId = docId;
	}

	// Constructor - Package Private
	public ResultObj(String docId, double ranking, boolean correct) {
		// Sets the local variables
		_docId = docId;
		_ranking = ranking;
        _correct = correct;
	}
	
	public String getDocId() {return _docId;}
	public double getRanking() {return _ranking;}
	public boolean isCorrect() {return _correct; }
	// The comparable interface allows an array list of results to be ordered
	public int compareTo(Result e) {
		if (_ranking > e.getRanking()) {
			return -1;
		} else if (_ranking == e.getRanking()) {
			return 0;
		} else {
			return 1;
		}
	}

    public boolean equals(Result r){
        return this._docId.equals(r.getDocId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResultObj other = (ResultObj) obj;
        if ((this._docId == null) ? (other._docId != null) : !this._docId.equals(other._docId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return _docId.hashCode();
    }    
}
