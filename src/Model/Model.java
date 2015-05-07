package Model;
import java.io.*;
import java.util.*;

import Model.DataContainer.Document;
import edu.mit.jwi.item.Word;


public class Model {
	private static File[] selectedFiles = null;
	private static String outputDirectory = null;
	private static int longestDocumentNameLen = 0;
    private static Wordnet wordnet = null;
    private static ArrayList<DocumentTermFrequency> modelDocs = null;
    private static ArrayList<String> unifiedWordsVector = null;


	public static  void setSelectedFile(File[] _selectedFiles) {
		selectedFiles = _selectedFiles;
	}

	public static void setOutputDirectory(String _outputDirectory) {
		outputDirectory = _outputDirectory;
	}
    public static Wordnet getWordnet(){
        if(wordnet == null)
        {
            wordnet = new Wordnet(false);
        }
        return wordnet;
    }

	private static ArrayList<Document> extractDocumentWords() {
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

	public static void preprocessData() {
        //get documents from file
		ArrayList<Document> documents = extractDocumentWords();
		Preprocessing pre = new Preprocessing();
		pre.setDocuments(documents);

        //Do phase 1 : stopping words, stemmer
		documents = pre.preprocessPhase1();

        //empty used data
        pre.unsetWords();

        //get documents with term frequencies and detailed global words
        ArrayList<WordInfo> unifiedWordsInfoVector = null;
		createUnifiedTermFrequency(documents, unifiedWordsInfoVector);

        //start phase 2
        unifiedWordsVector = pre.preprocessPhase2(modelDocs, unifiedWordsInfoVector);

        //write output
		writeOutput(unifiedWordsVector);
	}


	private static void createUnifiedTermFrequency(ArrayList<Document> documents,
                                                           ArrayList<WordInfo> unifiedWordsInfoVector) {
        class Value{
            public HashSet<DocumentTermFrequency> set;
            public int freq;
            public Value()
            {
                set = new HashSet<>();
                freq = 0;
            }
        }
		HashMap<String, Value> globalWordToDouments = new HashMap<>();

        //loop over each document
		for (Document d : documents) {
            //track longest document name [for visualization purposes]
			updateLongestDocumentNameLen(d.getDocumentName());
            //create a new document ter frequency
			DocumentTermFrequency dtf = new DocumentTermFrequency(d.getDocumentName());

            //loop over document words to add them to the new dtf
			LinkedList<String> words = d.getWords();
			for (String word : words) {
                //add word to new dtf
				dtf.addTerm(word);

                Value globalValue = globalWordToDouments.get(word);
                // if global word doesnt exist create it
                if(globalValue == null)
                {
                    globalValue = new Value();
                    globalWordToDouments.put(word, globalValue);
                }
                //add document to the word
                globalValue.set.add(dtf);
                //add frequency
                globalValue.freq += 1;


			}
			modelDocs.add(dtf);
		}
        //now create the unified vector with detailed info
        for(String word : globalWordToDouments.keySet())
        {
            Value v = globalWordToDouments.get(word);
            unifiedWordsInfoVector.add(new WordInfo(word, v.set.size(), v.freq));

        }

	}

    private static void writeOutput(ArrayList<String> globalWords) {
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
            for (DocumentTermFrequency doc : modelDocs) {
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



    private static void updateLongestDocumentNameLen(String name) {
		if (name.length() > longestDocumentNameLen)
			longestDocumentNameLen = name.length();

	}

	private static int getColLenght(int index) {
		return Math.max(unifiedWordsVector.get(index).length(), 10) + 2;
	}

	private static int getMaxDocumentNameLen() {
		return longestDocumentNameLen + 2;
	}

	private  static String center(String text, int len) {
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
