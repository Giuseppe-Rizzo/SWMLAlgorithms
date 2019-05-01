package it.uniba.di.lacam.ml.samplers;


import java.util.ArrayList;
import java.util.Random;

/**
 * A class to perform a normal (i.e. random) sampling of the instances 
 * @author Utente
 *
 */
public class NormalSampler<T>{
	private Random generator;
	/**
	 * Perform a random sampling of a set of object
	 * @param set1
	 * @param n
	 * @return
	 */
	public ArrayList<T> sample(ArrayList<T> set1,double n) {
		// TODO Auto-generated method stub
		System.out.println("n: " +n);
		ArrayList<T> result=  new ArrayList<T>();
		System.out.println("----"+ result.size());
		System.out.println("Dimensione esem"+ set1.size());
//		generator.nextInt(set1.size());
		for(int i=0;i<n;i++){
			T t = set1.get(generator.nextInt(set1.size()));
			System.out.println("Elemento"+ t);
			result.add(t);
		}
//	
		
		return result;
	}
	
	public NormalSampler(){
		
	generator= new Random();
	
	}

}
