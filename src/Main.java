import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
	
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
//		Scanner sc = new Scanner(System.in);
		nodes = new ArrayList<Node>();
//		
//		System.out.println("Input number of node ");
//		int numNode = Integer.parseInt(sc.nextLine());
//		for(int i =1;i<= numNode;i++){
//			System.out.print("Node : "+ i+ "\n");
//			System.out.print("Please Input Name : ");
//			int name = Integer.parseInt(sc.nextLine());
//			System.out.print("Please Input X : ");
//			double x1 = Double.parseDouble(sc.nextLine());
//			System.out.print("Please Input Y : ");
//			double y1 = Double.parseDouble(sc.nextLine());	
//			Node n = new Node(name+"",x1,y1);
//			nodes.add(n);
//		}
//		System.out.println("Input number of edge ");
//		int numEdge = Integer.parseInt(sc.nextLine());
//		for(int i =1;i<= numEdge;i++){
//			System.out.print("Edge : "+ i+ "\n");
//			
//			System.out.print("Please Input source : ");
//			int source = Integer.parseInt(sc.nextLine());
//			System.out.print("Please Input sink : ");
//			int sink = Integer.parseInt(sc.nextLine());
//			nodes.get(source-1).addNeighbor(nodes.get(sink-1));
//			nodes.get(sink-1).addNeighbor(nodes.get(source-1));
//		}
//		Network network = new Network(nodes);
		
		//read file from json and create node 
//		JSONParser parser = new JSONParser();
//		Object obj = parser.parse(new FileReader("config.json"));
//		JSONObject jsonObject = (JSONObject) obj;
//		JSONArray jsonarraynode = (JSONArray)jsonObject.get("node");
//		Iterator<JSONObject> iteratornode = ((List<JSONObject>) jsonarraynode).iterator();
//        while (iteratornode.hasNext()) {
//        	JSONObject i = iteratornode.next();
//        	String name = (String)i.get("name");
//        	double x = Double.parseDouble((String) i.get("x"));
//        	double y = Double.parseDouble((String)i.get("y"));
//        	Node node = new Node(name,x,y);
//            nodes.add(node);
//        }
       
		//read file 
		
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		List<String> lines = new ArrayList<String>();
		String currentLine = null;
		
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
							if((n.getName().equals(node.getName())||(n.getX()==node.getX()&&n.getY()==node.getY()))){
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
		System.out.println(numLink);
		count++;
		for(int i=0;i<=numLink-1;i++){
			String s = lines.get(count);
			String[] arr = s.split(" ");
			
			boolean existstart = false;
			boolean existend = false;
			try{
					//nodes.get(nodes.indexOf(arr[0])).addNeighbor(nodes.get(nodes.indexOf(arr[1])));
					//nodes.get(nodes.indexOf(arr[1])).addNeighbor(nodes.get(nodes.indexOf(arr[0])));
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
		double agentVelo = Double.parseDouble(lines.get(count));
		
		GUI gui =  new GUI(nodes,agentVelo);
		gui.setVisible(true);
		
		try{
			for(Map.Entry<Agent, List<String>> entry: gui.getAgents().entrySet()){
				for(String str : entry.getValue()){
					System.out.println(str);
					gui.moveAgent(str);
					gui.getNetworkPanel().repaint();
					//gui.captureScreen(gui.getNetworkPanel(),new File(gui.getCount()+".jpeg"));
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e ) {
						e.printStackTrace();
					}
				}
			}
			
		}finally{
			
            if (gui.getOut() != null) {
                gui.getOut().close();
            }
		}	
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
