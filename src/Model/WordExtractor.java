package Model;
import java.util.ArrayList;

public class WordExtractor {
	private ArrayList<Document> documents;
	private ArrayList<String> stoppingWords;
	
	public WordExtractor(ArrayList<Document> doc, ArrayList<String> stopping) {
		documents = (ArrayList<Document>) doc;
		stoppingWords = (ArrayList<String>) stopping;
	}
	public ArrayList<Document> elemenateStoppingWords() {
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
			docs.add(temp);
		}
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
	public static void simpleEleminateStopWordsTest() {
		WordExtractor x = new WordExtractor(new ArrayList<Document>(),new ArrayList<String>());
		Document doc = new Document();
		doc.setDocumentName("doc 1");
		ArrayList<String> words = new ArrayList<String>();
		words.add("the");
		words.add("ahmed");
		words.add("alaa");
		words.add("a");
		doc.setWords(words);
		ArrayList<String> stop = new ArrayList<String>();
		stop.add("a");
		stop.add("the");
		x.addStoppingWords(stop);
		x.addDocument(doc);
		x.elemenateStoppingWords();
		x.printDocument();
	}
}
