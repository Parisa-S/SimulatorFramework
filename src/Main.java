import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;












import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;





public class Main {
	private static List<Node> nodes;
	private static final String fileName = "config.txt";
	private static int count=0;
	private static double[][] trafficMatrix;
	

	public static void main(String[] args) throws IOException {
		nodes = new ArrayList<Node>();
		
		
		//read file 
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		List<String> lines = new ArrayList<String>();
		String currentLine = null;
		Map<String,Double> rateOfArrivals = new HashMap<String,Double>();
		
		while((currentLine = br.readLine()) != null){
			if(!currentLine.startsWith("#")|| currentLine.trim().isEmpty()){
				lines.add(currentLine);
			}
		}
		int numNode = Integer.parseInt(lines.get(count));
		count++;
		for(int i=0;i<=numNode-1;i++){
			String s = lines.get(count);
			String[] arr = s.split(" ");
			//check the node that is over than input number of node or not 
			try{
				Node node = new Node(arr[0],Double.parseDouble(arr[1]),Double.parseDouble(arr[2]));
				//check scale
				if(checkScale(Double.parseDouble(arr[1]),Double.parseDouble(arr[2]))){
					//check duplicate
					if(!nodes.isEmpty()){
						for(Node n:nodes){
							if((n.getName().equals(node.getName())||(n.getPosX()==node.getPosX()&&n.getPosY()==node.getPosY()))){
								System.out.println("duplicate node at "+node);
								return;
							}
						}
						nodes.add(node);
					}
					else {
						nodes.add(node);
					}
				}else {
					System.out.println("over scale");
					return;
				}
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("can not create node number"+count );
				return;
			}
			count++;
		}
		if(!checkNumberOfLine(lines.get(count))){
			System.out.println("err: please check number of node : number of node is less than node that created");
			return;
		}
		int numLink = Integer.parseInt(lines.get(count));
		
		count++;
		for(int i=0;i<=numLink-1;i++){
			String s = lines.get(count);
			String[] arr = s.split(" ");
			
			boolean existstart = false;
			boolean existend = false;
			try{
				for(Node n: nodes){
					if(n.getName().equals(arr[0])){
						existstart = true;
						continue;
					}
					if(n.getName().equals(arr[1])){
						existend = true;
						continue;
					}
				}
				if(existstart && existend){
					nodes.get(Integer.parseInt(arr[0])-1).addNeighbor(nodes.get(Integer.parseInt(arr[1])-1));
					nodes.get(Integer.parseInt(arr[1])-1).addNeighbor(nodes.get(Integer.parseInt(arr[0])-1));
				}else {
					System.out.println("node is not exist");
					return;
				}
			}catch(NumberFormatException e){
				System.out.println("the number of link and link detail is not match");
				return;
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("can not create link number"+(i+1) );
				return;
			}
			count++;
		}
		if(!checkNumberOfLineD(lines.get(count))){
			System.out.println("err: please check number of link : number of link is less than link that created");
			return;
		}
		double mean= Double.parseDouble(lines.get(count));
		count++;
		double stdVa = Double.parseDouble(lines.get(count));

		//rate of arrival
		count++;
		for(int i=0;i<=numNode-1;i++){
			String s = lines.get(count);
			String[] arr = s.split(" ");

			int nodeName = Integer.parseInt(arr[0]);
			double rate = Double.parseDouble(arr[1]);
			rateOfArrivals.put(arr[0], Double.parseDouble(arr[1]));
			nodes.get(nodeName-1).setRateOfArrival(rate);
			count++;
		}
		//traffic matrix
		count++;
		trafficMatrix = new double[numNode+1][numNode+1];
		
		for(int i=1;i<=numNode;i++){
			double sum = 0;
			String s = lines.get(count);
			String[] arr = s.split(" ");
			for(int j=1;j<=numNode;j++){
				trafficMatrix[i][j] = Double.parseDouble(arr[j]);
				sum += Double.parseDouble(arr[j]);
				
			}
			if(trafficMatrix[i][i]!=0){
				System.out.println("not equal 0");
				return;
			}
			if(sum > 1){
				System.out.println("Summation of line is over than 1");
				return;
			}
			
			count++;
		}
		double[][] changedMatrix = changeMatrix();

		//add to network
		Network network = new Network(nodes);

		//create simulator
		AgentSimulator agentSimulator = new AgentSimulator(network, mean,stdVa,changedMatrix );
		Map<Node,Node> sourceSink = agentSimulator.randomNode();
		for(Map.Entry<Node, Node> entry:sourceSink.entrySet()){
			List<Node> path = agentSimulator.computePath(entry.getKey(), entry.getValue());
			agentSimulator.generateAgent(entry.getKey(),entry.getValue(),path);	
			agentSimulator.numberOfAgents++;
		}
		
		AgentSimulatorGUI agentSimulatorGUI = new AgentSimulatorGUI(agentSimulator);
		CorrelationGUI correlationGUI = new CorrelationGUI(agentSimulator);
		Thread a = new Thread(agentSimulatorGUI);
		Thread b = new Thread(correlationGUI);
		Thread c = new Thread(new Runnable(){

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
					while(true){
						agentSimulator.updatePosition();
						
						try {
							Thread.sleep(10);
						} catch (InterruptedException e ) {
							e.printStackTrace();				
						}
					}
			}
			
		});
		a.start();
		b.start();
		c.start();
	}
	public static double[][] changeMatrix(){
		double[][] changed = new double[trafficMatrix.length][trafficMatrix.length];
		for(int i=0;i<=trafficMatrix.length-1;i++){
			for(int j=0;j<=trafficMatrix.length-1;j++){
				changed[i][j] = 0;
			}
		}
		for(int i=1;i<=trafficMatrix.length-1;i++){
			for(int j=1;j<=trafficMatrix.length-1;j++){
				changed[i][j] = changed[i][j-1]+trafficMatrix[i][j];
			}
		}
		return changed;
	}
	public static boolean checkNumberOfLine(String inputLine){
		boolean check = true;
		try{
			Integer.parseInt(inputLine);
		}catch(NumberFormatException e){
			check = false;
		}
		return check;
	}
	public static boolean checkNumberOfLineD(String inputLine){
		boolean check = true;
		try{
			Double.parseDouble(inputLine);
		}catch(NumberFormatException e){
			check = false;
		}
		return check;
	}
	public static boolean checkScale(double x, double y){
		if(x>10||y>10){
			return false;
		}
		return true;
	}
}
