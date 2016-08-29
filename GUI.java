
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



public class GUI extends JFrame implements Observer{

	private JPanel cardHolder, setupOptionsCard, textSetupCard, processingCard, FFPResultsCard, textSetup, textOperations, finalise, editAndConfirm, optionsPanel,
	FFPSettings, editOptions, resultsPanel, outputOptions, output, ngramPanel;
	private JList<String> texts;
	private Border textTitle, optionsPanelBorder;
	private JLabel addText, ngramLengthLbl, timeLabel;
	private JButton add, remove, edit, confirm, options, confirmOptions, cancelProcess, resultsBack, save, textHelp, ngramHelp;
	private JSpinner ngramLength;
	private JComboBox<String> ffpStyle;
	private JScrollPane treeDisplay;
	private PhyloGraphView treeView; 

	private CardLayout cl;
	private ProgressLabel pl;
	private PhyloTree tree;
	private IGraphDrawer treeDrawer;

	//CONSISTENCY OF VARIABLES EH
	private String[] ffpOutputOpts = {"Radial", "Parallel", "Circular", "Angled"};


	private FFPController con;

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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setVisible(true);

	}


	public void layoutComponents(){


		setupOptionsCard = new JPanel();
		setupOptionsCard.add(setupOptions());

		textSetupCard = new JPanel();
		textSetupCard.add(setupDashboard());

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
	
	public JPanel setupDashboard(){
		JPanel dashFrame = new JPanel(new BorderLayout());
		dashFrame.add(textSetupPanel(), BorderLayout.NORTH);
		dashFrame.add(setupOptions(), BorderLayout.CENTER);
		finalise = new JPanel(new FlowLayout());
		confirm = new JButton("Confirm");
		confirm.addActionListener(con);
		finalise.add(confirm);
		dashFrame.add(finalise, BorderLayout.SOUTH);
		JSeparator separator = new JSeparator();
		
		return dashFrame;
	}

	public JPanel textSetupPanel(){
		textSetup = new JPanel(new BorderLayout());
		textSetup.setBorder(new EmptyBorder(20, 20, 20, 20));

		textTitle = BorderFactory.createTitledBorder(loweredEtched, "1. Add texts for comparison");
		textSetup.setBorder(textTitle);
		texts = new JList<String>();
		texts.setVisibleRowCount(15);
		JScrollPane textsPane = new JScrollPane(texts);
		textsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textsPane.setPreferredSize(new Dimension(100,200));

		String addTextInfo = "<html><p></p><center><p>Texts should be stored as plain .txt files. For a more accurate</p> <p>analysis "
				+ " any titles, chapter markers or additional content </p><p>such as introductions or footnotes "
				+ "should first be removed.</p></center></html>";

		//addText = new JLabel(addTextInfo);

		editAndConfirm = new JPanel();

		textOperations = new JPanel(new FlowLayout());

		add = new JButton("Add text(s)");
		
		add.addActionListener(con);
		remove = new JButton("Remove text(s)");
		remove.addActionListener(con);
		edit = new JButton("Edit author/title");
		edit.addActionListener(con);
		textHelp = new JButton("?");
		textHelp.setFont(new Font("SansSerif", Font.BOLD, 12));
		textHelp.setForeground(Color.BLUE);
		textHelp.addActionListener(con);
		textOperations.add(add);
		

		textOperations.add(remove);
		textOperations.add(edit);
		textOperations.add(textHelp);
		
		editAndConfirm.add(new JLabel());
		editAndConfirm.add(textOperations);
		//editAndConfirm.add(finalise);

		//textSetup.add("North", addText);
		textSetup.add("North", textsPane);
		textSetup.add("Center", editAndConfirm);
		
		

		return textSetup;
	}

	public JPanel setupOptions(){


		optionsPanel = new JPanel();
		optionsPanel.add(new JLabel());

		//optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 80));
		optionsPanelBorder = BorderFactory.createTitledBorder(loweredEtched, "2. Edit Analysis Parameters");
		optionsPanel.setBorder(optionsPanelBorder);
		
		ngramPanel = new JPanel(new FlowLayout());
		SpinnerModel ngramModel = new SpinnerNumberModel(9, 2, 20, 1); 
		ngramLength = new JSpinner(ngramModel);
		
		
		ngramHelp = new JButton("?");
		ngramHelp.setFont(new Font("SansSerif", Font.BOLD, 12));
		ngramHelp.setForeground(Color.BLUE);
		ngramHelp.addActionListener(con);
		
		
		//ngramLengthLbl = new JLabel("Edit ngram size:");
		ngramPanel.add(new JLabel("Edit ngram size: "));
		ngramPanel.add(ngramLength);
		ngramPanel.add(ngramHelp);
		
		String ngramInfo = "<html><p></p><center><p>Ngrams are strings of characters generated from input</p><p>texts. "
				+ "The occurrence of each feature is</p><p>counted and the divergence of each count is measured.</p> "
				+ "<p>Optimal ngram length varies from text to text though most </p><p>often falls between 9 and 10.</p></center></html>";

		optionsPanel.add("Center", ngramPanel);
		//optionsPanel.add(finalise, BorderLayout.SOUTH);

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
		cancelProcess.addActionListener(con);
		optionsPanel.add(timeLabel);

		optionsPanel.add(cancelProcess);


		return optionsPanel;
	}

	public JPanel FFPresults(){

		//cancel timer
		pl.cancel(true);

		outputOptions = new JPanel(new GridLayout(8,1));
		outputOptions.add(new JLabel("Change output style: "));

		if(ffpStyle == null){
			ffpStyle = new JComboBox<String>(ffpOutputOpts);
			ffpStyle.setActionCommand("FFP style");
			ffpStyle.addActionListener(con);
		}
		outputOptions.add(ffpStyle);
		save = new JButton("Save image");
		save.addActionListener(con);
		outputOptions.add(save);

		outputOptions.add(new JButton("ONE"));
		outputOptions.add(new JLabel(""));

		resultsBack = new JButton("Return to text setup");
		outputOptions.add(resultsBack);
		resultsBack.addActionListener(con);
		
		

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

	public void refreshOutput(){
		output.remove(treeView.getScrollPane());
		resultsPanel.remove(outputOptions);
		treeView = getTreePanel();
		output.add(treeView.getScrollPane());
		resultsPanel.add(outputOptions);
		pack();
		resultsPanel.revalidate();
		resultsPanel.repaint();	
	}

	public void switchCard(String cardName){
		cl.show(cardHolder, cardName);
		System.out.println(cardName);
		if(!cardName.equals(FFPRESULTSPANEL)){
			this.setSize(new Dimension(500,225));
			this.pack();
		}
//		this.pack();
	}

	public int getNgramValue(){
		return (int)ngramLength.getValue();
	}

	public String getSelectedOutputType(){
		return ffpOutputOpts[ffpStyle.getSelectedIndex()];
	}

	public int[] getSelectedTexts(){
		return texts.getSelectedIndices();
	}

	public BufferedImage getOutputGraphics(){
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
		return image;
	}

	public void progressLabelStart(){
		pl = new ProgressLabel();
		pl.execute();
	}

	public void progressLabelStop(){
		pl.cancel(true);
	}
	
	@Override
	public void update(Observable obs, Object obj) {

		if(obs instanceof TextList){
			texts.setListData(tl.getTextDetails());
		}
	}

	private class ProgressLabel extends SwingWorker<Void,Void> {

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
