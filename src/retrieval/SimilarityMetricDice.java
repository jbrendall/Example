package retrieval;

/**
 * @author Carlos Castro
 * 
 * Class that implements the SimilarityMetric Interface.
 * Uses the Dice metric
 *
 */
final class SimilarityMetricDice implements SimilarityMetric{
	public double computeSimilarity(double dotProductOfQueryAndDocument, double queryVectorSize, double documentVectorSize){
		return (2 * dotProductOfQueryAndDocument) / ( Math.pow(queryVectorSize,2) + Math.pow(documentVectorSize, 2) );
	}
}
