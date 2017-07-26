

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
	
	private static int frameSize = 800;
	private static int frameSizeH = 820;
	private JLabel a,b,c,d,f;
	private AVIOutputStreamOLD out;
	private List<File> imageLists = new ArrayList<File>();
	
	

	public AgentSimulatorGUI(AgentSimulator agentSimulator){
		setSize(frameSize,frameSizeH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.agentSimulator = agentSimulator;
		this.agentSimulator.addObserver(this);
		this.initComponent();
	}

	public void initComponent() {
		a = new JLabel("0");
		a.setForeground(Color.YELLOW);
		b = new JLabel("1");
		b.setForeground(Color.YELLOW);
		c = new JLabel("2");
		c.setForeground(Color.YELLOW);
		d = new JLabel("3");
		d.setForeground(Color.YELLOW);
		f = new JLabel("5");
		f.setForeground(Color.YELLOW);
		
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
						g.drawLine((int)calculateSimPosX(node.getPosX()), (int)calculateSimPosY(node.getPosY()), (int)calculateSimPosX(neighbor.getPosX()), (int)calculateSimPosY(neighbor.getPosY()));
					}
				}
				
				//draw node
				g.setColor(Color.YELLOW);
				for(Node node: agentSimulator.getNetwork().getNodes()){
					g.fillOval((int)calculateSimPosX(node.getPosX())-15, (int)calculateSimPosY(node.getPosY())-15, 30, 30);
					g.drawOval((int)calculateSimPosX(node.getPosX())-55, (int)calculateSimPosY(node.getPosY())-55, 110,110);
				}
				for(Node node: agentSimulator.getNetwork().getNodeprime()){
					g.fillOval((int)calculateSimPosX(node.getPosX())-15, (int)calculateSimPosY(node.getPosY())-15, 30, 30);
				}
				
				this.add(a);
				this.add(b);
				this.add(c);
				this.add(d);
				this.add(f);
				a.setLocation((int)calculateSimPosX(12)-30, (int)calculateSimPosY(12)-30);
				b.setLocation((int)calculateSimPosX(2)-30, (int)calculateSimPosY(12)-30);
				c.setLocation((int)calculateSimPosX(2)-30, (int)calculateSimPosY(2)-30);
				d.setLocation((int)calculateSimPosX(12)-30, (int)calculateSimPosY(2)-30);
				f.setLocation((int)calculateSimPosX(7)-30, (int)calculateSimPosY(2)-30);
				
				String countAgentNodeA = ""+agentSimulator.getNetwork().getNodes().get(1).getEventLog().get(agentSimulator.getNetwork().getNodes().get(0).getEventLog().size()-1).getValue();
				a.setText(countAgentNodeA);
				String countAgentNodeB = ""+agentSimulator.getNetwork().getNodes().get(0).getEventLog().get(agentSimulator.getNetwork().getNodes().get(0).getEventLog().size()-1).getValue();
				b.setText(countAgentNodeB);
				String countAgentNodeC = ""+agentSimulator.getNetwork().getNodes().get(2).getEventLog().get(agentSimulator.getNetwork().getNodes().get(2).getEventLog().size()-1).getValue();
				c.setText(countAgentNodeC);
				String countAgentNodeD = ""+agentSimulator.getNetwork().getNodes().get(3).getEventLog().get(agentSimulator.getNetwork().getNodes().get(0).getEventLog().size()-1).getValue();
				d.setText(countAgentNodeD);
				String countAgentNodeF = ""+agentSimulator.getNetwork().getNodes().get(5).getEventLog().get(agentSimulator.getNetwork().getNodes().get(2).getEventLog().size()-1).getValue();
				f.setText(countAgentNodeF);		
				//draw agent position way
				g.setColor(Color.CYAN);
				for(Map.Entry<Agent, List<String>> entry: agentSimulator.getAgents().entrySet()){
					g.fillOval((int)calculateSimPosX(entry.getKey().getPositionX())-10, (int)calculateSimPosX(entry.getKey().getPositionY())-10, 20, 20);
				}
				
			}
		};
		add(networkPanel);
//		try {
//			 out = new AVIOutputStreamOLD(new File("test1.avi"), AVIOutputStreamOLD.AVIVideoFormat.JPG, 24);
//			 out.setFrameRate(400);
//			 out.setTimeScale(10);
//			 out.setVideoDimension(800, 800);
//		} catch (IOException e) {
//			 e.printStackTrace();
//		}
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
//	public AVIOutputStreamOLD getOut() {
//		return out;
//	}
//
//	public void setOut(AVIOutputStreamOLD out) {
//		this.out = out;
//	}
}
