

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



public class GUI extends JFrame {
	public Controller controller;
	private JPanel networkPanel;
	private static int frameSize = 600;
	private static int frameSizeH = 620;
	private List<String> finalresult;
	private Agent agent;
	private int count = 0;
	private List<Node> nodes = new ArrayList<Node>();
	private Map<Agent,List<String>> agents = new HashMap<Agent,List<String>>();

	public GUI(List<Node> nodes,double agentVelo){
		setSize(frameSize,frameSizeH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.nodes = nodes;
		//velocity = agentVelo;
		run();
	}
	@SuppressWarnings("deprecation")
	public void run() {
		controller = new Controller(nodes);
		
//		for(int i=0;i<2;i++){
//			sourceNode = controller.randomSourceNode();
//			System.out.println(sourceNode.getName());
//			destNode = controller.randomDestNode(sourceNode);
//			System.out.println(destNode.getName());
//			
//			path = new ArrayList<Node>();
//			controller.computePaths(sourceNode,nodes);
//			path = controller.getShortestPathTo(destNode);
//			System.out.println("Path : "+ path);
//			
//			agent = new Agent(i,sourceNode,destNode);
//			agent.setVelocity(velocity);
//			System.out.println(agent.getPositionX()+" "+agent.getPositionY());
//			finalresult = controller.calculateTrajectory(path,agent);
//			agents.put(agent, finalresult);
//			//write file
//			try {
//				String[] sentence = finalresult.get(0).split(" ");
//				FileWriter writer = new FileWriter(sentence[0]+"_"+sourceNode.getX()+"_"+sourceNode.getY()+"_"+destNode.getX()+"_"+destNode.getY()+".txt");
//				writer.write(sourceNode+" "+destNode);
//				for(String result: finalresult){
//					writer.write(result);
//					writer.write(System.lineSeparator());
//				}
//				writer.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		networkPanel = new JPanel(){
			public void paintComponent(Graphics g){
				count += 1;
				Graphics2D gf = (Graphics2D) g;
				Insets insets = getInsets();
				 int h = getHeight() - insets.top - insets.bottom;
				 gf.scale(1.0, -1.0);
				 gf.translate(0, -h - insets.top);
//	         
				super.paintComponent(g);
				this.setBackground(Color.black);
				//draw rectangle
				g.setColor(Color.YELLOW);
				for(Node node:nodes){
					for(Node neighbor:node.getNeighbor()){
						g.drawLine((int)calculateSimPosX(node.getX()), (int)calculateSimPosY(node.getY()), (int)calculateSimPosX(neighbor.getX()), (int)calculateSimPosY(neighbor.getY()));
					}
				}
				//draw node
				g.setColor(Color.YELLOW);
				for(Node node:nodes){
					g.fillOval((int)calculateSimPosX(node.getX())-15, (int)calculateSimPosY(node.getY())-15, 30, 30);
				}
				//draw way
				g.setColor(Color.CYAN);
				for(Map.Entry<Agent, List<String>> entry: agents.entrySet()){
					g.fillOval((int)entry.getKey().getSimPosX()-10, (int)entry.getKey().getSimPosY()-10, 20, 20);
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
	public void moveAgent(String str){
		String[] splited;
			splited = str.split(" ");
			agent.setSimPosX(calculateSimPosX(Double.parseDouble(splited[1])));
			System.out.println("agent simulate X : "+ agent.getSimPosX());
			agent.setSimPosY(calculateSimPosY(Double.parseDouble(splited[2])));
			System.out.println("agent simulate Y : "+ agent.getSimPosY());
	}
	public Agent getAgent() {
		return agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	public JPanel getNetworkPanel() {
		return networkPanel;
	}
	public void setNetworkPanel(JPanel networkPanel) {
		this.networkPanel = networkPanel;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Map<Agent, List<String>> getAgents() {
		return agents;
	}
	public void setAgents(Map<Agent, List<String>> agents) {
		this.agents = agents;
	}
	
}
