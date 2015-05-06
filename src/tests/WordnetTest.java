package tests;


import edu.mit.jwi.*;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class WordnetTest {
    public static void main(String[] args) throws  Exception{
        IDictionary dic = getDic(new File(System.getProperty("user.dir")+"/wordnet/dict"));


    }
    public static IDictionary getDic ( File wnDir ) throws Exception {
        IRAMDictionary dict = new RAMDictionary ( wnDir , ILoadPolicy. NO_LOAD ) ;
        dict.open();
        return dict;
    }

    public static ArrayList<String> getHypernyms ( IDictionary dict ) {
        // get the synset
        IIndexWord idxWord = dict . getIndexWord ( " sale " , POS . NOUN ) ;
        IWordID wordID = idxWord . getWordIDs () . get (0) ; // 1 st meaning
        IWord word = dict . getWord ( wordID ) ;
        ISynset synset = word . getSynset () ;
        // get the hypernyms

        List<ISynsetID> hypernyms =
                synset .getRelatedSynsets(Pointer.HYPERNYM) ;
        ArrayList<String> hypers = new ArrayList<>(5);
        while(!hypernyms.isEmpty() && hypers.size() < 5)
        {
            // print out each h y p e r n y m s id and synonyms
            ISynset s = dict.getSynset(hypernyms.get(0));
            hypers.add(s.getWords().get(0).getLemma());
            hypernyms = s.getRelatedSynsets(Pointer.HYPERNYM);
        }
        return hypers;
    }

}
