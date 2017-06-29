import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;


public class AgentSimulator extends Observable{
	private Controller controller;
	private Network network;
	private double velocity;
	private Map<Agent,List<String>> agents;
	private Map<Agent, Integer> agentsRunner;
	private Map<String,Double> rateOfArrivals;
	private double time =0; 
	
	public AgentSimulator(Network network,double velocity,Map<String,Double> rateOfArrivals){
		this.network = network;
		this.velocity = velocity;
		controller = new Controller(network.getNodes());
		agents = new HashMap<Agent,List<String>>();
		agentsRunner = new HashMap<Agent,Integer>();
		this.rateOfArrivals = rateOfArrivals;
	}
	
	public void updatePosition(){
		this.time += 0.01;
		List<Agent> agentToRemove = new ArrayList<Agent>();
		
		for(Map.Entry<Agent, List<String>> entry: agents.entrySet()){			
			Agent agent = entry.getKey();
			List<String> listPosition = entry.getValue();
			int runner = agentsRunner.get(agent);
			System.out.println("Runner is " + runner);
			
			String currentPosition = listPosition.get(runner);
			String[] splited = currentPosition.split(" ");
			this.moveAgent(agent, Double.parseDouble(splited[1]), Double.parseDouble(splited[2]));
			
			if(runner+1>=listPosition.size()){
				agentToRemove.add(agent);
			}else{
				agentsRunner.replace(agent, runner+1);
			}
		}
		
		if(agentToRemove.size() > 0){
			for(Agent agent : agentToRemove){
				agents.remove(agent);
				agentsRunner.remove(agent);
				this.generateAgent();
			}
		}
		
		this.setChanged();
		this.notifyObservers();
	}
	
	public void moveAgent(Agent agent, double x, double y){		
		agent.setPositionX(x);
		agent.setPositionY(y);
		System.out.println("agent X : "+ agent.getPositionX());
		System.out.println("agent Y : "+ agent.getPositionY());
	}
	public void randomNode(){
		Node sourceNode = controller.randomSourceNode();
		System.out.println(sourceNode.getName());
		Node destNode = controller.randomDestNode(sourceNode);
		System.out.println(destNode.getName());
		
		List<Node> path = new ArrayList<Node>();
		controller.computePaths(sourceNode,network.getNodes());
		path = controller.getShortestPathTo(destNode);
		System.out.println("Path : "+ path);
	}
	public void generateAgent(Node sourceNode,Node destNode,List<Node> path){
		Agent agent = new Agent(sourceNode,destNode);
		agent.setVelocity(velocity);
		System.out.println(agent.getPositionX()+" "+agent.getPositionY());
		List<String> finalresult = controller.calculateTrajectory(time,path,agent);
//		if(checkDuplicateAgent(time,agent)){
//			this.generateAgent(sourceNode,destNode,path);
//		}
		agents.put(agent, finalresult);
		agentsRunner.put(agent, 0);
		this.writeLogFile(agent, finalresult);
	}
		
	public boolean checkDuplicateAgent(double time, Agent agent){
		boolean duplicateAgent = false;
		for(Map.Entry<Agent,List<String>> entry: agents.entrySet()){
			Node oldAgentSource = entry.getKey().getSource();
			Node oldAgentSink = entry.getKey().getSink();
			
			String[] str = entry.getValue().get(0).split(" ");
			if(oldAgentSource.equals(agent.getSource())&&oldAgentSink.equals(agent.getSink())&&Double.parseDouble(str[0])==time){
				duplicateAgent = true;
			}
		}
		return duplicateAgent;
	}
	public void writeLogFile(Agent agent, List<String> finalresult){
		try {
			String[] sentence = finalresult.get(0).split(" ");
			FileWriter writer = new FileWriter(sentence[0]+"_"+agent.getSource().getX()+"_"+agent.getSource().getY()+"_"+agent.getSink().getX()+"_"+agent.getSink().getY()+".txt");
			for(String result: finalresult){
				writer.write(result);
				writer.write(System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public Map<Agent, List<String>> getAgents() {
		return agents;
	}

	public void setAgents(Map<Agent, List<String>> agents) {
		this.agents = agents;
	}

	public Map<Agent, Integer> getAgentsRunner() {
		return agentsRunner;
	}

	public void setAgentsRunner(Map<Agent, Integer> agentsRunner) {
		this.agentsRunner = agentsRunner;
	}

}