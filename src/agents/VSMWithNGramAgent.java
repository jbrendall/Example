package agents;

import java.util.HashSet;
import java.util.List;

import dictionaries.DictBuilderNGram;
import dictionaries.Dictionary;
import dictionaries.DictionaryStaticFactory;
import documents.DocumentManager;
import documents.OueryAugmentedWithNGram;
import documents.Query;
import preprocessing.PreProcCode;
import preprocessing.PreProcessor;
import retrieval.Result;
import retrieval.Searcher;
import retrieval.SearcherStaticFactory;

/**
*
* @author Jin Guo
* 
* VSM enhanced with NGram tokens. The NGrams of size between "ngram_min" and "ngram_max" are added to the Vector space 
* for calculating the similarity between query and document. 
*/

public class VSMWithNGramAgent implements Agent {
	public VSMWithNGramAgent(DocumentManager docMgr, Dictionary dict, String similarity, PreProcessor preProc) {
        this.docMgr = docMgr;
        this.dict = dict;
        //this.ngram_max = 4;
        //this.ngram_min = 2;
        
        /*preProc = new PreProcCode(stopWordsFile); 
        ((PreProcCode)preProc).setPreserveCamelCaseWord(false);*/

        /*DictBuilderNGram builder = (DictBuilderNGram) DictionaryStaticFactory.getDictionaryBuilder("NGram");
        builder.setTextPreProcessor(preProc);
        ((DictBuilderNGram)builder).setNgram_max(ngram_max);
        ((DictBuilderNGram)builder).setNgram_min(ngram_min);

        dict = builder.build("dict", docMgr);*/
        
        searcher = SearcherStaticFactory.getSearcher(similarity);
        
        // Set up the preProcesser for phrase matching (leave stop words from erasing)
        OueryAugmentedWithNGram.setTextPreProcessor(preProc);
//        OueryAugmentedWithPhrase.setAhoCorasick(builder.getTreeForStringMatch());
    }

    public void run(){
        queryDoc.calculateTermWeights(dict);
        results = searcher.search(queryDoc, dict, docMgr);
    }

    public DocumentManager getDocumentManager() {
        return docMgr;
    }

    public Dictionary getDictionary() {
        return dict;
    }

    public List<Result> getResults() {
        return results;
    }
   
    public void setQuery(Query doc){
    	// Using the augmented query class to achieve phrase matching
        queryDoc = new OueryAugmentedWithNGram(doc.getId(), doc.getOriginalText(), doc.getText());
//        String query = preProc.process(doc.getOriginalText());
//        queryDoc.setText(query); 
    }

    private OueryAugmentedWithNGram queryDoc;
    
    private Searcher searcher;
    private DocumentManager docMgr;
    private Dictionary dict;
    private List<Result> results;
    private int ngram_min = 2;
    private int ngram_max = 3;

}
