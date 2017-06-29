

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
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
import javax.swing.JPanel;

import org.monte.media.avi.AVIOutputStream;
import org.monte.media.avi.AVIOutputStreamOLD;



public class AgentSimulatorGUI extends JFrame implements Observer, Runnable{
	private JPanel networkPanel;
	private AgentSimulator agentSimulator;
	
	private static int frameSize = 600;
	private static int frameSizeH = 620;
	

	public AgentSimulatorGUI(AgentSimulator agentSimulator){
		setSize(frameSize,frameSizeH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.agentSimulator = agentSimulator;
		this.agentSimulator.addObserver(this);
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
				//draw agent position way
				g.setColor(Color.CYAN);
				for(Map.Entry<Agent, List<String>> entry: agentSimulator.getAgents().entrySet()){
					g.fillOval((int)calculateSimPosX(entry.getKey().getPositionX())-10, (int)calculateSimPosX(entry.getKey().getPositionY())-10, 20, 20);
				}
			}
		};
		add(networkPanel);
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
	
	
	
}
