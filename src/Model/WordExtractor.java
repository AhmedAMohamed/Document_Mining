package Model;
import java.util.ArrayList;
import java.util.List;

public class WordExtractor {
	private ArrayList<Document> documents;
	private ArrayList<String> stoppingWords;
	
	public WordExtractor(ArrayList<Document> doc, ArrayList<String> stopping) {
		documents = (ArrayList<Document>) doc;
		stoppingWords = (ArrayList<String>) stopping;
	}
	public ArrayList<Document> elemenateStoppingWords() {
		ArrayList<Document> docs = new ArrayList<Document>();
		
		return docs;
	}
	public void addDocument(Document doc) {
		documents.add(doc);
	}
	public void addStoppingWords(ArrayList<String> words) {
		stoppingWords = words;
	}
	public void printDocument() {
		for(Document doc :documents) {
			System.out.println(doc);
		}
	}
	
	public static void main(String [] args) {
		WordExtractor x = new WordExtractor(new ArrayList<Document>(),new ArrayList<String>());
		Document doc = new Document();
		doc.setDocumentName("doc 1");
		ArrayList<String> words = new ArrayList<String>();
		words.add("ahmed");
		words.add("alaa");
		doc.setWords(words);
		ArrayList<String> stop = new ArrayList<String>();
		stop.add("alaa");
		stop.add("ahmed");
		x.addStoppingWords(stop);
		x.addDocument(doc);
		ArrayList<Document> y = x.elemenateStoppingWords();
		for(int i = 0; i < y.size(); i++) {
			System.out.println(y.get(i));
		}
		System.out.println("end of code");
		//x.printDocument();
	}
}
