import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Agent implements Comparable<Node>{

	private Node source;
	private Node sink;
	private double posX;
	private double posY;
	private double velocity;
	private double simPosX =0;
	private double simPosY =0;
	public double minDistance = Double.POSITIVE_INFINITY;
	private List<Node> path;
	private List<Boolean> entered;
	
	public Agent( Node source, Node sink){
		this.posX = source.getPosX();
		this.posY = source.getPosY();
		this.source = source;
		this.sink =sink;
		this.velocity = 1.50;
	}
	public Agent(List<Node> path){
		this.path = new ArrayList<Node>();;
		this.entered = new ArrayList<Boolean>();
		//System.out.println(path.size());
		for(int i=0; i<path.size(); i++){
			entered.add(false);
			this.path.add(path.get(i));
			//System.out.println(path.get(i) + " " + entered.size());
		}
		//System.out.println(entered.size());
		//System.out.println();
		
		this.source = path.get(0);
		this.sink =path.get(path.size()-1);
		this.posX = source.getPosX();
		this.posY = source.getPosY();
		this.velocity = 1.50;
		
	}
	public double getPositionX() {
		return posX;
	}

	public void setPositionX(double x) {
		this.posX = x;
	}

	public double getPositionY() {
		return posY;
	}

	public void setPositionY(double y) {
		this.posY = y;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	
	public double getSimPosX(){
		return simPosX;
	}
	
	public double getSimPosY() {
		return simPosY;
	}

	public void setSimPosY(double simPosY) {
		this.simPosY = simPosY;
	}

	public void setSimPosX(double simPosX) {
		this.simPosX = simPosX;
	}

	public String toString(){
		return "Agent at x : "+posX+" y : "+posY;
	}
	
	public int compareTo(Node other){
        return Double.compare(minDistance, other.minDistance);
    }

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getSink() {
		return sink;
	}

	public void setSink(Node sink) {
		this.sink = sink;
	}
	public List<Node> getPath() {
		return path;
	}
	public void setPath(List<Node> path) {
		this.path = path;
	}
	public List<Boolean> getEntered() {
		return entered;
	}
	public boolean getEnteredI(int index) {
		return entered.get(index);
	}
	public void setEntered(List<Boolean> entered) {
		this.entered = entered;
	}
	public void setEnteredI(int index,Boolean value){
		this.entered.set(index, value);
	}
}
