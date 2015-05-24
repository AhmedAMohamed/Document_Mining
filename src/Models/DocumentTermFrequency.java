package Models;

import Main.Fuzzy.FuzzyVariable;

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
    private int clusterMatricesIndex = 0;

    /**
     * Construct with name.
     *
     * @param name The document name.
     */
    public DocumentTermFrequency(String name)
    {
        this.name = name == null ? "":name;
        this.termFreq = new HashMap<>();
        this.maxTermFrequency = 0;
        
    }

    /**
     * Get the max frequency found in any one term in the document
     *
     * @return max frequency
     */
    public int getMaxTermFrequency(){
        return maxTermFrequency;
    }

    /**
     * Add term to the document or increase its frequency if already there.
     *
     * @param word word to add
     * @return the term frequency after this add operation
     */
    public int addTerm(String word){
        return addTerm(word, 1);
    }


    /**
     * Add term to the document with supplied frequency or increase the current term frequency if already exist.
     *
     * @param word  word to add
     * @param freq  frequency
     * @return the term frequency after this add operation
     */
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

    /**
     * Remove a term from the document.
     *
     * @param word   the word to remove
     */
    public void removeTerm(String word)
    {
        termFreq.remove(word);
    }

    /**
     * Get the document name
     * @return   document name
     */
    public String getName(){
        return name;
    }

    /**
     * Get the frequency of the given word in this documents.
     *
     * @param word  the word to get its frequency
     * @return  the word frequency
     */
    public int getWordFreq(String word)
    {
        Info stored = termFreq.get(word);
        return stored == null ? 0: stored.freq;
    }

    /**
     * Get the fuzzy variable value of the word in this document.
     *
     * @param word              The word
     * @param fuzzyVariable     FuzzyVariable enum to get its value
     * @see Main.Fuzzy.FuzzyVariable
     * @return the fuzzy variable value attached to the word
     */
    public double getFuzzyValue(String word, FuzzyVariable fuzzyVariable) {
    	Info stored = termFreq.get(word);
    	if(stored != null) {
    		return stored.fuzzyValues[fuzzyVariable.getId()];
    	}
    	return 0;
    }

    /**
     * Get the index used to get the value of this document in the Document-Term &
     * Document-Cluster Matrices used in clustering stage.
     *
     * @see Main.Clustering#calculateDTM
     * @see Main.Clustering#calculateDCM
     * @return index in DTM matrix
     */
    public int getClusterMatricesIndex(){
        return clusterMatricesIndex;
    }

    /**
     * Set the index used to get the value of this document in the Document-Term &
     * Document-Cluster Matrices used in clustering stage.
     *
     * @param clusterMatricesIndex
     * @see Main.Clustering#calculateDTM
     * @see Main.Clustering#calculateDCM
     */
    public void setClusterMatricesIndex(int clusterMatricesIndex)
    {
        this.clusterMatricesIndex = clusterMatricesIndex;
    }

    /**
     * Set the fuzzy variable values of a word in this document.
     *
     * @param word      The word
     * @param low       Fuzzy value attached to FuzzyVariable.LOW
     * @param mid       Fuzzy value attached to FuzzyVariable.MEDIUM
     * @param high      Fuzzy value attached to FuzzyVariable.HIGH
     */
    public void setFuzzyValue(String word, double low, double mid, double high) {
    	Info stored = termFreq.get(word);
    	if(stored != null) {
    		stored.fuzzyValues[FuzzyVariable.LOW.getId()] = low;
    		stored.fuzzyValues[FuzzyVariable.MEDIUM.getId()] = mid;
    		stored.fuzzyValues[FuzzyVariable.HIGH.getId()] = high;
    	}
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
