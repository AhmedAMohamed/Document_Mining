package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by karim on 5/7/15.
 */
public class WordInfo {
    public HashSet<DocumentTermFrequency> docs;
    public int freq;
    public ArrayList<String> hypernyms;

    public WordInfo()
    {
        docs = new HashSet<>();
        freq = 0;
        hypernyms = null;
    }

    public WordInfo( int freq)
    {
        this.freq = freq;
    }

    public void addOther(WordInfo wi)
    {
        this.docs.addAll(wi.docs);
        this.freq += wi.freq;
    }
    public WordInfo(int freq, ArrayList<String> hypernyms)
    {
        this.freq = freq;
        this.hypernyms = hypernyms;
    }
}
