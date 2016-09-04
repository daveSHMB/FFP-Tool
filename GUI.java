
import java.awt.BorderLayout;
import java.awt.CardLayout;

import java.awt.Dimension;

import java.awt.FlowLayout;

import java.awt.GridLayout;

import java.awt.event.KeyEvent;

import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.*;


public class GUI extends JFrame implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel cardHolder, textSetupCard, processingCard, textSetup, textOperations, finalise, editAndConfirm, optionsPanel,
	ngramPanel, processingPanel;
	private JList<String> texts;
	private Border textTitle, optionsPanelBorder;
	private JLabel progressLabel;
	private JButton add, remove, edit, confirm, cancelProcess;
	JButton textHelp, treeHelp;
	JButton ngramHelp;
	private JSpinner ngramLength;
	
	
 

	private CardLayout cl;


	private FFPController con;

	
	public final static String TEXTSETUPPANEL = "Tab for text addition";
	public final static String PROCESSINGPANEL = "Tab shown while data processing";
	

	//move the below declarations???
	private TextList tl = new TextList();
	private Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	public GUI(){


		super("FFP Tool");
		con = new FFPController(this, tl);
		
		ResultsWindow rw = new ResultsWindow();
		con.addView(rw);
		rw.addController(con);
		tl.addObserver(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		layoutComponents();
		getContentPane().add(cardHolder);
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
		processingCard.setLayout(new BorderLayout(0, 0));
		processingCard.add(processing());

		cardHolder = new JPanel(new CardLayout());
		cardHolder.add(textSetupCard, TEXTSETUPPANEL);

		cardHolder.add(processingCard, PROCESSINGPANEL);

		cl = (CardLayout)cardHolder.getLayout();
		cl.show(cardHolder,  TEXTSETUPPANEL);

	}


	public JPanel setupDashboard(){
		JPanel dashFrame = new JPanel(new BorderLayout());
		dashFrame.add(textSetupPanel(), BorderLayout.NORTH);
		dashFrame.add(setupOptionsPanel(), BorderLayout.CENTER);
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
		textsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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
		textHelp.setBorderPainted(false);
		textHelp.setOpaque(false);
		textHelp.setContentAreaFilled(false);
		textHelp.setToolTipText(addTextInfo);


		textOperations.add(add);


		textOperations.add(remove);
		textOperations.add(edit);
		textOperations.add(textHelp);

		editAndConfirm.add(new JLabel());
		editAndConfirm.add(textOperations);

		textSetup.add("North", textsPane);
		textSetup.add("Center", editAndConfirm);
		
		setupMenuBar();

		return textSetup;
	}
	
	public void setupMenuBar(){
		
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem openTree = new JMenuItem("Open existing tree");
		file.add(openTree);
		menuBar.add(file);
		openTree.addActionListener(con);
		
		setJMenuBar(menuBar);
	}

	public JPanel setupOptionsPanel(){


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
		ngramHelp.setToolTipText(ngramHelpTxt);
		ngramHelp.setBorderPainted(false);
		ngramHelp.setOpaque(false);
		ngramHelp.setContentAreaFilled(false);

		//ensures all tooltips stay on screen for a very long duration
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

		ngramPanel.add(new JLabel("Edit ngram size: "));
		ngramPanel.add(ngramLength);
		ngramPanel.add(ngramHelp);

		optionsPanel.add("Center", ngramPanel);

		return optionsPanel;
	}

	public JPanel processing(){
		processingPanel = new JPanel(new GridLayout(3,1));

		processingPanel.add(new JLabel());
		//processingPanel.add(new JLabel());
		JPanel progressLabelPnl = new JPanel(new FlowLayout());
		progressLabel = new JLabel("Processing FFP");
		progressLabelPnl.add(progressLabel);
		JPanel cancelPanel = new JPanel(new FlowLayout());
		cancelProcess = new JButton("Cancel");
		cancelProcess.addActionListener(con);
		cancelPanel.add(cancelProcess);
		
		
		processingPanel.add(progressLabelPnl);
		processingPanel.add(cancelPanel);


		return processingPanel;
	}

	public void switchCard(String cardName){
		cl.show(cardHolder, cardName);
	}

	public int getNgramValue(){
		return (int)ngramLength.getValue();
	}


	public int[] getSelectedTexts(){
		return texts.getSelectedIndices();
	}

	
	public JLabel getProgressLabel(){
		return progressLabel;
	}

	

	@Override
	public void update(Observable obs, Object obj) {

		if(obs instanceof TextList){
			texts.setListData(tl.getTextDetails());
		}
	}


	
	

}
