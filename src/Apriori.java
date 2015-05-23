import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.HashSet;


public class Apriori {



    private static ArrayList<ArrayList<Cluster>> getFrequentItemsets(ArrayList<Cluster> l1,
                                                                     ArrayList<DocumentTermFrequency> docs,
                                                                     HashMap<String, WordInfo> wordsVector){

        ArrayList<ArrayList<Cluster>> output = new ArrayList<>();
        output.add(l1);

        while(true)
        {
            ArrayList<Cluster> L = output.get(output.size()-1);
            if(L.isEmpty())
            {
                output.remove(output.size()-1);
                break;
            }
            //generate candidate
            ArrayList<Cluster> candidates = generateCandidates(L);
            //add the new L
            output.add(generateNextL(candidates, docs, wordsVector));
        }

        pruneOnConfident(output);

        return output;
    }

    private static void pruneOnConfident(ArrayList<ArrayList<Cluster>> output) {

        //holds cluster passed confidence
        ArrayList<Cluster> last = new ArrayList<>();

        //generate hash for fast retrieval
        HashMap<Integer, Double> history = new HashMap<>();
        for(int i = 0; i < output.size(); i++)
        {
            ArrayList<Cluster> level = output.get(i);
            for(Cluster c :level)
            {
                history.put(c.hashCode(),c.support);
            }
        }

        //calculate rules
        //iterate over all levels >= 2
        for(int i = 1; i < output.size();i++)
        {
            ArrayList<Cluster> level = output.get(i);
            for (Cluster c : level)
            {
                //holds

                for(int j = 0; j < c.terms.size(); j++)
                {

                    StringBuilder builder  = new StringBuilder();
                    for(int s = 0; s < c.terms.size(); s++)
                    {
                        if(s != j)
                            builder
                    }
                }

            }


        }

    }

    private static ArrayList<Cluster> generateNextL(ArrayList<Cluster> candidates, ArrayList<DocumentTermFrequency> docs,
                                                   HashMap<String, WordInfo> wordsVector) {
        ArrayList<Cluster> nextL = new ArrayList<>();

        for(Cluster c : candidates)
        {
            double w = 0;

            //extract fuzzy support of cluser from documents
            for(DocumentTermFrequency d : docs)
            {
                double min = Double.MAX_VALUE;
                for(String t : c.terms)
                {
                    double v = d.getFuzzyValue(t,wordsVector.get(t).fuzzyState);
                    if(v < min)
                        min = v;
                }
                w += min;
            }
            c.support = w;
            //if fuzzy support is greater than minsup then add it and continue
            if((w/ docs.size()) >= Algorithm.minSup)
            {
                nextL.add(c);
            }
        }
        return nextL;
    }

    private static ArrayList<Cluster> generateCandidates(ArrayList<Cluster> L) {
        HashSet<Cluster> seen = new HashSet<>(L);
        ArrayList<Cluster> candidates = new ArrayList<>();

        //join
        for(int i = 0; i < L.size(); i++)
        {
            for(int j = i+1; j < L.size(); j++)
            {
                Cluster joined = tryJoin(L.get(i), L.get(j));
                if(joined == null)
                    break;

                if(!tryPrune(joined, seen))
                    candidates.add(joined);
            }
        }
        return candidates;
    }

    private static boolean tryPrune(Cluster joined, HashSet<Cluster> seen) {
        ArrayList<String> arr = new ArrayList<>(joined.terms.size()-1);
        while(arr.size() < joined.terms.size()-1) arr.add("");
        return !combinations(seen, joined, joined.terms.size()-1, 0,
                new Cluster(arr));
    }

    private static Cluster tryJoin(Cluster a, Cluster b)
    {

        ArrayList<String> joined = new ArrayList<>(a.terms.size()+1);
        //check for prefixes
        boolean same = true;
        for(int i = 0; i < a.terms.size()-1; i++)
        {
            if(!a.terms.get(i).equals(b.terms.get(i)))
            {
                same =false;
                break;
            }
            joined.add(a.terms.get(i));
        }
        //if not same prefix exit
        if(!same)
        {
            return null;
        }
        //add last in a
        joined.add(a.terms.get(a.terms.size()-1));
        //add last in b
        joined.add(b.terms.get(b.terms.size() - 1));

        return new Cluster(joined);

    }
    //abcdef
    //r: ab
    //   ac
    static boolean combinations(HashSet<Cluster> seen,
                        Cluster arr, int len, int startPosition, Cluster result){
        if (len == 0){
            return seen.contains(result);
        }
        for (int i = startPosition; i <= arr.terms.size()-len; i++){
            result.terms.set(result.terms.size() - len, arr.terms.get(i));
            if(!combinations(seen, arr, len-1, i+1, result))
                return false;
        }

        return true;
    }


    public static void main(String[] args) {


//        for(Cluster it: c)
//        {
//            for(String s: it.terms){
//                System.out.print(s + ", ");
//            }
//            System.out.println();
//        }
    }
}
