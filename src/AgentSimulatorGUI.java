

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.lang.*;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.monte.media.avi.AVIOutputStream;
import org.monte.media.avi.AVIOutputStreamOLD;



public class AgentSimulatorGUI extends JFrame implements Observer, Runnable{
	private JPanel networkPanel;
	private AgentSimulator agentSimulator;
	
	private static int frameSize = 600;
	private static int frameSizeH = 620;
	private JLabel a;
	private JLabel b;
	private JLabel c;
	private AVIOutputStreamOLD out;
	private List<File> imageLists = new ArrayList<File>();
	
	

	public AgentSimulatorGUI(AgentSimulator agentSimulator){
		setSize(frameSize,frameSizeH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.agentSimulator = agentSimulator;
		this.agentSimulator.addObserver(this);
		a = new JLabel("0");
		a.setForeground(Color.YELLOW);
		b = new JLabel("1");
		b.setForeground(Color.YELLOW);
		c = new JLabel("2");
		c.setForeground(Color.YELLOW);
		this.init();
	}

	public void init() {

		networkPanel = new JPanel(){
			
			public void paintComponent(Graphics g){
				Graphics2D gf = (Graphics2D) g;
				Insets insets = getInsets();
				 int h = getHeight() - insets.top - insets.bottom;
				 gf.scale(1.0, -1.0);
				 gf.translate(0, -h - insets.top);
	         
				super.paintComponent(g);
				this.setBackground(Color.black);
				//draw line
				g.setColor(Color.YELLOW);
				for(Node node: agentSimulator.getNetwork().getNodes()){
					for(Node neighbor:node.getNeighbor()){
						g.drawLine((int)calculateSimPosX(node.getX()), (int)calculateSimPosY(node.getY()), (int)calculateSimPosX(neighbor.getX()), (int)calculateSimPosY(neighbor.getY()));
					}
				}
				//draw node
				g.setColor(Color.YELLOW);
				for(Node node: agentSimulator.getNetwork().getNodes()){
					g.fillOval((int)calculateSimPosX(node.getX())-15, (int)calculateSimPosY(node.getY())-15, 30, 30);
				}
				g.setColor(Color.RED);
				g.drawOval((int)calculateSimPosX(10)-55, (int)calculateSimPosY(10)-55, 110,110);
				g.drawOval((int)calculateSimPosX(0)-55, (int)calculateSimPosY(10)-55, 110,110);
				g.drawOval((int)calculateSimPosX(0)-55, (int)calculateSimPosY(0)-55, 110,110);
				this.add(a);
				this.add(b);
				this.add(c);
				a.setLocation((int)calculateSimPosX(10)-30, (int)calculateSimPosY(10)-30);
				b.setLocation((int)calculateSimPosX(0)-30, (int)calculateSimPosY(10)-30);
				c.setLocation((int)calculateSimPosX(0)-30, (int)calculateSimPosY(0)-30);
				
				String textnodea = ""+agentSimulator.getNetwork().getNodes().get(1).getEventLog().get(agentSimulator.getNetwork().getNodes().get(0).getEventLog().size()-1).getValue();
				a.setText(textnodea);
				String textnodeb = ""+agentSimulator.getNetwork().getNodes().get(0).getEventLog().get(agentSimulator.getNetwork().getNodes().get(0).getEventLog().size()-1).getValue();
				b.setText(textnodeb);
				String textnodec = ""+agentSimulator.getNetwork().getNodes().get(2).getEventLog().get(agentSimulator.getNetwork().getNodes().get(2).getEventLog().size()-1).getValue();
				c.setText(textnodec);
							
				//draw agent position way
				g.setColor(Color.CYAN);
				for(Map.Entry<Agent, List<String>> entry: agentSimulator.getAgents().entrySet()){
					g.fillOval((int)calculateSimPosX(entry.getKey().getPositionX())-10, (int)calculateSimPosX(entry.getKey().getPositionY())-10, 20, 20);
				}
				
			}
		};
		add(networkPanel);
		try {
			 out = new AVIOutputStreamOLD(new File("test1.avi"), AVIOutputStreamOLD.AVIVideoFormat.JPG, 24);
			 out.setFrameRate(400);
			 out.setTimeScale(10);
			 out.setVideoDimension(800, 800);
		} catch (IOException e) {
			 e.printStackTrace();
		}
	}
	@SuppressWarnings("deprecation")
	public void captureScreen(JComponent component, File imageFile) throws IOException {
		 BufferedImage capimage = new BufferedImage(component.getSize().width, component.getSize().height, BufferedImage.TYPE_INT_RGB);  
		 component.paint(capimage.createGraphics());    
		 ImageIO.write(capimage, "jpeg", imageFile);
		 imageLists.add(imageFile);
		 out.writeFrame(imageFile);
	}
	public double calculateSimPosX(double x){
		return ((505-55)/10)*x+55;
	}
	
	public double calculateSimPosY(double y){
		return ((505-55)/10)*y+55;
	}
	@Override
	public void update(Observable arg0, Object arg1) {
		networkPanel.repaint();
	}

	@Override
	public void run() {
		this.setVisible(true);
	}

	public JPanel getNetworkPanel() {
		return networkPanel;
	}

	public void setNetworkPanel(JPanel networkPanel) {
		this.networkPanel = networkPanel;
	}

	public AVIOutputStreamOLD getOut() {
		return out;
	}

	public void setOut(AVIOutputStreamOLD out) {
		this.out = out;
	}
	
	
	
}
