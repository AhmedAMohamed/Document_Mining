package Model;

import java.util.ArrayList;

/**
 * Created by karim on 5/7/15.
 */
public class WordInfo {
    public String word;
    public int df;
    public int freq;
    public ArrayList<String> hypernyms;

    public WordInfo(String word)
    {
        this.word = word;
        df = 0;
        freq = 0;
        hypernyms = null;
    }

    public WordInfo(String word, int df, int freq)
    {
        this.word = word;
        this.df = df;
        this.freq = freq;
    }

    public WordInfo(String word, int df, int freq, ArrayList<String> hypernyms)
    {
        this.word = word;
        this.df = df;
        this.freq = freq;
        this.hypernyms = hypernyms;
    }
}
