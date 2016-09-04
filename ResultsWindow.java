import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


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

public class ResultsWindow extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel outputPanel, outputStylePanel, output, displayOptions, resultsPanel;
	private JComboBox<String> ffpStyle;
	private FFPController con;
	
	private String[] ffpOutputOpts = {"Radial Phylogram", "Radial Cladogram", "Parallel Phylogram", "Parallel Cladogram", "Circular Phylogram", "Circular Cladogram", "Angled Phylogram", "Angled Cladogram"};
	private JButton treeHelp, saveImage, saveTree, back;
	private PhyloTree phyloTree, cladoTree;
	private PhyloGraphView treeView; 
	private IGraphDrawer treeDrawer;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void layoutComponents(){

		outputPanel = new JPanel(new GridLayout(13,1));

		outputStylePanel = new JPanel(new FlowLayout());
		displayOptions = new JPanel(new GridLayout(2,1));

		ffpStyle = new JComboBox<String>(ffpOutputOpts);
		ffpStyle.setActionCommand("FFP style");
	


		displayOptions.add(new JLabel("Change output style: "));
		displayOptions.add(ffpStyle);


		ImageIcon icon = new ImageIcon("img/helpicon.png");
		treeHelp = new JButton(icon);
		treeHelp.setBorderPainted(false);
		treeHelp.setOpaque(false);
		treeHelp.setContentAreaFilled(false);
		String treeHelpText = "<html><p>Phylograms show the true relative distance between branches.</p><p>"
				+ "Cladograms equalise the distances of each branch, which</p><p>often makes visualisation easier</p></html>";
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
		back.addActionListener(con);

		resultsPanel = new JPanel();

		resultsPanel.setLayout(new FlowLayout());


		//TODO consistency of border layout stuff
		output = new JPanel(new BorderLayout());
		output.setPreferredSize(new Dimension(1000,800));

		

		resultsPanel.add(output);
		resultsPanel.add(outputPanel);
		this.add(resultsPanel);

	}

	public void addTree(PhyloTree tree){
		this.phyloTree = tree;
		this.cladoTree = levelTreeDistances((PhyloTree)tree.clone());
	}

	public void displayTree(){

		String style = ffpOutputOpts[ffpStyle.getSelectedIndex()];

		if(style.contains("Phylo")){		
			treeView = new PhyloGraphView(phyloTree, 750, 550);
		}
		else{
			System.out.println("CLADO CLADO CLADO");
			treeView = new PhyloGraphView(cladoTree, 750, 550);
		}
		treeView.getScrollPane().setPreferredSize(new Dimension(1000, 800));
		
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
			System.out.println("hey");
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
		

		formatTreeDisplay();

		treeView.trans.setCoordinateRect(treeView.getBBox());
		treeView.getScrollPane().revalidate();
		treeView.fitGraphToWindow();
		repaintTree();

	}

	public void formatTreeDisplay(){
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
	}

	public void repaintTree(){
		output.removeAll();
		output.add(treeView.getScrollPane());
		output.repaint();
		output.revalidate();
	}

	public PhyloTree levelTreeDistances(PhyloTree tree){


		for(Edge e : tree.getEdges()){
			tree.setWeight(e, 1);
		}

		return tree;
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


	public void addController(FFPController con){
		ffpStyle.addActionListener(con);
		saveImage.addActionListener(con);
		saveTree.addActionListener(con);
		back.addActionListener(con);
	}
	


}
