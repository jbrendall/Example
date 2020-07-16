package dictionaries;

public class DictionaryStaticFactory {
	// A static class
	private DictionaryStaticFactory() {}

	
	public static DictionaryBuilder getDictionaryBuilder(String builderName){
		if (builderName.equalsIgnoreCase("Basic")) {
			return new DictBuilderBasic() ;
		} else if (builderName.equalsIgnoreCase("TFIDF")) {
			return new DictBuilderTFIDF();
		} else if (builderName.equalsIgnoreCase("NGram")) {
			return new DictBuilderNGram();
		} else if (builderName.equalsIgnoreCase("CamelCaseWeighting")) {
			return new DictBuilderCamelCaseWeighting();
		} else {
			throw new IllegalArgumentException("The specified builder object can not be found.  Please check the argument name.");
		}		
	}

}
 
