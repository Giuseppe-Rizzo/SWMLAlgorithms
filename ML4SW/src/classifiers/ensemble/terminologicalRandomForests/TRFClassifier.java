package classifiers.ensemble.terminologicalRandomForests;


import java.util.ArrayList;
import knowledgeBasesHandler.*;

import org.semanticweb.owl.model.OWLDescription;

import classifiers.ensemble.Ensemble;
import classifiers.refinementOperator.RefinementOperator;
import classifiers.trees.RandomizedTDTClassifier;
import classifiers.trees.models.DLTree;
import evaluation.Parameters;
import samplers.BalancedDataset;
import utils.Triple;

public class TRFClassifier {
	

	private RandomizedTDTClassifier data;
	public TRFClassifier(KnowledgeBase kb) {
		 data = new RandomizedTDTClassifier(kb);
	}

	public Ensemble<DLTree> induceDLForest(ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs, int dim,int dimForest, double prPos, double prNeg, RefinementOperator op ){

		ArrayList<Triple<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>>> splitting= new ArrayList<Triple<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>>>();
		// step 1: boostrap sampling with undersampling of the uncertainty instances
		for (int i = 0; i < dimForest; i++) {
			Triple<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>> triple= new Triple<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>>();

			ArrayList<Integer> posExsEns= new ArrayList<Integer>();
			ArrayList<Integer> negExsEns= new ArrayList<Integer>();
			ArrayList<Integer> undExsEns= new ArrayList<Integer>();


			BalancedDataset<Integer> bd= new BalancedDataset<Integer>(); // a balance of th)e instances
			bd.balanceTheDataset(posExs, negExs, undExs, posExsEns, negExsEns, undExsEns, Parameters.samplingrate); //no sampling
			
			// performing undersampling on uncertainty instances
			
			// under sampling of the uncertainty instances
	

			triple.setFirstElem(posExsEns);
			triple.setSecondElem(negExsEns);
			triple.setThirdElem(undExsEns);
			
			System.out.println(posExsEns.size()+"--"+negExsEns.size()+"---"+undExsEns.size());
			splitting.add(triple);
		}

		// induction if single terminological decision tree
		Ensemble<DLTree> forest= new Ensemble<DLTree>();
		for (int i = 0; i < dimForest; i++) {
			// examples used to induce a single tree
			ArrayList<Integer> posExsEns= splitting.get(i).getFirstElem();
			ArrayList<Integer> negExsEns= splitting.get(i).getSecondElem();
			ArrayList<Integer> undExsEns= splitting.get(i).getThirdElem();
			System.out.printf(" %d Training set composition: %d %d %d", i, posExsEns.size(),negExsEns.size(), undExsEns.size());
			DLTree tree=data.induceDLTree(posExsEns, negExsEns, undExsEns, dim, prPos, prNeg, op);
			forest.addClassifier(tree);

		}
		System.out.println("forest size: "+ forest.getSize());

		return forest;
	}

	


	public int classifyEnsemble(int indTestEx, Ensemble<DLTree> forest){
		int classValue=0;
		int pos=0;
		int neg=0;
		int und=0;
		for (int tree=0; tree<forest.getSize(); tree++){
			if (!Parameters.missingValueTreatmentForTDT){
			classValue=data.classifyExample(indTestEx,forest.getClassifier(tree));
			
			}
			else{
				ArrayList<Integer> list= new ArrayList<Integer>();
				classValue=data.classifyExample(list, indTestEx, forest.getClassifier(tree));
			}
			if(classValue==1)
				pos++;
			else if(classValue== -1)
				neg++;
			else
				und++;
		}
		
		System.out.println("Voting: "+ pos+"--"+neg+"--"+und);
		if (pos>neg)
			if(pos>und)
					return +1;
			else
				return 0;
		else
			if(neg>und)
				return -1;
			else
				return 0;


	}

	@SuppressWarnings("unchecked")
	public	void classifyExamples(int indTestEx, @SuppressWarnings("rawtypes") Ensemble[] forests, int[] results, OWLDescription[] testConcepts, int...rclass) {

		int length = testConcepts!=null?testConcepts.length:1;
		for (int c=0; c < length; c++) {
			
			results[c] = classifyEnsemble(indTestEx, forests[c]);
			System.out.println(forests[c].printVotes());
		} // for c



	}
}






