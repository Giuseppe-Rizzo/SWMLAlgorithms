package it.uniba.di.lacam.ml.samplers;



import java.util.ArrayList;
import java.util.Random;
/**
 * Implementation of a random UnderSampler
 * @author Utente
 *
 * @param <T>
 */
public class UnderSampling<T> implements Sampler<T> {
	private Random generator;
	@Override
	public void sampleOn(double threshold, ArrayList<T> set1, ArrayList<T> set2) {
		
		if(!set1.isEmpty()){
			
			int setSize = set1.size(); 
//			System.out.printf("%d ------> %d %f", resSize, setSize);
			int numberofExample= (int) (threshold*setSize);
			
			System.out.println(numberofExample);
			
			for(int i= numberofExample;i>0;i--){
				int nextInt = generator.nextInt(set1.size());
				//System.out.println("result: "+ result.get(nextInt)+"-"+(double)(resSize/setSize));
				set1.remove(nextInt);
				//System.out.println("result: "+ result.get(nextInt)+"-"+(double)(resSize/setSize));			
			}
			
		}
		
	}
	
	public UnderSampling(){
		
	generator= new Random();
	}

	@Override
	public ArrayList<T> sample(double threshold, ArrayList<T> population,
			ArrayList<T> referencepopulation) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
