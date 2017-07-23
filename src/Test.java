import java.util.ArrayList;
import java.util.List;


public class Test {
	public static List<Integer> addZero(int front,int back,int[] c){
		List<Integer> e = new ArrayList<Integer>();
		if(front>0){
			for(int j=0;j<front;j++){
				e.add(0);
			}
		}
		for(int j= 0;j<=c.length-1;j++){
			e.add(c[j]);
		}
		if(back>0){
			for(int j=0;j<back;j++){
				e.add(0);
			}
		}
		return e;
	}
	
	public static int[] addZero2(int front,int back,int[] c){
		int[] result = new int[front+back+c.length];
		System.out.println(result.length);
		for(int i=0; i<c.length; i++){
			result[front+i] = c[i];
		}
		return result;
	}
	// arguments are passed using the text field below this editor
	public static void main(String[] args)
	{
		int[] a = {1,2,3,4,5};
		int[] b = addZero2(2000, 2000, a);
//		for(int i=0; i<b.length; i++){
//			System.out.print(b[i]);
//		}
//		System.out.println();
		
		int[] c = addZero(200000,200000,a).stream().mapToInt(j->j).toArray();
		for(int i=0; i<c.length; i++){
			System.out.print(c[i]);
		}
	}
}
