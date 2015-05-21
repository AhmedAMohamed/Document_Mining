import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class AssociationRuleMining {

	public static ArrayList<DocumentTermFrequency> documents;
    public static HashMap<String, WordInfo> wordsVector;
    public static Membership memFunctions;
    public ArrayList<Cluster> l1;
    
    public AssociationRuleMining(ArrayList<DocumentTermFrequency> documents, HashMap<String, WordInfo> wordsVector) {
		this.documents = documents;
		this.wordsVector = wordsVector;
		l1 = new ArrayList<>();
	}
    
    public void getL1(double support) {
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
    	for(String word : wordsVector.keySet()) {
    		double l = wordsVector.get(word).count[FuzzyState.LOW.getId()];
    		double m = wordsVector.get(word).count[FuzzyState.MEDIUM.getId()];
    		double h = wordsVector.get(word).count[FuzzyState.HIGH.getId()];
    		
    		if(l >= m && l >= h) {
    			wordsVector.get(word).maxFuzzyVarriable = FuzzyState.LOW;
    			wordsVector.get(word).maxCount = l;
    			
    			if(l/documents.size() > support) {
    				ArrayList<String> terms = new ArrayList<>();
    				terms.add(word);
    				l1.add(new Cluster(terms, l/documents.size()));
    			}
    			
    		}
    		else if(m >= l && m >= h) {
    			wordsVector.get(word).maxFuzzyVarriable = FuzzyState.MEDIUM;
    			wordsVector.get(word).maxCount = m;
    			
    			if(m/documents.size() > support) {
    				ArrayList<String> terms = new ArrayList<>();
    				terms.add(word);
    				l1.add(new Cluster(terms, m/documents.size()));
    			}
    		}
    		else {
    			wordsVector.get(word).maxFuzzyVarriable = FuzzyState.HIGH;
    			wordsVector.get(word).maxCount = h;
    			
    			if(h/documents.size() > support) {
    				ArrayList<String> terms = new ArrayList<>();
    				terms.add(word);
    				l1.add(new Cluster(terms, h/documents.size()));
    			}
    		}
    		Collections.sort(l1, new Cluster());
    	}
    }
    
}
