package Models;

import java.util.*;

public class Cluster{
    private List<String> terms;
    private double support;



    private Set<DocumentTermFrequency> docs;


    public Cluster(ArrayList<String> terms){
        this.terms = terms;
    }

    public Cluster(String str){
        terms = new ArrayList<>(1);
        terms.add(str);
    }

    public Cluster(String str, double support){
        terms = new ArrayList<>(1);
        terms.add(str);
        this.support = support;
    }

    public Cluster(ArrayList<String> terms, double support) {
    	this.terms = terms;
    	this.support = support; 
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

}
