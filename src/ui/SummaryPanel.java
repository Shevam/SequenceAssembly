package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;

import assembler.Main;

public class SummaryPanel extends JPanel {
	private static final long serialVersionUID = 3L;
	private JTabbedPane tabbedPane;
	final JFXPanel fxPanel;
	private static String greedy = "Greedy";
	private static String overlap = "Overlap";
	private static String deBruijn = "De Bruijn";
	private static String improvedDBG = "Improved De Bruijn";
	private JButton btnPrevious;
	private JButton btnExit;
	static ObservableList<Series<String, Number>> observableList;
	static BarChart<String, Number> barChart;
	
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
		
		btnPrevious = new JButton("Run!"); // TODO: Rename to Previous
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//tabbedPane.setSelectedIndex(MainFrame.GenomeAssemblerTabIndex);
				new Thread() {
					public void run() {
						startMonitoring();
						Main.main(new String[0]);				
					};
				}.start();
			}
		});

		btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnPrevious)
							.addPreferredGap(ComponentPlacement.RELATED, 532, Short.MAX_VALUE)
							.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(22)
							.addComponent(fxPanel, GroupLayout.PREFERRED_SIZE, 628, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
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
	
	private static void startMonitoring() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				while (!Main.isCompleted) {
					try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							//barChart.setAnimated(false);
							observableList.set(0, getGraphConstructionSeries());
							observableList.set(1, getContigGenerationSeries());
							//barChart.setAnimated(true);
						}
					});
				}
			}
		}.start();
	}
	
	@SuppressWarnings("unchecked")
	private static Scene createScene() {
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Method");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Time (ms)");
		barChart = new BarChart<String, Number>(xAxis, yAxis);
		barChart.setTitle("Genome Assembly Methods");
		barChart.setCategoryGap(20);
		barChart.setBarGap(2);
		barChart.setVerticalGridLinesVisible(false);
		barChart.setAnimated(false);
		xAxis.setAnimated(false);
		yAxis.setAnimated(false);
		
		Scene scene = new Scene(barChart, 800, 600);
		observableList = FXCollections.observableArrayList(getGraphConstructionSeries(), getContigGenerationSeries());
		barChart.setData(observableList);
		
		return (scene);
	}
	
	static Series<String, Number> getGraphConstructionSeries() {
		BarChart.Series<String, Number> graphConstructionSeries = new BarChart.Series<String, Number>();
		graphConstructionSeries.setName("Graph Construction");
		graphConstructionSeries.getData().add(new BarChart.Data<String, Number>(deBruijn, Main.getGraphConstructionRunTime(Main.AssemblyMethods.DE_BRUIJN)));
		graphConstructionSeries.getData().add(new BarChart.Data<String, Number>(overlap, Main.getGraphConstructionRunTime(Main.AssemblyMethods.OVERLAP)));
		graphConstructionSeries.getData().add(new BarChart.Data<String, Number>(greedy, Main.getGraphConstructionRunTime(Main.AssemblyMethods.GREEDY)));
		graphConstructionSeries.getData().add(new BarChart.Data<String, Number>(improvedDBG, Main.getGraphConstructionRunTime(Main.AssemblyMethods.IMPROVED_DE_BRUIJN)));
		return graphConstructionSeries;
	}
	
	static Series<String, Number> getContigGenerationSeries() {
		BarChart.Series<String, Number> contigGenerationSeries = new BarChart.Series<String, Number>();
		contigGenerationSeries.setName("Contig Generation");
		contigGenerationSeries.getData().add(new BarChart.Data<String, Number>(deBruijn, Main.getContigGenerationRunTime(Main.AssemblyMethods.DE_BRUIJN)));
		contigGenerationSeries.getData().add(new BarChart.Data<String, Number>(overlap, Main.getContigGenerationRunTime(Main.AssemblyMethods.OVERLAP)));
		contigGenerationSeries.getData().add(new BarChart.Data<String, Number>(greedy, Main.getContigGenerationRunTime(Main.AssemblyMethods.GREEDY)));
		contigGenerationSeries.getData().add(new BarChart.Data<String, Number>(improvedDBG, Main.getContigGenerationRunTime(Main.AssemblyMethods.IMPROVED_DE_BRUIJN)));
		return contigGenerationSeries;
	}
}
