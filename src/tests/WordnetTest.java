package tests;


import edu.mit.jwi.*;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.IStemmer;
import edu.mit.jwi.morph.SimpleStemmer;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.File;
import java.util.ArrayList;
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

    public static List<String> stemWordNet (String word, IDictionary dict) {
    	WordnetStemmer stemmer = new WordnetStemmer(dict);
    	List<String> stemed = stemmer.findStems(word, null);
    	return stemed;
    }
    
    public static void getHypernyms(IDictionary dict) {
        // get the synset
        IIndexWord idxWord = dict . getIndexWord ( "bakes" ,POS.NOUN) ;
        IWordID wordID = idxWord . getWordIDs () . get (0) ; // 1 st meaning
        IWord word = dict . getWord ( wordID );
        System.out.println(word.getLemma());
    }

}
