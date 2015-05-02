package Model.DataContainer;
import java.util.ArrayList;
import java.util.LinkedList;

public class Document {
	private String documentName;
	private LinkedList<String> words;
	
	public Document() {
		documentName = new String();
		words = new LinkedList<>();
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public LinkedList<String> getWords() {
		return words;
	}
	public void setWords(LinkedList<String> words) {
		this.words = words;
	}
	@Override
	public String toString() {
		String stringValue = new String();
		stringValue += documentName + "\n";
		for(String word : words) {
			stringValue += word + "\n";
		}
		return stringValue;
	}
}
