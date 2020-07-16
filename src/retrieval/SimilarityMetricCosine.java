package retrieval;

/**
 * @author Carlos Castro
 * 
 * Class that implements the SimilarityMetric Interface.
 * Uses cosine metric
 *
 */
final class SimilarityMetricCosine implements SimilarityMetric{
	public double computeSimilarity(double dotProductOfQueryAndDocument, double queryVectorSize, double documentVectorSize){
		return dotProductOfQueryAndDocument / (queryVectorSize * documentVectorSize);
	}
}
