package ui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SummaryPanel extends JPanel {
	private static final long serialVersionUID = 3L;
	private JTabbedPane tabbedPane;
	final JFXPanel fxPanel;
	private final static String greedy = "Greedy";
	private final static String overlap = "Overlap";
	private final static String deBruijn = "De Bruijn";
	private final static String improvedDBG = "Improved De Bruijn";
	private JButton btnPrevious;
	private JButton btnExit;
	
	public SummaryPanel(JTabbedPane tp) {
		this.tabbedPane = tp;
		this.setSize(680, 550);
		
		fxPanel = new JFXPanel();
		fxPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initFX(fxPanel);
			}
		});
		
		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		btnPrevious = new JButton("Previous");
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setSelectedIndex(MainFrame.GenomeAssemblerTabIndex);
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(536, Short.MAX_VALUE)
					.addComponent(btnPrevious)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(22)
					.addComponent(fxPanel, GroupLayout.PREFERRED_SIZE, 628, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(30, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(19, Short.MAX_VALUE)
					.addComponent(fxPanel, GroupLayout.PREFERRED_SIZE, 479, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnExit)
						.addComponent(btnPrevious))
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
	
	private static void initFX(JFXPanel fxPanel) {
		// This method is invoked on the JavaFX thread
		Scene scene = createScene();
		fxPanel.setScene(scene);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Scene createScene() {
		final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = 
            new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Country Summary");
        xAxis.setLabel("Country");       
        yAxis.setLabel("Value");
 
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Graph Construction");       
        series1.getData().add(new XYChart.Data(greedy, 25601.34));
        series1.getData().add(new XYChart.Data(overlap, 20148.82));
        series1.getData().add(new XYChart.Data(deBruijn, 10000));
        series1.getData().add(new XYChart.Data(improvedDBG, 35407.15));    
        
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Contig Generation");
        series2.getData().add(new XYChart.Data(greedy, 57401.85));
        series2.getData().add(new XYChart.Data(overlap, 41941.19));
        series2.getData().add(new XYChart.Data(deBruijn, 45263.37));
        series2.getData().add(new XYChart.Data(improvedDBG, 117320.16));
        
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Thread Runtime");
        series3.getData().add(new XYChart.Data(greedy, 45000.65));
        series3.getData().add(new XYChart.Data(overlap, 44835.76));
        series3.getData().add(new XYChart.Data(deBruijn, 18722.18));
        series3.getData().add(new XYChart.Data(improvedDBG, 17557.31));
        
        Scene scene  = new Scene(bc,800,600);
        bc.getData().addAll(series1, series2, series3);

		return (scene);
	}
}
