package Model;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Wordnet {

    IDictionary dict;
    public Wordnet(boolean openInMemory) {
        try {
            IRAMDictionary dict = new RAMDictionary(
                    new File(System.getProperty("user.dir")+"/wordnet/dict"),
                    ILoadPolicy. NO_LOAD ) ;
            dict.open();
            if(openInMemory)
            {
                dict.load(true);
            }
            this.dict = dict;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }



    }

    public  ArrayList<String> getHypernyms (String _word, int levels) {
        // get the synset
        IIndexWord idxWord = dict .getIndexWord(_word , POS. NOUN ) ;
        if(idxWord == null)
        {
            return new ArrayList<>();
        }
        IWordID wordID = idxWord . getWordIDs () . get(0) ; // 1 st meaning
        IWord word = dict . getWord ( wordID ) ;
        ISynset synset = word . getSynset () ;
        // get the hypernyms
        List<ISynsetID> hypernyms =
                synset .getRelatedSynsets(Pointer.HYPERNYM) ;
        ArrayList<String> hypers = new ArrayList<>(levels);
        while(!hypernyms.isEmpty() && hypers.size() < levels)
        {
            ISynset s = dict.getSynset(hypernyms.get(0));
            hypers.add(s.getWords().get(0).getLemma());
            hypernyms = s.getRelatedSynsets(Pointer.HYPERNYM);
        }
        return hypers;
    }

    public boolean inNoun(String _word){
        return dict.getIndexWord(_word, POS.NOUN) != null;

    }
}
