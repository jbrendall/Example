package retrieval;

/**
 * @author Carlos Castro
 *
 * Factory class that creates the searchers
 */
public class SearcherStaticFactory {
	// A Static class
	private SearcherStaticFactory() {}
	
	public static Searcher getSearcher(String searcherName) {
		if (searcherName.equalsIgnoreCase("SimpleMatching")) {
			return new SearcherBasic(new SimilarityMetricSimpleMatching()) ;
		} else if (searcherName.equalsIgnoreCase("Cosine")) {
			return new SearcherBasic(new SimilarityMetricCosine()) ;
		} else if (searcherName.equalsIgnoreCase("Dice")) {
			return new SearcherBasic(new SimilarityMetricDice()) ;
		} else if (searcherName.equalsIgnoreCase("Jaccard")) {
			return new SearcherBasic(new SimilarityMetricJaccard()) ;
		} else {
			throw new IllegalArgumentException("The specified builder object can not be found.  Please check the argument name.");
		}				
	}
	

}
