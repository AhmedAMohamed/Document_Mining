import java.util.ArrayList;

public class Cluster {
    public ArrayList<String> terms;

    public Cluster(){
        terms = new ArrayList<>();
    }

    public Cluster(ArrayList<String> terms){
        this.terms = terms;
    }

    public Cluster(String str){
        terms = new ArrayList<>();
        terms.add(str);
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
