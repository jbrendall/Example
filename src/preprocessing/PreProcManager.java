package preprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class PreProcManager {
	
	private PreProcessor preProc = null;
	private final String stopWordsFile = "resources/stopwords.txt";
	
	private String preprocFile;	

	public PreProcManager(int stopwords, int cleanup, int stemmer, int code, int ngram) {		
		List<PreProcessor> preProcList = new ArrayList<PreProcessor>();
		if(stopwords == 1) preProcList.add(new PreProcStopWords(stopWordsFile));
		if(cleanup == 1) preProcList.add(new PreProcCleanUp());
		if(stemmer == 1) preProcList.add(new PreProcStemmer());
		//ngram preprocessing is the same as code, without camel case preservation
		if(code == 1) {
			PreProcessor p = new PreProcCode(stopWordsFile);
			if(ngram == 1) ((PreProcCode)p).setPreserveCamelCaseWord(false);
			preProcList.add(p);
		}
		
		for(PreProcessor p: preProcList) {
			if(p != null) {
				if(preProc == null) preProc = p;
				else preProc.setNextPreProcessor(p);
			}
		}
		
		preprocFile = Integer.toString(stopwords)+Integer.toString(cleanup)+Integer.toString(stemmer)+Integer.toString(code)+Integer.toString(ngram);
	}
	
	
	public PreProcessor getPreProcessor() {
		return preProc;
	}
	
	public String getFileName() {
		return preprocFile;
	}
}
