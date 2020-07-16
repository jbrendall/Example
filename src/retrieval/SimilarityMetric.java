package retrieval;

/**
 * @author Carlos Castro
 * 
 * This Interface exposes the main functions of a similarity metric
 *
 */
interface SimilarityMetric {
	public double computeSimilarity(double dotProductOfQueryAndDocument, double queryVectorSize, double documentVectorSize);
}
