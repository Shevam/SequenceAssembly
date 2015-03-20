package ui;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 0L;
	private static final String TabNameStyleStart = "<html><body marginwidth=10 marginheight=10>";
	private static final String TabNameStyleEnd = "</body></html>";
	
	static final int ReadsGeneratorTabIndex = 0;
	static final int GenomeAssemblerTabIndex = 1;
	static final int SummaryTabIndex = 2;
	
	JTabbedPane tabbedPane;
	
	public MainFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(850, 600);
		this.setResizable(false);
		this.setTitle("Computational Methods For Genome Assembly");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
		tabbedPane.addTab(TabNameStyleStart+"Reads Generator"+TabNameStyleEnd, new ReadsGeneratorPanel(tabbedPane));
		tabbedPane.addTab(TabNameStyleStart+"Assembly Methods"+TabNameStyleEnd, new GenomeAssemblyPanel(tabbedPane));
		tabbedPane.addTab(TabNameStyleStart+"Summary"+TabNameStyleEnd, new SummaryPanel(tabbedPane));
		
		this.setVisible(true);
	}

//	private class ReadsGeneratorPanel extends JPanel {
//	
//	}
//	
//	private class GenomeAssemblyPanel extends JPanel {
//	
//	}
//	
//	private class SummaryPanel extends JPanel {
//	
//	}
}
