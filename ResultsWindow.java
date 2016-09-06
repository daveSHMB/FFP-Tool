import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


import jloda.graph.Edge;
import jloda.graphview.IGraphDrawer;
import jloda.phylo.PhyloGraphView;
import jloda.phylo.PhyloTree;
import jloda.phylo.TreeDrawerAngled;
import jloda.phylo.TreeDrawerCircular;
import jloda.phylo.TreeDrawerParallel;
import jloda.phylo.TreeDrawerRadial;

/**
 * Window for phylogenetic tree display
 * 
 * @author David McLintock
 *
 */
public class ResultsWindow extends JFrame{


	private static final long serialVersionUID = 1L;
	private JPanel outputPanel, outputStylePanel, output, displayOptions, resultsPanel;
	private JComboBox<String> ffpStyle;
	private String[] ffpOutputOpts = {"Radial Phylogram", "Radial Cladogram", "Parallel Phylogram", "Parallel Cladogram", "Circular Phylogram", "Circular Cladogram", "Angled Phylogram", "Angled Cladogram"};
	private JButton treeHelp, saveImage, saveTree, back;
	private PhyloTree phyloTree, cladoTree;
	private PhyloGraphView treeView; 
	private IGraphDrawer treeDrawer;
	private final String treeHelpText = "<html><p>Phylograms show the true relative distance between branches.</p><p>"
			+ "Cladograms equalise the distances of each branch, which</p><p>often makes visualisation easier</p></html>";
	private final Dimension windowSize = new Dimension(1000,800);
	private URL helpImage = getClass().getResource("img/helpicon.png");
	
	/**
	 * Default constructor
	 */
	public ResultsWindow(){


		super("FFP Tool");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		layoutComponents();
		pack();
		setLocationRelativeTo(null);
		setResizable(false);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			//do nothing
		}
	}

	
	/**
	 * Setups up components
	 */
	public void layoutComponents(){

		outputPanel = new JPanel(new GridLayout(13,1));

		outputStylePanel = new JPanel(new FlowLayout());
		displayOptions = new JPanel(new GridLayout(2,1));

		ffpStyle = new JComboBox<String>(ffpOutputOpts);
		ffpStyle.setActionCommand("FFP style");
	
		displayOptions.add(new JLabel("Change output style: "));
		displayOptions.add(ffpStyle);

		ImageIcon icon = new ImageIcon(helpImage);
		
		//add help button and make border invisible
		treeHelp = new JButton(icon);
		treeHelp.setBorderPainted(false);
		treeHelp.setOpaque(false);
		treeHelp.setContentAreaFilled(false);
		treeHelp.setToolTipText(treeHelpText);

		outputStylePanel.add(displayOptions);
		outputStylePanel.add(treeHelp);

		outputPanel.add(outputStylePanel);
		outputPanel.add(new JLabel());

		saveImage = new JButton("Save image");
		outputPanel.add(saveImage);
		
		saveTree = new JButton("Save tree");
		outputPanel.add(saveTree);

		back = new JButton("Return to text setup");
		outputPanel.add(back);
	
		resultsPanel = new JPanel();
		resultsPanel.setLayout(new FlowLayout());

		output = new JPanel(new BorderLayout());
		output.setPreferredSize(windowSize);

		resultsPanel.add(output);
		resultsPanel.add(outputPanel);
		this.add(resultsPanel);

	}

	
	/**
	 * Adds tree to be displayed
	 * @param tree
	 */
	public void addTree(PhyloTree tree){
		//setup both phylotree and cladotree options
		this.phyloTree = tree;
		this.cladoTree = convertToCladoTree((PhyloTree)tree.clone());
	}

	
	/**
	 * Displays tree using currently selected style
	 */
	public void displayTree(){

		//get selected style
		String style = ffpOutputOpts[ffpStyle.getSelectedIndex()];
		
		//if current style is phylogram, use phyloTree
		if(style.contains("Phylo")){		
			treeView = new PhyloGraphView(phyloTree, 750, 550);
		}
		else{
			treeView = new PhyloGraphView(cladoTree, 750, 550);
		}
		//set dimension of tree scrollpane
		treeView.getScrollPane().setPreferredSize(new Dimension(1000, 800));
		
		//setup selected view style
		switch(style){
		case "Radial Phylogram":
			treeDrawer = new TreeDrawerRadial(treeView, phyloTree);
			treeDrawer.computeEmbedding(false);
			break;
		case "Parallel Phylogram":
			treeDrawer = new TreeDrawerParallel(treeView, phyloTree);
			treeDrawer.computeEmbedding(false);
			break;
		case "Circular Phylogram":
			treeDrawer = new TreeDrawerCircular(treeView, phyloTree);
			treeDrawer.computeEmbedding(false);
			break;
		case "Angled Phylogram":
			treeDrawer = new TreeDrawerAngled(treeView, phyloTree);
			treeDrawer.computeEmbedding(false);
			break;
		case "Radial Cladogram":
			treeDrawer = new TreeDrawerRadial(treeView, cladoTree);
			treeDrawer.computeEmbedding(true);
			break;
		case "Parallel Cladogram":
			treeDrawer = new TreeDrawerParallel(treeView, cladoTree);
			treeDrawer.computeEmbedding(true);
			break;
		case "Circular Cladogram":
			treeDrawer = new TreeDrawerCircular(treeView, cladoTree);
			treeDrawer.computeEmbedding(true);
			break;
		case "Angled Cladogram":
			treeDrawer = new TreeDrawerAngled(treeView, cladoTree);
			treeDrawer.computeEmbedding(true);
			break;
		}

		treeView.setGraphDrawer(treeDrawer);
		
		//amend display parameters
		formatTreeDisplay();
		
		//fit to window and repaint
		treeView.trans.setCoordinateRect(treeView.getBBox());
		treeView.getScrollPane().revalidate();
		treeView.fitGraphToWindow();
		repaintTree();

	}

	
	/**
	 * Formats tree for display
	 */
	public void formatTreeDisplay(){
		
		//change canvas colour
		treeView.setCanvasColor(Color.WHITE);
		
		//select all edges and change line width
		treeView.selectAllEdges(true);
		treeView.setLineWidthSelectedEdges((byte) 3.6);
		treeView.selectAllEdges(false);

		//select all nodes and change size
		treeView.selectAllNodes(true);
		treeView.setSizeSelectedNodes((byte) 7, (byte) 7);
		treeView.selectAllNodes(false);

		//set default font
		treeView.setFont(new Font("SansSerif", Font.PLAIN, 15));
		treeView.setAutoLayoutLabels(true);

		//allow user to edit node labels
		treeView.setAllowEdit(true);
	}

	
	/**
	 * Repaints tree upon changed options
	 */
	public void repaintTree(){
		output.removeAll();
		output.add(treeView.getScrollPane());
		output.repaint();
		output.revalidate();
	}

	
	/**
	 * Creates clado tree from phylo tree
	 * 
	 * @param tree the tree to be converted
	 * @return the converted tree
	 */
	public PhyloTree convertToCladoTree(PhyloTree tree){

		//loop through branch lengths and set each to identical value
		for(Edge e : tree.getEdges()){
			tree.setWeight(e, 1);
		}

		return tree;
	}
	
	
	/**
	 * Gets the image shown in the output window
	 * @return the image from the output window
	 */
	public BufferedImage getOutputGraphics(){
		
		//get currently tree zoom and alignment
		Rectangle2D rect = treeView.getVisibleRect();

		int x = (int)rect.getX();
		int y = (int)rect.getY();

		//get scrollbar height and width
		int scrollbarWidthHeight = ((Integer)UIManager.get("ScrollBar.width")).intValue(); 
		
		//get image at correct size and paint to graphics/image file
		BufferedImage image = new BufferedImage(treeView.getScrollPane().getWidth() - scrollbarWidthHeight, treeView.getScrollPane().getHeight() - scrollbarWidthHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.translate(-x, -y);
		g.setClip(rect);
		g.fillRect(x, y, (int)rect.getWidth(), (int)rect.getHeight());
		treeView.getPanel().paint(g);
		return image;
	}


	/**
	 * Adds controller to the view and all necessary components
	 * @param con the controller to be added
	 */
	public void addController(FFPController con){
		ffpStyle.addActionListener(con);
		saveImage.addActionListener(con);
		saveTree.addActionListener(con);
		back.addActionListener(con);
	}
	


}
