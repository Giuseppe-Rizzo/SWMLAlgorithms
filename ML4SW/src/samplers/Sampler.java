package samplers;


import  java.util.*;

/**
 * An interface to implement a sampling method to deal with class imbalance
 * The method perform a sampling until a proportion between two population is not respected
 * @author Utente
 *
 * @param <T>
 */
public interface Sampler<T> {
	public  ArrayList<T> sample(double threshold,ArrayList<T> population, ArrayList<T> referencepopulation);
	public  void sampleOn(double threshold,ArrayList<T> population, ArrayList<T> referencepopulation);
	
}
