package it.uniba.di.lacam.ml.samplers;


import  java.util.*;

/**
 * An interface to implement a sampling method to deal with class imbalance
 * The method perform a sampling until a proportion between two population is not respected
 * @author Giuseppe
 *
 * @param <T>
 */
public interface Sampler<T> {
	/**
	 * sampling generating a new list of objects
	 * @param threshold
	 * @param population
	 * @param referencepopulation
	 * @return
	 */
	public  ArrayList<T> sample(double threshold,ArrayList<T> population, ArrayList<T> referencepopulation);
	
	/**
	 * sampling removing objects
	 * @param threshold
	 * @param population
	 * @param referencepopulation
	 */
	public  void sampleOn(double threshold,ArrayList<T> population, ArrayList<T> referencepopulation);
	
}
