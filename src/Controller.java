
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
	private List<Node> path = new ArrayList<Node>();
	private double intervalTime = 0.01;
	private double time =0;
	
	public Controller(List<Node> nodes2){
		this.nodes = nodes2;
	}
	
	public List<Node> randomSourceNode(){
		List<Node> createNode = new ArrayList<Node>();
		for(Node node: nodes){
			double n = Math.random();
			//System.out.println(node+ " "+ n);
			if(n<node.getRateOfArrival()){
				createNode.add(node);
			}
		}
//		if(createNode.isEmpty()){
//			//System.out.println("empty " + createNode);
//			createNode = randomSourceNode();
//		}

		return createNode;
	}
	
	public Node randomDestNode(Node source,double[][] changedMatrix){
		//System.out.println("sourcename : "+source.getName());
		double m = Math.random();
		//System.out.println("m : "+ m);
		int num = 0;
		for(int i=1;i<=changedMatrix.length-1;i++){
			if(changedMatrix[Integer.parseInt(source.getName())][i-1]<=m && m<changedMatrix[Integer.parseInt(source.getName())][i]){
				num = i-1;
				//System.out.println("num"+num);
				break;
			}
			else if(changedMatrix[Integer.parseInt(source.getName())][i-1]<=m && m==changedMatrix[Integer.parseInt(source.getName())][i]){
				num = i-1;
				//System.out.println("num"+num);
				break;
			}
			//System.out.println("num"+num);
		}
		//System.out.println(nodes.get(num));
		return nodes.get(num);
		
//		Node dest = nodes.get((int)(Math.floor(Math.random()*nodes.size())));
//		while(dest.equals(source)){
//			dest = nodes.get((int)(Math.floor(Math.random()*nodes.size())));
//		}
//		return dest;
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
	public double initLongestDistance(Node source,List<Node> nodes){
		for(Node node:nodes){
			node.minDistance = 0;
			node.setVisited(false);
		}
		return computeLongestDistance(source);
	}
	public double computeLongestDistance(Node source){
			source.minDistance = 0;
			double max = 0;
			double dist;
			source.setVisited(true);

			for(Node n : source.getNeighbor()){
				if(!n.isVisited()){
					dist = calculateDistance(source,n)+computeLongestDistance(n);
					if(dist> max){
						max = dist;
						System.out.println(source.getName()+" "+n.getName()+" "+max);
					}
				}
			}
			source.setVisited(false);
			
		return max;
		
	}
	public List<String> calculateTrajectory(double time,List<Node> path,Agent agent){
		List<String> result = new ArrayList<String>();
		
		for(int i=0;i<=path.size()-2;i++){
			double distance = calculateDistance(path.get(i),path.get(i+1));
			//System.out.println("distance from i to j : "+distance);
			double agentPosX = agent.getPositionX();
			double agentPosY = agent.getPositionY();
			//System.out.println("agent Pos X : "+agentPosX);
			//System.out.println("agent Pos Y  : "+agentPosY);
			//while(agentPosX<=path.get(i+1).getX()&&agentPosY<=path.get(i+1).getY()){
	
			double needToWalkX = Math.abs(path.get(i).getX() - path.get(i+1).getX());
			double needToWalkY = Math.abs(path.get(i).getY() - path.get(i+1).getY());
			double walkDistanceX = 0;
			double walkDistanceY = 0;

			//while(!((Math.abs(agentPosX-path.get(i+1).getX())<=0.07) && (Math.abs(agentPosY-path.get(i+1).getY())<=0.07))) {
			while(walkDistanceX<needToWalkX || walkDistanceY<needToWalkY){
				//System.out.println("x value "+Math.abs(agentPosX-path.get(i+1).getX()));
				//System.out.println("y value "+Math.abs(agentPosY-path.get(i+1).getY()));
				double calX = agentPosX+(intervalTime*agent.getVelocity()*(path.get(i+1).getX()-path.get(i).getX())/distance);
				double calY = agentPosY+(intervalTime*agent.getVelocity()*(path.get(i+1).getY()-path.get(i).getY())/distance);
				walkDistanceX += Math.abs(intervalTime*agent.getVelocity()*(path.get(i+1).getX()-path.get(i).getX())/distance);
				walkDistanceY += Math.abs(intervalTime*agent.getVelocity()*(path.get(i+1).getY()-path.get(i).getY())/distance);
		
				//System.out.println("cal X : "+calX);
				//System.out.println("Cal Y : "+calY);
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

	public double getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(double intervalTime) {
		this.intervalTime = intervalTime;
	}
	
}
