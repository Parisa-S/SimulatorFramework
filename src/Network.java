import java.util.List;


public class Network {
	private List<Node> nodes;
	private List<Node> nodeprime;
	private List<Node> allnodes;
	public Network(List<Node> nodes2,List<Node> nodeprime,List<Node> allnodes){
		this.nodes = nodes2;
		this.nodeprime = nodeprime;
		this.allnodes = allnodes;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Node> getNodeprime() {
		return nodeprime;
	}

	public void setNodeprime(List<Node> nodeprime) {
		this.nodeprime = nodeprime;
	}

	public List<Node> getAllnodes() {
		return allnodes;
	}

	public void setAllnodes(List<Node> allnodes) {
		this.allnodes = allnodes;
	}
	
	
}
