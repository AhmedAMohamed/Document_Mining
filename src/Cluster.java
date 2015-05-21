import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Cluster implements Comparator<Cluster> {
    public ArrayList<String> terms;
    public double support;
    public Set<DocumentTermFrequency> docs; 
    
    public Cluster() {
        terms = new ArrayList<>();
        support = 0;
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
        return terms.hashCode();
    }

	@Override
	public int compare(Cluster o1, Cluster o2) {
		return o1.terms.get(0).compareTo(o2.terms.get(0));
	}
}
