package traceDynamo;

import java.io.IOException;
import java.util.HashMap;

import caching.Cache;
import links.Link;

public class TraceDynamoLC {
	private HashMap<String, String> source;
	private HashMap<String, String> target;
	private HashMap<String, String> links;
	private String directory;
	private HashMap<String, String> similarityResults;
	private HashMap<Link, Integer> votingResults;

	/*
	 * Constructor for LC component
	 * Currently just includes source and target artifacts, and save directory name
	 */
	public TraceDynamoLC(HashMap<String, String> source, HashMap<String, String> target, String directory) {
		this.source = source;
		this.target = target;
		this.directory = directory;
	}
	
	/*
	 * Current TraceDynamo includes option to perform more than one tracing method
	 * Will need to get results for every method
	 */
	public HashMap<Link, Double> getSimilarities(int stopwords, int cleanup, int stemmer, int code, int ngram,
			int vsm, int vsm_ngram, int ngram_max, 
			int lda, int lsi,
			int num_topics, int passes, int chunk_size, double alpha, double beta,
			int similarity) throws ClassNotFoundException, IOException {
		TraceDynamo td = new TraceDynamo(source, target, directory);
		td.run(stopwords, cleanup, stemmer, code, ngram, vsm, vsm_ngram, ngram_max, lda, lsi,
			num_topics, passes, chunk_size, alpha, beta, similarity);
		HashMap<Link, Double> results = getSimResults(td);
		return results;
	}
	
	/*
	 * Returns similarity results from every method
	 * Currently no label for method type, but would be easy to add if necessary
	 */
	private HashMap<Link, Double> getSimResults(TraceDynamo td) throws ClassNotFoundException, IOException{
		HashMap<Link, Double> simResults = new HashMap<Link, Double>();
		HashMap<String, String> maps = td.getSimilarityResults();
		if(maps != null) {
			if(maps.containsKey("VSM")) {
				simResults.putAll(Cache.loadMapLinks(maps.get("VSM")));
			}
			if(maps.containsKey("VSMnGram")) {
				simResults.putAll(Cache.loadMapLinks(maps.get("VSMnGram")));
			}
			if(maps.containsKey("LDA")) {
				simResults.putAll(Cache.loadMapLinks(maps.get("LDA")));
			}
			if(maps.containsKey("LSI")) {
				simResults.putAll(Cache.loadMapLinks(maps.get("LSI")));
			}
		}
		return simResults;
	}
}
