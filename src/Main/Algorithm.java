package Main;

import Main.Fuzzy.FuzzyMining;
import Models.Cluster;
import Models.DocumentTermFrequency;
import Models.WordInfo;
import Models.Wordnet;
import Utility.Watch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Algorithm {

    public static double TFIDF_THRESHOLD = 0.8;
    public static int HYPERNYM_LEVELS = 3;
    public static double MIN_SUPPORT = 0.20;
    public static double MIN_CONFIDENCE= 0.7;
    public static double MIN_INTER_SIMILARITY = 0.5;

    public static  String DOCUMENT_DIRECTORY = "classic";
    public static String MAIN_DIRECTORY = System.getProperty("user.dir")+"/";


    private static ArrayList<DocumentTermFrequency> documents;
    private static HashMap<String, WordInfo> wordsVector;
    private static Wordnet wordnet;

    /**
     * Run the algorithm.
     */
    public static void run() throws IOException {
        Watch.start();

        //initialize data holders
        wordsVector = new HashMap<>();
        documents = new ArrayList<>();

        // preprocessing
        System.out.println("\n\n---------------- Preprocessing ----------------\n");
        Watch.lapBegin();
        Preprocess.apply(getFiles(MAIN_DIRECTORY + DOCUMENT_DIRECTORY), documents, wordsVector);
        //Preprocess.saveToFile();
        Watch.lapStop("preprocessing");

        // fuzzy mining
        System.out.println("\n\n---------------- Fuzzy Mining ----------------\n");
        Watch.lapBegin();
        //update word vector info used by fuzzy membership
        for(String w: wordsVector.keySet())
        {
            wordsVector.get(w).updateMinMaxAvg(w, wordsVector.size());
        }
        
        ArrayList<Cluster> candidateCluster = FuzzyMining.mineFrequentItemSets(documents, wordsVector);
        Watch.lapStop("fuzzy mining");

        
        ////////////////////////
        System.out.println("word vector size: " + wordsVector.size());
        System.out.println("Candidae clusters size: " + candidateCluster.size());
        ///////////////////////
        // Clustering
        System.out.println("\n\n---------------- Clustering ----------------\n");
        Watch.lapBegin();
        ArrayList<Cluster> clusters = Clustering.cluster(documents, wordsVector, candidateCluster);
        Watch.lapStop("clustering");

        Watch.stop("running the algorithm");

        System.out.println(clusters.size());
        for (Cluster c : clusters) {
        	System.out.println(c.getDocs().size());
        }
    }



    /**
     * Run the algorithm with custom parameters dynamically.
     *
     * @param tfidf_threshold        Threshold used to cut terms in tfidf process
     * @param hypernym_levels        number of hypernym levels used in enrichment process
     * @param min_support            Minimum support used in frequent item sets mining
     * @param min_inter_similarity   Minimum internal similarity between clusters used in merging similar clusters
     */
    public static void run(double tfidf_threshold, int hypernym_levels, double min_support,
                           double min_inter_similarity) throws IOException {

        //set settings
        TFIDF_THRESHOLD = tfidf_threshold;
        HYPERNYM_LEVELS = hypernym_levels;
        MIN_SUPPORT = min_support;
        MIN_INTER_SIMILARITY = min_inter_similarity;

        //then run
        run();
    }

    /**
     * Create wordnet object and cache it for further usage.
     *
     * @return  wordnet object
     */
    public static Wordnet getWordnet(){
        if(wordnet == null)
        {
            wordnet = new Wordnet(true);
        }
        return wordnet;
    }


    /**
     * Close the wordnet object if it is opened.
     */
    public static void closeWordnet(){
        if(wordnet != null)
            wordnet.close();
    }

    /**
     * Get all files found in a directory.
     *
     * @param directory   path to a folder that contains files to read
     * @return ArrayList of Files found in the supplied path
     */
    private static ArrayList<File> getFiles(String directory) {
        File folder = new File(directory);
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
}