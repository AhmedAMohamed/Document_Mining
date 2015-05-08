package Model;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import Model.DataContainer.Document;
import Model.StemmingAlgorithms.IteratedLovinsStemmer;
import com.sun.org.apache.xpath.internal.operations.Mod;


public class Preprocessing {

	private ArrayList<Document> documents;
	private HashSet<String> stoppingWords;

	public Preprocessing() {
		documents = new ArrayList<>();
		stoppingWords = new HashSet<>();
		addStoppingWords(new File("stop-words/stop-words_english_1_en.txt"));
		addStoppingWords(new File("stop-words/stop-words_english_2_en.txt"));
		addStoppingWords(new File("stop-words/stop-words_english_3_en.txt"));
		addStoppingWords(new File("stop-words/stop-words_english_4_en.txt"));
		addStoppingWords(new File("stop-words/stop-words_english_5_en.txt"));
		addStoppingWords(new File("stop-words/stop-words_english_6_en.txt"));
	}

	public void setDocuments(ArrayList<Document> docs) {
		documents = docs;
	}

    public void unsetWords()
    {
        documents = null;
        stoppingWords = null;
    }
	public void elemenateStoppingWords() {
		for (int i = 0; i < documents.size(); i++) {
            LinkedList<String> words = documents.get(i).getWords();
            ListIterator<String> wordIterator = words.listIterator();
            while (wordIterator.hasNext()) {
                String word = wordIterator.next();
                if(stoppingWords.contains(word))
                {
                    wordIterator.remove();
                }
            }
		}
	}
	public void eleminateShortWords() {
		for (int i = 0; i < documents.size(); i++) {
            LinkedList<String> words = documents.get(i).getWords();
            ListIterator<String> wordIterator = words.listIterator();
            while (wordIterator.hasNext()) {
                String word = wordIterator.next();
                if(word.length()< 3)
                {
                    wordIterator.remove();
                }

            }

		}
	}
	public ArrayList<Document> stemWithLovin(ArrayList<Document> docs) {
		IteratedLovinsStemmer ls = new IteratedLovinsStemmer();
		for (Document doc : docs) {
            LinkedList<String> words = doc.getWords();
            ListIterator<String> iter = words.listIterator(0);
            while(iter.hasNext()){
                String word = iter.next();
                if(!Model.getWordnet().inNoun(word))
                {
                    iter.set(ls.stem(word));
                }
            }
		}
		return docs;
	}
	public boolean addStoppingWords(File stopFile) {
		if (stopFile.canRead()) {
			try {
				Scanner scan = new Scanner(stopFile);
				while (scan.hasNext()) {
					String line = scan.nextLine();
					String[] words = line.split(" ");
					for (String word : words) {
						stoppingWords.add(word);
					}
				}
				scan.close();
				return true;
			} catch (FileNotFoundException e) {
                System.out.println("Cannot include stopwords file.");

				return false;
			}
		} else {
			return false;
		}
	}
	private static ArrayList<File> getFiles(String folderName) {
		File folder = new File(folderName);
		ArrayList<File> files = new ArrayList<File>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				getFiles(fileEntry.getName());
			} else {
				files.add(fileEntry);
			}
		}
		return files;
	}
	public ArrayList<Document> preprocessPhase1() { // before call this function it is a must to put
        // the documents in the documents array list
        // also the stopping words
		eleminateShortWords();
		elemenateStoppingWords();
		return stemWithLovin(documents);
	}

    /**
     * Perform tfidf and document enrichment.
     */
    public ArrayList<String> preprocessPhase2(ArrayList<DocumentTermFrequency> docs,
                                              HashMap<String, WordInfo> unifiedWordsInfoVector){
        //prune 1 on terms
        tfidf(docs, unifiedWordsInfoVector);

        //store hypernyms
        for(String word : unifiedWordsInfoVector.keySet())
        {

            unifiedWordsInfoVector.get(word).hypernyms = Model.getWordnet().getHypernyms(word);
        }

        //enrich adjusting unified info vector to include new hypernyms
        enrichDocument(docs, unifiedWordsInfoVector);

        //prune 2 on terms+hypernyms
        tfidf(docs,unifiedWordsInfoVector);

        //create an array of terms and return it
        return new ArrayList<>(unifiedWordsInfoVector.keySet());
    }

    private void enrichDocument(ArrayList<DocumentTermFrequency> docs,
                                               HashMap<String, WordInfo> unifiedWordsInfoVector)
    {

        //mantain a hypernym to make tfidf on it later
        HashMap<String, WordInfo> globalHypernyms = new HashMap<>();

        //now enrich each document with hypernyms and store the global freq
        for(DocumentTermFrequency d : docs)
        {
            for(String word : unifiedWordsInfoVector.keySet())
            {
                //get term frequency
                int tf = d.getWordFreqUnified(word);

                //if doesnt exist continue
                if(tf == 0)
                    continue;

                WordInfo wi = unifiedWordsInfoVector.get(word);
                //if exist enrich document by adding hypernym to it if not exist and increase its count
                for(String hypernym : wi.hypernyms)
                {
                    //enrich by adding hypernym to document
                    d.addTerm(hypernym, tf);

                    //ensure hypernyme exist in global hypers
                    WordInfo globalHyper = globalHypernyms.get(hypernym);

                    // if global hyper doesnt exist create it
                    if(globalHyper == null)
                    {
                        globalHyper = new WordInfo();
                        globalHypernyms.put(hypernym, globalHyper);
                    }
                    //add document to the hypernyme
                    globalHyper.docs.add(d);
                    //add frequency
                    globalHyper.freq += tf;
                }
            }
        }

        //adjust the unified info vector to include the new hypernyms
        for(String word : globalHypernyms.keySet())
        {
            //check if it exist in unified vector and if so add them to each other
            WordInfo wi = unifiedWordsInfoVector.get(word);
            if(wi == null)
            {
                //new just add this term to the unified vector
                unifiedWordsInfoVector.put(word, globalHypernyms.get(word));
            }
            else {
                //already there then update
                wi.addOther(globalHypernyms.get(word));
            }
        }

    }
    /**
     * Loop over all documents and calculate the tfidf in the first pass. In the second pass
     * prone terms that has tfidf less than (min+max)/2 of the document. Also remove terms
     * from the global array when they no longer exist in any document
     * @param docs
     * @param globals
     */
    public void tfidf(ArrayList<DocumentTermFrequency> docs,HashMap<String, WordInfo> globals)
    {
        //loop over each document
        for(DocumentTermFrequency d : docs)
        {
            double min = Double.MAX_VALUE; double max = Double.MIN_VALUE;
            //first loop to get min and max
            for(String word : globals.keySet())
            {
                WordInfo wordInfo = globals.get(word);
                //get tf
                int tf = d.getWordFreqUnified(word);

                //if word doesnt exist in document or it have frequency 0
                //then do nothing and continue
                if(tf == 0)
                {
                    continue;
                }

                //word exist calculate its tfidf
                double tfidf = 0.5 + (0.5* (((double)tf)/d.getMaxTermFrequency()) *
                        (Math.log(1+ (((double)docs.size())/wordInfo.docs.size())) ));

                //update minmax
                if(tfidf < min)
                {
                    min = tfidf;
                }
                if(tfidf > max)
                {
                    max = tfidf;
                }


            }

            double threshold = 0.65;

            //anther loop to prune
            Iterator<Map.Entry<String,WordInfo>> wordIterator = globals.entrySet().iterator();
            while(wordIterator.hasNext())
            {
                Map.Entry<String, WordInfo> entry = wordIterator.next();
                WordInfo wordInfo = entry.getValue();
                //get tf
                int tf = d.getWordFreqUnified(entry.getKey());
                //if word doesnt exist in document
                if(tf == 0)
                {
                    continue;
                }

                //word exist calculate its tfidf
                double tfidf = 0.5 + (0.5* (((double)tf)/d.getMaxTermFrequency()) *
                        (Math.log(1+ (((double)docs.size())/wordInfo.docs.size())) ));

                //if less than threshold remove term from document and decrease global count
                if(tfidf < threshold) {
                    wordInfo.freq -= d.removeTerm(entry.getKey());
                    //if no more of this word exist in all documents then remove it from globals
                    if(wordInfo.freq <= 0)
                    {
                        wordIterator.remove();
                    }
                }
            }
        }
    }




}
