package it.uniba.di.lacam.ml.classifiers.ensemble.trfs;


import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClassExpression;

import it.uniba.di.lacam.ml.classifiers.ensemble.Ensemble;
import it.uniba.di.lacam.ml.classifiers.refops.RefinementOperator;
import it.uniba.di.lacam.ml.classifiers.trees.RandomizedTDTClassifier;
import it.uniba.di.lacam.ml.classifiers.trees.models.DLTree;
import it.uniba.di.lacam.ml.evaluation.Parameters;
import it.uniba.di.lacam.ml.kbhandler.*;
import it.uniba.di.lacam.ml.samplers.BalancedDataset;
import it.uniba.di.lacam.ml.utils.Triple;
/**
 * A class implementing  terminological random forest classifier
 * @author Giuseppe
 *
 */
public class TRFClassifier {
	

	private RandomizedTDTClassifier data;
	private KnowledgeBase kb;
	public TRFClassifier(KnowledgeBase kb) {
		this.kb=kb;
		 data = new RandomizedTDTClassifier(kb);
	}

	/**
	 * Algorithm for terminological random forest induction
	 * @param posExs, positive examples (namely, the index)
	 * @param negExs, negative examples
	 * @param undExs, uncertain-membership examples
	 * @param dim, the beam for the number of refinements generated per turn
	 * @param dimForest, the ensemble size
	 * @param prPos, prior probability for leaves without examples
	 * @param prNegprior probability for leaves without examples
	 * @return a non empty ensemble of DLTree
	 */
	public Ensemble<DLTree> induceDLForest(ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs, int dim,int dimForest, double prPos, double prNeg ){

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
			DLTree tree=data.induceDLTree(kb.getDataFactory().getOWLThing(),posExsEns, negExsEns, undExsEns, dim, prPos, prNeg);
			forest.addClassifier(tree);

		}
		System.out.println("forest size: "+ forest.getSize());

		return forest;
	}

	

/**
 * Algorithm for instance classification through a forest
 * @param indTestEx, the test individual (i.e. its index)
 * @param forest, the terminological random forest
 * @return the label (+1,-1,0)
 */
	public int classifyEnsemble(int indTestEx, Ensemble<DLTree> forest){
		int classValue=0;
		int pos=0;
		int neg=0;
		int und=0;
		for (int tree=0; tree<forest.getSize(); tree++){
			if (!Parameters.missingValueTreatmentForTDT){
			classValue=data.classify(kb.getIndividuals()[indTestEx],forest.getClassifier(tree));
			
			}
			else{
				ArrayList<Integer> list= new ArrayList<Integer>();
				classValue=data.classify(kb.getIndividuals()[indTestEx],  forest.getClassifier(tree));
			}
			if(classValue==1)
				pos++;
			else if(classValue== -1)
				neg++;
			else
				und++;
		}
		
		System.out.println("Voting: "+ pos+"--"+neg+"--"+und);
		if (Math.abs((pos-neg))<5)
			return 0; //rejection threshold
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
	public void classifyExamples(int indTestEx, @SuppressWarnings("rawtypes") Ensemble[] forests, int[] results, OWLClassExpression[] testConcepts, int...rclass) {

		int length = testConcepts!=null?testConcepts.length:1;
		for (int c=0; c < length; c++) {
			
			results[c] = classifyEnsemble(indTestEx, forests[c]);
			
		} // for c



	}
}






