package traceDynamo;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import argumentChecker.ArgsChecker;
import chromosome.Chromosome;
import links.Link;
import preprocessing.PreProcManager;
import traceModels.TracingManager;
import voter.VoterManager;

public class TraceDynamo {
	private HashMap<String, String> source;
	private HashMap<String, String> target;
	private HashMap<String, String> links;
	private String directory;
	private HashMap<String, String> similarityResults;
	private HashMap<Link, Integer> votingResults;
	
	private String chromosome;
	
	static {System.setProperty("logback.configurationFile", "resources/logback-full.xml");}

	private static Logger logger = null;
	public static double runtime;
	
	public TraceDynamo(HashMap<String, String> source, HashMap<String, String> target, String directory) {
		this.source = source;
		this.target = target;
		this.directory = directory;
		
		System.setProperty("USER_HOME", directory);

		logger = LoggerFactory.getLogger(TraceDynamo.class);
		
		this.runtime = (double) 0;
	}
	
	public void run(int stopwords, int cleanup, int stemmer, int code, int ngram,
			int vsm, int vsm_ngram, int ngram_max, 
			int lda, int lsi,
			int num_topics, int passes, int chunk_size, double alpha, double beta,
			int similarity) throws ClassNotFoundException, IOException {
		logger.info("Starting TraceDynamo...");
		
		Chromosome c = new Chromosome(stopwords, cleanup, stemmer, code, ngram,
				vsm, vsm_ngram, ngram_max,
				lda, lsi,
				num_topics, passes, chunk_size, alpha, beta,
				similarity);
		this.run(c);
	}
	
	public void run(Chromosome c) throws IOException, ClassNotFoundException {
		this.chromosome = 
				Integer.toString(c.stopwords)+"_"+Integer.toString(c.cleanup)+"_"+Integer.toString(c.stemmer)+"_"+Integer.toString(c.code)+"_"+Integer.toString(c.ngram)+"_"
						+Integer.toString(c.vsm)+"_"+Integer.toString(c.vsm_ngram)+"_"+Integer.toString(c.ngram_max)+"_" 
						+Integer.toString(c.lda)+"_"+Integer.toString(c.lsi)+"_"
						+Integer.toString(c.num_topics)+"_"+Integer.toString(c.passes)+"_"+Integer.toString(c.chunk_size)+"_"+Double.toString(c.alpha)+"_"+Double.toString(c.beta)+"_"
						+Integer.toString(c.similarity);
		
		logger.info("Chromosome is {}", chromosome);
		
		if(c.isValid()) {		
			PreProcManager pM = new PreProcManager(c.stopwords, c.cleanup, c.stemmer, c.code, c.ngram);
			
			String filename = pM.getFileName();
			TracingManager tM = new TracingManager(c.vsm, c.vsm_ngram, c.ngram_max, 
					c.lda, c.lsi, c.num_topics, c.passes, c.chunk_size, c.alpha, c.beta,
					c.similarity, source, target, pM.getPreProcessor(),
					directory, filename);
			
			similarityResults = tM.getSimilarityResults();
		}
		else {
			logger.info("Not able to execute...aborting");
		}
		
		logger.info("TraceDynamo analysis complete");
	}
	
	public void performVoting(int voter) {
		ArgsChecker.checkForRange("voter", voter, 0, 1, true);
		
		HashMap<String, Double> thresholds = new HashMap<String, Double>();
		thresholds.put("VSM", 0.5);
		thresholds.put("VSMnGram", 0.2);
		thresholds.put("LDA", 0.7);
		thresholds.put("LSI", 0.8);
		
		VoterManager vM = new VoterManager(voter, similarityResults, thresholds, directory+chromosome);
		votingResults = vM.getResults();
	}
	
	public HashMap<String, String> getSimilarityResults() {
		return similarityResults;
	}
	
	public HashMap<Link, Integer> getVotingResults() {
		return votingResults;
	}
}
