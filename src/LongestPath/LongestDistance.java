package LongestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class LongestDistance {
	private static List<Node> nodes = new ArrayList<Node>();
	private static double maxDistance = 0;
	
	public static void main(String[] args){
		Node a = new Node("1",0,10);
		Node b = new Node("2",10,10);
		Node c = new Node("3",0,0);
		Node d = new Node("4",10,0);
		Node e = new Node("5",10,5);
		Node f = new Node("6",5,0);
		
		a.addNeighbor(b);
		b.addNeighbor(a);
		a.addNeighbor(c);
		c.addNeighbor(a);
		c.addNeighbor(e);
		e.addNeighbor(c);
		a.addNeighbor(d);
		d.addNeighbor(a);
		d.addNeighbor(f);
		f.addNeighbor(d);
		
		nodes.add(a);
		nodes.add(b);
		nodes.add(c);
		nodes.add(d);
		nodes.add(e);
		nodes.add(f);
		
		double maxVal = 0;
		//init(a,nodes);
		for(Node n: nodes){
			maxVal = initLongestDistance(n,nodes);
		}
		System.out.println(maxVal);
	}
	public static double initLongestDistance(Node source,List<Node> nodes){
		for(Node node:nodes){
			node.minDistance = 0;
			node.setVisited(false);
		}
		return computeLongestDistance(source);
	}
	public static double computeLongestDistance(Node source){
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
	public static void longestPath(Node source,List<Node> nodes){
		
	}
	public static double calculateDistance(Node source,Node dest){
		return Math.sqrt(Math.pow(source.getX()-dest.getX(), 2)+Math.pow(source.getY()-dest.getY(), 2));
	}
}
