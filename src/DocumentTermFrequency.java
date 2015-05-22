import java.util.HashMap;

public class DocumentTermFrequency {
	
	private class Info {
		public int freq;
		public double[] fuzzyValues;
		
		Info(int freq) {
			fuzzyValues = new double[3];
			this.freq = freq;
		}
	}
    private String name;
    private HashMap<String, Info> termFreq;
    private int maxTermFrequency;

    public DocumentTermFrequency(String name)
    {
        this.name = name == null ? "":name;
        this.termFreq = new HashMap<>();
        this.maxTermFrequency = 0;
        
    }

    public int getMaxTermFrequency(){
        return maxTermFrequency;
    }

    public int addTerm(String word){
        return addTerm(word, 1);
    }

    public int addTerm(String word, int freq){
        Info stored = termFreq.get(word);
        if(stored != null)
        {
        	stored.freq = stored.freq + freq;
        	if(stored.freq > maxTermFrequency)
                maxTermFrequency = stored.freq;
            return stored.freq;
        }

        termFreq.put(word, new Info(freq));
        if(freq > maxTermFrequency)
            maxTermFrequency =  freq;

        return freq;
    }
    public void removeTerm(String word)
    {
        termFreq.remove(word);
    }

    public String getName(){
        return name;
    }

    public int getWordFreq(String word)
    {
        Info stored = termFreq.get(word);
        return stored == null ? 0: stored.freq;
    }
    
    public double getFuzzyValue(String word, FuzzyState fs) {
    	Info stored = termFreq.get(word);
    	if(stored != null) {
    		return stored.fuzzyValues[fs.getId()];
    	}
    	return 0;
    }

    public void setFuzzyValue(String word, double low, double mid, double high) {
    	Info stored = termFreq.get(word);
    	if(stored != null) {
    		stored.fuzzyValues[FuzzyState.LOW.getId()] = low;
    		stored.fuzzyValues[FuzzyState.MEDIUM.getId()] = mid;
    		stored.fuzzyValues[FuzzyState.HIGH.getId()] = high;
    	}
    }
    
    public FuzzyState getWordMaxFuzzyValue(String word) {
    	Info stored = termFreq.get(word);
    	if(stored != null) {
    		double l = getFuzzyValue(word, FuzzyState.LOW);
    		double m = getFuzzyValue(word, FuzzyState.MEDIUM);
    		double h = getFuzzyValue(word, FuzzyState.HIGH);
    		if(l >= m && l >= h) {
    			return FuzzyState.LOW;
    		}
    		else if(m >= l && m >= h) {
    			return FuzzyState.MEDIUM;
    		}
    		else {
    			return FuzzyState.HIGH;
    		}
    		
    	}
    	return FuzzyState.NONE;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentTermFrequency that = (DocumentTermFrequency) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
