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
	final JFXPanel fxPanel_dbg;
	final JFXPanel fxPanel_olp;
	private static String greedy = "Greedy";
	private static String overlap = "Overlap";
	private static String deBruijn = "De Bruijn";
	private static String improvedDBG = "Improved De Bruijn";
	private JButton btnPrevious;
	private JButton btnExit;
	static ObservableList<Series<String, Number>> observableList_dbg;
	static ObservableList<Series<String, Number>> observableList_olp;
	static BarChart<String, Number> barChart_dbg;
	static BarChart<String, Number> barChart_olp;
	
	public SummaryPanel(JTabbedPane tp) {
		this.tabbedPane = tp;
		this.setSize(680, 550);
		
		fxPanel_dbg = new JFXPanel();
		fxPanel_dbg.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		fxPanel_olp = new JFXPanel();
		fxPanel_olp.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initFX(fxPanel_dbg, fxPanel_olp);
			}
		});
		
		btnPrevious = new JButton("Previous");
		btnPrevious.setToolTipText("View genome assembler and logs");
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setSelectedIndex(MainFrame.GenomeAssemblerTabIndex);
			}
		});

		btnExit = new JButton("Exit");
		btnExit.setToolTipText("Shut down application");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnPrevious)
							.addPreferredGap(ComponentPlacement.RELATED, 532, Short.MAX_VALUE)
							.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(fxPanel_dbg, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(fxPanel_olp, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)
							.addGap(26))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(19)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(fxPanel_dbg, GroupLayout.PREFERRED_SIZE, 479, GroupLayout.PREFERRED_SIZE)
						.addComponent(fxPanel_olp, GroupLayout.PREFERRED_SIZE, 479, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnExit)
						.addComponent(btnPrevious))
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
	
	private static void initFX(JFXPanel fxPanel_dbg, JFXPanel fxPanel_olp) {
		// This method is invoked on the JavaFX thread
		Scene scene_dbg = createSceneForDBG();
		Scene scene_olp = createSceneForOLP();
		fxPanel_dbg.setScene(scene_dbg);
		fxPanel_olp.setScene(scene_olp);
	}
	
	public static void startAssembly() {
		new Thread() {
			public void run() {
				startMonitoring();
				this.setPriority(Thread.MAX_PRIORITY);
				Main.main(new String[0]);
			};
		}.start();
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
							observableList_dbg.set(0, getGraphConstructionSeriesForDBG());
							observableList_dbg.set(1, getContigGenerationSeriesForDBG());
							observableList_olp.set(0, getGraphConstructionSeriesForOLP());
							observableList_olp.set(1, getContigGenerationSeriesForOLP());
							//barChart.setAnimated(true);
						}
					});
				}
				try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
				GenomeAssemblyPanel.addToLstLog("De Bruijn Construction: " + observableList_dbg.get(0).getData().get(0).getYValue() + " ms & Contig generation: " + observableList_dbg.get(1).getData().get(0).getYValue() + " ms");
				GenomeAssemblyPanel.addToLstLog("Overlap Construction: " + observableList_olp.get(0).getData().get(0).getYValue() + " ms & Contig generation: " + observableList_olp.get(1).getData().get(0).getYValue() + " ms");
				GenomeAssemblyPanel.addToLstLog("Greedy Construction: " + observableList_olp.get(0).getData().get(1).getYValue() + " ms & Contig generation: " + observableList_olp.get(1).getData().get(1).getYValue() + " ms");
				GenomeAssemblyPanel.addToLstLog("Improved de Bruijn Construction: " + observableList_dbg.get(0).getData().get(1).getYValue() + " ms & Contig generation: " + observableList_dbg.get(1).getData().get(1).getYValue() + " ms");
			}
		}.start();
	}
	
	@SuppressWarnings("unchecked")
	private static Scene createSceneForDBG() {
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Method");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Time (ms)");
		barChart_dbg = new BarChart<String, Number>(xAxis, yAxis);
		barChart_dbg.setTitle("De Bruijn Methods");
		barChart_dbg.setCategoryGap(20);
		barChart_dbg.setBarGap(2);
		barChart_dbg.setVerticalGridLinesVisible(false);
		barChart_dbg.setAnimated(false);
		xAxis.setAnimated(false);
		yAxis.setAnimated(false);
		
		Scene scene = new Scene(barChart_dbg, 800, 600);
		observableList_dbg = FXCollections.observableArrayList(getGraphConstructionSeriesForDBG(), getContigGenerationSeriesForDBG());
		barChart_dbg.setData(observableList_dbg);
		
		return (scene);
	}
	
	@SuppressWarnings("unchecked")
	private static Scene createSceneForOLP() {
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Method");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Time (ms)");
		barChart_olp = new BarChart<String, Number>(xAxis, yAxis);
		barChart_olp.setTitle("Overlap Methods");
		barChart_olp.setCategoryGap(20);
		barChart_olp.setBarGap(2);
		barChart_olp.setVerticalGridLinesVisible(false);
		barChart_olp.setAnimated(false);
		xAxis.setAnimated(false);
		yAxis.setAnimated(false);
		
		Scene scene = new Scene(barChart_olp, 800, 600);
		observableList_olp = FXCollections.observableArrayList(getGraphConstructionSeriesForOLP(), getContigGenerationSeriesForOLP());
		barChart_olp.setData(observableList_olp);
		
		return (scene);
	}
	
	static Series<String, Number> getGraphConstructionSeriesForDBG() {
		BarChart.Series<String, Number> graphConstructionSeries = new BarChart.Series<String, Number>();
		graphConstructionSeries.setName("Graph Construction");
		graphConstructionSeries.getData().add(new BarChart.Data<String, Number>(deBruijn, Main.getGraphConstructionRunTime(Main.AssemblyMethods.DE_BRUIJN)));
		graphConstructionSeries.getData().add(new BarChart.Data<String, Number>(improvedDBG, Main.getGraphConstructionRunTime(Main.AssemblyMethods.IMPROVED_DE_BRUIJN)));
		return graphConstructionSeries;
	}
	
	static Series<String, Number> getContigGenerationSeriesForDBG() {
		BarChart.Series<String, Number> contigGenerationSeries = new BarChart.Series<String, Number>();
		contigGenerationSeries.setName("Contig Generation");
		contigGenerationSeries.getData().add(new BarChart.Data<String, Number>(deBruijn, Main.getContigGenerationRunTime(Main.AssemblyMethods.DE_BRUIJN)));
		contigGenerationSeries.getData().add(new BarChart.Data<String, Number>(improvedDBG, Main.getContigGenerationRunTime(Main.AssemblyMethods.IMPROVED_DE_BRUIJN)));
		return contigGenerationSeries;
	}
	
	static Series<String, Number> getGraphConstructionSeriesForOLP() {
		BarChart.Series<String, Number> graphConstructionSeries = new BarChart.Series<String, Number>();
		graphConstructionSeries.setName("Graph Construction");
		graphConstructionSeries.getData().add(new BarChart.Data<String, Number>(overlap, Main.getGraphConstructionRunTime(Main.AssemblyMethods.OVERLAP)));
		graphConstructionSeries.getData().add(new BarChart.Data<String, Number>(greedy, Main.getGraphConstructionRunTime(Main.AssemblyMethods.GREEDY)));
		return graphConstructionSeries;
	}
	
	static Series<String, Number> getContigGenerationSeriesForOLP() {
		BarChart.Series<String, Number> contigGenerationSeries = new BarChart.Series<String, Number>();
		contigGenerationSeries.setName("Contig Generation");
		contigGenerationSeries.getData().add(new BarChart.Data<String, Number>(overlap, Main.getContigGenerationRunTime(Main.AssemblyMethods.OVERLAP)));
		contigGenerationSeries.getData().add(new BarChart.Data<String, Number>(greedy, Main.getContigGenerationRunTime(Main.AssemblyMethods.GREEDY)));
		return contigGenerationSeries;
	}
}
