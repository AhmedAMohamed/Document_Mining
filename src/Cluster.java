import java.util.ArrayList;
import java.util.Comparator;

public class Cluster implements Comparator<Cluster> {
    public ArrayList<String> terms;
    public double support;
    
    
    public Cluster() {
        terms = new ArrayList<>();
        support = 0;
    }

    public Cluster(ArrayList<String> terms, double support) {
    	this.terms = terms;
    	this.support = support; 
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
