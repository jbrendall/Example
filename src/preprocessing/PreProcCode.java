package preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jin Guo
 * 
 * This Pre-Processor is for textual input with code elements. It first remove all the non-alphanumeric characters, 
 * and then split the CamelCase tokens. The stop words are then removed and the remaining words are stemmed using 
 * the Porter Stemmer. The original camel case tokens can be reserved by setting the boolean flag. 
 */

public class PreProcCode implements PreProcessor{
	// Next in the chain of command
	PreProcessor _next;
	boolean preserveCamelCaseWord;
	boolean preserveNumber;
	// Array list that holds the stop words
	ArrayList<String> stopWords;

	
	public PreProcCode(String stopWordsFile) {
		preserveCamelCaseWord = true;
		try{
			// The args should specify the location of the stop words file			
			// Array with the stop words
			stopWords = new ArrayList<String>();
			String stopWord;
			
			// Creates a buffered reader
			BufferedReader in;
			in = new BufferedReader(new FileReader(stopWordsFile));

			// Reads the file
	        while ((stopWord = in.readLine()) != null) {
	        	// Adds the stop word to the array
	        	stopWords.add(stopWord);
	        }
	        in.close();
			
		} catch (Exception e) {
			System.out.println("The following error ocurred:\n" + e.getMessage());
			System.out.println("Details:\n");
			e.printStackTrace();
		}
	}

	public boolean isPreserveCamelCaseWord() {
		return preserveCamelCaseWord;
	}
	public void setPreserveCamelCaseWord(boolean preserveCamelCaseWord) {
		this.preserveCamelCaseWord = preserveCamelCaseWord;
	}

	@Override
	public PreProcessor setNextPreProcessor(PreProcessor next){
		// Integrity checks
		if (next==null)
			throw new IllegalArgumentException("The next preProcessor can't be null");

		// Sets the next chain link
		_next = next;
		
		return this;
	}
		
	static String splitCamelCase(String s) {
		   return s.replaceAll(
		      String.format("%s|%s|%s",
		         "(?<=[A-Z])(?=[A-Z][a-z])",
		         "(?<=[^A-Z])(?=[A-Z])",
		         "(?<=[A-Za-z])(?=[^A-Za-z])"
		      ),
		      " "
		   );
	}


	@Override
	public String process(String text) {
		Stemmer porter = new Stemmer();
		String initialText = text;
		String result = "";
		StringBuilder builder = new StringBuilder();
		String stemmedWord;

		
		// Reduces the text to only characters - using Regular Expressions
		result = initialText.replaceAll("[^A-Za-z0-9]", " ");
		
		// Splits the text into tokens and iterates over them
		String[] tokens = result.split(" ");
		for (String word : tokens) {
			if(word.length()==0)
				continue;
			String splittedWord = splitCamelCase(word);
			if(splittedWord.equals(word)) { // No camel case found
				if (stopWords.contains(word)) {
					continue; 
				}
				// Calls the porter class to do the stemming for the current word
				porter.add(word.toCharArray(), word.length());
				porter.stem();
				stemmedWord = porter.toString();
				builder.append(stemmedWord.toLowerCase() + " ");
			} else { // Found camel case
				String[] actualTokens = splittedWord.split(" ");
				for (String actualWord : actualTokens) {
					if (stopWords.contains(actualWord)) {
						continue; 
					}
					porter.add(actualWord.toCharArray(), actualWord.length());
					porter.stem();
					stemmedWord = porter.toString();
					if(stopWords.contains(stemmedWord)) {
						continue;
					}
					builder.append(stemmedWord.toLowerCase() + " ");
				}
				// Add the original Camel Cased word as well if the flag is set as true.
				if(preserveCamelCaseWord)
					builder.append(word.toLowerCase() + " "); 
			}
			
		}
		result = builder.toString();
		
		if (_next != null) {
			result = _next.process(result);
		}
		
		return result;
	}
	
	public List<String> getCamelCaseTokens(String text) {
		List<String> camelCaseTokens = new ArrayList<String>();
		String initialText = text;		
		// Reduces the text to only characters - using Regular Expressions
		initialText = initialText.replaceAll("[^A-Za-z0-9]", " ");
		
		// Splits the text into tokens and iterates over them
		String[] tokens = initialText.split(" ");
		for (String word : tokens) {
			if(word.length()==0)
				continue;
			String splittedWord = splitCamelCase(word);
			if(!splittedWord.equals(word)) {// Found camel case
				camelCaseTokens.add(word.toLowerCase());
			}
			
		}
		return camelCaseTokens;
	}
	
	
	private static void testCamelCase(){
		String[] tests = {
	        "lowercase",        // [lowercase]
	        "lowerUpperCase",   // [lower Upper Case]
	        "Class",            // [Class]
	        "MyClass",          // [My Class]
	        "HTML",             // [HTML]
	        "PDFLoader",        // [PDF Loader]
	        "AString",          // [A String]
	        "SimpleXMLParser",  // [Simple XML Parser]
	        "GL11Version",      // [GL 11 Version]
	        "99Bottles",        // [99 Bottles]
	        "May5",             // [May 5]
	        "BFG9000",          // [BFG 9000]
	    };
	    for (String test : tests) {
	        System.out.println("[" + splitCamelCase(test) + "]");
	    }
	}
	
	public static void main(String args[]) {
		testCamelCase();
		String stopWordFile = "src/resources/stopwords.txt";
	    PreProcCode procCode = new PreProcCode(stopWordFile);
	    String testString = "DERBY-2008: NPE with 2-arg SUBSTR call in GROUP BY clause\n\nThis patch was contributed by Yip Ng (yipng168@gmail.com)\n\nFor SUBSTR function, there can be 2 or 3 arguments, and in the\ncase of 2-args, the rightOperand of the TernaryOperatorNode will\nbe null. In its isEquivalent() method, it did not take care of\nthis case; thus, the NPE.\n\n\ngit-svn-id: https://svn.apache.org/repos/asf/db/derby/code/trunk@468696 13f79535-47bb-0310-9956-ffa450edef68\n";
	    System.out.println(procCode.process(testString));
	}

}
