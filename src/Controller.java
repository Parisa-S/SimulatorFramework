
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class Controller {
	private  List<Node> nodes = new ArrayList<Node>();
	private  Network network;
	private List<Node> path = new ArrayList<Node>();
	private Agent agent;
	private double intervalTime = 0.01;
	public double time = 0;
	
	public Controller(List<Node> nodes2){
		this.nodes = nodes2;
	}
	
	public Node randomSourceNode(){
		return nodes.get((int)(Math.floor(Math.random()*nodes.size())));
	}
	
	public Node randomDestNode(Node source){
		Node dest = nodes.get((int)(Math.floor(Math.random()*nodes.size())));
		while(dest.equals(source)){
			dest = nodes.get((int)(Math.floor(Math.random()*nodes.size())));
		}
		return dest;
	}

	public void computePaths(Node source,List<Node> nodes){
		for(Node node:nodes){
			node.previos = null;
			node.minDistance = Double.POSITIVE_INFINITY;
		}
		source.minDistance = 0;
		PriorityQueue<Node> nodeQueue = new PriorityQueue<>();
		nodeQueue.add(source);
		while(!nodeQueue.isEmpty()){
			Node u = nodeQueue.poll();
			for(Node n : u.getNeighbor()){
				double weight = calculateDistance(u,n);
				double distanceThroughU = u.minDistance+weight;
				if(distanceThroughU < n.minDistance){
					n.minDistance = distanceThroughU;
					n.previos = u;
					nodeQueue.add(n);
					
				}
			}
		}
	}
	
	public List<Node> getShortestPathTo(Node dest){
		path.clear();
		for(Node node=dest;node!=null;node=node.previos){
			path.add(node);
		}
		Collections.reverse(path);
		return path;
	}
	
	public double calculateDistance(Node source,Node dest){
		return Math.sqrt(Math.pow(source.getX()-dest.getX(), 2)+Math.pow(source.getY()-dest.getY(), 2));
	}
	
	public int samplingInterval(double time){
		return (int) (time/intervalTime);
	}
	
	public List<String> calculateTrajectory(List<Node> path,Agent agent){
		List<String> result = new ArrayList<String>();
		
		for(int i=0;i<=path.size()-2;i++){
			double distance = calculateDistance(path.get(i),path.get(i+1));
			System.out.println("distance from i to j : "+distance);
			double agentPosX = agent.getPositionX();
			double agentPosY = agent.getPositionY();
			System.out.println("agent Pos X : "+agentPosX);
			System.out.println("agent Pos Y  : "+agentPosY);
			
			while(!((Math.abs(agentPosX-path.get(i+1).getX())<=0.01) && (Math.abs(agentPosY-path.get(i+1).getY())<=0.01))){
				double calX = agentPosX+(intervalTime*agent.getVelocity()*(path.get(i+1).getX()-path.get(i).getX())/distance);
				double calY = agentPosY+(intervalTime*agent.getVelocity()*(path.get(i+1).getY()-path.get(i).getY())/distance);
				//System.out.println("cal X : "+calX);
				//System.out.println("Cal Y  : "+calY);
				agent.setPositionX(Math.round(calX*10000)/10000.0);
				agent.setPositionY(Math.round(calY*10000)/10000.0);
				
				time = Math.round((time+intervalTime)*1000)/1000.0;
			
				
				agentPosX = agent.getPositionX();
				agentPosY = agent.getPositionY();
				
				result.add(time+" "+agentPosX+" "+agentPosY);
				//System.out.println(result.get(result.size()-1));
			}
		}
		return result;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
	
//	public List<String> CalculateTrajectory(List<Node> path){
//	List<String> result = new ArrayList<String>();
//	for(int i =0; i<path.size()-2;i++){
//		double distance = double.valueOf(agent.getVelocity().doubleValue()*intervalTime.doubleValue());
//		while(!(path.get(i).getX()==path.get(i+1).getX()&&path.get(i).getY()==path.get(i+1).getY())){
//			time = double.valueOf(time.doubleValue() + intervalTime.doubleValue());
//			if(path.get(i).getX()-path.get(i+1).getX()>0&&path.get(i).getY()==path.get(i+1).getY()){
//				path.get(i).setX(path.get(i).getX()-distance);
//			}
//			else if(path.get(i).getX()-path.get(i+1).getX()<0&&path.get(i).getY()==path.get(i+1).getY()){
//				path.get(i).setX(path.get(i).getX()+distance);
//			}
//			else if(path.get(i).getY()-path.get(i+1).getY()>0&&path.get(i).getX()==path.get(i+1).getX()){
//				path.get(i).setY(path.get(i).getY()-distance);
//			}
//			else {
//				path.get(i).setX(path.get(i).getX()+distance);
//			}
//			result.add(time+" "+path.get(i).getX()+" "+path.get(i).getY());
//		}
//	}
//	return result;
//}
	
//	public static double toDecimal(double num){
//		int decimal = 4;
//		double bd = new double(num);
//		
//		bd.setScale(decimal, double.ROUND_HALF_UP);
//		
//		return bd;
//	}
	
}
