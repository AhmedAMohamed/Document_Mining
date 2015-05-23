import java.util.ArrayList;
import java.util.HashSet;

public class WordInfo {
    public HashSet<DocumentTermFrequency> docs;
    private int freq;
    public ArrayList<String> hypernyms;
    public int maxFreq, minFreq;
    public double average = 0;
    public FuzzyState fuzzyState;
    public double[] count;
    public FuzzyState maxFuzzyVarriable;
    public double maxFuzzyValue;
    
    private DocumentTermFrequency curDoc;
    public WordInfo()
    {
        docs = new HashSet<>();
        freq = 0;
        hypernyms = null;
        curDoc = null;
        minFreq = Integer.MAX_VALUE;
        maxFreq = Integer.MIN_VALUE;
        average = 0;
        fuzzyState = FuzzyState.NONE;
        count = new double[3]; 	
    }

    public void addOther(WordInfo wi)
    {
        this.docs.addAll(wi.docs);
        this.freq += wi.freq;
    }


    public void incrementFrequency(int n)
    {
        freq += n;
    }
    public void incrementFrequency()
    {
        incrementFrequency(1);
    }
    public int getFrequency(){
        return freq;
    }

    public void removeDocument(DocumentTermFrequency doc, int tf)
    {
        if(docs.remove(doc))
        {
            freq -= tf;
        }
    }

    public boolean updateMinMaxAvg(String word, int K){

        boolean empty = (freq == 0);

        boolean found = false;
        int f = 0;
        for(DocumentTermFrequency d : docs)
        {
            f = d.getWordFreq(word);
            if(f<minFreq)
                minFreq = f;

            if(f> maxFreq)
                maxFreq = f;

            average+=f;

        }

        if(empty && average > 0)
        {
            System.out.println("Failed Empty but has words in document: " + word);
        }
        else if(!empty && average == 0)
        {
            System.out.println("Failed npt empty but has no words in document: " + word);
        }
        average /= K;
        return (freq != 0);
    }
}