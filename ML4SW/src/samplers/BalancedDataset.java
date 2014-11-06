package samplers;

import java.util.ArrayList;

import evaluation.Parameters;


public class BalancedDataset<E> {

	public BalancedDataset() {
		// TODO Auto-generated constructor stub
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void balanceTheDataset(ArrayList<E> pop1, ArrayList<E> pop2,ArrayList<E> pop3, ArrayList<E> sam1, ArrayList<E> sam2,ArrayList<E> sam3,double...percentage){
		StratifiedSampling ss= new StratifiedSampling();
		ss.getStratifiedSampling(pop1, pop2, pop3, sam1, sam2, sam3, percentage[0]);

		if (percentage[0]!=Parameters.originalImbalance){
			// rimuovo le istanze incerte
			UnderSampling u = new UnderSampling();
			u.sampleOn(1, sam3, sam3);

			if(pop1.size()<(0.3 * pop2.size()))
				u.sampleOn(0.7, sam2, sam2);
			else if(pop2.size()<(0.3 * pop1.size()))
				u.sampleOn(0.7, sam1, sam1);

		}
		//		 
		//		System.out.println("Sam 3: "+sam3);

	}

}
