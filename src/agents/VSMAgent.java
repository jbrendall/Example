package agents;

import java.util.List;

import dictionaries.Dictionary;
import documents.DocumentManager;
import documents.Query;
import retrieval.Result;
import retrieval.Searcher;
import retrieval.SearcherStaticFactory;

/**
 *
 * @author Marek
 */
public class VSMAgent implements Agent {

    public VSMAgent(DocumentManager docMgr, Dictionary dict, String similarity) {
        
        this.docMgr = docMgr;
        this.dict = dict;
        
        /*preProc1 = new PreProcCleanUp(); 
        preProc2 = new PreProcStopWords(stopWordsFile); 
        preProc3 = new PreProcStemmer();

        preProc1.setNextPreProcessor(preProc2.setNextPreProcessor(preProc3));*/

        //DictionaryBuilder builder = DictionaryStaticFactory.getDictionaryBuilder("TFIDF");
        
        searcher = SearcherStaticFactory.getSearcher(similarity);
        //dict = builder.build("dict", docMgr);
    }

//    public void run() {
//        //((SearcherBasic) searcher).calculateWeights(queryDoc, dict, answerSet);
//        results = searchersearch(queryDoc, dict, answerSet);
//    }
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
        queryDoc = doc;
//        String query = preProc1.process(doc.getOriginalText());
//        queryDoc.setText(query);
//        queryDoc.setText(doc.getOriginalText());
    }

    private Query queryDoc;
    
    private Searcher searcher;
    private DocumentManager docMgr;
    private Dictionary dict;
    private List<Result> results;
}
