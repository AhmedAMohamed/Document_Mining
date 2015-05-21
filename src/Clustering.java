import java.util.ArrayList;
import java.util.HashMap;

import Jama.Matrix;

public class Clustering {

	ArrayList<DocumentTermFrequency> documents;
	HashMap<String,WordInfo> wordsVector;
	ArrayList<Cluster> candidateClusters;
	
	public static Matrix dtm;
	public static Matrix tdm;
	public static Matrix dcm;
	
	public Clustering(HashMap<String, WordInfo> wordsVector, ArrayList<DocumentTermFrequency> documents, ArrayList<Cluster> candidateClusters) {
		this.documents = documents;
		this.wordsVector = wordsVector;
	}
	
	public void constructDTM() {
		double[][] dtm = new double [documents.size()][wordsVector.size()];
		int i = 0;
		for(DocumentTermFrequency d : documents) {
			int j = 0;
			for(String word : wordsVector.keySet()) {
				dtm[i][j] = d.getFuzzyValue(word, d.getWordMaxFuzzyValue(word));
				j++;
			}
			i++;
		}
		this.dtm = new Matrix(dtm);
	}
	
	public void calculateScore(Cluster cluster) {
		double score = 0;
		for(DocumentTermFrequency d : cluster.docs) {
			for(String word : wordsVector.keySet()) {
				score += d.getFuzzyValue(word, d.getWordMaxFuzzyValue(word));
			}
		}
	}
	
	public void constructTDM() {
		double[][] tdm = new double[wordsVector.size()][candidateClusters.size()];
	}
	
	public void calculateDcm() {
		dcm = dtm.times(tdm);
	}
	
	
}
