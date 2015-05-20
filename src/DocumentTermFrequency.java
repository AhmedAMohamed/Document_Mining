import java.util.HashMap;

public class DocumentTermFrequency {
    private String name;
    private HashMap<String, Integer> termFreq;
    private int maxTermFrequency;

    public DocumentTermFrequency(String name)
    {
        this.name = name == null ? "":name;
        this.termFreq = new HashMap<>();
        this.maxTermFrequency = 0;
    }

    public int getMaxTermFrequency(){
        return maxTermFrequency;
    }

    public int addTerm(String word){
        return addTerm(word, 1);
    }

    public int addTerm(String word, int freq){
        Integer stored = termFreq.get(word);
        if(stored != null)
        {
            termFreq.put(word, stored.intValue() + freq);
            if(stored+freq > maxTermFrequency)
                maxTermFrequency = stored + freq;

            return stored + freq;
        }

        termFreq.put(word, freq);
        if(freq > maxTermFrequency)
            maxTermFrequency =  freq;

        return freq;
    }
    public void removeTerm(String word)
    {
        termFreq.put(word, 0);

    }

    public String getName(){
        return name;
    }
    public int getActualWordFreq(String word)
    {
        Integer stored = termFreq.get(word);
        return stored == null ? -1: stored.intValue();
    }

    public int getWordFreq(String word)
    {
        Integer stored = termFreq.get(word);
        return stored == null ? 0: stored.intValue();
    }

    public void setEmptyWord(String word)
    {
        termFreq.put(word, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentTermFrequency that = (DocumentTermFrequency) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
