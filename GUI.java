
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import java.awt.Dimension;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

import jloda.graph.Edge;
import jloda.graphview.*;
import jloda.phylo.*;



public class GUI extends JFrame implements Observer{

	private JPanel cardHolder, setupOptionsCard, textSetupCard, processingCard, FFPResultsCard, textSetup, textOperations, finalise, editAndConfirm, optionsPanel,
	FFPSettings, treePanel, resultsPanel, outputPanel, output, ngramPanel, outputStylePanel, displayOptions, treeHelpPanel;
	private JList<String> texts;
	private Border textTitle, optionsPanelBorder;
	private JLabel addText, ngramLengthLbl, timeLabel;
	private JButton add, remove, edit, confirm, options, confirmOptions, cancelProcess, resultsBack, save;
	JButton textHelp, treeHelp;
	JButton ngramHelp;
	private JSpinner ngramLength;
	private JComboBox<String> ffpStyle;
	private JScrollPane treeDisplay;
	private PhyloGraphView treeView; 

	private CardLayout cl;
	private ProgressLabel pl;
	private PhyloTree phyloTree, cladoTree;
	private IGraphDrawer treeDrawer;

	//CONSISTENCY OF VARIABLES EH
	private String[] ffpOutputOpts = {"Radial Phylogram", "Radial Cladogram", "Parallel Phylogram", "Parallel Cladogram", "Circular Phylogram", "Circular Cladogram", "Angled Phylogram", "Angled Cladogram"};


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

		textSetupCard = new JPanel();
		textSetupCard.add(setupDashboard());

		processingCard = new JPanel();
		processingCard.add(processing());

		cardHolder = new JPanel(new CardLayout());
		cardHolder.add(textSetupCard, TEXTSETUPPANEL);

		cardHolder.add(processingCard, PROCESSINGPANEL);

		cl = (CardLayout)cardHolder.getLayout();
		cl.show(cardHolder,  TEXTSETUPPANEL);

	}

	public void setupFFPOutput(PhyloTree tree){

		this.phyloTree = tree;
		this.cladoTree = cladogramFormat((PhyloTree)tree.clone());

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

		return dashFrame;
	}

	public JPanel textSetupPanel(){
		textSetup = new JPanel(new BorderLayout());


		textTitle = BorderFactory.createTitledBorder(loweredEtched, "1. Add texts for comparison");
		textSetup.setBorder(textTitle);
		texts = new JList<String>();
		texts.setVisibleRowCount(15);
		JScrollPane textsPane = new JScrollPane(texts);
		textsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textsPane.setPreferredSize(new Dimension(100,200));

		String addTextInfo = "<html><p>Texts should be stored as plain .txt files. For a more accurate</p> <p>analysis "
				+ " any titles, chapter markers or additional content </p><p>such as introductions or footnotes "
				+ "should first be removed.</p></html>";



		editAndConfirm = new JPanel();

		textOperations = new JPanel(new FlowLayout());

		add = new JButton("Add text(s)");

		add.addActionListener(con);
		remove = new JButton("Remove text(s)");
		remove.addActionListener(con);
		edit = new JButton("Edit text name");
		edit.addActionListener(con);

		ImageIcon icon = new ImageIcon("img/helpicon.png");
		textHelp = new JButton(icon);
		textHelp.setBorderPainted(false);
		textHelp.setToolTipText(addTextInfo);


		textOperations.add(add);


		textOperations.add(remove);
		textOperations.add(edit);
		textOperations.add(textHelp);

		editAndConfirm.add(new JLabel());
		editAndConfirm.add(textOperations);

		textSetup.add("North", textsPane);
		textSetup.add("Center", editAndConfirm);



		return textSetup;
	}

	public JPanel setupOptions(){


		optionsPanel = new JPanel();
		optionsPanel.add(new JLabel());


		optionsPanelBorder = BorderFactory.createTitledBorder(loweredEtched, "2. Edit Analysis Parameters");
		optionsPanel.setBorder(optionsPanelBorder);

		ngramPanel = new JPanel(new FlowLayout());
		SpinnerModel ngramModel = new SpinnerNumberModel(9, 2, 20, 1); 
		ngramLength = new JSpinner(ngramModel);

		String ngramHelpTxt = "<html><p>Ngrams are strings of characters generated from input</p><p>texts. "
				+ "The occurrence of each feature is counted and</p><p>the divergence of each count is measured.</p><p></p>"
				+ "<p>Optimal ngram length varies from text to text though most </p><p>often falls between 9 and 10.</p></center></html>";


		ImageIcon icon = new ImageIcon("img/helpicon.png");
		ngramHelp = new JButton(icon);
		ngramHelp.setBorderPainted(false);
		ngramHelp.setToolTipText(ngramHelpTxt);
		//ensures all tooltips stay on screen for a very long duration
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);




		ngramPanel.add(new JLabel("Edit ngram size: "));
		ngramPanel.add(ngramLength);
		ngramPanel.add(ngramHelp);

		optionsPanel.add("Center", ngramPanel);

		return optionsPanel;
	}

	public JPanel processing(){
		optionsPanel = new JPanel(new GridLayout(3,1));

		optionsPanel.add(new JLabel("Processing, may take some time"));

		//TODO TIME LABEL????
		timeLabel = new JLabel("");
		cancelProcess = new JButton("Cancel");
		cancelProcess.addActionListener(con);
		optionsPanel.add(timeLabel);

		optionsPanel.add(cancelProcess);


		return optionsPanel;
	}

	public JPanel FFPresults(){

		outputPanel = new JPanel(new GridLayout(18,1));


		outputStylePanel = new JPanel(new FlowLayout());
		displayOptions = new JPanel(new GridLayout(2,1));


		ffpStyle = new JComboBox<String>(ffpOutputOpts);
		ffpStyle.setActionCommand("FFP style");
		ffpStyle.addActionListener(con);


		displayOptions.add(new JLabel("Change output style: "));
		displayOptions.add(ffpStyle);


		ImageIcon icon = new ImageIcon("img/helpicon.png");
		treeHelp = new JButton(icon);
		treeHelp.setBorderPainted(false);
		treeHelp.setOpaque(false);
		treeHelp.setContentAreaFilled(false);
		treeHelp.setToolTipText("YADDA YADDA CLADO PHYLO ETC");


		outputStylePanel.add(displayOptions);
		outputStylePanel.add(treeHelp);

		outputPanel.add(outputStylePanel);
		outputPanel.add(new JLabel());

		save = new JButton("Save image");
		save.addActionListener(con);
		outputPanel.add(save);

		resultsBack = new JButton("Return to text setup");
		outputPanel.add(resultsBack);
		resultsBack.addActionListener(con);

		treeView = getTreePanel();

		resultsPanel = new JPanel();

		resultsPanel.setLayout(new FlowLayout());


		//TODO consistency of border layout stuff
		output = new JPanel(new BorderLayout());
		output.add(treeView.getScrollPane(), BorderLayout.CENTER);



		resultsPanel.add(output);
		resultsPanel.add(outputPanel);



		return resultsPanel;

	}

	public PhyloGraphView getTreePanel(){


		String style = ffpOutputOpts[ffpStyle.getSelectedIndex()];
		if(style.contains("Phylo")){
			treeView = new PhyloGraphView(phyloTree, 750, 550);
		}
		else{
			treeView = new PhyloGraphView(cladoTree, 750, 550);
		}

		treeView.getScrollPane().setPreferredSize(new Dimension(1095, 890));

		switch(style){
		case "Radial Phylogram":
			treeDrawer = new TreeDrawerRadial(treeView, phyloTree);
			break;
		case "Parallel Phylogram":
			treeDrawer = new TreeDrawerParallel(treeView, phyloTree);
			break;
		case "Circular Phylogram":
			treeDrawer = new TreeDrawerCircular(treeView, phyloTree);
			break;
		case "Angled Phylogram":
			treeDrawer = new TreeDrawerAngled(treeView, phyloTree);
			break;
		case "Radial Cladogram":
			treeDrawer = new TreeDrawerRadial(treeView, cladoTree);
			break;
		case "Parallel Cladogram":
			treeDrawer = new TreeDrawerParallel(treeView, cladoTree);
			break;
		case "Circular Cladogram":
			treeDrawer = new TreeDrawerCircular(treeView, cladoTree);
			break;
		case "Angled Cladogram":
			treeDrawer = new TreeDrawerAngled(treeView, cladoTree);
		}


		treeDrawer.computeEmbedding(true);

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


		treeView.trans.setCoordinateRect(treeView.getBBox());
		treeView.fitGraphToWindow();

		return treeView;
	}

	public PhyloTree cladogramFormat(PhyloTree tree){

		for(Edge e :tree.getEdges()){
			tree.setWeight(e, 1.0);
		}

		return tree;
	}

	public void refreshOutput(){
		output.remove(treeView.getScrollPane());
		resultsPanel.remove(outputPanel);
		treeView = getTreePanel();
		output.add(treeView.getScrollPane());
		resultsPanel.add(outputPanel);
		pack();
		resultsPanel.revalidate();
		resultsPanel.repaint();	
	}

	public void switchCard(String cardName){
		cl.show(cardHolder, cardName);
		if(!cardName.equals(FFPRESULTSPANEL)){
			this.setSize(new Dimension(500,225));
			this.pack();
		}
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

	public void centreScrollPane(JScrollPane pane){
		Rectangle viewable = pane.getViewport().getViewRect();
		Dimension size = pane.getViewport().getViewSize();

		int x = (size.width - viewable.width) / 2;
		int y = (size.height - viewable.height) / 2;

		pane.getViewport().setViewPosition(new Point(x,y));

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
