import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;


public class FFPController implements ActionListener {

	GUI gui;
	TextList tl;
	CardLayout cl;
	FFP ffp;
	File workingDirectory;
	ExecuteFFP exec;

	public FFPController(GUI gui, TextList tl){
		this.gui = gui;
		this.tl = tl;
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
			gui.refreshOutput();	
		}
		else if(ae.getActionCommand().equals("Save image")){
			saveImage();
		}
		else if(ae.getActionCommand().equals("Return to text setup")){
			gui.switchCard(GUI.TEXTSETUPPANEL);
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

		int returnVal = saveFileChooser.showSaveDialog(gui);

		if(returnVal == JFileChooser.APPROVE_OPTION){

			String fn = saveFileChooser.getSelectedFile().getAbsolutePath() + ".png";

			BufferedImage image = gui.getOutputGraphics();

			try {
				ImageIO.write(image, "png", new File(fn));
			} catch (IOException ex) {

			}
		}
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
			gui.setupFFPOutput(ffp.getTree());
			gui.switchCard(GUI.FFPRESULTSPANEL);

			return null;		
		}
	}
}

