package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.print.Doc;

public class Preprocessing {
	private ArrayList<Document> documents;
	private ArrayList<String> stoppingWords;

	public Preprocessing() {
		System.out.println("hhh");
		documents = new ArrayList<Document>();
		stoppingWords = new ArrayList<String>();
		ArrayList<File> files = getFiles("stop-words");
		for(File file : files) {
			addStoppingWords(file);
		}
	}
	public Preprocessing(ArrayList<Document> doc, ArrayList<String> stopping) {
		documents = (ArrayList<Document>) doc;
		stoppingWords = (ArrayList<String>) stopping;
		
	}
	public void setDocuments(ArrayList<Document> docs) {
		documents = docs;
	}
	public void elemenateStoppingWords() {
		ArrayList<Document> docs = new ArrayList<Document>();
		for (int i = 0; i < documents.size(); i++) {
			Document temp = new Document();
			temp.setDocumentName(documents.get(i).getDocumentName());
			for (int j = 0; j < stoppingWords.size(); j++) {
				for (int k = 0; k < documents.get(i).getWords().size(); k++) {
					if (stoppingWords.get(j).equalsIgnoreCase(
							documents.get(i).getWords().get(k))) {
						documents.get(i).getWords().remove(k);
					}
				}
			}
		}
	}
	public void eleminateShortWords() {
		for (int i = 0; i < documents.size(); i++) {
			for (int j = 0; j < documents.get(i).getWords().size(); j++) {
				if (documents.get(i).getWords().get(j).length() < 3) {
					documents.get(i).getWords().remove(j);
				}
			}
		}
	}
	public ArrayList<Document> stemWithLovin(ArrayList<Document> docs) {
		ArrayList<Document> stemmedDocs = new ArrayList<Document>();
		IteratedLovinsStemmer ls = new IteratedLovinsStemmer();
		for (int count = 0; count < docs.size(); count++) {
			Document doc = new Document();
			doc.setDocumentName(docs.get(count).getDocumentName());
			for (int i = 0; i < docs.get(count).getWords().size(); i++) {
				doc.getWords().add(ls.stem(docs.get(count).getWords().get(i)));
			}
			stemmedDocs.add(doc);
		}
		return stemmedDocs;
	}
	public void addDocument(Document doc) {
		documents.add(doc);
	}
	public void addStoppingWords(ArrayList<String> words) {
		stoppingWords = words;
	}
	public boolean addStoppingWords(File stopFile) {
		if (stopFile.canRead()) {
			try {
				System.out.println("try");
				Scanner scan = new Scanner(stopFile);
				while (scan.hasNext()) {
					String line = scan.nextLine();
					System.out.println(line);
					String[] words = line.split(" ");
					for (String word : words) {
						stoppingWords.add(word);
					}
				}
				scan.close();
				return true;
			} catch (FileNotFoundException e) {
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
				new ArrayList<String>());
		Document doc = new Document();
		doc.setDocumentName("doc 1");
		ArrayList<String> words = new ArrayList<String>();
		words.add("compute");
		words.add("computer");
		words.add("computing");
		words.add("computed");
		doc.setWords(words);
		x.addDocument(doc);
		ArrayList<String> stop = new ArrayList<String>();
		stop.add("a");
		stop.add("the");
		x.addStoppingWords(stop);
		x.elemenateStoppingWords();
		x.printDocument();
	}
	public static void simpleExtractStoppingWordsFromFilesTest(
			String directoryName) {
		Preprocessing x = new Preprocessing(new ArrayList<Document>(),
				new ArrayList<String>());
		ArrayList<File> files = getFiles(directoryName);
		for (File file : files) {
			x.addStoppingWords(file);
		}
		x.printStoppingWords();
	}
	public static HashMap<String, HashMap<String,Integer>> getFrequentItems(ArrayList<Document> docs) {
		synchronized (docs) {
			HashMap<String,HashMap<String,Integer>> freqentItems = new HashMap<String, HashMap<String,Integer>>();
			for(Document doc : docs) {
				HashMap<String, Integer> wordFreq = new HashMap<>();
				for(String word : doc.getWords()) {
					if(wordFreq.containsKey(word)) {
						wordFreq.put(word, wordFreq.get(word)+1);
					}
					else {
						wordFreq.put(word, 1);
					}
				}
				freqentItems.put(doc.getDocumentName(), wordFreq);
			}
			return freqentItems;
		}
	}
	public static HashMap<String, HashMap<String,Integer>> getFrequentItemsFirstCut(int frequentCut, HashMap<String,HashMap<String,Integer>> freqentItems) {
		for(Entry<String, HashMap<String, Integer>> entry : freqentItems.entrySet()) {
			for (Map.Entry<String, Integer> it : entry.getValue().entrySet()) {
				if(it.getValue() <= frequentCut) {
					
				}
			}
		}
		return freqentItems;
	}
	public static void main(String []args) {
		Preprocessing x = new Preprocessing();
		
		Document doc = new Document();
		doc.setDocumentName("doc 1");
		ArrayList<String> words = new ArrayList<String>();
		words.add("the");
		words.add("computer");
		words.add("computing");
		words.add("computed");
		doc.setWords(words);
		x.addDocument(doc);
		ArrayList<Document> y = x.preprocess();
		for(Document t : y) {
			System.out.println(t);
		}
	}
}
