import java.util.ArrayList;
import java.util.HashMap;


public class AssociationRuleMining {

	public static ArrayList<DocumentTermFrequency> documents;
    public static HashMap<String, WordInfo> wordsVector;
    public static Membership memFunctions;
    
    public AssociationRuleMining(ArrayList<DocumentTermFrequency> documents, HashMap<String, WordInfo> wordsVector) {
		this.documents = documents;
		this.wordsVector = wordsVector;
	}
    
    public void getTermFreq() {
    	// step one
    	for(DocumentTermFrequency d : documents) {
    		for(String word : wordsVector.keySet()) {
    			WordInfo w = wordsVector.get(word);
    			@SuppressWarnings("static-access")
				double l = memFunctions.low(d.getWordFreq(word), w.minFreq, w.average, w.maxFreq);
    			@SuppressWarnings("static-access")
				double m = memFunctions.mid(d.getWordFreq(word), w.minFreq, w.average, w.maxFreq);
    			@SuppressWarnings("static-access")
				double h = memFunctions.high(d.getWordFreq(word), w.minFreq, w.average, w.maxFreq);
    			d.setFuzzyValue(word, l, m, h);
    		}
    	}
    	
    	// step two
    	for(String word : wordsVector.keySet()) {
    		for(DocumentTermFrequency d : documents) {
    			wordsVector.get(word).count[FuzzyState.LOW.getId()] += d.getFuzzyValue(word, FuzzyState.LOW);
    			wordsVector.get(word).count[FuzzyState.MEDIUM.getId()] += d.getFuzzyValue(word, FuzzyState.MEDIUM);
    			wordsVector.get(word).count[FuzzyState.HIGH.getId()] += d.getFuzzyValue(word, FuzzyState.HIGH);
    		}
    	}
    	
    	// step three
    	
    }
}
