package samplers;



import java.util.ArrayList;
import java.util.Random;
/**
 * Perform an oversampling of the minority class
 * @author Utente
 *
 * @param <T>
 */
public class OverSampling<T> implements Sampler<T> {
	private Random generator;
	@Override
	public ArrayList<T> sample(double threshold, ArrayList<T> set1, ArrayList<T> set2) {
		// TODO Auto-generated method stub
		ArrayList<T> result=  new ArrayList<T>(set1);
		int numberofExample= (int) (threshold*(set1.size()));
		
		System.out.println(numberofExample);
		
		for(int i= numberofExample;i>0;i--)
			result.add(set1.get(generator.nextInt(result.size())));
		
		return null;
	}
	
	public OverSampling(){
		
	generator= new Random();
	}

	@Override
	public void sampleOn(double threshold, ArrayList<T> population,
			ArrayList<T> referencepopulation) {
		// TODO Auto-generated method stub
		
	}
	
	

}
