package ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import assembler.ReadsGenerator;

public class ReadsGeneratorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField txtSequenceFile;
	private JLabel lblSequenceFile;
	private JButton btnBrowseSequenceFile;
	private JFileChooser fileChooser;
	private JLabel lblReadSize;
	private JLabel lblMinimumOverlapLength;
	private JLabel lblOutputReadsFile;
	private JTextField txtOutputReadsFile;
	private JButton btnGenerateReads;
	private JButton btnBrowseOutputReadsFile;
	private JSpinner spnMinOverlapLen;
	private JSpinner spnReadSize;
	private JButton btnNext;
	private JTextArea txtDescription;
	
	private JTabbedPane tabbedPane; // TODO Isolate
	private int GenomeAssemblerTabIndex = 1;
	
	public ReadsGeneratorPanel(JTabbedPane tp) {
		this.tabbedPane = tp;
		this.setSize(680, 550);
		
		lblSequenceFile = new JLabel("Sequence File");
		
		txtSequenceFile = new JTextField();
		txtSequenceFile.setColumns(10);
		
		btnBrowseSequenceFile = new JButton("Browse");
		btnBrowseSequenceFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("fasta files", "fasta");
				fileChooser.setFileFilter(filter);
				int browseForInput = fileChooser.showOpenDialog(null);
				if (browseForInput == JFileChooser.APPROVE_OPTION) {
					if (fileChooser.getSelectedFile() != null && fileChooser.getSelectedFile().getAbsolutePath().endsWith(".fasta"))
						txtSequenceFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
					else
						JOptionPane.showMessageDialog(null, "Sorry! Only .fasta files allowed until now.");
				} else if (browseForInput == JFileChooser.ERROR_OPTION) {
					JOptionPane.showMessageDialog(null, "Sorry! An error has occurred. Please select sequence file again.");
					txtSequenceFile.setText("");
				}
			}
		});
		
		lblReadSize = new JLabel("Read Size");
		
		spnReadSize = new JSpinner();
		spnReadSize.setModel(new SpinnerNumberModel(new Integer(200), new Integer(0), null, new Integer(1)));

		lblMinimumOverlapLength = new JLabel("Minimum Overlap Length");
		
		spnMinOverlapLen = new JSpinner();
		spnMinOverlapLen.setModel(new SpinnerNumberModel(new Integer(10), new Integer(0), null, new Integer(1)));

		lblOutputReadsFile = new JLabel("Output Reads File");
		
		txtOutputReadsFile = new JTextField();
		txtOutputReadsFile.setColumns(10);
		
		btnBrowseOutputReadsFile = new JButton("Browse");
		btnBrowseOutputReadsFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				int browseForOutput = fileChooser.showSaveDialog(null);
				if (browseForOutput == JFileChooser.APPROVE_OPTION) {
					txtOutputReadsFile.setText(fileChooser.getSelectedFile().getAbsolutePath() + "\\generatedReads.fasta");
				} else if (browseForOutput == JFileChooser.ERROR_OPTION) {
					JOptionPane.showMessageDialog(null, "Sorry! An error has occurred. Please select output directory again.");
					txtOutputReadsFile.setText("");
				}
			}
		});
		
		btnGenerateReads = new JButton("Generate Reads");
		btnGenerateReads.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String sequenceFile = txtSequenceFile.getText();
				if (!sequenceFile.endsWith(".fasta")) {
					JOptionPane.showMessageDialog(null, "<html><body>Sequence file should be in <i>.fasta</i> format.</body></html>");
					return;
				}
				int readSize = (int) spnReadSize.getValue();
				int minOverlap = (int) spnMinOverlapLen.getValue();
				String readsFile = txtOutputReadsFile.getText();
				if (!readsFile.endsWith(".fasta")) {
					JOptionPane.showMessageDialog(null, "<html><body>Reads file should be in <i>.fasta</i> format.</body></html>");
					return;
				}
				JOptionPane.showMessageDialog(null, ReadsGenerator.generateReads(sequenceFile, readSize, minOverlap, readsFile));
			}
		});
		
		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				tabbedPane.setSelectedIndex(GenomeAssemblerTabIndex);
			}
		});
		
		txtDescription = new JTextArea();
		txtDescription.setEditable(false);
		txtDescription.setBackground(Color.WHITE);
		txtDescription.setWrapStyleWord(true);
		txtDescription.setLineWrap(true);
		txtDescription.setText("Use the reads generator if you have a .fasta sequence file and you wish to generate reads for assembly.  \r\n\r\nNote that this generator does not insert, delete or substitute bases in the sequence. For these advanced sequencing simulation features, it is recommended to use simulators such as MetaSim (free and open-source). Sequence is assumed to be circular.\r\n\r\nClick \"Next\" to skip this step, or to continue after generating reads.");
					
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGap(21)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblReadSize)
								.addComponent(lblOutputReadsFile)
								.addComponent(lblSequenceFile))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(txtSequenceFile, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(spnReadSize, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
									.addComponent(lblMinimumOverlapLength)
									.addGap(29)
									.addComponent(spnMinOverlapLen, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE))
								.addComponent(txtOutputReadsFile, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnBrowseSequenceFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnBrowseOutputReadsFile)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(275)
							.addComponent(btnGenerateReads)
							.addPreferredGap(ComponentPlacement.RELATED, 253, GroupLayout.PREFERRED_SIZE))
						.addComponent(txtDescription, GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE))
					.addGap(20))
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(608, Short.MAX_VALUE)
					.addComponent(btnNext)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(26)
					.addComponent(txtDescription, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtSequenceFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowseSequenceFile)
						.addComponent(lblSequenceFile))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(spnMinOverlapLen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblMinimumOverlapLength))
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(spnReadSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblReadSize)))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtOutputReadsFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowseOutputReadsFile)
						.addComponent(lblOutputReadsFile))
					.addGap(27)
					.addComponent(btnGenerateReads)
					.addGap(115)
					.addComponent(btnNext)
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
}