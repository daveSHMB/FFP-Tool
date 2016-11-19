import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.border.*;


/**
 * Text setup GUI for the FFP tool.
 * 
 * @author David McLintock
 *
 */
public class SetupWindow extends JFrame implements Observer{

	private static final long serialVersionUID = 1L;
	private JPanel cardHolder, textSetupCard, processingCard, textSetup, textOperations, finalise, editAndConfirm, optionsPanel,
	ngramPanel, processingPanel;
	private JList<String> texts;
	private Border textTitle, optionsPanelBorder, loweredEtched;
	private JLabel progressLabel;
	private JButton add, remove, edit, confirm, cancelProcess, textHelp, ngramHelp;
	private JSpinner ngramLength;
	private CardLayout cl;
	public final static String TEXTSETUPPANEL = "Tab for text addition";
	public final static String PROCESSINGPANEL = "Tab shown while data processing";
	private final String ngramHelpTxt = "<html><p>Ngrams are strings of characters generated from input</p><p>texts. "
			+ "The occurrence of each feature is counted and</p><p>the divergence of each count is measured.</p><p></p>"
			+ "<p>Optimal ngram length varies from text to text though most </p><p>often falls between 9 and 10.</p></center></html>";
	private final 	String addTextInfo = "<html><p>Texts should be stored as plain .txt files. For a more accurate</p> <p>analysis "
			+ " any titles, chapter markers or additional content </p><p>such as introductions or footnotes "
			+ "should first be removed.</p></html>";
	private TextList tl;
	private final Dimension windowSize = new Dimension(100,200);
	private final int visibleRows = 15;
	private URL helpImage = getClass().getResource("img/helpicon.png");
	private JMenuItem openTree;
	
	
	/**
	 *  Default constructor
	 */
	public SetupWindow(FFPController con, TextList tl){

		super("FFP Tool");
		this.tl = tl;
		tl.addObserver(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		layoutComponents();
		getContentPane().add(cardHolder);
		pack();
		addController(con);
		con.addView(this);
		setLocationRelativeTo(null);
		setResizable(false);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			//do nothing, will default to system look
		}
		setVisible(true);
	}

	
	/**
	 * Setup components
	 */
	public void layoutComponents(){
		
		//create default border style
		loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
		//create cards
		textSetupCard = new JPanel();
		textSetupCard.add(setupDashboard());
		processingCard = new JPanel();
		processingCard.setLayout(new BorderLayout());
		processingCard.add(processing());

		//add cards to cardholder
		cardHolder = new JPanel(new CardLayout());
		cardHolder.add(textSetupCard, TEXTSETUPPANEL);
		cardHolder.add(processingCard, PROCESSINGPANEL);
		
		//display setup panel by default
		cl = (CardLayout)cardHolder.getLayout();
		cl.show(cardHolder,  TEXTSETUPPANEL);
	}

	
	/**
	 * Sets up the file bar for opening existing trees
	 */
	public void setupMenuBar(){
		
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		
		//keyboard shortcut
		file.setMnemonic(KeyEvent.VK_F);
		//open tree option
		openTree = new JMenuItem("Open existing tree");
		file.add(openTree);
		menuBar.add(file);
		setJMenuBar(menuBar);
	}
	
	
	/**
	 * Setup the text addition/settings card
	 * @return the completed panel
	 */
	public JPanel setupDashboard(){
		
		JPanel dashFrame = new JPanel(new BorderLayout());
		dashFrame.add(textSetupPanel(), BorderLayout.NORTH);
		dashFrame.add(setupOptionsPanel(), BorderLayout.CENTER);
		
		
		finalise = new JPanel(new FlowLayout());
		confirm = new JButton("Confirm");	
		finalise.add(confirm);
		dashFrame.add(finalise, BorderLayout.SOUTH);
		
		return dashFrame;
	}

	
	/**
	 * Sets up the "add text" section of the setup card
	 * @return the completed panel
	 */
	public JPanel textSetupPanel(){
		textSetup = new JPanel(new BorderLayout());

		textTitle = BorderFactory.createTitledBorder(loweredEtched, "1. Add texts for comparison");
		textSetup.setBorder(textTitle);
		texts = new JList<String>();
		texts.setVisibleRowCount(visibleRows);
		JScrollPane textsPane = new JScrollPane(texts);
		textsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		textsPane.setPreferredSize(windowSize);

		editAndConfirm = new JPanel();

		textOperations = new JPanel(new FlowLayout());

		add = new JButton("Add text(s)");
		remove = new JButton("Remove text(s)");
		edit = new JButton("Edit text name");
		
		ImageIcon icon = new ImageIcon(helpImage);
		textHelp = new JButton(icon);
		
		//make help button border invisible
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

		textSetup.add(textsPane, BorderLayout.NORTH);
		textSetup.add(editAndConfirm, BorderLayout.CENTER);
		
		//add menu bar to display
		setupMenuBar();

		return textSetup;
	}
	
	
	/**
	 * Sets up the panel for adjusting FFP parameters
	 * @return the completed JPanel
	 */
	public JPanel setupOptionsPanel(){

		optionsPanel = new JPanel();
		optionsPanel.add(new JLabel());

		optionsPanelBorder = BorderFactory.createTitledBorder(loweredEtched, "2. Edit Analysis Parameters");
		optionsPanel.setBorder(optionsPanelBorder);

		ngramPanel = new JPanel(new FlowLayout());
		
		//ngram select tool, default = 9
		SpinnerModel ngramModel = new SpinnerNumberModel(9, 2, 20, 1); 
		ngramLength = new JSpinner(ngramModel);

		ImageIcon icon = new ImageIcon(helpImage);
		ngramHelp = new JButton(icon);
		
		//make help button border invisible
		ngramHelp.setToolTipText(ngramHelpTxt);
		ngramHelp.setBorderPainted(false);
		ngramHelp.setOpaque(false);
		ngramHelp.setContentAreaFilled(false);

		//ensures all tooltips stay on screen for a very long duration
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

		ngramPanel.add(new JLabel("Edit ngram size: "));
		ngramPanel.add(ngramLength);
		ngramPanel.add(ngramHelp);

		optionsPanel.add(ngramPanel, BorderLayout.CENTER);

		return optionsPanel;
	}

	
	/**
	 * Sets up the card displayed while data is processing
	 * @return the completed JPanel
	 */
	public JPanel processing(){
		processingPanel = new JPanel(new GridLayout(3,1));

		processingPanel.add(new JLabel());
		JPanel progressLabelPnl = new JPanel(new FlowLayout());
		progressLabel = new JLabel("Processing FFP");
		progressLabelPnl.add(progressLabel);
		JPanel cancelPanel = new JPanel(new FlowLayout());
		cancelProcess = new JButton("Cancel");
		cancelPanel.add(cancelProcess);
			
		processingPanel.add(progressLabelPnl);
		processingPanel.add(cancelPanel);

		return processingPanel;
	}
	
	
	/**
	 * Adds controller to the view and all necessary components
	 * @param con the controller to be added
	 */
	public void addController(FFPController con){
		confirm.addActionListener(con);
		add.addActionListener(con);
		remove.addActionListener(con);
		edit.addActionListener(con);
		cancelProcess.addActionListener(con);
		openTree.addActionListener(con);
	}

	
	/**
	 * Used by the controller to switch cards
	 * @param cardName the name of the card to switch to
	 */
	public void switchCard(String cardName){
		cl.show(cardHolder, cardName);
	}

	
	/**
	 * Used by the controller to get the selected ngram value
	 * @return the selected ngram length
	 */
	public int getNgramValue(){
		return (int)ngramLength.getValue();
	}


	/**
	 * Used by the controller to access selected texts in the text list
	 * @return the selected indices
	 */
	public int[] getSelectedTexts(){
		return texts.getSelectedIndices();
	}

	
	/**
	 * Used by the controller to get the label to be updated during processing
	 * @return the progress label
	 */
	public JLabel getProgressLabel(){
		return progressLabel;
	}


	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable obs, Object obj) {
		
		//if observed is textlist, update displayed text list
		if(obs instanceof TextList){
			texts.setListData(tl.getTextDetails());
		}
	}


	
	

}
