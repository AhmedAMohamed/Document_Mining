package Model;

import java.util.ArrayList;

public class Document {
	private String documentName;
	private ArrayList<String> words;
	
	public Document() {
		documentName = new String();
		words = new ArrayList<String>();
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public ArrayList<String> getWords() {
		return words;
	}
	public void setWords(ArrayList<String> words) {
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
