import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import javax.swing.SwingWorker;
import org.forester.evoinference.matrix.distance.*;
import org.forester.phylogeny.Phylogeny;
import dendroscope.core.TreeData;
import dendroscope.io.Nexus;
import jloda.phylo.PhyloTree;
import org.forester.evoinference.distance.*;



/**
 * Class which performs FFP processing on a text.
 * 
 * @author David McLintock
 *
 */
public class FFP extends SwingWorker<Void, String>{

	private LinkedHashSet<String> featureSet;
	private TextList tl;
	private ArrayList<HashMap<String, Integer>> textNgramCount;
	private double[][] frequencies;
	private BasicSymmetricalDistanceMatrix dm;
	private int ngramLength;
	private Phylogeny tree;
	private PhyloTree treeOut;
	private String current;
	private boolean complete = false;

	
	/**
	 * Default constructor
	 * @param tl the list of texts under study
	 * @param ngramLength the length of ngram to be used
	 */
	public FFP(TextList tl, int ngramLength){

		this.tl = tl;
		this.ngramLength = ngramLength;
	}

	
	/**
	 * Gets features, frequencies and merges details
	 */
	public void buildFeatureList(){
		publish("Building feature list");
		
		//map of individual text ngram/counts
		textNgramCount = new ArrayList<HashMap<String, Integer>>();

		//map of all ngrams/zeroed counts
		featureSet = new LinkedHashSet<String>();
		
		//get ngrams/counts from each text
		for(Text t:tl.getTextList()){
		
			//store ngrams/count for this text
			textNgramCount.add(getNgrams(t.getText()));
			if(isCancelled()){
				return;
			}
		}
		
		//get ngrams from all texts and merge them into a unified feature profile
		publish("Creating feature profile");
		featureSet = mergeFeatures();
		
		//create frequency array of all ngrams for each text
		publish("Counting feature frequencies");
		frequencies = getFeatureFrequencies();
	}

	
	/**
	 * Creates pairwise distance matrix from divergence measure
	 * @return the completed distance matrix
	 */
	public DistanceMatrix computeDistanceMatrix(){
		publish("Computing distance matrix");
		
		dm = new BasicSymmetricalDistanceMatrix(frequencies.length);

		
		//divergence method used
		DivergenceMethod divergence = new JensenShannonDivergence();
		
		
		//compare each frequency vector with each other
		for(int i=0; i < frequencies.length; i++){
			for(int j=0; j < frequencies.length; j++){
				
				if(isCancelled()){
					return null;
				}
				
				if(i == j){
					dm.setValue(i, j, 0.0);
				}
				else{
					double distance = divergence.computeDivergence(frequencies[i], frequencies[j]);
					dm.setValue(i, j, distance);
				
				}
			}
		}

		//add identifying text labels
		for(int i=0; i < tl.getTextListLength(); i++){
			dm.setIdentifier(i, tl.getText(i).getTitle());
		}
		return dm;
	}

	
	/**
	 * Performs neighbour joining and creates tree
	 * @param dm
	 */
	public void neighbourJoin(BasicSymmetricalDistanceMatrix dm){
		publish("Preparing output");
		NeighborJoining nj = NeighborJoining.createInstance();
		tree = nj.execute(dm);

	}
	
	
	/**
	 * Gets ngrams and their frequency from the text
	 * @param text the text content error
	 * @return a HashMap of unique ngrams and their frequency
	 */
	public HashMap<String, Integer> getNgrams(String text){

		//remove all spaces from text
		String noSpaces = text.replaceAll("\\s+", "");

		HashMap<String, Integer> ngrams = new HashMap<String, Integer>();
		

		for(int i = 0; i < noSpaces.length(); i++){
			//tests if end of string reached
			if((i+ngramLength <= noSpaces.length())){
				String ngram = noSpaces.substring(i, i+ngramLength);
				//adds ngrams which do not contain punctuation to list, defaulting count to 1 or adding 1 to existing count
				if(ngram.matches("[a-zA-Z]+")){
					ngrams.put(ngram, ngrams.getOrDefault(ngram, 1) + 1);
				}
			}
		}
		return ngrams;
	}


	/**
	 * Merges feature lists
	 * @return an in order list of ngrams
	 */
	public LinkedHashSet<String> mergeFeatures(){
		for(HashMap<String, Integer> ngrams: textNgramCount){
			for(String s:ngrams.keySet()){
			
				//add only if feature occurs more than once
				if(ngrams.get(s) > 1){
					featureSet.add(s);
					if(isCancelled()){
						return null;
					}
				}
			}
		}
		return featureSet;
	}

	
	/**
	 * Creates Frequency Profile for each text
	 * @return 2d array containing all Frequency Profiles
	 */
	public double[][] getFeatureFrequencies(){
		frequencies = new double[tl.getTextListLength()][featureSet.size()];

		int i = 0;
		
		//for each ngram get frequency if part of text, set to zero if not
		for(HashMap<String, Integer> text:textNgramCount){
			int j = 0;
			for(String s:featureSet){
			
				if(text.get(s) != null){
					frequencies[i][j++] = text.get(s);
				}
				else{
					frequencies[i][j++] = 0.0;
				}	
			}
			i++;
		}
		return frequencies;
	}


	/**
	 * Returns whether FFP completed successfully
	 * @return true if complete
	 */
	public boolean isComplete(){
		return complete;
	}

	
	/**
	 * Formats tree using Dendroscope library
	 */
	public void formatTree(){
		
		String treeData = tree.toNexus();
		
		BufferedWriter writer = null;
		try {
			
			//save tree file and reopen as tree (enforced due to library limitations)
			writer = new BufferedWriter(new FileWriter("out"));
			writer.write(treeData);
			writer.close();
			Nexus ns = new Nexus();
			File f = new File("out");
			TreeData[] td = ns.read(f);
			treeOut = td[0];

		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}

	
	/**
	 * Returns Newick string representing phylo tree
	 * @return string representation of tree
	 */
	public String getNewick(){
		return tree.toNewHampshire();
	}
	

	/**
	 * Returns the final phylo tree object
	 * @return the computed tree
	 */
	public PhyloTree getTree(){
		return treeOut;
	}
	
	
	/**
	 * Update current state of FFP processing for GUI output
	 * @param current the details of the present operation
	 */
	public void updateStatus(String current){
		this.current = current;
	}
	
	
	/**
	 * Gets the current status of FFP processing for GUI output
	 * @return the current status of FFP processing
	 */
	public String getStatus(){
		return current;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#process(java.util.List)
	 */
	@Override
	protected void process(List<String> progress){
		updateStatus(progress.get(progress.size() - 1));
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {

		//test at every step whether cancel has been pressed
		while(!isCancelled()){
			if(!isCancelled()){
			buildFeatureList();
			}
			if(!isCancelled()){
			computeDistanceMatrix();
			}
			if(!isCancelled()){
			neighbourJoin(dm);
			}
			if(!isCancelled()){
			formatTree();
			}
			//set FFP status to complete
			complete = true;
			this.cancel(true);
		}
		
		return null;
	}
	

}
