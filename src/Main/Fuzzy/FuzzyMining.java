package Main.Fuzzy;

import Main.Algorithm;
import Models.Cluster;
import Models.DocumentTermFrequency;
import Models.WordInfo;
import Utility.Watch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FuzzyMining {

	private static ArrayList<DocumentTermFrequency> documents;
	private static HashMap<String, WordInfo> wordsVector;

	/**
	 * Get the candidate clusters by fuzzy frequent item set mining.
	 *
	 * @param documents
	 *            documents
	 * @param wordsVector
	 *            unified words vector
	 *
	 * @return Arraylist of Clusters representing the found candidate clusters
	 *
	 */
	public static ArrayList<Cluster> mineFrequentItemSets(
			ArrayList<DocumentTermFrequency> documents,
			HashMap<String, WordInfo> wordsVector) {

		FuzzyMining.documents = documents;
		FuzzyMining.wordsVector = wordsVector;

		// fuzzy variable calculation
		Watch.lapBegin();
		calculateFuzzyVariables();
		Watch.lapStop("calculating fuzzy variables");

		
		// L1 candidate clusters generation
		Watch.lapBegin();
		ArrayList<Cluster> l1_candidate_clusters = calculateMaxFuzzyVariableAndL1();

		Watch.lapStop("calculating L1 and max fuzzy variable");

		// Apriori
		Watch.lapBegin();
		Apriori.wordsVector = wordsVector;
		ArrayList<Cluster> candidate_clusters = Apriori
				.generateAllCandidateClusters(l1_candidate_clusters, documents,
						wordsVector);

		for (Cluster c : candidate_clusters) {
			System.out.print("{");
			for (String s : c.getTerms()) {
				System.out.print(s + ", ");
			}
			System.out.println("}");
		}
		Watch.lapStop("apriori");

		return candidate_clusters;
	}

	/**
	 * Calculate the fuzzy variables value for all terms in each document and
	 * calculate the summed values for each terms in the unified words vector.
	 */
	private static void calculateFuzzyVariables() {

		for (DocumentTermFrequency d : documents) {
			for (String word : wordsVector.keySet()) {
				WordInfo w = wordsVector.get(word);
				// step 1 set member ship values for term in each document
				double l = Membership.low(d.getWordFreq(word), w.getMinFreq(),
						w.getAverage(), w.getMaxFreq());
				double m = Membership.mid(d.getWordFreq(word), w.getMinFreq(),
						w.getAverage(), w.getMaxFreq());
				double h = Membership.high(d.getWordFreq(word), w.getMinFreq(),
						w.getAverage(), w.getMaxFreq());
				d.setFuzzyValue(word, l, m, h);

				// step 2 calculate summed membership values for term in words
				// vector
				w.incrementSummedFuzzyValue(FuzzyVariable.LOW, l);
				w.incrementSummedFuzzyValue(FuzzyVariable.MEDIUM, m);
				w.incrementSummedFuzzyValue(FuzzyVariable.HIGH, h);

			}
		}
	}

	/**
	 * Go over each term in the unified term vector and set its maximum fuzzy
	 * variable which is the variable with the highest summed value over all
	 * documents. Also produce the L1 which is candidate clusters of size 1.
	 *
	 * @return ArrayList of candidate clusters of size 1
	 */
	private static ArrayList<Cluster> calculateMaxFuzzyVariableAndL1() {

		ArrayList<Cluster> l1_candidate_clusters = new ArrayList<>();
		FuzzyVariable[] variables = { FuzzyVariable.LOW, FuzzyVariable.MEDIUM,
				FuzzyVariable.HIGH };

		
		Iterator<Map.Entry<String, WordInfo>> wordIterator = wordsVector
				.entrySet().iterator();
		while (wordIterator.hasNext()) {
			String word = wordIterator.next().getKey();
			WordInfo w = wordsVector.get(word);

			// find the maximum fuzzy variable
			int max_fuzzy_index = 0;
			double max_value = -1; // suitable as fuzzy value is between 0 and 2
			for (int i = 0; i < variables.length; i++) {
				double cur_value = w.getSummedFuzzyValue(variables[i]);
				if (cur_value > max_value) {
					max_fuzzy_index = i;
					max_value = cur_value;
				}
			}

			// set the max fuzzy variable
			w.setMaxSummedFuzzyVariable(variables[max_fuzzy_index]);

			// check if we should add to l1
			double support = max_value / documents.size();
			if (support > Algorithm.MIN_SUPPORT) {
				l1_candidate_clusters.add(new Cluster(word, support));
			} else {
				wordIterator.remove();
			}
		}
		return l1_candidate_clusters;
	}
}
