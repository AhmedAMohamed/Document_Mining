import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Jama.Matrix;

public class Clustering {

	ArrayList<DocumentTermFrequency> documents;
	HashMap<String, WordInfo> wordsVector;
	ArrayList<Cluster> clusters;

	public static Matrix dtm;
	public static Matrix tdm;
	public static Matrix dcm;

	public Clustering(HashMap<String, WordInfo> wordsVector,
			ArrayList<DocumentTermFrequency> documents,
			ArrayList<Cluster> candidateClusters) {
		this.documents = documents;
		this.wordsVector = wordsVector;
		this.clusters = candidateClusters;
	}

	public void constructDTM() {
		double[][] dtm = new double[documents.size()][wordsVector.size()];
		int i = 0;
		for (DocumentTermFrequency d : documents) {
			int j = 0;
			for (String word : wordsVector.keySet()) {
				dtm[i][j] = d.getFuzzyValue(word, d.getWordMaxFuzzyValue(word));
				d.dcmIndex = i;
				j++;
			}
			i++;
		}
		this.dtm = new Matrix(dtm);
	}

	public double calculateScore(Cluster cluster) {
		double score = 0;
		for (DocumentTermFrequency doc : cluster.docs) {
			for (Cluster c : AssociationRuleMining.L1) {
				for(String word : c.terms) {
					score += doc.getFuzzyValue(word,
							wordsVector.get(word).maxFuzzyVarriable);
				}
			}
		}
		return score;
	}

	public void constructTDM() {
		double[][] tdm = new double[wordsVector.size()][clusters.size()];

		int j = 0;
		double totalMaxW = calculateTotalMaxW();
		for (int i = 0; i < tdm.length; i++) {
			j = 0;
			for (Cluster c : clusters) {
				tdm[i][j] = calculateScore(c) / totalMaxW;
				c.dcmIndex = j;
				j++;
			}
		}
		this.tdm = new Matrix(tdm);
	}

	private double calculateTotalMaxW() {
		double max = 0;
		for (DocumentTermFrequency doc : documents) {
			for (String word : wordsVector.keySet()) {
				max += doc.getFuzzyValue(word,
						wordsVector.get(word).maxFuzzyVarriable);
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
		for (DocumentTermFrequency d : documents) {
			int j = 0;
			double max = Double.MIN_VALUE;
			for (Cluster c : clusters) {
				if (dcm[i][j] > max) {
					max = dcm[i][j];
				}
			}
			Cluster c = getMaxCluster(max, clusters, dcm[i]);
			for (Cluster c2 : clusters) {
				if (c != c2) {
					c2.docs.remove(d);
				}
			}
		}
	}

	public void mergeClusters() {
		// merging step 1
		for (int i = 0; i < clusters.size(); i++) {
			if (clusters.get(i).docs.size() == 0) {
				clusters.remove(clusters.get(i));
			}
		}
		double[][] interSim = new double[clusters.size()][clusters.size()];
		for (int i = 0; i < interSim.length; i++) {
			for (int j = 0; j < interSim[i].length; j++) {
				if (i == j) {
					interSim[i][j] = 0;
				} else {
					interSim[i][j] = interSimilartyCalculatian(clusters.get(i),
							clusters.get(j), dcm.getArray());
				}
			}
		}

	}

	private double interSimilartyCalculatian(Cluster cluster, Cluster cluster2, double[][] dcm) {
		double sim = 0;
		double s1 = 0;
		double s2 = 0;
		double v1 = 0;
		double v2 = 0;
		Iterator<DocumentTermFrequency> a = cluster.docs.iterator();
		Iterator<DocumentTermFrequency> b = cluster2.docs.iterator();
		while(a.hasNext() && b.hasNext()) {
			s1 = dcm[a.next().dcmIndex][cluster.dcmIndex];
			s2 = dcm[b.next().dcmIndex][cluster2.dcmIndex];
			sim += (s1 * s2);
		}
		a = cluster.docs.iterator();
		b = cluster2.docs.iterator();
		
		while(a.hasNext()) {
			v1 += Math.pow(dcm[a.next().dcmIndex][cluster.dcmIndex], 2);
		}
		while(b.hasNext()) {
			v2 += Math.pow(dcm[b.next().dcmIndex][cluster2.dcmIndex], 2);
		}
		
		return (sim/Math.sqrt(v1*v2));
	}

	private Cluster getMaxCluster(double max,
			ArrayList<Cluster> candidateClusters2, double[] dcm) {
		for (int i = 0; i < dcm.length; i++) {
			if (dcm[i] == max) {
				return candidateClusters2.get(i);
			}
		}
		return null;
	}

}
