import java.util.ArrayList;

public class Cluster {
    public ArrayList<String> terms;
    public double support;

    public Cluster(){
        terms = new ArrayList<>();
        support = 0;
    }

}
