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
				for (String word : c.terms) {
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
			if (clusters.get(i).docs.size() == 0 || clusters == null) {
				clusters.remove(clusters.get(i));
			}
		}

		// merging step 2
		ArrayList<ArrayList<Double>> interSim = new ArrayList<>();

		for (int i = 0; i < interSim.size(); i++) {
			interSim.add(new ArrayList<Double>());
			for (int j = 0; j < interSim.get(i).size(); j++) {
				if (i == j) {
					interSim.get(i).add((double) 1);
				} else {
					interSim.get(i).add(
							interSimilartyCalculatian(clusters.get(i),
									clusters.get(j), dcm.getArray()));
				}
			}
		}
		double[] index_value = getMaxVal(interSim);
		while (index_value[2] >= Algorithm.minInterSim) {
			Cluster mCluster = new Cluster();
			Cluster[] mergeedClusters = getClusters(index_value[0],
					index_value[1]);
			mCluster.docs.addAll(mergeedClusters[0].docs);
			mCluster.docs.addAll(mergeedClusters[1].docs);
			mCluster.terms.addAll(mergeedClusters[0].terms);
			mCluster.terms.addAll(mergeedClusters[1].terms);
			clusters.add(mCluster);
			ArrayList<Double> a = interSim.get((int) index_value[0]);
			a.set((int)index_value[1], (double) -1);
		}
	}

	private Cluster[] getClusters(double d, double e) {
		Cluster[] mClusters = new Cluster[2];
		for (Cluster c : clusters) {
			if (c.dcmIndex == d) {
				mClusters[0] = c;
			}
			if (c.dcmIndex == e) {
				mClusters[1] = c;
			}
		}

		return null;
	}

	private double[] getMaxVal(ArrayList<ArrayList<Double>> interSim) {
		double max = Double.MIN_VALUE;
		int maxIindex = 0;
		int maxJindex = 0;
		int i = 0;
		int j = 0;
		for (ArrayList<Double> row : interSim) {
			for (double val : row) {
				if (val > max) {
					val = max;
					maxJindex = j;
				}
				j++;
			}
			maxIindex = i;
			i++;
		}
		double[] valueData = new double[3];
		valueData[0] = maxIindex;
		valueData[1] = maxJindex;
		valueData[2] = max;
		return valueData;
	}

	private double interSimilartyCalculatian(Cluster cluster, Cluster cluster2,
			double[][] dcm) {
		double sim = 0;
		double s1 = 0;
		double s2 = 0;
		double v1 = 0;
		double v2 = 0;
		Iterator<DocumentTermFrequency> a = cluster.docs.iterator();
		Iterator<DocumentTermFrequency> b = cluster2.docs.iterator();
		while (a.hasNext() && b.hasNext()) {
			s1 = dcm[a.next().dcmIndex][cluster.dcmIndex];
			s2 = dcm[b.next().dcmIndex][cluster2.dcmIndex];
			sim += (s1 * s2);
		}
		a = cluster.docs.iterator();
		b = cluster2.docs.iterator();

		while (a.hasNext()) {
			v1 += Math.pow(dcm[a.next().dcmIndex][cluster.dcmIndex], 2);
		}
		while (b.hasNext()) {
			v2 += Math.pow(dcm[b.next().dcmIndex][cluster2.dcmIndex], 2);
		}

		return (sim / Math.sqrt(v1 * v2));
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
