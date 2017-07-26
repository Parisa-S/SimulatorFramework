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
	private static List<Node> nodeobs,nodeprime,allnodes;
	private static final String fileConfig = "config.txt";
	private static final String filePreferences = "preferences.txt";
	private static int count=0;
	private static int countPre = 0;
	private static double[][] trafficMatrix;
	

	public static void main(String[] args) throws IOException {
		nodeobs = new ArrayList<Node>();
		nodeprime = new ArrayList<Node>();
		allnodes = new ArrayList<Node>();
		
		//read file 
		FileReader fr = new FileReader(fileConfig);
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
					if(!nodeobs.isEmpty()){
						for(Node n:nodeobs){
							if((n.getName().equals(node.getName())||(n.getPosX()==node.getPosX()&&n.getPosY()==node.getPosY()))){
								System.out.println("duplicate node at "+node);
								return;
							}
						}
						nodeobs.add(node);
						allnodes.add(node);
					}
					else {
						nodeobs.add(node);
						allnodes.add(node);
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
			System.out.println(count);
		}
		for(int i=0;i<=numNode-1;i++){
			String s = lines.get(count);
			String[] arr = s.split(" ");
			//check the node that is over than input number of node or not 
			try{
				Node node = new Node(arr[0],Double.parseDouble(arr[1]),Double.parseDouble(arr[2]));
				//check scale
				if(checkScale(Double.parseDouble(arr[1]),Double.parseDouble(arr[2]))){
					//check duplicate
					if(!nodeprime.isEmpty()){
						for(Node n:nodeprime){
							if((n.getName().equals(node.getName())||(n.getPosX()==node.getPosX()&&n.getPosY()==node.getPosY()))){
								System.out.println("duplicate node at "+node);
								return;
							}
						}
						nodeprime.add(node);
						allnodes.add(node);
					}
					else {
						nodeprime.add(node);
						allnodes.add(node);
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
			boolean existstartP = false;
			boolean existendP = false;
			try{
				for(Node n: nodeobs){
					if(n.getName().equals(arr[0])){
						existstart = true;
						continue;
					}
					if(n.getName().equals(arr[1])){
						existend = true;
						continue;
					}
				}
				for(Node n: nodeprime){
					if(n.getName().equals(arr[0])){
						existstartP = true;
						continue;
					}
					if(n.getName().equals(arr[1])){
						existendP = true;
						continue;
					}
				}
				//System.out.println(existstart+" "+existend+" "+existstartP+" "+existendP+" ");
				if(existstart && existend){
					nodeobs.get(Integer.parseInt(arr[0])-1).addNeighbor(nodeobs.get(Integer.parseInt(arr[1])-1));
					nodeobs.get(Integer.parseInt(arr[1])-1).addNeighbor(nodeobs.get(Integer.parseInt(arr[0])-1));
					System.out.println("test");
				}else if((existstart||existend) && existendP){
					nodeobs.get(Integer.parseInt(arr[0])-1).addNeighbor(nodeprime.get(Integer.parseInt(arr[1])-(numNode+1)));
					nodeprime.get(Integer.parseInt(arr[1])-(numNode+1)).addNeighbor(nodeobs.get(Integer.parseInt(arr[0])-1));
					System.out.println("test 2");
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
			nodeobs.get(nodeName-1).setRateOfArrival(rate);
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
		//read file
		FileReader frPre = new FileReader(filePreferences);
		BufferedReader brPre = new BufferedReader(frPre);
		List<String> linesPre = new ArrayList<String>();
		String currentLinePre = null;
		while((currentLinePre = brPre.readLine()) != null){
			if(!currentLinePre.startsWith("#")|| currentLinePre.trim().isEmpty()){
				linesPre.add(currentLinePre);
			}
		}
		int eventSt = Integer.parseInt(linesPre.get(countPre));
		//add to network
		Network network = new Network(nodeobs,nodeprime,allnodes);
		

		//create simulator
		AgentSimulator agentSimulator = new AgentSimulator(network, mean,stdVa,changedMatrix,eventSt );
		Map<Node,Node> sourceSink = agentSimulator.randomNode();
		for(Map.Entry<Node, Node> entry:sourceSink.entrySet()){
			List<Node> path = agentSimulator.computePath(entry.getKey(), entry.getValue());
			agentSimulator.generateAgent(entry.getKey(),entry.getValue(),path);	
			agentSimulator.numberOfAgents++;
		}
		
		AgentSimulatorGUI agentSimulatorGUI = new AgentSimulatorGUI(agentSimulator);
		CorrelationGUI correlationGUI = new CorrelationGUI(agentSimulator);
		EventLogGUI eventLogGUI = new EventLogGUI(agentSimulator);
		Thread a = new Thread(agentSimulatorGUI);
		Thread b = new Thread(correlationGUI);
		Thread d = new Thread(eventLogGUI);
		Thread c = new Thread(new Runnable(){

			@Override
			public void run() {
				
					while(true){
						agentSimulator.updatePosition(eventSt);
						
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
		d.start();
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
		if(x>16||y>16){
			return false;
		}
		return true;
	}
}
