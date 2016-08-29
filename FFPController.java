import java.awt.CardLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import javax.swing.JFileChooser;
import javax.swing.JOptionPane;



public class FFPController implements ActionListener {

	GUI gui;
	TextList tl;
	CardLayout cl;
	FFP ffp;
	

	public FFPController(GUI gui, TextList tl){
		this.gui = gui;
		this.tl = tl;
	}


	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub


		if(ae.getActionCommand().equals("Add text(s)")){
			addText();
			System.out.println("ADD!");
		}
		else if(ae.getActionCommand().equals("Remove text(s)")){
			removeText();
		}
		else if(ae.getActionCommand().equals("Edit author/title")){
			editText();
		}
		else if(ae.getActionCommand().equals("Options")){
			gui.switchCard(GUI.OPTIONSPANEL);
		}
		else if(ae.getActionCommand() == "Confirm"){
						
						if(tl.getTextList().isEmpty()){
							
							JOptionPane.showMessageDialog(gui, "Please add texts for analysis.");
							return;
						}
						else if(tl.getTextList().size() == 1){
							JOptionPane.showMessageDialog(gui, "Please add at least two texts for comparison.");
							return;
						}
						
						gui.switchCard(GUI.PROCESSINGPANEL);
						gui.processingStart();
						
						ffp = new FFP(tl, gui.getNgramValue());
						ffp.execute();
						
						//test periodically if FFP has completed
						new Thread(){
							@Override
							public void run() {
								while (!ffp.isComplete()){
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								//setup and display output
								gui.setupFFPOutput(ffp.getTree());
							}
						}.start();
		}
		//
		//		else if(ae.getActionCommand() == "options"){
		//			gui.switchCard(GUI.OPTIONSPANEL);
		//			//this.setLocationRelativeTo(null);
		//		}
		//		else if(ae.getActionCommand() == "confirmOptions"){
		//			gui.switchCard(GUI.TEXTSETUPPANEL);
		//		}
		//		else if(ae.getActionCommand() == "cancelProcess"){
		//			ffp.cancel(true);
		//			//t.cancel(true);
		//			gui.switchCard(GUI.TEXTSETUPPANEL);
		//		}
		//		else if(ae.getActionCommand() == "Back"){
		//			cl.show(cardHolder, TEXTSETUPPANEL);
		//		}
		//		else if(ae.getSource() == ffpStyle){
		//			output.remove(treeView.getScrollPane());
		//			resultsPanel.remove(outputOptions);
		//			treeView = getTreePanel();
		//			output.add(treeView.getScrollPane());
		//			resultsPanel.add(outputOptions);
		//			pack();
		//			resultsPanel.revalidate();
		//			resultsPanel.repaint();		
		//		}
		//		else if(ae.getSource() == save){
		//			saveImage();
	}


	public void addText(){

		JFileChooser textSelect = new JFileChooser();
		textSelect.setMultiSelectionEnabled(true);
		int returnVal = textSelect.showOpenDialog(gui);

		if (returnVal == JFileChooser.APPROVE_OPTION){
			File[] files = textSelect.getSelectedFiles();

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

			String author = (String)JOptionPane.showInputDialog(gui, "Enter author's name (if known");
			String title = (String)JOptionPane.showInputDialog(gui, "Enter text title (if known)");


			if(!author.equals("")){
				tl.setAuthor(selected[i], author);
			}
			if(!title.equals("")){
				tl.setTitle(selected[i], title);
			}
		}
	}


	public void saveImage(){
		//		treeView.trans.setCoordinateRect(treeView.getBBox());
		//	
		//		treeView.fitGraphToWindow();
		//		//treeView.centerGraph();

		//		Rectangle2D rect = treeView.getVisibleRect();
		//		
		//		int x = (int)rect.getX();
		//		int y = (int)rect.getY();
		//		
		//		//get scrollbar height and width
		//		int scrollbarWidthHeight = ((Integer)UIManager.get("ScrollBar.width")).intValue(); 
		//		
		//		BufferedImage image = new BufferedImage(1000 - scrollbarWidthHeight, 800 - scrollbarWidthHeight, BufferedImage.TYPE_INT_ARGB);
		//		Graphics g = image.getGraphics();
		//		g.translate(-x, -y);
		//		g.setClip(rect);
		//		g.fillRect(x, y, (int)rect.getWidth(), (int)rect.getHeight());
		//		treeView.getPanel().paint(g);
		//		 try {
		//		        ImageIO.write(image, "png", new File("dicks.png"));
		//		    } catch (IOException ex) {
		//		        
		//		   }
	}
}

