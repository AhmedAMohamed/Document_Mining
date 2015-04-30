import java.util.HashMap;

/**
 * Created by karim on 4/30/15.
 */
public class DocumentTermFrequency {
    private String name;
    private HashMap<String, Integer> termFreq;

    public DocumentTermFrequency(String name)
    {
        this.name = name == null ? "":name;
        this.termFreq = new HashMap<>();
    }

    public void addTerm(String word){
        Integer stored = termFreq.get(word);
        if(stored != null)
        {
            termFreq.put(word, stored.intValue() + 1);
        }
        else
        {
            termFreq.put(word, 1);
        }
    }

    public String getName(){
        return name;
    }
    public int getWordFreq(String word)
    {
        Integer stored = termFreq.get(word);
        return stored == null ? -1: stored.intValue();
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
