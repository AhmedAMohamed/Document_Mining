package Main.Fuzzy;

import Main.Algorithm;
import Models.Cluster;
import Models.DocumentTermFrequency;
import Models.WordInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


class Apriori {

    private static HashMap<String, Double> seen =  new HashMap<>();
    
    public static HashMap<String, WordInfo> wordsVector;
    
    
    /**
     * Apply fuzzy apriori to get candidate clusters of level >= 1
     * @param l1            The L1 candidate clusters
     * @param docs          The documents
     * @param wordsVector   The unified words vector
     * @return    all candidate cluster with confidence and min sup greater than threshold
     *            {@link Main.Algorithm#MIN_SUPPORT}, {@link Main.Algorithm#MIN_CONFIDENCE}
     */
    public static ArrayList<Cluster> generateAllCandidateClusters(ArrayList<Cluster> l1,
                                                                  ArrayList<DocumentTermFrequency> docs,
                                                                  HashMap<String, WordInfo> wordsVector){

        ArrayList<ArrayList<Cluster>> output = new ArrayList<>();

        //add level 1 clusters
        output.add(l1);

        // loop to get larger levels
        while(true)
        {
            // get the last generated level
            ArrayList<Cluster> L = output.get(output.size()-1);
            // is it empty?
            if(L.isEmpty())
            {
                // if so remove it and we are done
                output.remove(output.size()-1);
                break;
            }
            // otherwise generate candidate
            ArrayList<Cluster> candidates = generateCandidates(L);
            // and add the new level
            output.add(generateNextLevel(candidates, docs, wordsVector));
        }
        int count = 0;
        for(int i = 0; i < output.size(); i++)
        {
            count += output.get(i).size();
        }
        System.out.println("Apriori: candidate size bfore confidence is " + count);

        // prune candidate cluster with low confidence
        ArrayList<Cluster> largeClusters  = pruneOnConfident(output);

        // append level 1 clusters
        largeClusters.addAll(l1);

        System.out.println("Apriori: candidate size after confidence is " + largeClusters.size());

        return largeClusters;
    }


    /**
     * Apriori candidate generation.
     *
     * @param level    level to generate the candidates from
     * @return     candidate level ArrayList with candidate clusters
     */
    private static ArrayList<Cluster> generateCandidates(ArrayList<Cluster> level) {

        // add level to seen
        for(int i = 0; i < level.size(); i++)
        {
            seen.put(stringifyTerms(level.get(i).getTerms()), level.get(i).getSupport());
        }


        ArrayList<Cluster> candidates = new ArrayList<>();

        //apriori joining
        for(int i = 0; i < level.size(); i++)
        {
            for(int j = i+1; j < level.size(); j++)
            {
                // try to join with other
                Cluster joined = tryJoin(level.get(i), level.get(j));

                // if joined failed break this happen when they dont have same prefix
                if(joined == null)
                    break;

                // create all subsets and try to prune if doesnt exist in previous level
                if(!tryPrune(joined))
                    candidates.add(joined);
            }
        }
        return candidates;
    }

    /**
     * Try to prune a generated candidate item set for next level mining.
     * @param joined    The joined candidate
     * @return
     */
    private static boolean tryPrune(Cluster joined) {
        // create terms list with the next level size and fill it
        ArrayList<String> arr = new ArrayList<>(joined.getTermsSize()-1);
        while(arr.size() < joined.getTerms().size()-1) arr.add("");

        // get all subsets which return false if one of the subsets is not in seen
        return !combinations(joined, joined.getTermsSize()-1, 0,
                arr);
    }

    /**
     * Try joining 2 terms.
     * @param a    term 1
     * @param b    term 2
     * @return    return null when join fails else return a new cluster with the joined result.
     */
    private static Cluster tryJoin(Cluster a, Cluster b)
    {

        ArrayList<String> joined = new ArrayList<>(a.getTermsSize()+1);
        //check for prefixes
        boolean same = true;
        for(int i = 0; i < a.getTermsSize()-1; i++)
        {
            if(!a.getTerm(i).equals(b.getTerm(i)))
            {
                same =false;
                break;
            }
            joined.add(a.getTerm(i));
        }
        //if not same prefix exit
        if(!same)
        {
            return null;
        }

        //add last in a
        joined.add(a.getTerm(a.getTermsSize() -1));
        //add last in b
        joined.add(b.getTerm(b.getTermsSize() - 1));

        return new Cluster(joined, wordsVector);

    }


    /**
     * Get all subsets of the given len in order and check if the subset is in seen
     * @param arr              cluster to get its subsets
     * @param len              len of the subsets
     * @param startPosition    start position
     * @param result           save the recursive result
     * @return        indicate whether all subsets of given cluster items exist in previous level
     */
    static boolean combinations(
                        Cluster arr, int len, int startPosition, ArrayList<String> result){
        // if we found a subset
        if (len == 0){
            // check if it is in the seen
            return seen.get(stringifyTerms(result)) != null;
        }

        for (int i = startPosition; i <= arr.getTermsSize()-len; i++){
            result.set(result.size() - len, arr.getTerm(i));

            // if the subset was not found in seen return false to stop the recursive function
            if(!combinations(arr, len-1, i+1, result))
                return false;
        }

        return true;
    }

    /**
     * Generate next level from the candidates.
     * @param candidates   the candidates
     * @param docs         documents
     * @param wordsVector  unified words vector
     * @return     next level
     */
    private static ArrayList<Cluster> generateNextLevel(ArrayList<Cluster> candidates,
                                                        ArrayList<DocumentTermFrequency> docs,
                                                        HashMap<String, WordInfo> wordsVector) {
        ArrayList<Cluster> nextL = new ArrayList<>();

        for(Cluster c : candidates)
        {
            double w = 0;

            //extract fuzzy support of cluser from documents
            for(DocumentTermFrequency d : docs)
            {
                double min = Double.MAX_VALUE;
                for(String t : c.getTerms())
                {
                    double v = d.getFuzzyValue(t,wordsVector.get(t).getMaxSummedFuzzyVariable());
                    if(v < min)
                        min = v;
                }
                w += min;
            }
            c.setSupport(w / docs.size());
            //if fuzzy support is greater than minsup then add it and continue
            if(c.getSupport() >= Algorithm.MIN_SUPPORT)
            {
                nextL.add(c);
            }
        }
        return nextL;
    }


    /**
     * Prune produced clusters by checking their confidence.
     *
     * @param output    produced clusters
     * @return     pruned clusters
     */
    private static ArrayList<Cluster> pruneOnConfident(ArrayList<ArrayList<Cluster>> output) {

        ArrayList<Cluster> candidateClusters = new ArrayList<>();
        //calculate rules
        //iterate over all levels >= 2
        for(int i = 1; i < output.size();i++)
        {
            ArrayList<Cluster> level = output.get(i);
            for (Cluster c : level)
            {

                boolean dontAdd = false;
                // loop over each term in cluster to produce a rule {all}-{termj} -> termj
                for(int j = 0; j < c.getTermsSize(); j++)
                {
                    // create string representing itemset without termj
                    StringBuilder builder  = new StringBuilder();
                    for(int s = 0; s < c.getTermsSize(); s++)
                    {
                        if(s != j)
                            builder.append(","+c.getTerm(s));
                    }

                    // get the itemset support from seen
                    double down = seen.get(builder.toString());

                    // calculate the rule confidence sup({all})/sup({all}-{termj}}
                    if(c.getSupport()/down < Algorithm.MIN_CONFIDENCE)
                    {
                        dontAdd = true;
                        break;
                    }

                }

                if(!dontAdd)
                {
                    candidateClusters.add(c);
                }

            }


        }
        return candidateClusters;
    }

    /**
     * Produce a comma separated string with words in given list. Used to make a hash of frequent items.
     *
     * @param terms      List with terms
     * @return    comma separated string of terms
     */
    private static String stringifyTerms(List<String> terms){
        String tmp = "";
        for(int i = 0; i < terms.size(); i++)
        {

            tmp += "," + terms.get(i);
        }
        return tmp;
    }


}
