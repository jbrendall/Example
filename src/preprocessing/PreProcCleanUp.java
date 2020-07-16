package preprocessing;

/**
 * @author Jin Guo
 * 
 * This Pre-Processor cleans up the text, eliminating all 'non alphanumeric' characters
 *
 */
public final class PreProcCleanUp implements PreProcessor{
	// Next in the chain of command
	PreProcessor _next;
	
	// Constructor - Package Private
	public PreProcCleanUp() {}
	
	public PreProcessor setNextPreProcessor(PreProcessor next){
		// Integrity checks
		if (next==null)
			throw new IllegalArgumentException("The next preProcessor can't be null");

		// Sets the next chain link
		_next = next;
		
		return this;
	}
	
	public String process(String text) {
		String initialText = text;
		String result = "";
		
		// Reduces the text to only characters - using Regular Expressions
		result = initialText.replaceAll("[^A-Za-z0-9]", " ");
		// Eliminates any duplicate whitespace - using Regular Expressions
		result = result.replaceAll("\\s+", " ");
		// Lowers the case
		result = result.toLowerCase();
		
		if (_next != null) {
			result = _next.process(result);
		}
		
		return result;
	}
}
