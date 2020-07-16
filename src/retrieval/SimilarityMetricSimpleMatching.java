package retrieval;

/**
 * @author Carlos Castro
 * 
 * Class that implements the SimilarityMetric Interface.
 * Uses simple matching - ie dot product of the query vector times the document vector
 *
 */
final class SimilarityMetricSimpleMatching implements SimilarityMetric{
	public double computeSimilarity(double dotProductOfQueryAndDocument, double queryVectorSize, double documentVectorSize){
		return dotProductOfQueryAndDocument;
	}
}
