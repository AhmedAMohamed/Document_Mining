package Main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Jama.Matrix;
import Models.Cluster;
import Models.DocumentTermFrequency;
import Models.WordInfo;

public class Clustering {

	public static ArrayList<Cluster> clusters;

	public static Matrix dtm;
	public static Matrix tdm;
	public static Matrix dcm;

	public static void calculateDTM(ArrayList<DocumentTermFrequency> documents,
			HashMap<String, WordInfo> wordsVector) {
		double[][] dtm = new double[documents.size()][wordsVector.size()];
		int i = 0;
		for (DocumentTermFrequency d : documents) {
			for (String word : wordsVector.keySet()) {
                WordInfo wi = wordsVector.get(word);
				dtm[i][wi.getClusterMatricesIndex()] = d.getFuzzyValue(word, wi
                        .getMaxSummedFuzzyVariable());
			}
            d.setClusterMatricesIndex(i);
			i++;
		}
		Clustering.dtm = new Matrix(dtm);
	}

	public static void constructTDM(HashMap<String, WordInfo> wordsVector) {

		double[][] tdm = new double[wordsVector.size()][clusters.size()];

        for (WordInfo wi : wordsVector.values()) {
			for (int j = 0; j < clusters.size(); j++) {
				tdm[wi.getClusterMatricesIndex()][j] = clusters.get(j).getScore() /  wi
                        .getMaxSummedFuzzyValue();
			}
		}
		Clustering.tdm = new Matrix(tdm);
	}

	public static void calculateDCM() {
		dcm = dtm.times(tdm);
		double[][] d = dcm.getArray();

        boolean notFound = true;
        int count = 0;
		DecimalFormat df = new DecimalFormat("#.00");
		for(double[] row : d) {
			for(double val : row) {
			    if(val > 0)
                {
                    notFound = false;
                }
			}
            if(notFound)
            {
                count++;
            }

		}
        System.out.println("dcm zeros: " + count);
        System.out.println();
	}

	public static void generateClusters(
			ArrayList<DocumentTermFrequency> documents) {


		double[][] dcm = Clustering.dcm.getArray();

        int i = 0;
		for (DocumentTermFrequency d : documents) {

			double max = Double.MIN_VALUE;
			Cluster maxCluster = null;
			for (int j = 0; j < clusters.size(); j++) {
				if (dcm[i][j] > max) {
					max = dcm[i][j];
					if (maxCluster != null) {
						maxCluster.getDocs().remove(d);
					}
					maxCluster = clusters.get(j);
				} else {
					clusters.get(j).getDocs().remove(d);
				}
			}
			i++;
		}
	}

	public static void mergeClusters() {
		// merging step 1
		System.out.println("number of clusters before: " + clusters.size());
		Iterator<Cluster> itr = clusters.iterator();
		while(itr.hasNext()) {
			if(itr.next().getDocs().size() > 0) {
				
			}
			else {
				
				System.out.println("one removed");
				itr.remove();
			}
		}
		/*
		 * // merging step 2 ArrayList<ArrayList<Double>> interSim = new
		 * ArrayList<>();
		 * 
		 * for (int i = 0; i < interSim.size(); i++) { interSim.add(new
		 * ArrayList<Double>()); for (int j = 0; j < interSim.get(i).size();
		 * j++) { if (i == j) { interSim.get(i).add((double) 1); } else {
		 * interSim.get(i).add( interSimilartyCalculatian(clusters.get(i),
		 * clusters.get(j), dcm.getArray())); } } } double[] index_value =
		 * getMaxVal(interSim);
		 */
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
		Iterator<DocumentTermFrequency> a = cluster.getDocs().iterator();
		Iterator<DocumentTermFrequency> b = cluster2.getDocs().iterator();
		while (a.hasNext() && b.hasNext()) {
			s1 = dcm[a.next().getClusterMatricesIndex()][cluster
					.getClusterMatrixIndex()];
			s2 = dcm[b.next().getClusterMatricesIndex()][cluster2
					.getClusterMatrixIndex()];
			sim += (s1 * s2);
		}
		a = cluster.getDocs().iterator();
		b = cluster2.getDocs().iterator();

		while (a.hasNext()) {
			v1 += Math.pow(dcm[a.next().getClusterMatricesIndex()][cluster
					.getClusterMatrixIndex()], 2);
		}
		while (b.hasNext()) {
			v2 += Math.pow(dcm[b.next().getClusterMatricesIndex()][cluster2
					.getClusterMatrixIndex()], 2);
		}

		return (sim / Math.sqrt(v1 * v2));
	}

	public static ArrayList<Cluster> cluster(
			ArrayList<DocumentTermFrequency> documents,
			HashMap<String, WordInfo> wordsVector,
			ArrayList<Cluster> candidateCluster) {


		Clustering.clusters = candidateCluster;
		
		for(Cluster c : clusters) {
        	c.updateClusterDocuments(wordsVector);
        	c.calculateScore(wordsVector);
        }

        //update cluster indexes
        for(int i = 0; i < clusters.size(); i++)
        {
            clusters.get(i).setClusterMatricesIndex(i);
        }

        //update words indexes
        int i = 0;
        for(WordInfo wi : wordsVector.values())
        {
            wi.setClusterMatricesIndex(i++);

        }

		Clustering.calculateDTM(documents, wordsVector);
		Clustering.constructTDM(wordsVector);
		Clustering.calculateDCM();


		Clustering.generateClusters(documents);
		Clustering.mergeClusters();

		return clusters;
	}
}
