
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;


public class GUI extends JFrame implements ActionListener{

	private JPanel cardHolder, textSetup, textOperations, finalise, editAndConfirm, optionsPanel,
	PCASettings, FFPSettings, editOptions;
	private JList<String> texts;
	private Border textTitle, PCASettingsBorder, FFPSettingsBorder;
	private JLabel addText, ngramLengthLbl, timeLabel;
	private JButton add, remove, edit, confirm, options, confirmOptions, cancelProcess;
	private JTextField ngramLength;
	private JFileChooser textSelect;

	private CardLayout cl;

	private ProcessTimer t;
	private FFP ffp;

	//Do these need to be static???
	private final static String TEXTSETUPPANEL = "Tab for text addition";
	private final static String OPTIONSPANEL = "Tab for editing options";
	private final static String PROCESSINGPANEL = "Tab shown while data processing";

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
		setVisible(true);

	}


	public void layoutComponents(){


		JPanel setupOptionsCard = new JPanel();
		setupOptionsCard.add(setupOptions());

		JPanel textSetupCard = new JPanel();
		textSetupCard.add(textSetupPanel());

		JPanel processingCard = new JPanel();
		processingCard.add(processing());


		cardHolder = new JPanel(new CardLayout());
		cardHolder.add(setupOptionsCard, OPTIONSPANEL);
		cardHolder.add(textSetupCard, TEXTSETUPPANEL);
		cardHolder.add(processingCard, PROCESSINGPANEL);

		cl = (CardLayout)cardHolder.getLayout();
		cl.show(cardHolder, TEXTSETUPPANEL);


	}

	public JPanel textSetupPanel(){
		textSetup = new JPanel(new BorderLayout());
		textSetup.setSize(new Dimension(100,300));
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
		optionsPanel.setSize(new Dimension(1000,1000));
		BorderFactory.createTitledBorder(loweredEtched, "Edit options");

		PCASettings = new JPanel(new GridLayout(2,6));
		PCASettingsBorder = BorderFactory.createTitledBorder(loweredEtched, "PCA Options");
		PCASettings.setBorder(PCASettingsBorder);
		JCheckBox one = new JCheckBox("Average word length", true);
		JCheckBox two = new JCheckBox("Average sentence length", true);
		JCheckBox three = new JCheckBox("SD of sentence length", true);
		JCheckBox four = new JCheckBox("SD of sentence length", true);
		JCheckBox five = new JCheckBox("SD of sentence length", true);
		JCheckBox six = new JCheckBox("SD of sentence length", true);
		PCASettings.add(one);
		PCASettings.add(two);
		PCASettings.add(three);
		PCASettings.add(four);
		PCASettings.add(five);
		PCASettings.add(six);


		FFPSettings = new JPanel(new FlowLayout());
		FFPSettingsBorder = BorderFactory.createTitledBorder(loweredEtched, "FFP Options");
		FFPSettings.setBorder(FFPSettingsBorder);
		ngramLengthLbl = new JLabel("Enter ngram length:");
		FFPSettings.add(ngramLengthLbl);
		ngramLength = new JTextField(20);
		FFPSettings.add(ngramLength);

		editOptions = new JPanel(new GridLayout(2,1));
		editOptions.add(PCASettings);
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


		optionsPanel.setSize(new Dimension(1000,1000));
		optionsPanel.add(new JLabel("Processing, may take some time"));
		timeLabel = new JLabel("");
		timeLabel.setFont (timeLabel.getFont().deriveFont(64.0f));
		cancelProcess = new JButton("Cancel");
		cancelProcess.addActionListener(this);
		optionsPanel.add(timeLabel);

		optionsPanel.add(cancelProcess);


		return optionsPanel;
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
			ffp = new FFP(tl, (Integer.parseInt(ngramLength.getText())));
			ffp.execute();
		}
		else if(ae.getSource() == options){
			cl.show(cardHolder, OPTIONSPANEL);
			//this.setLocationRelativeTo(null);
		}
		else if(ae.getSource() == confirmOptions){
			System.out.println("WHAT");
			cl.show(cardHolder, TEXTSETUPPANEL);
		}
		else if(ae.getSource() == cancelProcess){
			ffp.cancel(true);
			t.cancel(true);
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


	//CATCH INTERRUPTED EXCEPTIONNNNNNNNNNNNNNNN
	private class ProcessTimer extends SwingWorker<Void,Long> {

		protected Void doInBackground() throws Exception {

			long start = System.currentTimeMillis();

			while(!isCancelled()){
				long now = System.currentTimeMillis();
				long elapsed = now - start;

				publish(elapsed);
			}

			return null;
		}



		protected void process(List<Long> milliseconds){
			Long time = milliseconds.get(milliseconds.size() - 1);
			int hour = (int) ((time / (1000 * 60 * 60)) % 24);
			int minute = (int) ((time / (1000 * 60)) % 60);
			int second = (int) ((time / 1000) % 60);
			timeLabel.setText(String.format("%02d:%02d:%02d", hour, minute, second));		
		}
	}

}
