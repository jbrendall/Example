package argumentChecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ArgsChecker {
	private final static Logger logger = LoggerFactory.getLogger(ArgsChecker.class);
	
	public static boolean checkForRange(String name, int number, int low, int high, boolean valid) {
		if(valid == true) {
			if ((number < low) || (number > high)) {
				valid = false;
				String error = name + ": " + number + " not in range " + low + ".." + high;
				logger.info(error);
			}
		}
		return valid;
	}
	
	public static boolean checkForValue(String name, int input, int val1, int val2, boolean valid) {
		if(valid == true) {
			if((input != val1) && (input != val2)) {
				valid = false;
				String error = name + ": " + input + " not either " + val1 + ".." + val2;
				logger.info(error);
			}
		}
		return valid;
	}
	
	public static boolean checkForAbove(String name, int number, int low, boolean valid) {
		if(valid == true) {
			if (number <= low) {
				valid = false;
				String error = name + ": " + number + " less than " + low;
				logger.info(error);
			}
		}
		return valid;
	}
	
	public static boolean checkForAbove(String name, double number, double low, boolean valid) {
		if(valid == true) {	
			if (number < low) {
				valid = false;
				String error = name + ": " + number + " less than " + low;
				logger.info(error);
			}
		}
		return valid;
	}
}
