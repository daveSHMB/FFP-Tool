import java.awt.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.SwingWorker;

public class FFP extends SwingWorker<Void, Void>{

	TreeMap<String, Integer> featureSet;
	String[] featureList;
	TextList tl;
	ArrayList<TreeMap<String, Integer>> textNgramCount;
	double[][] frequencies;
	int ngramLength;

	public FFP(TextList tl, int ngramLength){

		this.tl = tl;
		this.ngramLength = ngramLength;
	}


	//gets all ngrams from all texts
		public void buildFeatureList(int ngramLength){
			
			//map of individual text ngram/counts
			textNgramCount = new ArrayList<TreeMap<String, Integer>>();
			//map of all ngrams/zeroed counts
			featureSet = new TreeMap<String, Integer>();
			
			for(Text t:tl.getTextList()){
				//go back to integer??
				TreeMap<String, Integer> ngrams = new TreeMap<String, Integer>();
				ngrams = t.getNgrams(ngramLength);
				//store ngrams/count for this text
				textNgramCount.add(ngrams);
								
				//add ngrams/zeroed counts to list of all ngrams for merging
				featureSet.putAll(ngrams);
				
				//zero the counts in list of all
				for(String s:featureSet.keySet()){
					featureSet.put(s, 0);
				}
			}
			
			//create frequency array of all ngrams for each text
			
			frequencies = new double[tl.getTextListLength()][featureSet.size()];
			
			int i = 0;
			for(TreeMap<String,Integer> tm:textNgramCount){
				int j = 0;
				for(String s: featureSet.keySet()){
					if(tm.get(s) != null){
						frequencies[i][j++] = (double)tm.get(s);
					}
					else{
						frequencies[i][j++] = 0.0;
					}
				}
			}
			
		
			//REMOVE THE BELOW IF THE ABOVE WORKS!! Which I bet it doesn't. Even though it looks like it does.
			
//			//merge the profile for each text with the overall ngram counts
//			
//			
//			
//			
//			ArrayList<TreeMap<String, Double>> tempFrequencies = new ArrayList<TreeMap<String, Double>>();
//			
//			for(TreeMap<String, Double> ngrams: textNgramCount){
//				TreeMap<String, Double> temp = new TreeMap<String, Double>();
//				temp.putAll(featureSet);
//				
//				for(String s: ngrams.keySet()){
//					temp.put(s, ngrams.get(s));
//				}
//				
//				tempFrequencies.add(temp);
//				
//			}
//			
//			//convert values to a 2d array
//			frequencies =  new Double[tempFrequencies.size()][featureSet.values().size()];
//			
//			for(int i = 0; i < frequencies.length; i++){
//				frequencies[i] = tempFrequencies.get(i).values().toArray(frequencies[i]);
//			}
//			
//			//currently frequencies are not primitive, should they be unboxed??
			
		}

	public void computeDistanceMatrix(){
		
		double[][] dm = new double[frequencies.length][frequencies.length];
		
		for(int i=0; i < frequencies.length; i++){
			for(int j=0; j < frequencies.length; j++){
				
				if(i == j){
					dm[i][j] = 0;
					continue;
				}
				else{
					
					//OBVS THIS SHOULD BE JSD
					dm[i][j] = 1;
				}
			}
		}
		int u = 99;

	}

	@Override
	protected Void doInBackground() throws Exception {

		System.out.println("Uhhhhh?");
		while(!isCancelled()){
			buildFeatureList(ngramLength);
			computeDistanceMatrix();
			this.cancel(true);
		}

		System.out.println("Completed! Or cancelled. Whatevs");

		return null;
	}






}
