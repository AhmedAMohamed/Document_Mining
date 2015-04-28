package Model;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class WordExtractor {
	private ArrayList<Document> documents;
	private ArrayList<String> stoppingWords;
	
	public WordExtractor() {
		documents = new ArrayList<Document>();
		stoppingWords = new ArrayList<String>();
		addStoppingWords(new File("stop-words"));
	}
	public WordExtractor(ArrayList<Document> doc, ArrayList<String> stopping) {
		documents = (ArrayList<Document>) doc;
		stoppingWords = (ArrayList<String>) stopping;
	}
	public void setDocuments (ArrayList<Document> docs) {
		documents = docs;
	}
	public void elemenateStoppingWords() {
		ArrayList<Document> docs = new ArrayList<Document>();
		for(int i = 0; i < documents.size(); i++) {
			Document temp = new Document();
			temp.setDocumentName(documents.get(i).getDocumentName());
			for(int j = 0; j < stoppingWords.size(); j++) {
				for(int k = 0; k < documents.get(i).getWords().size(); k++) {
					if(stoppingWords.get(j).equalsIgnoreCase(documents.get(i).getWords().get(k))) {
						documents.get(i).getWords().remove(k);
					}
				}
			}
		}
	}
	public void eleminateShortWords() {
		for(int i = 0; i < documents.size(); i++) {
			for(int j = 0; j < documents.get(i).getWords().size(); j++) {
				if(documents.get(i).getWords().get(j).length() < 3) {
					documents.get(i).getWords().remove(j);
				}
			}
		}
	}
	public void addDocument(Document doc) {
		documents.add(doc);
	}
	public void addStoppingWords(ArrayList<String> words) {
		stoppingWords = words;
	}
	public boolean addStoppingWords(File stopFile) {
		if(stopFile.canRead()) {
			try {
				Scanner scan = new Scanner(stopFile);
				while(scan.hasNext()) {
					String line = scan.nextLine();
					String [] words = line.split(" ");
					for(String word : words) {
						stoppingWords.add(word);
					}
				}
				scan.close();
				return true;
			} catch (FileNotFoundException e) {
				return false;
			}
		}
		else {
			return false;
		}
	}
	private static ArrayList<File> getFiles(String folderName) {
		File folder = new File(folderName);
		ArrayList<File> files = new ArrayList<File>();
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	getFiles(fileEntry.getName());
	        } 
	        else {
	            files.add(fileEntry);
	        }
	    }
		return files;
	}
	public void printDocument() {
		for(Document doc :documents) {
			System.out.println(doc);
		}
	}
	public void printStoppingWords() {
		for(String word : stoppingWords) {
			System.out.println(word);
		}
	}
	public void preprocess() { // before call this function it is a must to put the documents in the documents array list also the stopping words
		eleminateShortWords();
		elemenateStoppingWords();
		HashMap<String,File> filesToStem = getFiles();
		HashMap<String,File> filesAfterStemming = Stemmer.getStemedFiles(filesToStem);
	}
	private HashMap<String, File> getFiles() {
		HashMap<String,File> filesToStem = new HashMap<String,File>();
		for(int i = 0; i < documents.size(); i++) {
			File file = new File("/doc-after-stopping-words/"+documents.get(i).getDocumentName());
			try {
				PrintWriter writer = new PrintWriter(file);
				for(int j = 0; j < documents.get(i).getWords().size(); j++) {
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
		WordExtractor x = new WordExtractor(new ArrayList<Document>(),new ArrayList<String>());
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
	public static void simpleExtractStoppingWordsFromFilesTest(String directoryName) {
		WordExtractor x = new WordExtractor(new ArrayList<Document>(),new ArrayList<String>());
		ArrayList<File> files = getFiles(directoryName);
		for(File file : files) {
			x.addStoppingWords(file);
		}
		x.printStoppingWords();
	}
}
