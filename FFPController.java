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
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import dendroscope.core.TreeData;
import dendroscope.io.Newick;


/**
 * Controller for the SetupWindow and ResultsWindow classes.
 * 
 * @author David McLintock
 *
 */
public class FFPController implements ActionListener {

	private SetupWindow sw;
	private ResultsWindow rw;
	private TextList tl;
	private FFP ffp;
	private File workingDirectory;
	private ExecuteFFP exec;

	
	/**
	 * Default constructor for the FFP controller
	 */
	public FFPController(){
		
	}
	
	
	/**
	 * Default constructor for the controller
	 * @param tl
	 */
	public FFPController(SetupWindow sw, TextList tl){
		this.sw = sw;
		this.tl = tl;
	}
	
	
	/**
	 * Adds model
	 * @param tl the model to be added
	 */
	public void addModel(TextList tl){
		this.tl = tl;
	}
	
	
	/**
	 * Adds SetupWindow instance to controller 
	 * @param sw the main setup window
	 */
	public void addView(SetupWindow sw){
		this.sw = sw;
	}
	
	
	/**
	 * Adds ResultsWindow instance to controller
	 * @param rw
	 */
	public void addView(ResultsWindow rw){
		this.rw = rw;
	}
	

	/* 
	 * Responds to user input
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		
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
			sw.switchCard(SetupWindow.TEXTSETUPPANEL);
		}
		else if(ae.getActionCommand().equals("FFP style")){
			rw.displayTree();
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
			
			int returnVal = openTree.showOpenDialog(sw);
			
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File f = openTree.getSelectedFile();
				
				openTree(f);
			}
			
		}
		else if(ae.getActionCommand().equals("Return to text setup")){
			rw.setVisible(false);
			sw.switchCard(SetupWindow.TEXTSETUPPANEL);
			sw.setVisible(true);
		}
	}


	/**
	 * Gets texts to be added and sends to text list
	 */
	public void addText(){

		JFileChooser textSelect = new JFileChooser();
		
		//set valid file extensions
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text file", "txt");
		textSelect.setFileFilter(filter);
		textSelect.setMultiSelectionEnabled(true);
		
		//store current directory as system default
		if(workingDirectory != null){
			textSelect.setCurrentDirectory(workingDirectory);
		}

		int returnVal = textSelect.showOpenDialog(sw);

		if (returnVal == JFileChooser.APPROVE_OPTION){
			File[] files = textSelect.getSelectedFiles();

			workingDirectory = files[0].getParentFile();
			//for each selected file, read text and add to textlist in lower case
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
					//filename used as default
					tl.addText(f.getName(), sb.toString().toLowerCase());

				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(sw, "Could not open file", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(sw, "Could not open file", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	
	/**
	 * Gets texts to be removed from a list and sends to text list
	 */
	public void removeText(){

		int[] selected = sw.getSelectedTexts();
		//tests if no texts in list
		if(tl.getTextListLength() == 0){
			JOptionPane.showMessageDialog(sw, "No texts to remove");
			return;
		}
		//tests if no texts have been selected for removal
		if(selected.length == 0){
			JOptionPane.showMessageDialog(sw, "Please select text(s) to remove.");
			return;
		}
		
		//loop through selected texts and remove
		for(int i=selected.length - 1; i >= 0; i--){
			tl.removeText(selected[i]);
		}
	}

	
	/**
	 * Gets text to be edited and sends to text list
	 */
	public void editText(){

		int[] selected = sw.getSelectedTexts();
		
		//warn user if no texts selected
		if(selected.length == 0){
			JOptionPane.showMessageDialog(sw, "No text(s) selected for editing");
			return;
		}

		//loop through selected texts, prompting user to change default title
		for(int i= 0; i < selected.length; i++){

			String title = JOptionPane.showInputDialog(sw, "Enter text title (if known)", tl.getText(selected[i]).getTitle());
			if(title != null){
				tl.setTitle(selected[i], title);
			}
		}
	}
	
	
	/**
	 * Opens a tree file in newick format
	 * @param f the tree file to be opened
	 */
	public void openTree(File f){
		
		Newick nw = new Newick();
		//tests file format then converts to tree format
		if(nw.isCorrectFileType(f)){
			try {
				TreeData[] tree = nw.read(f);
				rw.addTree(tree[0]);
				rw.displayTree();
				rw.setVisible(true);
				
			} catch (IOException e) {
				JOptionPane.showMessageDialog(sw, "Could not open file", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else{
			JOptionPane.showMessageDialog(sw, "Invalid tree format.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	
	/**
	 * Tests input text size and starts FFP
	 */
	public void runFFP(){
		//prevents execution if no texts entered
		if(tl.getTextList().isEmpty()){

			JOptionPane.showMessageDialog(sw, "Please add texts for analysis.");
			return;
		}
		//prevents execution if only one text entered
		else if(tl.getTextList().size() == 1){
			JOptionPane.showMessageDialog(sw, "Please add at least two texts for comparison.");
			return;
		}
		//switch to processing panel and begin FFP process
		sw.switchCard(SetupWindow.PROCESSINGPANEL);
		exec = new ExecuteFFP();
		exec.execute();

	}

	
	/**
	 * Saves an .png of the current tree
	 */
	public void saveImage(){
		//gets filename and directory from user
		JFileChooser saveFileChooser = new JFileChooser(workingDirectory);
		saveFileChooser.setFileFilter(new FileNameExtensionFilter("png", "png"));
		saveFileChooser.setAcceptAllFileFilterUsed(false);

		int returnVal = saveFileChooser.showSaveDialog(rw);
		//saves image file
		if(returnVal == JFileChooser.APPROVE_OPTION){

			String fn = saveFileChooser.getSelectedFile().getAbsolutePath() + ".png";

			BufferedImage image = rw.getOutputGraphics();

			try {
				ImageIO.write(image, "png", new File(fn));
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(sw, "Unable to save file.");
			}
		}
	}
	
	
	/**
	 * Saves the current tree in newick format
	 */
	public void saveTree(){
		//gets filename and directory from user
		JFileChooser saveFileChooser = new JFileChooser(workingDirectory);
		saveFileChooser.setFileFilter(new FileNameExtensionFilter("Newick", "new"));
		saveFileChooser.setAcceptAllFileFilterUsed(false);
		
		int returnVal = saveFileChooser.showSaveDialog(rw);
		//saves tree file
		if(returnVal == JFileChooser.APPROVE_OPTION){
			String fn = saveFileChooser.getSelectedFile().getAbsolutePath() + ".new";
			
			String outputText = ffp.getNewick();
			
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter(fn));
				bw.write(outputText);
				bw.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(sw, "Tree save error.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	
	/**
	 * Creates FFP object and executes while updating progress
	 * @author David McLintock
	 *
	 */
	private class ExecuteFFP extends SwingWorker<Void,Void> {

		@Override
		protected Void doInBackground() {

			JLabel progress = sw.getProgressLabel();
			String current = "Preparing FFP";
			ffp = new FFP(tl, sw.getNgramValue());
			ffp.execute();
			//while process not cancelled
			while(!isCancelled()){
				while(!ffp.isComplete()){
					if(current.endsWith("...")){
						current = ffp.getStatus();
					}
					else{
						current += ".";
					}
					progress.setText(current);
					//allow time for next update
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						//do nothing
					}

				}
				this.cancel(true);
			}
			
			//hide setup panel, add tree to results window and make visible
			sw.setVisible(false);
			rw.addTree(ffp.getTree());
			rw.displayTree();
			rw.setVisible(true);
			return null;		
		}
	}
}

