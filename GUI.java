
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import java.awt.Dimension;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;



import jloda.graphview.*;
import jloda.phylo.*;



public class GUI extends JFrame implements ActionListener{

	private JPanel cardHolder, setupOptionsCard, textSetupCard, processingCard, FFPResultsCard, textSetup, textOperations, finalise, editAndConfirm, optionsPanel,
	PCASettings, FFPSettings, editOptions, resultsPanel, outputOptions, output;
	private JList<String> texts;
	private Border textTitle, PCASettingsBorder, FFPSettingsBorder;
	private JLabel addText, ngramLengthLbl, timeLabel;
	private JButton add, remove, edit, confirm, options, confirmOptions, cancelProcess, resultsBack, save;
	private JTextField ngramLength;
	private JFileChooser textSelect;
	private JComboBox<String> ffpStyle;
	private JScrollPane treeDisplay;
	private PhyloGraphView treeView; 

	private CardLayout cl;

	private ProcessTimer t;
	private FFP ffp;

	private IGraphDrawer treeDrawer;

	//CONSISTENCY OF VARIABLES EH
	private String[] ffpOutputOpts = {"Radial", "Parallel", "Circular", "Angled"};




	//Do these need to be static???
	private final static String TEXTSETUPPANEL = "Tab for text addition";
	private final static String OPTIONSPANEL = "Tab for editing options";
	private final static String PROCESSINGPANEL = "Tab shown while data processing";
	private final static String FFPRESULTSPANEL = "Tab shown when processing complete";

	//move the below declarations???
	private TextList tl = new TextList();
	private Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	public GUI(){

		super("FFP Tool 0.1");

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		layoutComponents();
		add(cardHolder);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);

	}


	public void layoutComponents(){


		setupOptionsCard = new JPanel();
		setupOptionsCard.add(setupOptions());

		textSetupCard = new JPanel();
		textSetupCard.add(textSetupPanel());

		processingCard = new JPanel();
		processingCard.add(processing());




		cardHolder = new JPanel(new CardLayout());

		cardHolder.add(textSetupCard, TEXTSETUPPANEL);
		cardHolder.add(setupOptionsCard, OPTIONSPANEL);
		cardHolder.add(processingCard, PROCESSINGPANEL);

		cl = (CardLayout)cardHolder.getLayout();
		cl.show(cardHolder,  TEXTSETUPPANEL);



	}


	public void setupFFPOutput(){

		FFPResultsCard = new JPanel();
		FFPResultsCard.add(FFPresults());
		cardHolder.add(FFPResultsCard, FFPRESULTSPANEL);
		cl.show(cardHolder,  FFPRESULTSPANEL);

		pack();
		setLocationRelativeTo(null);

	}

	public JPanel textSetupPanel(){
		textSetup = new JPanel(new BorderLayout());
		
		textTitle = BorderFactory.createTitledBorder(loweredEtched, "1. Add texts for comparison");
		textSetup.setBorder(textTitle);
		texts = new JList<String>(tl.getTextDetails());
		texts.setVisibleRowCount(13);
		JScrollPane textsPane = new JScrollPane(texts);
		textsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textsPane.setPreferredSize(new Dimension(100,200));

		addText = new JLabel("Add texts for analysis. Accepted formats are X, Y, Z.");

		editAndConfirm = new JPanel(new GridLayout(2,1));

		textOperations = new JPanel(new FlowLayout());

		add = new JButton("Add text(s)");
		add.addActionListener(this);
		remove = new JButton("Remove text(s)");
		remove.addActionListener(this);
		edit = new JButton("Edit author/title");
		edit.addActionListener(this);
		textOperations.add(add);

		textOperations.add(remove);
		textOperations.add(edit);

		finalise = new JPanel(new FlowLayout());
		confirm = new JButton("Confirm");
		confirm.addActionListener(this);
		options = new JButton("Options");
		options.addActionListener(this);
		finalise.add(options);
		finalise.add(confirm);

		editAndConfirm.add(textOperations);
		editAndConfirm.add(finalise);

		textSetup.add("North", addText);
		textSetup.add("Center", textsPane);
		textSetup.add("South", editAndConfirm);

		return textSetup;
	}

	public JPanel setupOptions(){


		optionsPanel = new JPanel(new BorderLayout());
		
		FFPSettings = new JPanel(new FlowLayout());
		FFPSettingsBorder = BorderFactory.createTitledBorder(loweredEtched, "FFP Options");
		FFPSettings.setBorder(FFPSettingsBorder);
		ngramLengthLbl = new JLabel("Enter ngram length:");
		FFPSettings.add(ngramLengthLbl);
		ngramLength = new JTextField(20);
		FFPSettings.add(ngramLength);

		editOptions = new JPanel(new GridLayout(2,1));
		//editOptions.add(PCASettings);
		editOptions.add(FFPSettings);

		JPanel confirmOptionsPanel = new JPanel();
		confirmOptions = new JButton("Confirm");
		confirmOptions.addActionListener(this);
		confirmOptionsPanel.add(confirmOptions);

		optionsPanel.add("North", editOptions);
		optionsPanel.add("Center", confirmOptionsPanel);


		//YOU CAN CHANGE DIMENSIONS LIKE THIS WOOOOOOP
		//this.setPreferredSize(new Dimension(550,225));
		//this.pack();
		return optionsPanel;
	}

	public JPanel processing(){
		optionsPanel = new JPanel(new GridLayout(3,1));

		optionsPanel.add(new JLabel("Processing, may take some time"));
		timeLabel = new JLabel("");
		//timeLabel.setFont (timeLabel.getFont().deriveFont(64.0f));
		cancelProcess = new JButton("Cancel");
		cancelProcess.addActionListener(this);
		optionsPanel.add(timeLabel);

		optionsPanel.add(cancelProcess);


		return optionsPanel;
	}

	public JPanel FFPresults(){

		//cancel timer
		t.cancel(true);

		outputOptions = new JPanel(new GridLayout(8,1));
		outputOptions.add(new JLabel("Change output style: "));

		if(ffpStyle == null){
			ffpStyle = new JComboBox<String>(ffpOutputOpts);
			ffpStyle.addActionListener(this);
		}
		outputOptions.add(ffpStyle);
		save = new JButton("Save image");
		save.addActionListener(this);
		outputOptions.add(save);
		
		outputOptions.add(new JButton("ONE"));
		outputOptions.add(new JLabel(""));
	
		resultsBack = new JButton("Back");
		outputOptions.add(resultsBack);
		resultsBack.addActionListener(this);
		outputOptions.add(new JLabel(""));
		outputOptions.add(new JButton("TWOY"));
		

		treeView = getTreePanel();

		resultsPanel = new JPanel();

		resultsPanel.addKeyListener(treeView.getGraphViewListener());

		resultsPanel.setLayout(new FlowLayout());

		if(treeDisplay != null){
			resultsPanel.remove(treeDisplay);
		}
		
		//TODO consistency of border layout stuff
		output = new JPanel(new BorderLayout());
		output.add(treeView.getScrollPane(), BorderLayout.CENTER);
		
		resultsPanel.add(output);
		resultsPanel.add(outputOptions);

		return resultsPanel;

	}

	public PhyloGraphView getTreePanel(){

		PhyloTree tree = ffp.getTree();
	
		treeView = new PhyloGraphView(tree, 1000 - 17, 800 - 17);
		treeView.getScrollPane().setPreferredSize(new Dimension(1000,800));

		String style = ffpOutputOpts[ffpStyle.getSelectedIndex()];
		switch(style){
		case "Radial":
			treeDrawer = new TreeDrawerRadial(treeView, tree);
			break;
		case "Parallel":
			treeDrawer = new TreeDrawerParallel(treeView, tree);
			break;
		case "Circular":
			treeDrawer = new TreeDrawerCircular(treeView, tree);
			break;
		case "Angled":
			treeDrawer = new TreeDrawerAngled(treeView, tree);
		}


		treeDrawer.computeEmbedding(false);
		treeView.setGraphDrawer(treeDrawer);

		treeView.setCanvasColor(Color.WHITE);
		treeView.selectAllEdges(true);
		treeView.setLineWidthSelectedEdges((byte) 3.6);
		treeView.selectAllEdges(false);
		
		treeView.selectAllNodes(true);
		//tv.getSelectedEdges();
		//tv.setShapeSelectedNodes((byte) 2);
		treeView.setSizeSelectedNodes((byte) 7, (byte) 7);
		treeView.selectAllNodes(false);
		
		treeView.setFont(new Font("SansSerif", Font.PLAIN, 15));
		treeView.setAutoLayoutLabels(true);
		//tv.setAllowRotation(true);
		treeView.setAllowEdit(true);
		

//		treeView.getScrollPane().addComponentListener(new ComponentAdapter() {
//			public void componentResized(ComponentEvent event) {
//				{
//					if (treeView.getScrollPane().getSize().getHeight() > 600 && treeView.getScrollPane().getSize().getWidth() > 600)
//						treeView.fitGraphToWindow();
//					else
//						treeView.trans.fireHasChanged();
//				}
//			}
//		});

		
		
		//
	
		//
		
		
		//FIX WINDOW CENTERING
		treeView.trans.setCoordinateRect(treeView.getBBox());
		treeView.fitGraphToWindow();
		treeView.centerGraph();

		//treeView.trans.fitToSize(new Dimension(800, 600));
		
		treeView.trans.fireHasChanged();
		
		
		return treeView;


	}

	public void actionPerformed(ActionEvent ae) {

		if(ae.getSource() == add){
			addText();
		}
		else if(ae.getSource() == remove){
			removeText();
		}
		else if(ae.getSource() == edit){
			editText();
		}
		else if(ae.getSource() == confirm){

			cl.show(cardHolder, PROCESSINGPANEL);
			t = new ProcessTimer();
			t.execute();

			if(!ngramLength.getText().equals("")){
				ffp = new FFP(tl, (Integer.parseInt(ngramLength.getText())));
			}
			else{
				ffp = new FFP(tl, 5);
			}

			ffp.execute();
			

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
					setupFFPOutput();
				}
			}.start();
		}

		else if(ae.getSource() == options){
			cl.show(cardHolder, OPTIONSPANEL);
			//this.setLocationRelativeTo(null);
		}
		else if(ae.getSource() == confirmOptions){
			cl.show(cardHolder, TEXTSETUPPANEL);
		}
		else if(ae.getSource() == cancelProcess){
			ffp.cancel(true);
			t.cancel(true);
			cl.show(cardHolder, TEXTSETUPPANEL);
		}
		else if(ae.getSource() == resultsBack){
			cl.show(cardHolder, TEXTSETUPPANEL);
		}
		else if(ae.getSource() == ffpStyle){
			output.remove(treeView.getScrollPane());
			resultsPanel.remove(outputOptions);
			treeView = getTreePanel();
			output.add(treeView.getScrollPane());
			resultsPanel.add(outputOptions);
			pack();
			resultsPanel.revalidate();
			resultsPanel.repaint();		
		}
		else if(ae.getSource() == save){
			saveImage();
		}
	}


	public void addText(){
		textSelect = new JFileChooser();
		textSelect.setMultiSelectionEnabled(true);
		int returnVal = textSelect.showOpenDialog(this);

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
					texts.setListData(tl.getTextDetails());

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

		int[] selected = texts.getSelectedIndices();

		for(int i=selected.length - 1; i >= 0; i--){
			tl.removeText(selected[i]);
		}

		texts.setListData(tl.getTextDetails());

	}

	public void editText(){

		if(texts.getSelectedIndex() != -1){

			String author = (String)JOptionPane.showInputDialog(this, "Enter author's name (if known");
			String title = (String)JOptionPane.showInputDialog(this, "Enter text title (if known)");

			if(author != null){
				tl.getText(texts.getSelectedIndex()).setAuthor(author);
			}

			if(title != null){
				tl.getText(texts.getSelectedIndex()).setTitle(title);
			}

			texts.setListData(tl.getTextDetails());

		}
		else{
			JOptionPane.showMessageDialog(this, "First select a text from the list above");
		}
	}
	
	
	public void saveImage(){
//		treeView.trans.setCoordinateRect(treeView.getBBox());
//	
//		treeView.fitGraphToWindow();
//		//treeView.centerGraph();
		
		Rectangle2D rect = treeView.getVisibleRect();
		
		int x = (int)rect.getX();
		int y = (int)rect.getY();
		
		//get scrollbar height and width
		int scrollbarWidthHeight = ((Integer)UIManager.get("ScrollBar.width")).intValue(); 
		
		BufferedImage image = new BufferedImage(1000 - scrollbarWidthHeight, 800 - scrollbarWidthHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.translate(-x, -y);
		g.setClip(rect);
		g.fillRect(x, y, (int)rect.getWidth(), (int)rect.getHeight());
		treeView.getPanel().paint(g);
		 try {
		        ImageIO.write(image, "png", new File("dicks.png"));
		    } catch (IOException ex) {
		        
		   }
	}


	//this could probably be refactored into the actionlistener for the confirm button
	private class ProcessTimer extends SwingWorker<Void,Void> {

		protected Void doInBackground() throws Exception {

			String processing = "Processing...";

			while(!isCancelled()){
				
				for(int i = 10; i <= processing.length(); i++){
					timeLabel.setText(processing.substring(0, i));
					Thread.sleep(500);
				}
			}
			
			return null;		
		}
	}

}
