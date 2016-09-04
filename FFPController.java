import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import dendroscope.core.TreeData;
import dendroscope.io.Newick;
import jloda.graphview.IGraphDrawer;

import jloda.phylo.PhyloGraphView;
import jloda.phylo.TreeDrawerRadial;


public class FFPController implements ActionListener {

	GUI gui;
	ResultsWindow rw;
	TextList tl;
	CardLayout cl;
	FFP ffp;
	File workingDirectory;
	ExecuteFFP exec;
	PhyloGraphView treeView;
	IGraphDrawer treeDrawer;

	public FFPController(GUI gui, TextList tl){
		this.gui = gui;
		this.tl = tl;
	}
	
	public FFPController(ResultsWindow rw){
		this.rw = rw;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub

		if(ae.getActionCommand().equals("Add text(s)")){
			addText();
		}
		else if(ae.getActionCommand().equals("Remove text(s)")){
			removeText();
		}
		else if(ae.getActionCommand().equals("Edit text name")){
			editText();
		}
		else if(ae.getActionCommand() == "Confirm"){
			runFFP();	
		}
		else if(ae.getActionCommand() == "Cancel"){
			ffp.cancel(true);
			exec.cancel(true);
			gui.switchCard(GUI.TEXTSETUPPANEL);
		}
		else if(ae.getActionCommand().equals("FFP style")){
			rw.displayTree();
			System.out.println("hey");
			//sendOutput(getTreePanel(rw.getSelectedOutputType()));	
		}
		else if(ae.getActionCommand().equals("Save image")){
			saveImage();
		}
		else if(ae.getActionCommand().equals("Save tree")){
			saveTree();
		}
		else if(ae.getActionCommand().equals("Open existing tree")){
			JFileChooser openTree = new JFileChooser();
			if(workingDirectory != null){
				openTree.setCurrentDirectory(workingDirectory);
			}
			
			int returnVal = openTree.showOpenDialog(gui);
			
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File f = openTree.getSelectedFile();
				
				openTree(f);
			}
			
			
		}
		else if(ae.getActionCommand().equals("Return to text setup")){
			rw.setVisible(false);
			gui.switchCard(GUI.TEXTSETUPPANEL);
			gui.setVisible(true);
		}
	}


	public void addText(){

		JFileChooser textSelect = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text file", "txt");
		textSelect.setFileFilter(filter);
		textSelect.setMultiSelectionEnabled(true);
		if(workingDirectory != null){
			textSelect.setCurrentDirectory(workingDirectory);
		}

		int returnVal = textSelect.showOpenDialog(gui);

		if (returnVal == JFileChooser.APPROVE_OPTION){
			File[] files = textSelect.getSelectedFiles();

			workingDirectory = files[0].getParentFile();

			for(File f: files){
				try {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					StringBuilder sb = new StringBuilder();

					String line = reader.readLine();
					while(line != null){
						sb.append(line);
						line = reader.readLine();
					}

					reader.close();
					//uses filename by default
					tl.addText(f.getName(), sb.toString().toLowerCase());

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void removeText(){

		int[] selected = gui.getSelectedTexts();

		if(tl.getTextListLength() == 0){
			JOptionPane.showMessageDialog(gui, "No texts to remove");
			return;
		}

		if(selected.length == 0){
			JOptionPane.showMessageDialog(gui, "Please select text(s) to remove.");
			return;
		}

		for(int i=selected.length - 1; i >= 0; i--){
			tl.removeText(selected[i]);
		}
	}

	public void editText(){

		int[] selected = gui.getSelectedTexts();

		if(selected.length == 0){
			JOptionPane.showMessageDialog(gui, "No text(s) selected for editing");
			return;
		}


		for(int i= 0; i < selected.length; i++){

			String title = JOptionPane.showInputDialog(gui, "Enter text title (if known)", tl.getText(i).getTitle());

			if(!title.equals("")){
				tl.setTitle(selected[i], title);
			}
		}
	}
	
	public void openTree(File f){
		
		Newick nw = new Newick();

		if(nw.isCorrectFileType(f)){
			try {
				TreeData[] tree = nw.read(f);
				rw.addTree(tree[0]);
				rw.displayTree();
				rw.setVisible(true);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			JOptionPane.showMessageDialog(gui, "Invalid tree format.");
		}
		
		
		
	}

	public void runFFP(){
		if(tl.getTextList().isEmpty()){

			JOptionPane.showMessageDialog(gui, "Please add texts for analysis.");
			return;
		}
		else if(tl.getTextList().size() == 1){
			JOptionPane.showMessageDialog(gui, "Please add at least two texts for comparison.");
			return;
		}

		gui.switchCard(GUI.PROCESSINGPANEL);
		exec = new ExecuteFFP();
		exec.execute();

	}

	public void saveImage(){

		JFileChooser saveFileChooser = new JFileChooser(workingDirectory);
		saveFileChooser.setFileFilter(new FileNameExtensionFilter("png", "png"));
		saveFileChooser.setAcceptAllFileFilterUsed(false);

		int returnVal = saveFileChooser.showSaveDialog(rw);

		if(returnVal == JFileChooser.APPROVE_OPTION){

			String fn = saveFileChooser.getSelectedFile().getAbsolutePath() + ".png";

			BufferedImage image = rw.getOutputGraphics();

			try {
				ImageIO.write(image, "png", new File(fn));
			} catch (IOException ex) {

			}
		}
	}
	
	public void saveTree(){
		
		JFileChooser saveFileChooser = new JFileChooser(workingDirectory);
		saveFileChooser.setFileFilter(new FileNameExtensionFilter("Newick", "new"));
		saveFileChooser.setAcceptAllFileFilterUsed(false);
		
		int returnVal = saveFileChooser.showSaveDialog(rw);
		
		if(returnVal == JFileChooser.APPROVE_OPTION){
			String fn = saveFileChooser.getSelectedFile().getAbsolutePath() + ".new";
			
			String outputText = ffp.getNewick();
			
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter(fn));
				bw.write(outputText);
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public PhyloGraphView getTreePanel(){
		
		
		treeView = new PhyloGraphView(ffp.getTree(), 750, 550);
			
		treeView.getScrollPane().setPreferredSize(new Dimension(1000, 800));
		treeView.getScrollPane().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		treeDrawer = new TreeDrawerRadial(treeView, ffp.getTree());
		treeDrawer.computeEmbedding(false);

		treeView.setGraphDrawer(treeDrawer);
		treeView.setCanvasColor(Color.WHITE);
		treeView.selectAllEdges(true);


		treeView.setLineWidthSelectedEdges((byte) 3.6);
		treeView.selectAllEdges(false);

		treeView.selectAllNodes(true);
		treeView.setSizeSelectedNodes((byte) 7, (byte) 7);
		treeView.selectAllNodes(false);

		treeView.setFont(new Font("SansSerif", Font.PLAIN, 15));
		treeView.setAutoLayoutLabels(true);

		treeView.setAllowEdit(true);

	
		return treeView;
	}
	

	
	public void addView(ResultsWindow rw){
		this.rw = rw;
	}

	private class ExecuteFFP extends SwingWorker<Void,Void> {

		@Override
		protected Void doInBackground() throws Exception {

			JLabel progress = gui.getProgressLabel();
			String current = "Preparing FFP";
			ffp = new FFP(tl, gui.getNgramValue());
			ffp.execute();

			while(!isCancelled()){
				while(!ffp.isComplete()){
					if(current.endsWith("...")){
						current = ffp.getStatus();
					}
					else{
						current += ".";
					}
					progress.setText(current);

					Thread.sleep(500);

				}
				this.cancel(true);
			}
			
			gui.setVisible(false);
			rw.addTree(ffp.getTree());
			rw.displayTree();
			rw.setVisible(true);
			return null;		
		}
	}
}

