import java.util.ArrayList;
import java.util.List;


public class CrossCorrelation {
	private int[] cross;
	private double[] crossP;
	private double[] crossScale;
	private double[] crossCurrence;
	private Node source;
	private Node sink;
	private List<String> highIndex;
	public CrossCorrelation(Node a, Node b){
		this.source = a;
		this.sink = b;
		highIndex = new ArrayList<String>();
	}
	public int[] getCross() {
		return cross;
	}
	public void setCross(int[] cross) {
		this.cross = cross;
	}
	public double[] getCrossP() {
		return crossP;
	}
	public void setCrossP(double[] crossP) {
		this.crossP = crossP;
	}
	public double[] getCrossScale() {
		return crossScale;
	}
	public void setCrossScale(double[] crossScale) {
		this.crossScale = crossScale;
		System.out.println("already set");
	}
	public double[] getCrossCurrence() {
		return crossCurrence;
	}
	public void setCrossCurrence(double[] crossCurrence) {
		this.crossCurrence = crossCurrence;
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
	public List<String> getHighIndex() {
		return highIndex;
	}
	public void setHighIndex(List<String> highIndex) {
		this.highIndex = highIndex;
	}
	public void addHighIndex(String str){
		highIndex.add(str);
		//System.out.println(source.getName()+" "+sink.getName()+" "+highIndex.get(highIndex.size()-1));
	}
	
	
}
