package Main;

import java.util.*;

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
		for (int i = 0; i < documents.size(); i++) {
			for (String word : wordsVector.keySet()) {
                WordInfo wi = wordsVector.get(word);
				dtm[i][wi.getClusterMatricesIndex()] = documents.get(i).getFuzzyValue(word, wi
                        .getMaxSummedFuzzyVariable());
			}
            documents.get(i).setClusterMatricesIndex(i);
		}
        countZeroRow(dtm, "DTM");

		Clustering.dtm = new Matrix(dtm);


	}

    private static void countZeroRow(double [][] arr, String id)
    {
        int count = 0;
        for(double[] row : arr) {
            boolean notFound = true;
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
        System.out.println(id + " zeros: " + count);
    }
	public static void constructTDM(HashMap<String, WordInfo> wordsVector) {

		double[][] tdm = new double[wordsVector.size()][clusters.size()];

        for (WordInfo wi : wordsVector.values()) {
			for (int j = 0; j < clusters.size(); j++) {
                Cluster c = clusters.get(j);
                if(!c.hasTerm(wi.getWord()))
                {
                    tdm[wi.getClusterMatricesIndex()][j] = 0;
                }
                else{

                    tdm[wi.getClusterMatricesIndex()][j] = wi.getMaxSummedFuzzyValue() / c.getScore();
                }
			}
		}
        countZeroRow(tdm, "tdm");
		Clustering.tdm = new Matrix(tdm);
	}

	public static void calculateDCM() {
		dcm = dtm.times(tdm);

	}

	public static void generateClusters(
			ArrayList<DocumentTermFrequency> documents) {


		double[][] dcm = Clustering.dcm.getArray();

		for (int i = 0; i < documents.size(); i++) {

            DocumentTermFrequency d = documents.get(i);
			double max = 0;
			Cluster maxCluster = null;

			for (int j = 0; j < clusters.size(); j++) {
                Cluster c = clusters.get(j);
                if (dcm[i][j] > max) {
					max = dcm[i][j];

					if(maxCluster != null) {
                        maxCluster.getDocs().remove(d);
                    }

					maxCluster = c;

				}
                else {
                    c.getDocs().remove(d);
				}

            }
		}
	}
    private static  int countDocumentsInClusters(){
        //check documets
        HashSet<DocumentTermFrequency> test = new HashSet<>();
        for(Cluster c : clusters)
        {
            test.addAll(c.getDocs());
        }
        System.out.println("Documets found in clusters: " + test.size());
        return test.size();
    }


    private class MergeElement{
        double value;
        int index;
        double documentVis;

        public MergeElement(double value, int index, double documentVis) {
            this.value = value;
            this.index = index;
            this.documentVis = documentVis;
        }
    }
	public static void mergeClusters() {
        ArrayList<ArrayList<Iterator<MergeElement>>> iterators = new ArrayList<>(clusters.size());
        for(int i = 0; i < clusters.size(); i++)
        {
            ArrayList<Iterator<MergeElement>> elements = new ArrayList<>(clusters.size());

            for(int j = 0; j < clusters.size(); j++)
            {
                elements.set(i,null);
            }

            iterators.set(i, elements);
        }

        ArrayList<Iterator<MergeElement>> top = new ArrayList<>(clusters.size()-1);

        LinkedList<LinkedList<MergeElement>> proximity = new LinkedList<>();

        for(int i = 0; i < clusters.size()-1; i++)
        {
            
        }


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

        countDocumentsInClusters();

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

        countDocumentsInClusters();

		Clustering.generateClusters(documents);
		Clustering.mergeClusters();

		return clusters;
	}
}
