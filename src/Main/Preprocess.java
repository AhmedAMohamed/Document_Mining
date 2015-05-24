package Main;

import Models.DocumentTermFrequency;
import Models.WordInfo;
import StemmingAlgorithms.IteratedLovinsStemmer;
import Utility.Watch;

import java.io.*;
import java.util.*;


public class Preprocess {
    private static ArrayList<DocumentTermFrequency> _documents;
    private static HashMap<String, WordInfo> _wordsVector;
    private static HashSet<String> stoppingWords = null;

    /**
     * Apply preprocessing to the data set and return output to the given arguments.
     *
     * @param files         The files containing the data set
     * @param documents     document object that will get the result after preprocessing
     * @param wordsVector   map of wards and its info that will get the unified words vector
     */
    public static void apply(ArrayList<File> files, ArrayList<DocumentTermFrequency> documents,
                             HashMap<String, WordInfo> wordsVector) {


        setupStoppingWords();

        Watch.lapBegin();
        applyPhas1(files, documents, wordsVector);
        Watch.lapStop("extracting words and stemming");

        applyPhas2(documents, wordsVector);


        _documents = documents;
        _wordsVector = wordsVector;
    }

    /**
     * Write the result of the prerpocessing in a file as a comma sepearated Document-TermFrequency matrix with the
     * head row containing the unified words, and the head column contains the document names.
     */
    public static void saveToFile() {
        try {
            writeBuffered(8192);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Setup the set containing all the stopping words.
     */
    private static void setupStoppingWords() {
        stoppingWords = new HashSet<>();
        addStoppingWords(new File(Algorithm.MAIN_DIRECTORY + "stop-words/stop-words_english_1_en.txt"));
        addStoppingWords(new File(Algorithm.MAIN_DIRECTORY + "stop-words/stop-words_english_2_en.txt"));
        addStoppingWords(new File(Algorithm.MAIN_DIRECTORY + "stop-words/stop-words_english_3_en.txt"));
        addStoppingWords(new File(Algorithm.MAIN_DIRECTORY + "stop-words/stop-words_english_4_google_en.txt"));
        addStoppingWords(new File(Algorithm.MAIN_DIRECTORY + "stop-words/stop-words_english_5_en.txt"));
        addStoppingWords(new File(Algorithm.MAIN_DIRECTORY + "stop-words/stop-words_english_6_en.txt"));

    }

    /**
     * Add stopping words froma file to the stopping words set.
     *
     * @param stopFile   the file containing the stopping words as space separated values
     */
    private static void addStoppingWords(File stopFile) {
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
        } catch (FileNotFoundException e) {
            System.out.println("Cannot include stopwords file.");

        }
    }

    /**
     * Write preprocessed output to a file as a matrix of Documents & Termfreq.
     * @param bufSize       buffer size of the writer object
     * @throws IOException
     */
    private static void writeBuffered(int bufSize) throws IOException {

        FileWriter writer = null;
        try {
            writer = new FileWriter(Algorithm.MAIN_DIRECTORY + "preprocess_output");
            BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);

            System.out.print("Writing buffered (buffer size: " + bufSize + ")... ");
            write(bufferedWriter);
        } finally {
            // comment this out if you want to inspect the files afterward
            if(writer != null)
                writer.close();
        }
    }

    /**
     * Write preprocessed output to a file as a matrix of Documents & Termfreq.
     * @param writer     writer to write to
     * @throws IOException
     */
    private static void write(Writer writer) throws IOException {
        long start = System.currentTimeMillis();

        writer.write(_documents.size() + "," + _wordsVector.size());
        writer.write("\n");
        for (String record: _wordsVector.keySet()) {
            writer.write(record + ",");
        }
        for (DocumentTermFrequency d: _documents) {
            writer.write("\n");
            writer.write(d.getName());
            for (String record: _wordsVector.keySet()) {
                writer.write("," + d.getWordFreq(record));
            }
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000f + " seconds");
    }


    /**
     * Apply phase 1 of preprocessing [stop words & stemming].
     * <p>
     * We read the document and while reading each word we execlude it if short or a stop word, we stem it, then we
     * execlude it again if it is short after stemming. We then add the word to the unified words vector.
     *
     * @param files         files containing the data set
     * @param documents     document object that will get the result after preprocessing
     * @param wordsVector   map of wards and its info that will get the unified words vector
     */
    private static void applyPhas1(ArrayList<File> files, ArrayList<DocumentTermFrequency> documents,
                                   HashMap<String, WordInfo> wordsVector){

        IteratedLovinsStemmer ls = new IteratedLovinsStemmer();
        for (File file : files) {
            DocumentTermFrequency d = new DocumentTermFrequency(file.getName());
            try {
                // open the document
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file)));
                String line;

                //read each line
                while ((line = br.readLine()) != null) {
                    //split line by spaces
                    List<String> extracted = Arrays.asList(line.split("\\s+"));

                    // go over each word in line
                    for(String w: extracted)
                    {
                        //keep letters only
                        String word =w.replaceAll("[^A-Za-z]+", "");

                        
                        //if short ignore
                        if(word.length() < 3)
                        {
                            continue;
                        }
                        
                        //if stop word ignore
                        if(stoppingWords.contains(word))
                        {
                            continue;

                        }

                        //stem and add
                        word = ls.stem(word);
                      
                        //if short ignore
                        if(word.length() < 3)
                        {
                            continue;
                        }
                        
                        d.addTerm(word);

                        
                        //update wordsVector
                        WordInfo globalValue = wordsVector.get(word);
                        // if global word doesnt exist create it
                        if(globalValue == null)
                        {
                            globalValue = new WordInfo();
                            wordsVector.put(word, globalValue);
                        }
                        //add frequency
                        globalValue.incrementFrequency();
                        //add document
                        globalValue.addDocument(d);


                    }
                }
                documents.add(d);
            } catch (IOException t) {
                System.out.println("Couldn't read from file : "
                        + file.getAbsolutePath());
            }
        }
    }


    /**
     * Apply phase 2 [tfidf, enrichment & another tfidf].
     * @param documents     document object that will get the result after preprocessing
     * @param wordsVector   map of wards and its info that will get the unified words vector
     */
    private static void applyPhas2(ArrayList<DocumentTermFrequency> documents,
                                              HashMap<String, WordInfo> wordsVector){

    	System.out.println("vector size:  " + wordsVector.size() + " words.");

        //prune 1 on terms
        Watch.lapBegin();
        tfidf(documents, wordsVector);
        Watch.lapStop("1st tfidf");
        System.out.println("vector size:  " + wordsVector.size() + " words.");

        // get hypernyms of all words
        Watch.lapBegin();
        for(String s : wordsVector.keySet())
        {
            wordsVector.get(s).setHypernyms(Algorithm.getWordnet().getHypernyms(s));
        }
        Watch.lapStop("getting hypernyms");
        
        //enrich adjusting unified info vector to include new hypernyms
        Watch.lapBegin();
        enrichDocument(documents, wordsVector);
        Watch.lapStop("enrichment");
        System.out.println("vector size:  " + wordsVector.size() + " words.");


        //prune 2 on terms+hypernyms
        Watch.lapBegin();
        tfidf(documents, wordsVector);
        Watch.lapStop("2nd tfidf");
        System.out.println("vector size:  " + wordsVector.size() + " words.");
    }


    /**
     * Enrich the documents by getting the hypernyms of each word and adding it to the documents to find hidden
     * similarities.
     *
     * @param documents     document object that will get the result after preprocessing
     * @param wordsVector   map of wards and its info that will get the unified words vector
     */
    private static void enrichDocument(ArrayList<DocumentTermFrequency> documents,
                                HashMap<String, WordInfo> wordsVector)
    {

        // create an array of old wordsVector to loop over and extract hypernyms
        // we use a new array to be able to add the the hashmap while iterating
        // and to not get the hypernyms of an added hypernym
        String[] original = wordsVector.keySet().toArray(new String[wordsVector.size()]);

        //now enrich each document with hypernyms and store the global freq
        for(DocumentTermFrequency d : documents)
        {

            for(String word : original)
            {
                //get term frequency
                int tf = d.getWordFreq(word);

                //if doesnt exist continue
                if(tf == 0)
                    continue;

                WordInfo wi = wordsVector.get(word);
                //if exist enrich document by adding hypernym to it if not exist and increase its count
                for(String hypernym : wi.getHypernyms())
                {
                    //enrich by adding hypernym to document
                    d.addTerm(hypernym, tf);

                    //ensure hypernyme exist in global hypers
                    WordInfo globalHyper = wordsVector.get(hypernym);

                    // if global hyper doesnt exist create it
                    if(globalHyper == null)
                    {
                        globalHyper = new WordInfo();
                        wordsVector.put(hypernym, globalHyper);
                    }
                    //add document to the hypernym
                    globalHyper.addDocument(d);
                    //set frequency
                    globalHyper.incrementFrequency(tf);
                }
            }
        }


    }

    /**
     * Loop over all documents and remove the terms that have tfidf less than the threshold
     * {@link Main.Algorithm#TFIDF_THRESHOLD} and adjust the unified words vector as needed by decreementing freequency
     * when removing a word from a document and removing a word completly from the word vector when the word no longer
     * exists in any document.
     *
     * @param documents     document object that will get the result after preprocessing
     * @param wordsVector   map of wards and its info that will get the unified words vector
     */
    private  static void tfidf(ArrayList<DocumentTermFrequency> documents,HashMap<String, WordInfo> wordsVector)
    {
        //loop over each document
        for(DocumentTermFrequency d : documents)
        {
            double threshold = Algorithm.TFIDF_THRESHOLD;

            //loop over words vector
            Iterator<Map.Entry<String, WordInfo>> wordIterator = wordsVector.entrySet().iterator();
            while(wordIterator.hasNext())
            {
                Map.Entry<String, WordInfo> entry = wordIterator.next();
                WordInfo wordInfo = entry.getValue();

                //get tf
                int tf = d.getWordFreq(entry.getKey());
                //if word doesnt exist in document
                if(tf == 0)
                {
                    continue;
                }

                //word exist calculate its tfidf
                double tfidf = 0.5 + (0.5* (((double)tf)/d.getMaxTermFrequency()) *
                        (Math.log(1+ (((double)documents.size())/wordInfo.getDocumentsSize())) ));

                //if less than threshold remove term from document and decrease global count
                if(tfidf < threshold) {
                    //update global (removing document and deccreasing freq)
                     wordInfo.removeDocument(d, tf);

                    //remove from document
                    d.removeTerm(entry.getKey());

                    //if no more of this word exist in all documents then remove it from globals
                    if(wordInfo.getFrequency() <= 0)
                    {
                       wordIterator.remove();
                    }
                }
            }
        }
    }

}
