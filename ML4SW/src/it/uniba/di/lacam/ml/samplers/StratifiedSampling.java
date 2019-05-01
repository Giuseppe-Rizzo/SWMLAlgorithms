package it.uniba.di.lacam.ml.samplers;

import java.util.ArrayList;

/**
 * Performa a stratified sampling from a population
 * @author Giuseppe
 *
 * @param <E>
 */
public class StratifiedSampling<E> {

	public StratifiedSampling() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param pop1, first sub-population
	 * @param pop2, second sub-population
	 * @param pop3, third sub-population
	 * @param sam1, sample from the first sub-population
	 * @param sam2, sample from the second sub-population
	 * @param sam3, sample from the third sub-population
	 * @param percentage, the same rate for all the sub-populations
	 */
 public void getStratifiedSampling(ArrayList<E> pop1, ArrayList<E> pop2,ArrayList<E> pop3, ArrayList<E> sam1, ArrayList<E> sam2,ArrayList<E> sam3,double percentage){
	 
	 
			
			NormalSampler<E> ns= new NormalSampler<E>();
			sam1.addAll(ns.sample(pop1, (percentage*pop1.size()))); //sampling of positive example
		
			sam2.addAll(ns.sample(pop2, (percentage*pop2.size()))); //sampling of positive example
			
			sam3.addAll(ns.sample(pop3, (percentage*pop3.size()))); //sampling of positive example
			
		
	
}
}
