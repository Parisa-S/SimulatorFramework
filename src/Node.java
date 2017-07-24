
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
	private double rateOfArrival=0.00;
	private List<Event> eventLog;
	private boolean visited;
	
	public Node(String name,double x,double y){
		this.name = name;
		this.x = x;
		this.y = y;
		visited = false;
		neighbors = new ArrayList<>();
		eventLog = new ArrayList<Event>();
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public double getPosX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getPosY() {
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
	
	public int compareTo(Node other){
        return Double.compare(minDistance, other.minDistance);
    }
	
	public double getRateOfArrival() {
		return rateOfArrival;
	}
	
	public void setRateOfArrival(double rateOfArrival) {
		this.rateOfArrival = rateOfArrival;
	}

	public List<Event> getEventLog() {
		return eventLog;
	}
	
	public void setEventLog(List<Event> eventLog) {
		this.eventLog = eventLog;
	}
	
	public void addEvent(Event str){
		eventLog.add(str);
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
}
