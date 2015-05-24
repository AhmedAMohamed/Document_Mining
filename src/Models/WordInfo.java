package Models;

import Main.Fuzzy.FuzzyVariable;

import java.util.*;

public class WordInfo {
    private HashSet<DocumentTermFrequency> docs;
    private ArrayList<String> hypernyms;

    private int freq;
    private int maxFreq, minFreq;
    private double average;


    private FuzzyVariable maxSummedFuzzyVariable;
    private double [] fuzzySummedValues = {0.0,0.0,0.0};


    /**
     * Construct a wordinfo object. This object holds lots of information for a word in the data set.
     */
    public WordInfo()
    {
        docs = new HashSet<>();
        freq = 0;
        hypernyms = null;
        minFreq = Integer.MAX_VALUE;
        maxFreq = Integer.MIN_VALUE;
        average = 0;
        maxSummedFuzzyVariable = FuzzyVariable.NONE;

    }

    /**
     * Increment the frequency of this word.
     *
     * @param n    The value to increment the frequency with
     */
    public void incrementFrequency(int n)
    {
        freq += n;
    }

    /**
     * Increment the frequency of this word by 1.
     */
    public void incrementFrequency()
    {
        incrementFrequency(1);
    }

    /**
     * Get the frequency of this word.
     *
     * @return  the word frequency
     */
    public int getFrequency(){
        return freq;
    }

    /**
     * Remove a document from the word's documents. This indicate that this word is no longer in the removed document.
     * Word frequency is adjusted after the removal.
     *
     * @param doc   The document to remove
     * @param tf    The term frequency of this word in the document
     */
    public void removeDocument(DocumentTermFrequency doc, int tf)
    {
        if(docs.remove(doc))
        {
            freq -= tf;
        }
    }

    /**
     * Update minimum, maximum and average frequency values of this word in all documents containing it. These values
     * are used in the fuzzy memership functions.
     *
     * @param word   The word
     * @param K      amount of words found in the data set
     *
     * @see Main.Fuzzy.Membership#low
     * @see Main.Fuzzy.Membership#mid
     * @see Main.Fuzzy.Membership#high
     */
    public void updateMinMaxAvg(String word, int K){

        int f;
        for(DocumentTermFrequency d : docs)
        {
            f = d.getWordFreq(word);
            if(f<minFreq)
                minFreq = f;

            if(f> maxFreq)
                maxFreq = f;

            average+=f;

        }
        average /= K;
    }


    /**
     * Get the documents where this word exist.
     *
     * @return unmodifiable set of documents
     */
    public Set<DocumentTermFrequency> getDocs() {
        return Collections.unmodifiableSet(docs);
    }


    /**
     * Get the hypernyms of this word.
     *
     * @see Models.Wordnet#getHypernyms(String)
     * @return unmodifiable list of hypernyms
     */
    public List<String> getHypernyms() {
        return Collections.unmodifiableList(hypernyms);
    }

    /**
     * Check if this word appeared in the given document.
     * @param doc      Document to check the word in.
     * @return   boolean indicating if the document have the word
     */
    public boolean hasDocument(DocumentTermFrequency doc){
        return docs.contains(doc);
    }

    /**
     * Get the amount of documents this word appeared in.
     * @return number of documents
     */
    public int getDocumentsSize(){
        return docs.size();
    }
    /**
     * Add a document to this word indicating that this word appear in the given document.
     * @param doc      The document that the word appears in.
     * @return   boolean indiciating if the word already have thid document
     */
    public boolean addDocument(DocumentTermFrequency doc)
    {
        return docs.add(doc);
    }

    /**
     * Set the hypernyms of this word.
     *
     * @param hypernyms  List of hypernyms
     *
     * @see Models.Wordnet#getHypernyms(String)
     */
    public void setHypernyms(ArrayList<String> hypernyms)
    {
        this.hypernyms = hypernyms;
    }

    /**
     * Get the maximum frequncy of this word in all its documents.
     *
     * @see Main.Fuzzy.Membership#low
     * @see Main.Fuzzy.Membership#mid
     * @see Main.Fuzzy.Membership#high
     * @return maximum frequency.
     */
    public int getMaxFreq() {
        return maxFreq;
    }


    /**
     * Get the minimum frequncy of this word in all its documents.
     *
     * @see Main.Fuzzy.Membership#low
     * @see Main.Fuzzy.Membership#mid
     * @see Main.Fuzzy.Membership#high
     * @return minimum frequency.
     */
    public int getMinFreq() {
        return minFreq;
    }

    /**
     * Get the average frequncy of this word in all its documents, averaged by the number of words found in the
     * data set.
     *
     * @see Main.Fuzzy.Membership#low
     * @see Main.Fuzzy.Membership#mid
     * @see Main.Fuzzy.Membership#high
     * @return average frequency.
     */
    public double getAverage() {
        return average;
    }


    /**
     * Get the fuzzy variable with the maximum summed fuzzy value of this word in all the documents it appeared in.
     *
     * @return maximum fuzzy variable
     */
    public FuzzyVariable getMaxSummedFuzzyVariable() {
        return maxSummedFuzzyVariable;
    }


    /**
     * Gets the value of the maximum summed fuzzy variable.
     *
     * @see WordInfo#getMaxSummedFuzzyVariable
     * @return max fuzzy value
     */
    public double getMaxSummedFuzzyValue() {
        if(maxSummedFuzzyVariable == FuzzyVariable.NONE)
            return 0;

        return fuzzySummedValues[maxSummedFuzzyVariable.getId()];
    }

    /**
     * Set the maximum summed fuzzy variable.
     *
     * @param variable      The fuzzy variable
     *
     * @see WordInfo#getMaxSummedFuzzyVariable
     */
    public void setMaxSummedFuzzyVariable(FuzzyVariable variable)
    {
        maxSummedFuzzyVariable = variable;
    }

    /**
     * Increment the value of the summed fuzzy variable.
     *
     * @param variable     The variable to increment its summed value
     * @param value        Increment value
     *
     * @see Main.Fuzzy.FuzzyMining#caluclateFuzzyVariables
     */
    public void incrementSummedFuzzyValue(FuzzyVariable variable, double value)
    {
        fuzzySummedValues[variable.getId()] += value;
    }

    /**
     * Return the summation of the fuzzy variable of this word over all documents it appear in.
     *
     * @param variable     The variable to get its value
     * @return    the summed fuzzy varable value
     */
    public double getSummedFuzzyValue(FuzzyVariable variable){
        return fuzzySummedValues[variable.getId()];
    }

}