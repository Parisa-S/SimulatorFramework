

public class Agent implements Comparable<Node>{

	private Node source;
	private Node sink;
	private double posX;
	private double posY;
	private double velocity;
	private double simPosX =0;
	private double simPosY =0;
	public double minDistance = Double.POSITIVE_INFINITY;
	
	public Agent( Node source, Node sink){
		this.posX = source.getPosX();
		this.posY = source.getPosY();
		this.source = source;
		this.sink =sink;
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
	
}
