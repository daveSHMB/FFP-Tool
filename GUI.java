
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
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;


import jloda.graphview.*;
import jloda.phylo.*;



public class GUI extends JFrame implements ActionListener, Observer{

	private JPanel cardHolder, setupOptionsCard, textSetupCard, processingCard, FFPResultsCard, textSetup, textOperations, finalise, editAndConfirm, optionsPanel,
	FFPSettings, editOptions, resultsPanel, outputOptions, output;
	private JList<String> texts;
	private Border textTitle, optionsPanelBorder;
	private JLabel addText, ngramLengthLbl, timeLabel;
	private JButton add, remove, edit, confirm, options, confirmOptions, cancelProcess, resultsBack, save;
	private JSpinner ngramLength;
	private JFileChooser textSelect;
	private JComboBox<String> ffpStyle;
	private JScrollPane treeDisplay;
	private PhyloGraphView treeView; 

	private CardLayout cl;

	private ProcessTimer t;
	private FFP ffp;
	
	private PhyloTree tree;

	private IGraphDrawer treeDrawer;

	//CONSISTENCY OF VARIABLES EH
	private String[] ffpOutputOpts = {"Radial", "Parallel", "Circular", "Angled"};


	FFPController con;

	//Do these need to be static???
	public final static String TEXTSETUPPANEL = "Tab for text addition";
	public final static String OPTIONSPANEL = "Tab for editing options";
	public final static String PROCESSINGPANEL = "Tab shown while data processing";
	public final static String FFPRESULTSPANEL = "Tab shown when processing complete";

	//move the below declarations???
	private TextList tl = new TextList();
	private Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	public GUI(){
		
		
		super("FFP Tool 0.1");
		con = new FFPController(this, tl);
		tl.addObserver(this);
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


	public void setupFFPOutput(PhyloTree tree){
		
		this.tree = tree;
		
		FFPResultsCard = new JPanel();
		FFPResultsCard.add(FFPresults());
		cardHolder.add(FFPResultsCard, FFPRESULTSPANEL);
		cl.show(cardHolder,  FFPRESULTSPANEL);

		pack();
		setLocationRelativeTo(null);

	}

	public JPanel textSetupPanel(){
		textSetup = new JPanel(new BorderLayout());
		textSetup.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		textTitle = BorderFactory.createTitledBorder(loweredEtched, "1. Add texts for comparison");
		textSetup.setBorder(textTitle);
		texts = new JList<String>();
		texts.setVisibleRowCount(13);
		JScrollPane textsPane = new JScrollPane(texts);
		textsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textsPane.setPreferredSize(new Dimension(100,200));
		
		String addTextInfo = "<html><p></p><center><p>Texts should be stored as plain .txt files. For a more accurate</p> <p>analysis "
				+ " any titles, chapter markers or additional content </p><p>such as introductions or footnotes "
				+ "should first be removed.</p></center></html>";
		
		addText = new JLabel(addTextInfo);
	

		editAndConfirm = new JPanel(new GridLayout(2,1));

		textOperations = new JPanel(new FlowLayout());

		add = new JButton("Add text(s)");
		add.addActionListener(con);
		remove = new JButton("Remove text(s)");
		remove.addActionListener(con);
		edit = new JButton("Edit author/title");
		edit.addActionListener(con);
		textOperations.add(add);

		textOperations.add(remove);
		textOperations.add(edit);

		finalise = new JPanel(new FlowLayout());
		confirm = new JButton("Confirm");
		confirm.addActionListener(con);
		options = new JButton("Options");
		options.addActionListener(con);
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


		optionsPanel = new JPanel(new BorderLayout(1,6));
		
		//FFPSettings = new JPanel(new GridLayout(3,1));
		optionsPanelBorder = BorderFactory.createTitledBorder(loweredEtched, "Edit Analysis Parameters");
		optionsPanel.setBorder(optionsPanelBorder);
		
		String ngramInfo = "<html><p></p><center><p>Ngrams are strings of characters generated from input</p><p>texts. "
				+ "The occurrence of each feature is</p><p>counted and the divergence of each count is measured.</p> "
				+ "<p>Optimal ngram length varies from text to text though most </p><p>often falls between 9 and 10</p></center></html>";
		
		
		
		FFPSettings = new JPanel(new FlowLayout());
		ngramLengthLbl = new JLabel("Enter ngram length:");
		FFPSettings.add(ngramLengthLbl);
		
		SpinnerModel ngramModel = new SpinnerNumberModel(9, 2, 20, 1); 
		ngramLength = new JSpinner(ngramModel);
		FFPSettings.add(ngramLength);

		editOptions = new JPanel(new GridLayout(2,1));
		//editOptions.add(PCASettings);
		editOptions.add(FFPSettings);
		
		
		confirmOptions = new JButton("Confirm and Begin FFP");
		confirmOptions.addActionListener(this);
		
		optionsPanel.add(new JLabel(ngramInfo));
		optionsPanel.add(editOptions);
		optionsPanel.add(confirmOptions);


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

		//PhyloTree tree = ffp.getTree();
		
		
		
		
		treeView = new PhyloGraphView(tree, 800 - 17, 600 - 17);
		treeView.getScrollPane().setPreferredSize(new Dimension(1000,800));

		String style = ffpOutputOpts[ffpStyle.getSelectedIndex()];
		switch(style){
		case "Radial":
			treeDrawer = new TreeDrawerRadial(treeView, tree);
			break;
		case "Parallel":
			treeDrawer = new TreeDrawerParallel(treeView, tree);
			treeView.setSize(new Dimension(700, 200));
			break;
		case "Circular":
			treeDrawer = new TreeDrawerCircular(treeView, tree);
			break;
		case "Angled":
			treeDrawer = new TreeDrawerAngled(treeView, tree);
		}
		

		treeDrawer.computeEmbedding(true);
		
		treeView.setGraphDrawer(treeDrawer);

		treeView.setCanvasColor(Color.WHITE);
		treeView.selectAllEdges(true);
		
		//treeView.setLabelVisibleSelectedEdges(true);
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
		
		
		//FIX WINDOW CENTERING
		treeView.trans.setCoordinateRect(treeView.getBBox());
		treeView.fitGraphToWindow();
		treeView.centerGraph();

		//treeView.trans.fitToSize(new Dimension(800, 600));
		
		//treeView.trans.fireHasChanged();
		
		
		return treeView;


	}
	
	public void switchCard(String cardName){
		cl.show(cardHolder, cardName);
	}
	
	public int getNgramValue(){
		
		//if blank use default value - this will change when I DEFINITELY add input validation
		if(ngramLength.getValue().equals("")){
			return 5;
		}
		return (int)ngramLength.getValue();
	}
	
	public String getSelectedOutputType(){
		return ffpOutputOpts[ffpStyle.getSelectedIndex()];
	}
	
	public int[] getSelectedTexts(){
		return texts.getSelectedIndices();
	}
	
	public void processingStart(){
		t = new ProcessTimer();
		t.execute();
	}

	public void actionPerformed(ActionEvent ae) {

		if(ae.getSource() == options){
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


	@Override
	public void update(Observable obs, Object obj) {

		if(obs instanceof TextList){
			System.out.println("Alright");
			texts.setListData(tl.getTextDetails());
		}
	}

}
