
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Node implements Comparable<Node>{
	private String name;
	private double x;
	private double y;
	private List<Node> neighbors;
	public Node previos;
	public double minDistance = Double.POSITIVE_INFINITY;
	
	public Node(String name,double x,double y){
		this.name = name;
		this.x = x;
		this.y = y;
		neighbors = new ArrayList<>();
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public List<Node> getNeighbor() {
		return neighbors;
	}
	public void setNeighbor(List<Node> neighbors) {
		this.neighbors = neighbors;
	}
	public void addNeighbor(Node node){
		neighbors.add(node);
	}
	public String toString(){
		return "name: " +name+" |Position : "+ x+", "+y;
	}
	public int compareTo(Node other)
    {
        return Double.compare(minDistance, other.minDistance);
    }
	
}
