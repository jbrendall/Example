package chromosome;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import argumentChecker.ArgsChecker;

public class Chromosome {
	public int stopwords; public int cleanup; public int stemmer; public int code; public int ngram;
	public int vsm; public int vsm_ngram; public int ngram_max;
	public int lda; public int lsi;
	public int num_topics; public int passes; public int chunk_size; public double alpha; public double beta;
	public int similarity;
	
	private boolean valid = true;
	
	private final static Logger logger = LoggerFactory.getLogger(Chromosome.class);
	
	public Chromosome(int stopwords, int cleanup, int stemmer, int code, int ngram,
			int vsm, int vsm_ngram, int ngram_max, 
			int lda, int lsi,
			int num_topics, int passes, int chunk_size, double alpha, double beta,
			int similarity) {
		
		//check preprocessor inpouts are correct
		valid = ArgsChecker.checkForValue("stopwords", stopwords, 0, 1, valid);
		valid = ArgsChecker.checkForValue("cleanup", cleanup, 0, 1, valid);
		valid = ArgsChecker.checkForValue("stemmer", stemmer, 0, 1, valid);
		valid = ArgsChecker.checkForValue("code", code, 0, 1, valid);
		valid = ArgsChecker.checkForValue("ngram", ngram, 0, 1, valid);
		
		//check trace methods inputs are correct
		valid = ArgsChecker.checkForValue("vsm", vsm, 0, 1, valid);
		valid = ArgsChecker.checkForValue("vsm_ngram", vsm_ngram, 0, 1, valid);
		if(vsm_ngram == 1) {
			if(code != 1) {
				valid = false;
				logger.info("code set to " + code +", needs to be set to 1");
			}
			if(ngram != 1) {
				valid = false;
				logger.info("ngram set to " + ngram +", needs to be set to 1");
			}
			valid = ArgsChecker.checkForRange("ngram_max", ngram_max, 2, 5, valid);
		}
		valid = ArgsChecker.checkForValue("lda", lda, 0, 1, valid);
		if(lda == 1) {
			valid = ArgsChecker.checkForAbove("num_topics", num_topics, 0, valid);
			valid = ArgsChecker.checkForAbove("passes", passes, 0, valid);
			valid = ArgsChecker.checkForAbove("chunk_size", chunk_size, 10, valid);
			valid = ArgsChecker.checkForAbove("alpha", alpha, 0.001, valid);
			valid = ArgsChecker.checkForAbove("beta", beta, 0.001, valid);
		}
		valid = ArgsChecker.checkForValue("lsi", lsi, 0, 1, valid);
		/*if(lsi == 1) {
			ArgsChecker.checkForAbove(num_topics, 0);
			ArgsChecker.checkForAbove(chunk_size, 10);
		}*/
		valid = ArgsChecker.checkForRange("similarity", similarity, 0, 3, valid);
		
		if((vsm+vsm_ngram+lda+lsi) == 0) {
			valid = false;
			logger.info("vsm:"+vsm+", vsm_ngram:"+vsm_ngram+", lda:"+lda+", lsi:"+lsi);
		}
		
		this.stopwords = stopwords; this.cleanup = cleanup; this.stemmer = stemmer; this.code = code; this.ngram = ngram;
		this.vsm = vsm; this.vsm_ngram = vsm_ngram; this.ngram_max = ngram_max;
		this.lda = lda; this.lsi = lsi;
		this.num_topics = num_topics; this.passes = passes; this.chunk_size = chunk_size; this.alpha = alpha; this.beta = beta;
		this.similarity = similarity;
	}
	
	public boolean isValid() {
		return valid;
	}
}
