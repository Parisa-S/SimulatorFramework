

public class Agent implements Comparable<Node>{

	private Node source;
	private Node sink;
	private double x;
	private double y;
	private double velocity;
	private double simPosX =0;
	private double simPosY =0;
	public double minDistance = Double.POSITIVE_INFINITY;
	
	public Agent( Node source, Node sink){
		this.x = source.getX();
		this.y = source.getY();
		this.source = source;
		this.sink =sink;
		this.velocity = 1.50;
	}

	public double getPositionX() {
		return x;
	}

	public void setPositionX(double x) {
		this.x = x;
	}

	public double getPositionY() {
		return y;
	}

	public void setPositionY(double y) {
		this.y = y;
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
		return "Agent at x : "+x+" y : "+y;
	}
	public int compareTo(Node other)
    {
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
