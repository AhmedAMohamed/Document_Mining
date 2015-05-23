import java.util.ArrayList;
import java.util.HashMap;

import Jama.Matrix;

public class Clustering {
		
	ArrayList<DocumentTermFrequency> documents;
	HashMap<String,WordInfo> wordsVector;
	ArrayList<Cluster> clusters;
	
	public static Matrix dtm;
	public static Matrix tdm;
	public static Matrix dcm;
	
	public Clustering(HashMap<String, WordInfo> wordsVector, ArrayList<DocumentTermFrequency> documents, ArrayList<Cluster> candidateClusters) {
		this.documents = documents;
		this.wordsVector = wordsVector;
		this.clusters = candidateClusters;
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
	
	public double calculateScore(Cluster cluster) {
		double score = 0;
		for(DocumentTermFrequency doc : cluster.docs) {
			for(String word : cluster.terms) {
				score += doc.getFuzzyValue(word, wordsVector.get(word).maxFuzzyVarriable);
			}
		}
		return score;
	}
	
	public void constructTDM() {
		double[][] tdm = new double[wordsVector.size()][clusters.size()];

		int j = 0;
		double totalMaxW = calculateTotalMaxW();
		for(int i = 0; i < tdm.length; i++) {
			j = 0;
			for(Cluster c : clusters) {
				tdm[i][j] = calculateScore(c) / totalMaxW; 
				j++;
			}
		}
		this.tdm = new Matrix(tdm);
	}
	
	private double calculateTotalMaxW() {
		double max = 0;
		for(DocumentTermFrequency doc : documents) {
			for(String word : wordsVector.keySet()) {
				max += doc.getFuzzyValue(word, wordsVector.get(word).maxFuzzyVarriable);
			}
		}
		return max;
	}

	public void calculateDcm() {
		dcm = dtm.times(tdm);
	}

	public void generateClusters() {
		double[][] dcm = this.dcm.getArray();
		int i = 0;
		for(DocumentTermFrequency d : documents) {
			int j = 0;
			double max = Double.MIN_VALUE;
			for(Cluster c : clusters) {
				if(dcm[i][j] > max) {
					max = dcm[i][j];
				}
			}
			Cluster c = getMaxCluster(max, clusters,dcm[i]);
			for(Cluster c2 : clusters) {
				if(c != c2) {
					c2.docs.remove(d);
				}
			}
		}
	}

	private Cluster getMaxCluster(double max,ArrayList<Cluster> candidateClusters2, double[] dcm) {
		for(int i = 0; i < dcm.length; i++) {
			if(dcm[i] == max) {
				return candidateClusters2.get(i);
			}
		}
		return null;
	}

}
