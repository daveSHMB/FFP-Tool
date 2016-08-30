
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



public class FFP extends SwingWorker<Void, String>{

	//TODO HEY MAKE THESE HAVE SCOPE YO
	LinkedHashSet<String> featureSet;
	//String[] featureList;
	TextList tl;
	ArrayList<HashMap<String, Integer>> textNgramCount;
	double[][] frequencies;
	BasicSymmetricalDistanceMatrix dm;
	int ngramLength;
	Phylogeny tree;
	PhyloTree treeOut;
	String current;
	boolean complete = false;

	public FFP(TextList tl, int ngramLength){

		this.tl = tl;
		this.ngramLength = ngramLength;
	}

	//TODO break this down into submethods
	//gets all ngrams from all texts
	public void buildFeatureList(int ngramLength){
		publish("Building feature list");
		//null tree if one already exists
		tree = null;

		//map of individual text ngram/counts
		textNgramCount = new ArrayList<HashMap<String, Integer>>();

		//map of all ngrams/zeroed counts - initialise this in mergeFeatures() method??
		featureSet = new LinkedHashSet<String>();

		for(Text t:tl.getTextList()){
		
			//store ngrams/count for this text
			textNgramCount.add(getNgramCount(t));
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

	public DistanceMatrix computeDistanceMatrix(){
		publish("Computing distance matrix");
		
		dm = new BasicSymmetricalDistanceMatrix(frequencies.length);

		JensenShannonDivergence jsd = new JensenShannonDivergence();

		for(int i=0; i < frequencies.length; i++){
			for(int j=0; j < frequencies.length; j++){
				
				if(isCancelled()){
					return null;
				}
				
				if(i == j){
					dm.setValue(i, j, 0.0);
				}
				else{
					double distance = jsd.getJSDDivergence(frequencies[i], frequencies[j]);
					dm.setValue(i, j, distance);
				
				}
			}
		}

		//add identifiers
		for(int i=0; i < tl.getTextListLength(); i++){
			dm.setIdentifier(i, tl.getText(i).getTitle());
		}
		return dm;
	}

	public void neighbourJoin(BasicSymmetricalDistanceMatrix dm){
		publish("Preparing output");
		NeighborJoining nj = NeighborJoining.createInstance();
		tree = nj.execute(dm);

	}

	public HashMap<String, Integer> getNgramCount(Text t){
		return t.getNgrams(ngramLength);
	}

	public LinkedHashSet<String> mergeFeatures(){
		for(HashMap<String, Integer> ngrams: textNgramCount){
			for(String s:ngrams.keySet()){
			
				//add only if occurs more than once
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

	public double[][] getFeatureFrequencies(){
		frequencies = new double[tl.getTextListLength()][featureSet.size()];

		int i = 0;
		for(HashMap<String, Integer> text:textNgramCount){
			int j = 0;
			for(String s:featureSet){
			
				if(text.get(s) != null){
					frequencies[i][j++] = (double) text.get(s);
				}
				else{
					frequencies[i][j++] = 0.0;
				}
			
			}
			i++;
		}
		return frequencies;
	}


	//maybe change this??
	public boolean isComplete(){
		return complete;
	}

	public void formatTree(){
		
		String treeData = tree.toNexus();
		

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter( new FileWriter("out"));
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


	public PhyloTree getTree(){
		return treeOut;
	}
	
	public void updateStatus(String current){
		this.current = current;
	}
	
	public String getStatus(){
		return current;
	}
	
	protected void process(List<String> progress){
		updateStatus(progress.get(progress.size() - 1));
	}

	@Override
	protected Void doInBackground() throws Exception {


		while(!isCancelled()){
			if(!isCancelled()){
			buildFeatureList(ngramLength);
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
			complete = true;
			this.cancel(true);
		}
		
		return null;
	}
	

}
