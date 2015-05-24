package Models;

import Main.Algorithm;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Wordnet {

    private IDictionary dict;

    /**
     * Construct a wordnet object and open its dictionary.
     * @param openInMemory     Indicates whether to open dictionary in memory for fast access or not.
     *                         It takes some time to load the dictionary in memeory.
     */
    public Wordnet(boolean openInMemory) {
        try {
            // create the dictionary
            IRAMDictionary dict = new RAMDictionary(
                    new File(Algorithm.MAIN_DIRECTORY+"wordnet/dict"),
                    ILoadPolicy. NO_LOAD ) ;
            // open it
            dict.open();

            //check if we should load it in memory or not
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

    /**
     * Close the dictionary and remove it from memeory if it was loaded there.
     */
    public void close(){
        dict.close();
    }

    /**
     * Get hypernyms of a word. Only the top {@link Main.Algorithm#HYPERNYM_LEVELS} are extracted from the
     * wordnet dictionary.
     * <p>
     * Hypernym of a word is another word with more general meaning, this hypernym is used in the document
     * enrichment stage to group synynom terms under one term to have larger frequency.
     *
     * @param word    The word
     * @return  ArrayList of Strings with the word's hypernyms
     */
    public  ArrayList<String> getHypernyms (String word) {

        // try to get the noun of the given word
        IIndexWord idxWord = dict .getIndexWord(word , POS. NOUN );

        // check if we have got the synset, we wont get it if we have a word that is not recognized in the wordnet
        // dictionary

        // where we able to find a noun?
        if(idxWord == null)
        {
            // we coulnt so return an empty array
            return new ArrayList<>();
        }

        // choose the first word in the synset of the given word argument
        // get the noun word id from wordnet dictionary
        IWordID wordID = idxWord . getWordIDs () . get(0);

        // get the word object from the dictionary
        IWord iword = dict . getWord ( wordID ) ;

        // get the synset which is a list with all words that are synynoms to the given word argument
        ISynset synset = iword . getSynset () ;

        // get the direct hypernym of this synset
        List<ISynsetID> hypernyms =
                synset .getRelatedSynsets(Pointer.HYPERNYM) ;
        ArrayList<String> hypers = new ArrayList<>(Algorithm.HYPERNYM_LEVELS);

        // iterate to get the needed top level of hypernyms
        while(!hypernyms.isEmpty() && hypers.size() < Algorithm.HYPERNYM_LEVELS)
        {
            ISynset s = dict.getSynset(hypernyms.get(0));
            hypers.add(s.getWords().get(0).getLemma());
            hypernyms = s.getRelatedSynsets(Pointer.HYPERNYM);
        }
        return hypers;
    }

}
