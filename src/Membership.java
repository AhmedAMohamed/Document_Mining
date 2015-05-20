
public class Membership {

	
	/**
	 * this function is to convert the term frequency to a fuzzy variable 
	 * the returned value is the term importance to this variable
	 * @param tf term frequency
	 * @param min minimum value of a word
	 * @param average calculated in previous state
	 * @param max maximum frequency of a word
	 * @return a fuzzy value of the low fuzzy variable this value is bounded from 0 to 2
	 */
	public static double low(int tf, int min, double average, int max) {
		// given equations
		int a = 0;
		int b = min;
		double c = (average + min) / 2;
		double d = average;
		
		if(tf == 0) {
			return 0;
		}
		
		if(tf >= a && tf <= b) {
			return (1 + ((tf - a)/(b - a)));
		}
		
		if(tf > b && tf < c) {
			return 2;
		}
		
		if(tf >= c && tf <= d) {
			return (1 + ((tf - d)/(c - d)));
		}
		
		if(tf > d) {
			return 1;
		}
		
		return -1;
	}
	
	/**
	 * this function is to convert the term frequency to a fuzzy variable mid 
	 * the returned value is the term importance to this variable
	 * @param tf term frequency
	 * @param min minimum value of a word
	 * @param average calculated in previous state
	 * @param max maximum frequency of a word
	 * @return a fuzzy value of the mid fuzzy variable this value is bounded from 0 to 2
	 */
	public static double mid(int tf, int min, double average, int max) {
		// given equations
		int a = min;
		double b = (average + min) / 2;
		double c = average;
		double d = average + ((max - average)/4);

		if(tf == 0) {
			return 0;
		}
		
		if(tf < a) {
			return 1;
		}
		
		if(tf >= a && tf <= b) {
			return (1 + ((tf - a)/(b - a)));
		}
		
		if(tf > b && tf < c) {
			return 2;
		}
		
		if(tf >= c && tf <= d) {
			return (1 + ((tf - d)/(c - d)));
		}
		
		if(tf > d) {
			return 1;
		}
		
		return -1;
	}
	
	/**
	 * this function is to convert the term frequency to a fuzzy variable high 
	 * the returned value is the term importance to this variable
	 * @param tf term frequency
	 * @param min minimum value of a word
	 * @param average calculated in previous state
	 * @param max maximum frequency of a word
	 * @return a fuzzy value of the high fuzzy variable this value is bounded from 0 to 2
	 */
	public static double high(int tf, int min, double average, int max) {
		// given equations
		double a = average;
		double b = average + ((max - average)/4);
		double c = average + ((max - average)/2);
		double d = max;

		if(tf == 0) {
			return 0;
		}
		
		if(tf < a) {
			return 1;
		}
		
		if(tf >= a && tf <= b) {
			return (1 + ((tf - a)/(b - a)));
		}
		
		if(tf > b && tf < c) {
			return 2;
		}
		
		if(tf >= c && tf <= d) {
			return (1 + ((tf - d)/(c - d)));
		}
		
		return -1;
	}
	
}
