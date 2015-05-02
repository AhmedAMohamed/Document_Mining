package Model;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import Model.DataContainer.Document;
import Model.StemmingAlgorithms.IteratedLovinsStemmer;


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
	public Preprocessing(ArrayList<Document> doc, HashSet<String> stopping) {
		documents = doc;
		stoppingWords =  stopping;
	}
	public void setDocuments(ArrayList<Document> docs) {
		documents = docs;
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
	public void addDocument(Document doc) {
		documents.add(doc);
	}
	public void addStoppingWords(HashSet<String> words) {
		stoppingWords = words;
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
	public void printDocument() {
		for (Document doc : documents) {
			System.out.println(doc);
		}
	}
	public void printStoppingWords() {
		for (String word : stoppingWords) {
			System.out.println(word);
		}
	}
	public ArrayList<Document> preprocess() { // before call this function it is a must to put
								// the documents in the documents array list
								// also the stopping words
		eleminateShortWords();
		elemenateStoppingWords();
		/* stemming using porter
		HashMap<String, File> filesToStem = getFiles();
		HashMap<String, File> filesAfterStemming = Stemmer
				.getStemedFiles(filesToStem);
		 */
		return stemWithLovin(documents);
	}
	@SuppressWarnings("unused")
	private HashMap<String, File> getFiles() {
		HashMap<String, File> filesToStem = new HashMap<String, File>();
		for (int i = 0; i < documents.size(); i++) {
			File file = new File("/doc-after-stopping-words/"
					+ documents.get(i).getDocumentName());
			try {
				PrintWriter writer = new PrintWriter(file);
				for (int j = 0; j < documents.get(i).getWords().size(); j++) {
					writer.println(documents.get(i).getWords().get(j));
				}
				writer.close();
			} catch (FileNotFoundException e) {

			}
			filesToStem.put(documents.get(i).getDocumentName(), file);
		}
		return filesToStem;
	}
	public static void simpleEleminateStopWordsTest() {
		Preprocessing x = new Preprocessing(new ArrayList<Document>(),
				new HashSet<String>());
		Document doc = new Document();
		doc.setDocumentName("doc 1");
        LinkedList<String> words = new LinkedList<>();
		words.add("compute");
		words.add("computer");
		words.add("computing");
		words.add("computed");
		doc.setWords(words);
		x.addDocument(doc);
        HashSet<String> stop = new HashSet<String>();
		stop.add("a");
		stop.add("the");
		x.addStoppingWords(stop);
		x.elemenateStoppingWords();
		x.printDocument();
	}
	public static void simpleExtractStoppingWordsFromFilesTest(
			String directoryName) {
		Preprocessing x = new Preprocessing(new ArrayList<Document>(),
				new HashSet<String>());
		ArrayList<File> files = getFiles(directoryName);
		for (File file : files) {
			x.addStoppingWords(file);
		}
		x.printStoppingWords();
	}
	public static void main(String []args) {
		Preprocessing sample = new Preprocessing(new ArrayList<Document>(),new HashSet<String>());
		Document doc = new Document();
		doc.setDocumentName("doc 1");
        LinkedList<String> words = new LinkedList<>();
		words.add("compute");
		words.add("computer");
		words.add("computing");
		words.add("computed");
		doc.setWords(words);
		sample.addDocument(doc);
		Document doc1 = new Document();
		doc1.setDocumentName("doc 2");
        LinkedList<String> words1 = new LinkedList<>();
		words1.add("compute");
		words1.add("computer");
		words1.add("computing");
		words1.add("computed");
		doc1.setWords(words1);
		sample.addDocument(doc1);
		HashSet<String> stop = new HashSet<String>();
		stop.add("a");
		stop.add("compute");
		sample.addStoppingWords(stop);
		ArrayList<Document> y = sample.preprocess();
		for(Document t : y) {
			System.out.println(t);
		}
	}
}
