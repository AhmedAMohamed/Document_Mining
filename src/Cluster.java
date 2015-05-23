import java.util.*;

public class Cluster implements Comparator<Cluster> {
    public ArrayList<String> terms;
    private int sizeSinceLastHash = 0;
    private int hash = -1;
    public double support;
    public Set<DocumentTermFrequency> docs; 
    
    public Cluster() {
        terms = new ArrayList<>();
    }

    public Cluster(ArrayList<String> terms){
        this.terms = terms;
    }

    public Cluster(String str){
        terms = new ArrayList<>();
        terms.add(str);
    }

    public Cluster(ArrayList<String> terms, double support) {
    	this.terms = terms;
    	this.support = support; 
    }
    
    public void updateClusterDocuments(HashMap<String, WordInfo> wordsVector) {
		docs = new HashSet<>(wordsVector.get(terms.get(0)).docs);
		for(int i = 1; i < terms.size(); i++) {
			docs.retainAll(wordsVector.get(terms.get(i)).docs);
		}
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cluster cluster = (Cluster) o;

        if (!terms.equals(cluster.terms)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        //recompute hash when changed
        if(sizeSinceLastHash != terms.size())
        {
            sizeSinceLastHash = terms.size();
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < terms.size(); i++)
            {
                builder.append(terms.get(i));
            }
            hash = builder.toString().hashCode();
        }

        return hash;
    }

	@Override
	public int compare(Cluster o1, Cluster o2) {
		return o1.terms.get(0).compareTo(o2.terms.get(0));
	}
}
