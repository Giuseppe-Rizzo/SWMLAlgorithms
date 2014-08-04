package samplers;

import java.util.ArrayList;


public class StratifiedSampling<E> {

	public StratifiedSampling() {
		// TODO Auto-generated constructor stub
	}
 public void getStratifiedSampling(ArrayList<E> pop1, ArrayList<E> pop2,ArrayList<E> pop3, ArrayList<E> sam1, ArrayList<E> sam2,ArrayList<E> sam3,double percentage){
	 
	 
			
			NormalSampler<E> ns= new NormalSampler<E>();
			sam1.addAll(ns.sample(pop1, (percentage*pop1.size()))); //sampling of positive example
		
			sam2.addAll(ns.sample(pop2, (percentage*pop2.size()))); //sampling of positive example
			
			sam3.addAll(ns.sample(pop3, (percentage*pop3.size()))); //sampling of positive example
			
		
	
}
}
