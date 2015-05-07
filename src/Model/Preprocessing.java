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
		ArrayList<Document> stemmedDocs = new ArrayList<>();
		IteratedLovinsStemmer ls = new IteratedLovinsStemmer();
		for (int count = 0; count < docs.size(); count++) {
			Document doc = new Document();
			doc.setDocumentName(docs.get(count).getDocumentName());
            LinkedList<String> words = documents.get(count).getWords();
            for(String word : words){
                doc.getWords().add(ls.stem(word));

            }
            stemmedDocs.add(doc);
		}
		return stemmedDocs;
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
                                              ArrayList<WordInfo> unifiedWordsInfoVector){
        //prune 1 on terms
        tfidf(docs, unifiedWordsInfoVector);

        //store hypernyms
        for(WordInfo wi : unifiedWordsInfoVector)
        {
            wi.hypernyms = Model.getWordnet().getHypernyms(wi.word);
        }

        //enrich
        ArrayList<WordInfo> unifiedHypernymsInfoVector = enrichDocument(docs, unifiedWordsInfoVector);

        //prune 2 on hypernyms
        tfidf(docs,unifiedHypernymsInfoVector);


        HashSet<String> unifiedWordsVector = new HashSet<>(unifiedHypernymsInfoVector.size()+
                    unifiedWordsInfoVector.size());

        //add terms
        for(WordInfo wi: unifiedWordsInfoVector)
        {
            unifiedWordsVector.add(wi.word);
        }
        //add hypernyms
        for(WordInfo wi: unifiedHypernymsInfoVector)
        {
            unifiedWordsVector.add(wi.word);
        }

        return new ArrayList<>(unifiedWordsVector);
    }

    private ArrayList<WordInfo> enrichDocument(ArrayList<DocumentTermFrequency> docs,
                     ArrayList<WordInfo> unifiedWordsInfoVector)
    {
        class Value{
            public HashSet<DocumentTermFrequency> set;
            public int freq;
            public Value()
            {
                set = new HashSet<>();
                freq = 0;
            }
        }
        //mantain a hypernym to make tfidf on it later
        HashMap<String, Value> globalHypernyms = new HashMap<>();

        //now enrich each document with hypernyms and store the global freq
        for(DocumentTermFrequency d : docs)
        {
            for(WordInfo wi : unifiedWordsInfoVector)
            {
                //get term frequency
                int tf = d.getWordFreqUnified(wi.word);

                //if doesnt exist continue
                if(tf == 0)
                    continue;

                //if exist enrich document by adding hypernym to it if not exist and increase its count
                for(String hypernym : wi.hypernyms)
                {
                    //enrich by adding hypernym to document
                    d.addTerm(hypernym, tf);

                    //ensure hypernyme exist in global hypers
                    Value globalHyper = globalHypernyms.get(hypernym);

                    // if global hyper doesnt exist create it
                    if(globalHyper == null)
                    {
                        globalHyper = new Value();
                        globalHypernyms.put(hypernym, globalHyper);
                    }
                    //add document to the hypernyme
                    globalHyper.set.add(d);
                    //add frequency
                    globalHyper.freq += tf;
                }
            }
        }

        ArrayList<WordInfo> unifiedHypernymsInfoVector = new ArrayList<>();
        //now create the unified vector of hypernymes with detailed info
        for(String word : globalHypernyms.keySet())
        {
            Value v = globalHypernyms.get(word);
            unifiedHypernymsInfoVector.add(new WordInfo(word, v.set.size(), v.freq));

        }

        return unifiedHypernymsInfoVector;
    }
    /**
     * Loop over all documents and calculate the tfidf in the first pass. In the second pass
     * prone terms that has tfidf less than (min+max)/2 of the document. Also remove terms
     * from the global array when they no longer exist in any document
     * @param docs
     * @param globals
     */
    public void tfidf(ArrayList<DocumentTermFrequency> docs, ArrayList<WordInfo> globals)
    {
        //loop over each document
        for(DocumentTermFrequency d : docs)
        {
            double min = Double.MAX_VALUE; double max = Double.MIN_VALUE;
            //first loop to get min and max
            for(WordInfo wordInfo : globals)
            {
                //get tf
                int tf = d.getWordFreqUnified(wordInfo.word);

                //if word doesnt exist in document or it have frequency 0
                //then do nothing and continue
                if(tf == 0)
                {
                    continue;
                }

                //word exist calculate its tfidf
                double tfidf = 0.5 + (0.5* (tf/d.getMaxTermFrequency()) *
                        (Math.log(1+ (docs.size()/wordInfo.df)) ));

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

            double threshold = (min+max)/2.0;

            //anther loop to prune
            Iterator<WordInfo> wordIterator = globals.iterator();
            while(wordIterator.hasNext())
            {
                WordInfo wordInfo = wordIterator.next();
                //get tf
                int tf = d.getWordFreqUnified(wordInfo.word);
                //if word doesnt exist in document
                if(tf == 0)
                {
                    continue;
                }

                //word exists calculate its tfidf
                double tfidf = 0.5 + (0.5* (tf/d.getMaxTermFrequency()) *
                        (Math.log(1+ (docs.size()/wordInfo.df)) ));

                //if less than threshold remove term from document and decrease global count
                if(tfidf < threshold) {
                    wordInfo.freq -= d.removeTerm(wordInfo.word);
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
