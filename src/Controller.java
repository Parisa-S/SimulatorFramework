
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;




public class Controller {
	private  List<Node> nodeobsList = new ArrayList<Node>();
	private List<Node> nodeprime = new ArrayList<Node>();
	private List<Node> path = new ArrayList<Node>();
	private double intervalTime = 0.01;
	private double time =0;
	
	public Controller(Network network){
		this.nodeobsList = network.getNodes();
		this.nodeprime = network.getNodeprime();
	}
	
	public List<Node> randomSourceNode(){
		List<Node> createNode = new ArrayList<Node>();
		for(Node node: nodeprime){
			double randomNumber = Math.random();
			if(randomNumber<node.getRateOfArrival()){
				createNode.add(node);
			}
		}
		return createNode;
	}
	
	public Node randomDestNode(Node source,double[][] changedMatrix){
		double randomNum = Math.random();
		int num = 0;
		for(int i=1;i<=changedMatrix.length-1;i++){
			if(changedMatrix[Integer.parseInt(source.getName())][i-1]<=randomNum && randomNum<changedMatrix[Integer.parseInt(source.getName())][i]){
				num = i-1;
				break;
			}
			else if(changedMatrix[Integer.parseInt(source.getName())][i-1]<=randomNum && randomNum==changedMatrix[Integer.parseInt(source.getName())][i]){
				num = i-1;
				break;
			}
		}
		return nodeprime.get(num);
	}

	public void computePaths(Node source,List<Node> nodeList){
		for(Node node:nodeList){
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
	
	public List<Node> getShortestPathTo(Node sink){
		path.clear();
		for(Node node=sink;node!=null;node=node.previos){
			path.add(node);
		}
		Collections.reverse(path);
		return path;
	}
	
	public double calculateDistance(Node source,Node sink){
		return Math.sqrt(Math.pow(source.getPosX()-sink.getPosX(), 2)+Math.pow(source.getPosY()-sink.getPosY(), 2));
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

			double agentPosX = agent.getPositionX();
			double agentPosY = agent.getPositionY();
	
			double needToWalkX = Math.abs(path.get(i).getPosX() - path.get(i+1).getPosX());
			double needToWalkY = Math.abs(path.get(i).getPosY() - path.get(i+1).getPosY());
			double walkDistanceX = 0;
			double walkDistanceY = 0;

			//while(!((Math.abs(agentPosX-path.get(i+1).getX())<=0.07) && (Math.abs(agentPosY-path.get(i+1).getY())<=0.07))) {
			while(walkDistanceX<needToWalkX || walkDistanceY<needToWalkY){

				double calX = agentPosX+(intervalTime*agent.getVelocity()*(path.get(i+1).getPosX()-path.get(i).getPosX())/distance);
				double calY = agentPosY+(intervalTime*agent.getVelocity()*(path.get(i+1).getPosY()-path.get(i).getPosY())/distance);
				walkDistanceX += Math.abs(intervalTime*agent.getVelocity()*(path.get(i+1).getPosX()-path.get(i).getPosX())/distance);
				walkDistanceY += Math.abs(intervalTime*agent.getVelocity()*(path.get(i+1).getPosY()-path.get(i).getPosY())/distance);
		
				agent.setPositionX(Math.round(calX*10000)/10000.0);
				agent.setPositionY(Math.round(calY*10000)/10000.0);
				
				time = Math.round((time+intervalTime)*1000)/1000.0;
			
				agentPosX = agent.getPositionX();
				agentPosY = agent.getPositionY();
				
				result.add(time+" "+agentPosX+" "+agentPosY);
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
