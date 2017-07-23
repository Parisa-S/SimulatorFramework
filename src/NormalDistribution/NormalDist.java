package NormalDistribution;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NormalDist {
	private static Random random = new Random();

	
	public static void main(String[] args){
		random(1.5,0.2);
		random(3.0,0.5);
		random(1.0,0.7);
	}
	
	public static void random(double mean,double stdVa){
		try {
			FileWriter writer = new FileWriter("output"+mean+"_"+stdVa+".txt");
			for(int i=0;i<1000;i++){
				double val =(random.nextGaussian()*stdVa)+mean;
				writer.write(val+" ");
				writer.write(System.lineSeparator());
				System.out.println(val);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
