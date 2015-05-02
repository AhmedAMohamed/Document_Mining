package Model;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import Model.DataContainer.Document;

/**
 * Created by karim on 4/30/15.
 */
public class Model {
	private File[] selectedFiles;
	private String outputDirectory;
	private ArrayList<String> globalWords;
	private int longestDocumentNameLen;

	public Model() {
		this.selectedFiles = null;
		outputDirectory = null;
		longestDocumentNameLen = 0;
	}

	public void setSelectedFile(File[] selectedFiles) {
		this.selectedFiles = selectedFiles;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	private ArrayList<Document> extractDocumentWords() {
		ArrayList<Document> documents = new ArrayList<>();
		for (File file : selectedFiles) {
			Document d = new Document();
			d.setDocumentName(file.getName());
			LinkedList<String> words = new LinkedList<>();
			try {

				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file)));
				String line = null;
				while ((line = br.readLine()) != null) {
					words.addAll(Arrays.asList(line.split("\\s+")));
				}
				d.setWords(words);
				documents.add(d);
			} catch (IOException t) {
				System.out.println("Couldn't read from file : "
						+ file.getAbsolutePath());
			}
		}

		return documents;
	}

	public void preprocessData() {
		ArrayList<Document> documents = extractDocumentWords();
		Preprocessing pre = new Preprocessing();
		pre.setDocuments(documents);
		documents = pre.preprocess();
		ArrayList<DocumentTermFrequency> docsWithFreq = createUnifiedTermFrequency(documents);
		writeOutput(globalWords, docsWithFreq);
	}

	private void writeOutput(ArrayList<String> globalWords,
			ArrayList<DocumentTermFrequency> docsWithFreq) {
		try {
			PrintWriter writer = new PrintWriter(outputDirectory
					+ "/preprocess_output.txt", "UTF-8");
			// first line words
			// first output empty for document name
			writer.print(left("", getMaxDocumentNameLen()));
			// then write each word
			for (int i = 0; i < globalWords.size(); i++) {
				writer.print(center(globalWords.get(i), getColLenght(i)));

			}
			writer.println();
			writer.println();

			// start writing each document vecotr
			for (DocumentTermFrequency doc : docsWithFreq) {
				// first output document name
				writer.print(left(doc.getName(), getMaxDocumentNameLen()));
				for (int i = 0; i < globalWords.size(); i++) {
					writer.print(center(
							String.valueOf(doc.getWordFreq(globalWords.get(i))),
							getColLenght(i)));

				}
				writer.println();
			}

			writer.close();
		} catch (IOException t) {
			System.out.println("Couldn't produce output file.");
		}
	}

	private ArrayList<DocumentTermFrequency> createUnifiedTermFrequency(
			ArrayList<Document> documents) {
		HashMap<String, HashSet<DocumentTermFrequency>> globalWordToDouments = new HashMap<>();
		ArrayList<DocumentTermFrequency> docsFreq = new ArrayList<>();
		for (Document d : documents) {
			updateLongestDocumentNameLen(d.getDocumentName());
			DocumentTermFrequency dtf = new DocumentTermFrequency(
					d.getDocumentName());
			LinkedList<String> words = d.getWords();
			for (String word : words) {
				dtf.addTerm(word);
				if (globalWordToDouments.containsKey(word)) {
					globalWordToDouments.get(word).add(dtf);
				} else {
					HashSet<DocumentTermFrequency> docs = new HashSet<>();
					docs.add(dtf);
					globalWordToDouments.put(word, docs);

				}
			}
			docsFreq.add(dtf);
		}
		unifyVectoreSpace(docsFreq, globalWordToDouments);
		return docsFreq;
	}

	private void unifyVectoreSpace(ArrayList<DocumentTermFrequency> docsFreq,
			HashMap<String, HashSet<DocumentTermFrequency>> globalWordToDouments) {
		globalWords = new ArrayList<>(globalWordToDouments.keySet());

		for (String word : globalWords) {
			for (DocumentTermFrequency d : docsFreq) {
				if (!globalWordToDouments.get(word).contains(d))
					d.setEmptyWord(word);
			}
		}
	}

	private void updateLongestDocumentNameLen(String name) {
		if (name.length() > longestDocumentNameLen)
			longestDocumentNameLen = name.length();

	}

	private int getColLenght(int index) {
		return Math.max(globalWords.get(index).length(), 10) + 2;
	}

	private int getMaxDocumentNameLen() {
		return longestDocumentNameLen + 2;
	}

	private static String center(String text, int len) {
		String out = String
				.format("%" + len + "s%s%" + len + "s", "", text, "");
		float mid = (out.length() / 2);
		float start = mid - (len / 2);
		float end = start + len;
		return out.substring((int) start, (int) end);
	}

	private static String left(String text, int len) {
		return String.format("%s%" + Math.abs(len - text.length()) + "s", text,
				"");
	}

}
