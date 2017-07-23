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
	private JLabel labela = new JLabel();
	private AVIOutputStreamOLD out;
	
	public CorrelationGUI(AgentSimulator agentSimulator){
		setSize(frameSize,frameSizeH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.agentSimulator = agentSimulator;
		this.agentSimulator.addObserver(this);
		labela.setForeground(Color.yellow);
		this.init();
	}
	
	public void init(){
		double[] crossCurrenceAB = agentSimulator.getcrossAB().getCrossCurrence();
		double[] crossCurrenceBC = agentSimulator.getCrossBC().getCrossCurrence();
		double[] crossCurrenceDE = agentSimulator.getCrossDE().getCrossCurrence();

		//DefaultCategoryDataset dataset = createDataset(crossCurrence);
		XYDataset dataset = createXYDataset(crossCurrenceAB,crossCurrenceBC,crossCurrenceDE);
		JFreeChart lineChart = ChartFactory.createXYLineChart("CrossCorrelation", "Time", "value",dataset, PlotOrientation.VERTICAL,true,true,false);
		
		XYPlot xyplot = lineChart.getXYPlot();
		NumberAxis yAxis = (NumberAxis)xyplot.getRangeAxis();
		yAxis.setTickUnit(new NumberTickUnit(1));
		yAxis.setAutoRange(false);
		chartPanel = new ChartPanel( lineChart ){
			public void paintComponent(Graphics g){
//				Graphics2D gf = (Graphics2D) g;
//				Insets insets = getInsets();
//				 int h = getHeight() - insets.top - insets.bottom;
//				 gf.scale(1.0, -1.0);
//				 gf.translate(0, -h - insets.top);
				
				super.paintComponent(g);
				this.setBackground(Color.black);
				//System.out.println("test");
				
				double[] crossCurrenceAB = agentSimulator.getcrossAB().getCrossCurrence();
				double[] crossCurrenceBC = agentSimulator.getCrossBC().getCrossCurrence();
				double[] crossCurrenceDE = agentSimulator.getCrossDE().getCrossCurrence();
				//XYDataset dataset = resetXYDataset(crossCurrence);
				
				JFreeChart lineChart = ChartFactory.createXYLineChart("CrossCorrelation", "Time", "Value", createXYDataset(crossCurrenceAB,crossCurrenceBC,crossCurrenceDE));
			    chartPanel.setChart(lineChart);
				chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
			    setContentPane( chartPanel );
			    XYPlot xyplot = lineChart.getXYPlot();
				//NumberAxis yAxis = (NumberAxis)xyplot.getRangeAxis();
			    ValueAxis yAxis = (ValueAxis)xyplot.getRangeAxis();
				//yAxis.setTickUnit(new NumberTickUnit(1));
				//yAxis.setAutoRange(true);
				yAxis.setRange(-300,1000);
				
				//System.out.println("test");
// 				g.setColor(Color.YELLOW);
//				g.drawLine((int)calculateSimPosX(0), (int)calculateSimPosY(0), (int)calculateSimPosX(0), (int)calculateSimPosY(10));
//				g.drawLine((int)calculateSimPosX(0), (int)calculateSimPosY(0), (int)calculateSimPosX(15), (int)calculateSimPosY(0));
				
//				for(int i=0;i<crossCurrence.length;i++){
//					g.fillOval(i,crossCurrence[i], 5, 5);
//				}
				
			}
		};
		add(chartPanel);
		try {
			 out = new AVIOutputStreamOLD(new File("test2.avi"), AVIOutputStreamOLD.AVIVideoFormat.JPG, 24);
			 out.setFrameRate(400);
			 out.setTimeScale(10);
			 out.setVideoDimension(800, 800);
		} catch (IOException e) {
			 e.printStackTrace();
		}
	}
	private XYDataset createXYDataset(double[] crossCurrenceAB,double[] crossCurrenceBC,double[] crossCurrenceDE){
		 XYSeriesCollection dataset = new XYSeriesCollection();
		 XYSeries series1 = new XYSeries("AB");
		 XYSeries series2 = new XYSeries("BC");
		 XYSeries series3 = new XYSeries("DE");

		 for(int i=0;i<crossCurrenceAB.length;i++){
			 series1.add(i,crossCurrenceAB[i]);
			 //System.out.print(crossCurrenceAB[i]+" ");
		 }
		 //System.out.println();
		 for(int i=0;i<crossCurrenceBC.length;i++){
			 series2.add(i,crossCurrenceBC[i]);
			 //System.out.print(crossCurrenceBC[i]+" ");
		 }
		 //System.out.println();
		 for(int i=0;i<crossCurrenceDE.length;i++){
			 series3.add(i,crossCurrenceDE[i]);
			 //System.out.print(crossCurrenceDE[i]+" ");
		 }
		 //System.out.println();
		 dataset.addSeries(series1);
		 dataset.addSeries(series2);
		 dataset.addSeries(series3);
		 return dataset;
	}
	private DefaultCategoryDataset createDataset(double[] crossCurrence) {
	      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
	      for(int i=0;i<crossCurrence.length;i++){
	    	  dataset.addValue(crossCurrence[i], "AB", i+"");
	      }
//	      dataset.addValue( agentSimulator.getCounter()+20 , "schools" , "1970" );
//	      dataset.addValue( agentSimulator.getCounter()+410 , "schools" , "1980" );
//	      dataset.addValue( agentSimulator.getCounter()+60 , "schools" ,  "1990" );
//	      dataset.addValue( agentSimulator.getCounter() +80, "schools" , "2000" );
//	      dataset.addValue( agentSimulator.getCounter() +1500, "schools" , "2010" );
//	      dataset.addValue( agentSimulator.getCounter() +120, "schools" , "2014" );
	      return dataset;
	   }
	private DefaultCategoryDataset resetDataset(double[] crossCurrenceAB,double[] crossCurrenceBC) {
	      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
	      for(int i=0;i<crossCurrenceAB.length;i++){
	    	  dataset.setValue(crossCurrenceAB[i], "AB", i+"");
	      }
//	      dataset.addValue( agentSimulator.getCounter()+20 , "schools" , "1970" );
//	      dataset.addValue( agentSimulator.getCounter()+410 , "schools" , "1980" );
//	      dataset.addValue( agentSimulator.getCounter()+60 , "schools" ,  "1990" );
//	      dataset.addValue( agentSimulator.getCounter() +80, "schools" , "2000" );
//	      dataset.addValue( agentSimulator.getCounter() +1500, "schools" , "2010" );
//	      dataset.addValue( agentSimulator.getCounter() +120, "schools" , "2014" );
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
	public double calculateSimPosX(double x){
		return ((505-55)/10)*x+55;
	}
	
	public double calculateSimPosY(double y){
		return ((505-55)/10)*y+55;
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
