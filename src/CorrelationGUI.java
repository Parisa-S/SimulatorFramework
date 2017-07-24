import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.monte.media.avi.AVIOutputStreamOLD;


public class CorrelationGUI extends JFrame implements Observer,Runnable{
	private JPanel panel;
	private ChartPanel chartPanel;
	private AgentSimulator agentSimulator;
	
	private static int frameSize = 800;
	private static int frameSizeH = 620;
	private AVIOutputStreamOLD out;
	
	public CorrelationGUI(AgentSimulator agentSimulator){
		setSize(frameSize,frameSizeH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.agentSimulator = agentSimulator;
		this.agentSimulator.addObserver(this);
		this.initComponent();
	}
	
	public void initComponent(){
		double[] crossCurrenceAB = agentSimulator.getcrossAB().getCrossCurrence();
		double[] crossCurrenceBC = agentSimulator.getCrossBC().getCrossCurrence();
		double[] crossCurrenceDE = agentSimulator.getCrossDE().getCrossCurrence();

		XYDataset dataset = createXYDataset(crossCurrenceAB,crossCurrenceBC,crossCurrenceDE);
		JFreeChart lineChart = ChartFactory.createXYLineChart("CrossCorrelation", "Time", "value",dataset, PlotOrientation.VERTICAL,true,true,false);
		
		XYPlot xyplot = lineChart.getXYPlot();
		NumberAxis yAxis = (NumberAxis)xyplot.getRangeAxis();
		yAxis.setTickUnit(new NumberTickUnit(1));
		yAxis.setAutoRange(false);
		chartPanel = new ChartPanel( lineChart ){
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				this.setBackground(Color.black);
				
				double[] crossCurrenceAB = agentSimulator.getcrossAB().getCrossCurrence();
				double[] crossCurrenceBC = agentSimulator.getCrossBC().getCrossCurrence();
				double[] crossCurrenceDE = agentSimulator.getCrossDE().getCrossCurrence();
				
				JFreeChart lineChart = ChartFactory.createXYLineChart("CrossCorrelation", "Time", "Value", createXYDataset(crossCurrenceAB,crossCurrenceBC,crossCurrenceDE));
			    chartPanel.setChart(lineChart);
				chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
			    setContentPane( chartPanel );
			    
			    XYPlot xyplot = lineChart.getXYPlot();
			    ValueAxis yAxis = (ValueAxis)xyplot.getRangeAxis();
				yAxis.setRange(-300,1000);
			}
		};
		add(chartPanel);
//		try {
//			 out = new AVIOutputStreamOLD(new File("test2.avi"), AVIOutputStreamOLD.AVIVideoFormat.JPG, 24);
//			 out.setFrameRate(400);
//			 out.setTimeScale(10);
//			 out.setVideoDimension(800, 800);
//		} catch (IOException e) {
//			 e.printStackTrace();
//		}
	}
	
	private XYDataset createXYDataset(double[] crossCurrenceAB,double[] crossCurrenceBC,double[] crossCurrenceDE){
		 XYSeriesCollection dataset = new XYSeriesCollection();
		 XYSeries series1 = new XYSeries("AB");
		 XYSeries series2 = new XYSeries("BC");
		 XYSeries series3 = new XYSeries("DE");

		 for(int i=0;i<crossCurrenceAB.length;i++){
			 series1.add(i,crossCurrenceAB[i]);
		 }
		 for(int i=0;i<crossCurrenceBC.length;i++){
			 series2.add(i,crossCurrenceBC[i]);
		 }
		 for(int i=0;i<crossCurrenceDE.length;i++){
			 series3.add(i,crossCurrenceDE[i]);
		 }
		 dataset.addSeries(series1);
		 dataset.addSeries(series2);
		 dataset.addSeries(series3);
		 return dataset;
	}
	
	@SuppressWarnings("deprecation")
	public void captureScreen(JComponent component, File imageFile) throws IOException {
		 BufferedImage capimage = new BufferedImage(component.getSize().width, component.getSize().height, BufferedImage.TYPE_INT_RGB);  
		 component.paint(capimage.createGraphics());    
		 ImageIO.write(capimage, "jpeg", imageFile);
		 //imageLists.add(imageFile);
		 out.writeFrame(imageFile);
	}

	@Override
	public void run() {
		this.setVisible(true);	
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		chartPanel.repaint();
	}
}
