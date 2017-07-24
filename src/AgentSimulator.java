import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;




public class AgentSimulator extends Observable{
	private Controller controller;
	private Network network;
	private double mean;
	private double stdVa;
	private Map<Agent,List<String>> agentList;
	private Map<Agent, Integer> agentsRunner;
	private List<Double> velocityList;
	private double[][] trafficMatrix;
	private int[] crossTemp;
	private double[] crossPrevious;
	private double[] crossScale;
	private double[] crossCurrence;
	private double time =0;
	private int MaxNumberOfAgents = 10;
	public int numberOfAgents = 0;
	private Random random;
	private int veloRunner=0;
	private double longestDistance=0;
	private int sizeOfArray=0;
	private int samplingCounter = 0;
	private CrossCorrelation crossAB,crossBC,crossDE;
	private List<CrossCorrelation> crossList;
	private double meanCross = 0;

	public AgentSimulator(Network network,double mean,double stdVa,double[][] trafficMatrix){
		this.network = network;
		this.mean  = mean;
		this.stdVa = stdVa;
		this.trafficMatrix = trafficMatrix;
		controller = new Controller(network.getNodes());
		agentList = new HashMap<Agent,List<String>>();
		agentsRunner = new HashMap<Agent,Integer>();

		random = new Random();
		velocityList = randomGaussian(mean,stdVa);
		crossList = new ArrayList<CrossCorrelation>();
		longestDistance = calculateLongestDistance(network);
		//sizeOfArray = (int) ((2*maxVal)/(mean*controller.getIntervalTime()));
		sizeOfArray = 2600;
		initCrossCorrelation();
		for(int i=0 ;i<sizeOfArray;i++){
			for(Node n:network.getNodes()){
				n.addEvent(new Event("0.00",0));
			}
		}

	}
	public double calculateLongestDistance(Network network){
		double value =0;
		for(Node n: network.getNodes()){
			value = controller.initLongestDistance(n,network.getNodes());
		}
		return value;
	}
	public void initCrossCorrelation(){
		crossAB = new CrossCorrelation(network.getNodes().get(0),network.getNodes().get(1));
		crossBC = new CrossCorrelation(network.getNodes().get(1),network.getNodes().get(2));
		crossDE = new CrossCorrelation(network.getNodes().get(5),network.getNodes().get(3));
		
		crossCurrence = new double[2*sizeOfArray-1];
		crossPrevious = new double[2*sizeOfArray-1];

		crossAB.setCrossCurrence(crossCurrence);
		crossAB.setCrossP(crossPrevious);
		crossBC.setCrossCurrence(crossCurrence);
		crossBC.setCrossP(crossPrevious);
		crossDE.setCrossCurrence(crossCurrence);
		crossDE.setCrossP(crossPrevious);
		
		crossList.add(crossAB);
		crossList.add(crossBC);
		crossList.add(crossDE);
	}
	
	public void updatePosition(){
		this.time += 0.01;

		List<Agent> agentToRemove = new ArrayList<Agent>();
		Integer[] nodeEvent = new Integer[network.getNodes().size()];
		String[] splited = new String[3];

		for(int i=0;i<=network.getNodes().size()-1;i++){
			nodeEvent[i] = 0;
		}

		for(Map.Entry<Agent, List<String>> entry: agentList.entrySet()){			
			Agent agent = entry.getKey();
			List<String> listPosition = entry.getValue();
			int runner = agentsRunner.get(agent);

			String currentPosition = listPosition.get(runner);
			splited = currentPosition.split(" ");
			this.moveAgent(agent, Double.parseDouble(splited[1]), Double.parseDouble(splited[2]));

			for(Node node: network.getNodes()){
				if(Math.abs(agent.getPositionX()-node.getPosX())<=1.00 && Math.abs(agent.getPositionY()-node.getPosY())<=1.00){
					nodeEvent[Integer.parseInt(node.getName())-1] += 1; 
				}
			}
			if(runner+1>=listPosition.size()){
				agentToRemove.add(agent);
			}else{
				agentsRunner.replace(agent, runner+1);
			}
		}
		if(splited[0]!= null){
			for(Node node:network.getNodes()){
				if(node.getEventLog().size()<sizeOfArray){
					node.addEvent(new Event(splited[0],nodeEvent[Integer.parseInt(node.getName())-1]));
				}
				else{
					node.getEventLog().remove(node.getEventLog().get(0));
					node.addEvent(new Event(splited[0],nodeEvent[Integer.parseInt(node.getName())-1]));
				}
			}
		}
		checkNumberofAgent();
		removeAgent(agentToRemove);

		computeCrossCorrelation(crossAB);
		computeCrossCorrelation(crossBC);
		computeCrossCorrelation(crossDE);

		this.setChanged();
		this.notifyObservers();
	}
	public void checkNumberofAgent(){
		if(numberOfAgents <= MaxNumberOfAgents ){
			Map<Node,Node> sourceSink = randomNode();
			for(Map.Entry<Node, Node> entry:sourceSink.entrySet()){
				List<Node> path = computePath(entry.getKey(), entry.getValue());
				generateAgent(entry.getKey(),entry.getValue(),path);
				numberOfAgents++;
			}
		}
	}
	
	public void removeAgent(List<Agent> agentToRemove){
		if(agentToRemove.size() > 0){
			for(Agent agent : agentToRemove){
				agentList.remove(agent);
				agentsRunner.remove(agent);
				numberOfAgents--;
			}
		}
	}
	
	public void computeCrossCorrelation(CrossCorrelation cc){
		double max=0;
		String index = "0";
		crossCurrence = new double[2*sizeOfArray-1];
		crossTemp = new int[2*sizeOfArray-1];
		crossPrevious = cc.getCrossP();

		int[] sourceLog = new int[sizeOfArray];
		int[] sinkLog = new int[sizeOfArray];

		Arrays.setAll(sourceLog,l -> cc.getSource().getEventLog().get(l).getValue());
		Arrays.setAll(sinkLog,l -> cc.getSink().getEventLog().get(l).getValue());
		int sumCross =0;
		
		for(int i=0;i<2*sizeOfArray-1;i++){
			int numtest = sizeOfArray-1;
			int front = Math.max(numtest-i, 0);
			int back = Math.max(i-numtest, 0);
			
			int[] newSourceLog = addZero(front,back,sourceLog);
			int[] newSinkLog = addZero(back,front,sinkLog);
			
			int temp[] = new int[newSourceLog.length];
			
			Arrays.setAll(temp, k -> newSourceLog[k]*newSinkLog[k]);
			crossTemp[i] = IntStream.of(temp).sum();
			sumCross += crossTemp[i];	
		}
		meanCross = sumCross*1.0/crossTemp.length;
		crossScale = new double[crossTemp.length];
		
		Arrays.setAll(crossScale, j-> crossTemp[j]-meanCross);
		List<Integer> arr = Arrays.stream(crossTemp).boxed().collect(Collectors.toList());
		
		int tempT = IntStream.range(0, arr.size()).reduce((m,n)->arr.get(m)<arr.get(n)? n: m ).getAsInt();
		Arrays.setAll(crossCurrence, k -> ((crossPrevious[k]*samplingCounter)+crossScale[k])*1.0/(samplingCounter+1));
		crossPrevious = Arrays.copyOf(crossCurrence, crossCurrence.length);

		cc.setCrossCurrence(crossCurrence);
		cc.setCrossP(crossCurrence);
//		if(cc.getHighIndex().size()<6000){
//			cc.addHighIndex(index);
//		}
//		else{
//			cc.getHighIndex().remove(cc.getHighIndex().get(0));
//			cc.addHighIndex(index);
//		}
		if(this.samplingCounter%6000==0){
			writeLogFileHighIndex(cc);
		}
	}
	
	public int[] addZero(int front,int back,int[] eventLog){
		int[] result = new int[front+back+eventLog.length];
		System.arraycopy(eventLog, 0, result, front, eventLog.length);
		return result;
	}

	public void moveAgent(Agent agent, double x, double y){		
		agent.setPositionX(x);
		agent.setPositionY(y);
	}
	
	public Map<Node,Node> randomNode(){
		Node sinkNode;
		Map<Node,Node> sourceSink = new HashMap<Node,Node>();
		List<Node> sourceNode = new ArrayList<Node>();
		sourceNode = controller.randomSourceNode();

		for(Node node:sourceNode){
			sinkNode = controller.randomDestNode(node,trafficMatrix);
			sourceSink.put(node, sinkNode);
		}
		return sourceSink;
	}
	public List<Node> computePath(Node sourceNode,Node sinkNode){
		List<Node> path = new ArrayList<Node>();
		controller.computePaths(sourceNode,network.getNodes());
		path = controller.getShortestPathTo(sinkNode);
		return path;
	}
	public void generateAgent(Node sourceNode,Node sinkNode,List<Node> path){
		Agent agent = new Agent(sourceNode,sinkNode);
		if(veloRunner >=1000){
			veloRunner = 0;
		}
		else{
			double velocity = velocityList.get(veloRunner);
			agent.setVelocity(velocity);
			veloRunner++;
		}
		List<String> finalresult = controller.calculateTrajectory(time,path,agent);
		agentList.put(agent, finalresult);
		agentsRunner.put(agent, 0);
		this.writeLogFile(agent, finalresult);
	}

	public boolean checkDuplicateAgent(double time, Agent agent){
		boolean duplicateAgent = false;
		for(Map.Entry<Agent,List<String>> entry: agentList.entrySet()){
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
			FileWriter writer = new FileWriter(sentence[0]+"_"+agent.getSource().getPosX()+"_"+agent.getSource().getPosY()+"_"+agent.getSink().getPosX()+"_"+agent.getSink().getPosY()+".txt");
			for(String result: finalresult){
				writer.write(result);
				writer.write(System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeLogFileHighIndex(CrossCorrelation cc){
		try {
			List<String> sentence = cc.getHighIndex();
			FileWriter writers = new FileWriter("Test"+cc.getSource().getName()+"_"+cc.getSink().getName()+".txt");
			for(String result: sentence){
				writers.write(result);
				writers.write(System.lineSeparator());
			}
			writers.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Double> randomGaussian(double mean,double stdVa){
		List<Double> randomNumber = new ArrayList<Double>();
		for(int i=0;i<1000;i++){
			double val =(random.nextGaussian()*stdVa)+mean;
			randomNumber.add(val);
		}
		return randomNumber;
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

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getStdVa() {
		return stdVa;
	}

	public void setStdVa(double stdVa) {
		this.stdVa = stdVa;
	}

	public Map<Agent, List<String>> getAgents() {
		return agentList;
	}

	public void setAgents(Map<Agent, List<String>> agents) {
		this.agentList = agents;
	}

	public Map<Agent, Integer> getAgentsRunner() {
		return agentsRunner;
	}

	public void setAgentsRunner(Map<Agent, Integer> agentsRunner) {
		this.agentsRunner = agentsRunner;
	}

	public int getMaxNumberOfAgents() {
		return MaxNumberOfAgents;
	}

	public void setMaxNumberOfAgents(int maxNumberOfAgents) {
		MaxNumberOfAgents = maxNumberOfAgents;
	}

	public int getNumberOfAgents() {
		return numberOfAgents;
	}

	public void setNumberOfAgents(int numberOfAgents) {
		this.numberOfAgents = numberOfAgents;
	}

	public int getCounter() {
		return samplingCounter;
	}

	public void setCounter(int counter) {
		this.samplingCounter = counter;
	}

	public double[] getCrossCurrence() {
		return crossCurrence;
	}

	public void setCrossCurrence(double[] crossCurrence) {
		this.crossCurrence = crossCurrence;
	}

	public CrossCorrelation getcrossAB() {
		return crossAB;
	}

	public void setcrossAB(CrossCorrelation ab) {
		this.crossAB = ab;
	}

	public CrossCorrelation getCrossBC() {
		return crossBC;
	}

	public void setCrossBC(CrossCorrelation crossBC) {
		this.crossBC = crossBC;
	}

	public CrossCorrelation getCrossDE() {
		return crossDE;
	}

	public void setCrossDE(CrossCorrelation crossDE) {
		this.crossDE = crossDE;
	}

	public List<CrossCorrelation> getCrossList() {
		return crossList;
	}

	public void setCrossList(List<CrossCorrelation> crossList) {
		this.crossList = crossList;
	}

}