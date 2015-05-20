import java.util.ArrayList;

public class Cluster {
    public ArrayList<String> terms;
    public double support;

    public Cluster(){
        terms = new ArrayList<>();
        support = 0;
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
}
