package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import assembler.Main;

public class GenomeAssemblyPanel extends JPanel {
	private static final long serialVersionUID = 2L;
	private JTabbedPane tabbedPane;
	
	private JLabel lblReadsFile;
	private JTextField txtReadsFile;
	private JButton btnBrowseReadsFile;
	private JLabel lblContigsFile;
	private JTextField txtContigsFileLocation;
	private JButton btnBrowseContigsFile;

	private JPanel panelForMethodsParams;
	private JLabel lblChooseAssemblyMethods;
	private JCheckBox chckbxDeBruijnGraph;
	private JCheckBox chckbxOverlapGraph;
	private JCheckBox chckbxGreedy;
	private JCheckBox chckbxImprovedDeBruijn;
	private JLabel lblParameters;
	private JLabel lblKForDBG;
	private JSpinner spnKForDBG;
	private JLabel lblMinimumOverlapLength;
	private JSpinner spnMinOverlapLen;
	private JFileChooser fileChooser;
	
	private JButton btnStartAssembly;
	
	private JScrollPane scrollPaneForLstLog;
	private JList<String> lstLog;
	
	private JButton btnNext;
	
	public GenomeAssemblyPanel(JTabbedPane tp) {
		this.tabbedPane = tp;
		this.setSize(680, 550);
		
		lblReadsFile = new JLabel("Reads File");
		
		txtReadsFile = new JTextField();
		txtReadsFile.setColumns(10);
		
		btnBrowseReadsFile = new JButton("Browse");
		btnBrowseReadsFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("fasta files", "fasta");
				fileChooser.setFileFilter(filter);
				int browseForInput = fileChooser.showOpenDialog(null);
				if (browseForInput == JFileChooser.APPROVE_OPTION) {
					if (fileChooser.getSelectedFile() != null && fileChooser.getSelectedFile().getAbsolutePath().endsWith(".fasta"))
						txtReadsFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
					else
						JOptionPane.showMessageDialog(null, "Sorry! Only .fasta files allowed until now.");
				} else if (browseForInput == JFileChooser.ERROR_OPTION) {
					JOptionPane.showMessageDialog(null, "Sorry! An error has occurred. Please select reads file again.");
					txtReadsFile.setText("");
				}
			}
		});

		lblContigsFile = new JLabel("Contigs File");
		
		txtContigsFileLocation = new JTextField();
		txtContigsFileLocation.setColumns(10);
		
		btnBrowseContigsFile = new JButton("Browse");
		btnBrowseContigsFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				int browseForOutput = fileChooser.showSaveDialog(null);
				String absolutePath = "";
				if (browseForOutput == JFileChooser.APPROVE_OPTION) {
					absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
					if (!absolutePath.endsWith("\\")) {
						absolutePath = absolutePath + "\\";
					}
					txtContigsFileLocation.setText(absolutePath);
				} else if (browseForOutput == JFileChooser.ERROR_OPTION) {
					JOptionPane.showMessageDialog(null, "Sorry! An error has occurred. Please select output directory again.");
					txtContigsFileLocation.setText("");
				}
			}
		});
				
		panelForMethodsParams = new JPanel();
		panelForMethodsParams.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		
		scrollPaneForLstLog = new JScrollPane();
		lstLog = new JList<String>();
		scrollPaneForLstLog.setViewportView(lstLog);
		
		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tabbedPane.setSelectedIndex(MainFrame.SummaryTabIndex);
			}
		});
	
		btnStartAssembly = new JButton("Start Assembly");
		btnStartAssembly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (txtReadsFile.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Reads file field left blank.", "No file selected", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					String readsFile = txtReadsFile.getText();
					if (!readsFile.endsWith(".fasta")) {
						JOptionPane.showMessageDialog(null, "<html><body>Reads file should be in <i>.fasta</i> format.</body></html>");
						return;
					}
					Main.setReadsFile(txtReadsFile.getText());
				}
				if (txtContigsFileLocation.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Contigs file field left blank.", "No file selected", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					Main.setContigsFileLocation(txtContigsFileLocation.getText());
				}
				if (chckbxDeBruijnGraph.isSelected()) {
					Main.addAssemblyMethod(Main.AssemblyMethods.DE_BRUIJN);
				}
				if (chckbxOverlapGraph.isSelected()) {
					Main.addAssemblyMethod(Main.AssemblyMethods.OVERLAP);
				}
				if (chckbxGreedy.isSelected()) {
					Main.addAssemblyMethod(Main.AssemblyMethods.GREEDY);
				}
				if (chckbxImprovedDeBruijn.isSelected()) {
					Main.addAssemblyMethod(Main.AssemblyMethods.IMPROVED_DE_BRUIJN);
				}
				if (Main.getNoOfAssemblyMethods() == 0) {
					JOptionPane.showMessageDialog(null, "Please choose at least one assembly method.");
					return;
				}
				Main.setK((int) spnKForDBG.getValue());
				Main.setMinimumOverlapLength((int) spnMinOverlapLen.getValue());
				Main.resetTime();
				tabbedPane.setSelectedIndex(MainFrame.SummaryTabIndex);
				try {
					SummaryPanel.startAssembly();
				} catch (NullPointerException npe) {
					System.err.println("Input file " + txtReadsFile.getText() + " is of wrong format.");
					JOptionPane.showMessageDialog(null, "Contigs file is invalid. Supported file format content example:\n"
							+ "\n"
							+ ">Description\n"
							+ "AGTCGAGCA...");
				}
			}
		});
		
		
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(280)
							.addComponent(btnStartAssembly))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(scrollPaneForLstLog, GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
								.addComponent(panelForMethodsParams, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblReadsFile)
									.addGap(24)
									.addComponent(txtReadsFile, GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnBrowseReadsFile, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
								.addComponent(btnNext)))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblContigsFile)
							.addGap(18)
							.addComponent(txtContigsFileLocation, GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnBrowseContigsFile, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblReadsFile)
						.addComponent(txtReadsFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowseReadsFile))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblContigsFile)
						.addComponent(txtContigsFileLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowseContigsFile))
					.addGap(21)
					.addComponent(panelForMethodsParams, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)
					.addGap(36)
					.addComponent(btnStartAssembly)
					.addGap(38)
					.addComponent(scrollPaneForLstLog, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
					.addComponent(btnNext)
					.addContainerGap())
		);
		
		lblChooseAssemblyMethods = new JLabel("Choose assembly methods:");
		
		chckbxDeBruijnGraph = new  JCheckBox("De Bruijn Graph");
		chckbxDeBruijnGraph.setSelected(true);

		chckbxOverlapGraph = new JCheckBox("Overlap Graph");
		chckbxOverlapGraph.setSelected(true);

		chckbxGreedy = new JCheckBox("Greedy");
		chckbxGreedy.setSelected(true);
		
		chckbxImprovedDeBruijn = new JCheckBox("Improved De Bruijn Graph");
		chckbxImprovedDeBruijn.setSelected(true);
		
		lblParameters = new JLabel("Parameters:");
		
		lblKForDBG = new JLabel("k for De Bruijn graphs =");

		spnKForDBG = new JSpinner();
		spnKForDBG.setModel(new SpinnerNumberModel(new Integer(21), new Integer(1), null, new Integer(2)));

		lblMinimumOverlapLength = new JLabel("Minimum overlap for overlap graphs = ");
		
		spnMinOverlapLen = new JSpinner();
		spnMinOverlapLen.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		
		GroupLayout gl_panel = new GroupLayout(panelForMethodsParams);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxImprovedDeBruijn)
						.addComponent(chckbxOverlapGraph)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblChooseAssemblyMethods)
								.addComponent(chckbxDeBruijnGraph, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE))
							.addGap(153)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblParameters)
								.addGroup(gl_panel.createSequentialGroup()
									.addGap(12)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(lblMinimumOverlapLength)
										.addComponent(lblKForDBG))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(spnMinOverlapLen, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
										.addComponent(spnKForDBG, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)))))
						.addComponent(chckbxGreedy))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblChooseAssemblyMethods)
						.addComponent(lblParameters))
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxDeBruijnGraph)
						.addComponent(lblKForDBG)
						.addComponent(spnKForDBG, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxOverlapGraph)
						.addComponent(lblMinimumOverlapLength)
						.addComponent(spnMinOverlapLen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbxGreedy)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbxImprovedDeBruijn)
					.addContainerGap(49, Short.MAX_VALUE))
		);
		panelForMethodsParams.setLayout(gl_panel);

		setLayout(groupLayout);
		
		
	}
}
