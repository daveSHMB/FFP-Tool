
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



public class FFP extends SwingWorker<Void, Void>{

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
	boolean complete = false;

	public FFP(TextList tl, int ngramLength){

		this.tl = tl;
		this.ngramLength = ngramLength;
	}

	//TODO break this down into submethods
	//gets all ngrams from all texts
	public void buildFeatureList(int ngramLength){

		//null tree if one already exists
		tree = null;

		//map of individual text ngram/counts
		textNgramCount = new ArrayList<HashMap<String, Integer>>();
		//map of all ngrams/zeroed counts
		featureSet = new LinkedHashSet<String>();

		for(Text t:tl.getTextList()){
			//go back to integer??
			HashMap<String, Integer> ngrams = new HashMap<String,Integer>();
			ngrams = t.getNgrams(ngramLength);
			//store ngrams/count for this text
			textNgramCount.add(ngrams);

			//add ngrams to list of all ngrams for merging
			for(String s:ngrams.keySet()){
				
				//add only if occurs more than once
				if(ngrams.get(s) > 1){
				featureSet.add(s);
				}
			}
		}
		System.out.println("WHAT");
		//create frequency array of all ngrams for each text

		frequencies = new double[tl.getTextListLength()][featureSet.size()];
		System.out.println("WHAT");

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
	}

	public DistanceMatrix computeDistanceMatrix(){

		dm = new BasicSymmetricalDistanceMatrix(frequencies.length);

		//double[][] dm = new double[frequencies.length][frequencies.length];

		JensenShannonDivergence jsd = new JensenShannonDivergence();


		for(int i=0; i < frequencies.length; i++){
			for(int j=0; j < frequencies.length; j++){

				if(i == j){
					dm.setValue(i, j, 0.0);

				}
				else{
					double distance = JensenShannonDivergence.computeDivergence(frequencies[i], frequencies[j]);
					dm.setValue(i, j, distance);
				}
			}
		}

		//System.out.println(dm.toString());

		for(int i=0; i < tl.getTextListLength(); i++){
			dm.setIdentifier(i, tl.getText(i).getTitle());
		}

		System.out.println(dm.toString());
		return dm;

	}

	public void neighbourJoin(BasicSymmetricalDistanceMatrix dm){


		NeighborJoining nj = NeighborJoining.createInstance();
		tree = nj.execute(dm);
		//System.out.print(tree.toNexus());
		
	}



	//maybe change this??
	public boolean isComplete(){
		return complete;
	}

	public void formatTree(){

		//if(tree != null){
			System.out.println("HEY");
			String treeData = tree.toNexus();
			
			
			//File f = new File("out");
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter( new FileWriter("out"));
				writer.write(treeData);
				writer.close();
				//fw.close();
				Nexus ns = new Nexus();
				File f = new File("out");
				TreeData[] td = ns.read(f);
				treeOut = td[0];
			
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}

	
	public PhyloTree getTree(){
		return treeOut;
	}


	@Override
	protected Void doInBackground() throws Exception {

		
		while(!isCancelled()){
		System.out.println("Getting features...");
		buildFeatureList(ngramLength);
		System.out.println("Computing distance matrix...");
		computeDistanceMatrix();
		System.out.println("Joining neighbours...");
		neighbourJoin(dm);
		System.out.println("Formatting tree...");
		formatTree();
		complete = true;
		this.cancel(true);
		}
		return null;
	}

}
