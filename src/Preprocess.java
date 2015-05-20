import StemmingAlgorithms.IteratedLovinsStemmer;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


public class Preprocess {
    private static ArrayList<DocumentTermFrequency> _documents;
    private static HashMap<String, WordInfo> _wordsVector;
    private static HashSet<String> stoppingWords = null;

    public static void apply(ArrayList<File> files, ArrayList<DocumentTermFrequency> documents,
                             HashMap<String, WordInfo> wordsVector) {


        setupStoppingWords();

        applyPhas1(files, documents, wordsVector);
        applyPhas2(documents, wordsVector);
        _documents = documents;
        _wordsVector = wordsVector;
    }

    private static void setupStoppingWords() {
        stoppingWords = new HashSet<>();
        addStoppingWords(new File(Algorithm.mainDirectory + "stop-words/stop-words_english_1_en.txt"));
        addStoppingWords(new File(Algorithm.mainDirectory + "stop-words/stop-words_english_2_en.txt"));
        addStoppingWords(new File(Algorithm.mainDirectory + "stop-words/stop-words_english_3_en.txt"));
        addStoppingWords(new File(Algorithm.mainDirectory + "stop-words/stop-words_english_4_google_en.txt"));
        addStoppingWords(new File(Algorithm.mainDirectory + "stop-words/stop-words_english_5_en.txt"));
        addStoppingWords(new File(Algorithm.mainDirectory + "stop-words/stop-words_english_6_en.txt"));

    }

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

    public static void writeBuffered(int bufSize) throws IOException {

        FileWriter writer = null;
        try {
            writer = new FileWriter(Algorithm.mainDirectory + "preprocess_output");
            BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);

            System.out.print("Writing buffered (buffer size: " + bufSize + ")... ");
            write(bufferedWriter);
        } finally {
            // comment this out if you want to inspect the files afterward
            if(writer != null)
                writer.close();
        }
    }

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
    public static void saveToFile() {
        try {
            writeBuffered(8192);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private static void applyPhas1(ArrayList<File> files, ArrayList<DocumentTermFrequency> documents,
                                   HashMap<String, WordInfo> wordsVector){
        IteratedLovinsStemmer ls = new IteratedLovinsStemmer();
        for (File file : files) {
            DocumentTermFrequency d = new DocumentTermFrequency(file.getName());
            try {

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file)));
                String line;
                while ((line = br.readLine()) != null) {
                    List<String> extracted = Arrays.asList(line.split("\\s+"));
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
                        globalValue.docs.add(d);


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
     * Perform tfidf and document enrichment.
     */
    private static void applyPhas2(ArrayList<DocumentTermFrequency> docs,
                                              HashMap<String, WordInfo> globalWords){
        long start = System.currentTimeMillis();
        //prune 1 on terms
        tfidf(docs, globalWords);
        System.out.println("tfidf 1: " + (System.currentTimeMillis() - start)/1000.0);

        start = System.currentTimeMillis();
        for(String s : globalWords.keySet())
        {
            globalWords.get(s).hypernyms = Algorithm.getWordnet().getHypernyms(s);
        }
        System.out.println("wordnet: " + (System.currentTimeMillis() - start)/1000.0);

        start = System.currentTimeMillis();
        //enrich adjusting unified info vector to include new hypernyms
        enrichDocument(docs, globalWords);
        System.out.println("enrich: " + (System.currentTimeMillis() - start)/1000.0);

        start = System.currentTimeMillis();
        //prune 2 on terms+hypernyms
        tfidf(docs,globalWords);
        System.out.println("tfidf 2: " + (System.currentTimeMillis() - start)/1000.0);

    }

    private static void enrichDocument(ArrayList<DocumentTermFrequency> docs,
                                HashMap<String, WordInfo> globalWords)
    {

        String[] original = globalWords.keySet().toArray(new String[globalWords.size()]);

        //now enrich each document with hypernyms and store the global freq
        for(DocumentTermFrequency d : docs)
        {

            for(String word : original)
            {
                //get term frequency
                int tf = d.getWordFreq(word);

                //if doesnt exist continue
                if(tf == 0)
                    continue;

                WordInfo wi = globalWords.get(word);
                //if exist enrich document by adding hypernym to it if not exist and increase its count
                for(String hypernym : wi.hypernyms)
                {
                    //enrich by adding hypernym to document
                    d.addTerm(hypernym, tf);

                    //ensure hypernyme exist in global hypers
                    WordInfo globalHyper = globalWords.get(hypernym);

                    // if global hyper doesnt exist create it
                    if(globalHyper == null)
                    {
                        globalHyper = new WordInfo();
                        globalWords.put(hypernym, globalHyper);
                    }
                    //add document to the hypernyme
                    globalHyper.docs.add(d);
                    //add frequency
                    globalHyper.incrementFrequency(tf);
                }
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
    private  static void tfidf(ArrayList<DocumentTermFrequency> docs,HashMap<String, WordInfo> globals)
    {
        int dc = 0;
        //loop over each document
        for(DocumentTermFrequency d : docs)
        {
            dc++;
            double threshold = Algorithm.tfidfThreshold;

            int wc = 0;
            //anther loop to prune
            Iterator<Map.Entry<String,WordInfo>> wordIterator = globals.entrySet().iterator();
            while(wordIterator.hasNext())
            {
                wc++;
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
                        (Math.log(1+ (((double)docs.size())/wordInfo.docs.size())) ));

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
