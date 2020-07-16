package retrieval;

/**
 * @author Carlos Castro
 * 
 * Class that implements the SimilarityMetric Interface.
 * Uses the Jaccard metric
 *
 */
final class SimilarityMetricJaccard implements SimilarityMetric{
	public double computeSimilarity(double dotProductOfQueryAndDocument, double queryVectorSize, double documentVectorSize){
		return dotProductOfQueryAndDocument / ( Math.pow(queryVectorSize,2) + Math.pow(documentVectorSize, 2) - dotProductOfQueryAndDocument);
	}
}
