package Models;

import java.util.*;

public class Cluster{
	
    private List<String> terms;
    private HashSet<String> lookup;
    private double support;

    private Set<DocumentTermFrequency> docs;

    private int clusterMatricesIndex = 0;

    private double score = 0;
    
    
    public Cluster(ArrayList<String> terms){
        this.terms = terms;
        lookup = new HashSet<>(terms);
    }

    public Cluster(String str){
        terms = new ArrayList<>(1);
        terms.add(str);
        lookup = new HashSet<>(terms);
    }

    public Cluster(String str, double support){
        terms = new ArrayList<>(1);
        terms.add(str);
        this.support = support;
        lookup = new HashSet<>(terms);
    }

    public Cluster(ArrayList<String> terms, double support) {
    	this.terms = terms;
    	this.support = support;
        lookup = new HashSet<>(terms);
    }

    /**
     * Form the documents belonging to this cluster. This is used when the cluster is still a candidate.
     * Documents that have all the terms in this clusters are considered to belong to this cluster.
     *
     * @param wordsVector   the unified words vector
     */
    public void updateClusterDocuments(HashMap<String, WordInfo> wordsVector) {

        // first create a new set with documents in the first term in this cluster
		docs = new HashSet<>(wordsVector.get(terms.get(0)).getDocs());

        // then get the set intersection with the documents of the other terms in this cluster
		for(int i = 1; i < terms.size(); i++) {
			docs.retainAll(wordsVector.get(terms.get(i)).getDocs());
		}
    }

    /**
     * Get the terms of this cluster.
     *
     * @return unmodifiable list of terms
     */
    public List<String> getTerms(){
        return Collections.unmodifiableList(terms);
    }


    /**
     * Get terms size of this cluster.
     *
     * @return size of terms
     */
    public int getTermsSize(){
        return terms.size();
    }

    /**
     * Get a term from cluster terms
     * @param index   index of the term
     * @return   the selected term
     */
    public String getTerm(int index)
    {
        return terms.get(index);
    }

    /**
     * Get the cluster importance value.
     *
     * @return the support of the terms in this cluster
     */
    public double getSupport(){
        return support;
    }

    /**
     * Get the cluster matrix index
     *
     * @return the index of the cluster in cluster matrix DCM
     */
    public int getClusterMatrixIndex() {
    	return clusterMatricesIndex;
    }
    
    /**
     * Get the score value of this cluster
     * @return score value for this cluster
     */
    public double getScore() {
    	return this.score;
    }
    
    /**
     * Set Cluster matrix index
     *
     * @param index of the cluster in the DCM
     */
    public void setClusterMatricesIndex(int index) {
    	this.clusterMatricesIndex = index;
    }

    /**
     * Set the cluster importance value
     * @param support   the support
     */
    public void setSupport(double support)
    {
        this.support = support;
    }

    /**
     * Get Cluster documents
     * @return  documents belonging to this cluster
     */
    public Set<DocumentTermFrequency> getDocs() {
        return docs;
    }
    /**
     * Calculate score functions of this cluster
     */
    public void calculateScore(HashMap<String, WordInfo> wordsVector) {
		for (DocumentTermFrequency doc : this.getDocs()) {
			for (String word : this.getTerms()) {
				score += doc.getFuzzyValue(word, wordsVector.get(word)
						.getMaxSummedFuzzyVariable());
			}
		}
	}

    /**
     * Checks whether a term exist.
     *
     * @param term the term to look for.
     * @return boolean
     */
    public boolean hasTerm(String term)
    {
        return lookup.contains(term);
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(String s : terms)
        {
            builder.append(s + ", ");
        }
        builder.delete(builder.length()-2, builder.length());
        return "Cluster:  {id: "+ clusterMatricesIndex +" , terms:( " + builder + "), docSize: "+ docs.size() + " }";
    }
}
