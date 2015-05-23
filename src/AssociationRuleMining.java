import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class AssociationRuleMining {

	public static ArrayList<DocumentTermFrequency> documents;
    public static HashMap<String, WordInfo> wordsVector;
    public static ArrayList<Cluster> L1;
    
    public AssociationRuleMining(ArrayList<DocumentTermFrequency> documents, HashMap<String, WordInfo> wordsVector) {
		this.documents = documents;
		this.wordsVector = wordsVector;
		L1 = new ArrayList<>();
	}
    
    public void getL1(double support) {
    	// step one
    	for(DocumentTermFrequency d : documents) {
    		for(String word : wordsVector.keySet()) {
    			WordInfo w = wordsVector.get(word);
    			//step 1
				double l = Membership.low(d.getWordFreq(word), w.minFreq, w.average, w.maxFreq);
				double m = Membership.mid(d.getWordFreq(word), w.minFreq, w.average, w.maxFreq);
				double h = Membership.high(d.getWordFreq(word), w.minFreq, w.average, w.maxFreq);
    			d.setFuzzyValue(word, l, m, h);
    			
    			//step 2
    			w.count[FuzzyState.LOW.getId()] += l;
    			w.count[FuzzyState.MEDIUM.getId()] += m;
    			w.count[FuzzyState.HIGH.getId()] += h;
    			
    		}
    	}
    	
    	
    	// step three
    	for(String word : wordsVector.keySet()) {
    		WordInfo w = wordsVector.get(word);
    		double low = w.count[FuzzyState.LOW.getId()];
    		double medium = w.count[FuzzyState.MEDIUM.getId()];
    		double high = w.count[FuzzyState.HIGH.getId()];
    		
    		if(low >= medium && low >= high) {
    			w.maxFuzzyVarriable = FuzzyState.LOW;
    			w.maxFuzzyValue = low;
    			
    			// step 4
    			if(low/documents.size() > support) {
    				ArrayList<String> terms = new ArrayList<>();
    				terms.add(word);
    				L1.add(new Cluster(terms, low/documents.size()));
    			}
    			
    		}
    		else if(medium >= low && medium >= high) {
    			w.maxFuzzyVarriable = FuzzyState.MEDIUM;
    			w.maxFuzzyValue = medium;
    			
    			// step 4
    			if(medium/documents.size() > support) {
    				ArrayList<String> terms = new ArrayList<>();
    				terms.add(word);
    				L1.add(new Cluster(terms, medium/documents.size()));
    			}
    		}
    		else {
    			w.maxFuzzyVarriable = FuzzyState.HIGH;
    			w.maxFuzzyValue = high;
    			
    			// step 4
    			if(high/documents.size() > support) {
    				ArrayList<String> terms = new ArrayList<>();
    				terms.add(word);
    				L1.add(new Cluster(terms, high/documents.size()));
    			}
    		}
    		Collections.sort(L1, new Cluster());
    	}
    }
    
}
