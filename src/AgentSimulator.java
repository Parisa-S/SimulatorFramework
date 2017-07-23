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
	private Map<Agent,List<String>> agents;
	private Map<Agent, Integer> agentsRunner;
	private List<Double> velocitys;
	private double[][] trafficMatrix;
	private int[] cross;
	private double[] crossP;
	private double[] crossScale;
	private double[] crossCurrence;
	private double time =0;
	private int MaxNumberOfAgents = 10;
	public int numberOfAgents = 0;
	private Random random;
	private int veloRunner=0;
	private double maxVal=0;
	private int sizeOfArray=0;
	private int counter = 0;
	private CrossCorrelation crossAB,crossBC,crossDE;
	private List<CrossCorrelation> crossList;
	private double meanCross = 0;

	private double counter_time = 0;

	public AgentSimulator(Network network,double mean,double stdVa,double[][] trafficMatrix){
		this.network = network;
		this.mean  = mean;
		this.stdVa = stdVa;
		this.trafficMatrix = trafficMatrix;
		controller = new Controller(network.getNodes());
		agents = new HashMap<Agent,List<String>>();
		agentsRunner = new HashMap<Agent,Integer>();

		random = new Random();
		velocitys = randomGaussian(mean,stdVa);
		crossList = new ArrayList<CrossCorrelation>();
		maxVal = calculateLongestDistance(network);
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
		crossP = new double[2*sizeOfArray-1];

		crossAB.setCrossCurrence(crossCurrence);
		crossAB.setCrossP(crossP);
		crossBC.setCrossCurrence(crossCurrence);
		crossBC.setCrossP(crossP);
		crossDE.setCrossCurrence(crossCurrence);
		crossDE.setCrossP(crossP);
		
		crossList.add(crossAB);
		crossList.add(crossBC);
		crossList.add(crossDE);
	}
	public void updatePosition(){
		this.time += 0.01;

		List<Agent> agentToRemove = new ArrayList<Agent>();
		Integer[] checkevent = new Integer[network.getNodes().size()];
		String[] splited = new String[3];


		for(int i=0;i<=network.getNodes().size()-1;i++){
			checkevent[i] = 0;
		}

		for(Map.Entry<Agent, List<String>> entry: agents.entrySet()){			
			Agent agent = entry.getKey();
			List<String> listPosition = entry.getValue();
			int runner = agentsRunner.get(agent);

			String currentPosition = listPosition.get(runner);
			splited = currentPosition.split(" ");
			this.moveAgent(agent, Double.parseDouble(splited[1]), Double.parseDouble(splited[2]));

			for(Node node: network.getNodes()){
				if(Math.abs(agent.getPositionX()-node.getX())<=1.00 && Math.abs(agent.getPositionY()-node.getY())<=1.00){
					checkevent[Integer.parseInt(node.getName())-1] += 1; 
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
					node.addEvent(new Event(splited[0],checkevent[Integer.parseInt(node.getName())-1]));
				}
				else{
					node.getEventLog().remove(node.getEventLog().get(0));
					node.addEvent(new Event(splited[0],checkevent[Integer.parseInt(node.getName())-1]));
				}
			}
		}
		if(numberOfAgents <= MaxNumberOfAgents ){
			Map<Node,Node> sourceSink = randomNode();
			for(Map.Entry<Node, Node> entry:sourceSink.entrySet()){
				List<Node> path = computePath(entry.getKey(), entry.getValue());
				generateAgent(entry.getKey(),entry.getValue(),path);
				numberOfAgents++;
			}
		}

		if(agentToRemove.size() > 0){
			for(Agent agent : agentToRemove){
				agents.remove(agent);
				agentsRunner.remove(agent);
				numberOfAgents--;
			}
		}

		computeCrossCorrelation(crossAB);
		computeCrossCorrelation(crossBC);
		computeCrossCorrelation(crossDE);

		this.setChanged();
		this.notifyObservers();
	}
	public void computeCrossCorrelation(CrossCorrelation cc){
		double max=0;
		String index = "0";
		crossCurrence = new double[2*sizeOfArray-1];
		cross = new int[2*sizeOfArray-1];
		crossP = cc.getCrossP();

		int[] a = new int[sizeOfArray];
		int[] b = new int[sizeOfArray];

		Arrays.setAll(a,l -> cc.getSource().getEventLog().get(l).getValue());
		Arrays.setAll(b,l -> cc.getSink().getEventLog().get(l).getValue());
		int sumCross =0;
		
		for(int i=0;i<2*sizeOfArray-1;i++){
			int numtest = sizeOfArray-1;
			int front = Math.max(numtest-i, 0);
			int back = Math.max(i-numtest, 0);
			
			int[] f = addZero2(front,back,a);
			int[] g = addZero2(back,front,b);
			
			int temp[] = new int[f.length];
			
			Arrays.setAll(temp, k -> f[k]*g[k]);
			cross[i] = IntStream.of(temp).sum();
			sumCross += cross[i];	
		}
		meanCross = sumCross*1.0/cross.length;
		crossScale = new double[cross.length];
		
		Arrays.setAll(crossScale, j-> cross[j]-meanCross);
		List<Integer> arr = Arrays.stream(cross).boxed().collect(Collectors.toList());

		int tempT = IntStream.range(0, arr.size()).reduce((m,n)->arr.get(m)<arr.get(n)? n: m ).getAsInt();
		//System.out.print("cross P :");
		//for(int j=0;j<crossP.length;j++){
		//	System.out.print(crossP[j]+" ");
			//sumcrossP += crossP[j];
		//}
//		System.out.print(" "+sumcrossP);
		//System.out.println();
		//double[] tempP = new double[2*sizeOfArray-1];
		Arrays.setAll(crossCurrence, k -> ((crossP[k]*counter)+crossScale[k])*1.0/(counter+1));
		//System.out.print("tempP : ");
		//for(int j=0;j<tempP.length;j++){
		//	System.out.print(tempP[j]+" ");
		//}
		//System.out.println();
//		double[] tempC = new double[2*sizeOfArray-1];
//		Arrays.setAll(tempC, k -> tempP[k]+crossScale[k]);
		//System.out.print("tempC : ");
		//for(int j=0;j<tempC.length;j++){
		//	System.out.print(tempC[j]+" ");
		//}
		//System.out.println();
//		Arrays.setAll(crossCurrence, k -> (tempC[k]*1.0)/(counter+1));
		//System.out.print("crossCurrence : ");
		//for(int j=0;j<crossCurrence.length;j++){
		//	System.out.print(crossCurrence[j]+" ");
		//}
		//System.out.println();
		crossP = Arrays.copyOf(crossCurrence, crossCurrence.length);
		double sumcrossC = 0;
//		System.out.print("cross Currence "+cc.getSource().getName()+" "+cc.getSink().getName()+" ");
//		
//		for(int j=0;j<crossCurrence.length;j++){
//			sumcrossC += crossCurrence[j];
//			System.out.print(crossCurrence[j]+" ");
//		}
//		System.out.println();
//		System.out.print("sum Cross C "+sumcrossC);
//		System.out.println();
		cc.setCrossCurrence(crossCurrence);
		
		cc.setCrossP(crossCurrence);
//		if(cc.getHighIndex().size()<6000){
//			cc.addHighIndex(index);
//		}
//		else{
//			cc.getHighIndex().remove(cc.getHighIndex().get(0));
//			cc.addHighIndex(index);
//		}
		if(this.counter%6000==0){
			writeLogFileHighIndex(cc);
		}
	}
	
	
	public int[] addZero2(int front,int back,int[] c){
		//int[] result = new int[front+back+c.length];
		int[] result2 = new int[front+back+c.length];
		
//		for(int i=0; i<c.length; i++){
//			result[front+i] = c[i];
//		}
		System.arraycopy(c, 0, result2, front, c.length);
//		if(Arrays.equals(result, result2)){
//			System.out.println("same");
//		}
//		else{
//			System.out.println("not same");
//		}
		return result2;
	}

	public void moveAgent(Agent agent, double x, double y){		
		agent.setPositionX(x);
		agent.setPositionY(y);
	}
	public Map<Node,Node> randomNode(){
		//System.out.println("randonNode");
		Node destNode;
		Map<Node,Node> sourceSink = new HashMap<Node,Node>();
		List<Node> sourceNode = new ArrayList<Node>();
		sourceNode = controller.randomSourceNode();
		//System.out.println("source node: "+sourceNode);
		for(Node node:sourceNode){
			destNode = controller.randomDestNode(node,trafficMatrix);
			sourceSink.put(node, destNode);
			//System.out.println("sourceSink"+node.getName()+ " "+ destNode.getName());
		}
		return sourceSink;
	}
	public List<Node> computePath(Node sourceNode,Node sinkNode){
		List<Node> path = new ArrayList<Node>();
		controller.computePaths(sourceNode,network.getNodes());
		path = controller.getShortestPathTo(sinkNode);
		return path;
	}
	public void generateAgent(Node sourceNode,Node destNode,List<Node> path){
		Agent agent = new Agent(sourceNode,destNode);
		//System.out.println("already created agent");
		
		if(veloRunner >=1000){
			veloRunner = 0;
		}
		else{
			double velocity = velocitys.get(veloRunner);
			agent.setVelocity(velocity);
			veloRunner++;
		}
		
		//System.out.println(veloRunner);
		//System.out.println(agent.getPositionX()+" "+agent.getPositionY());
		List<String> finalresult = controller.calculateTrajectory(time,path,agent);
		//System.out.println("final result "+finalresult);
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
	public void writeLogFileTest(int[] test){
		try {
			//List<String> sentence = test;
			FileWriter writers = new FileWriter("test"+this.counter+".txt");
			for(int i=0;i<test.length;i++){
				writers.write(test[i]+" ");
				writers.write(System.lineSeparator());
			}
			writers.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeLogFileTestCrossScale(double[] test){
		try {
			//List<String> sentence = test;
			FileWriter writers = new FileWriter("test_Scale"+this.counter+".txt");
			for(int i=0;i<test.length;i++){
				writers.write(test[i]+" ");
				writers.write(System.lineSeparator());
			}
			writers.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeLogFileTestCrossP(double[] test){
		try {
			//List<String> sentence = test;
			FileWriter writers = new FileWriter("test_CP"+this.counter+".txt");
			for(int i=0;i<test.length;i++){
				writers.write(test[i]+" ");
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
			//System.out.println(val);
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
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
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