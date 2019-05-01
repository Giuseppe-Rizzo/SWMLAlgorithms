package it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.etrfs;




import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.semanticweb.owlapi.model.OWLClassExpression;

import it.uniba.di.lacam.ml.classifiers.ensemble.Ensemble;
import it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.dst.MassFunction;
import it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.dst.RuleType;
import it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.trees.DSTDLTree;
import it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.trees.EvidentialTDTClassifier;
import it.uniba.di.lacam.ml.classifiers.refinementOperator.RefinementOperator;
import it.uniba.di.lacam.ml.evaluation.Parameters;
import it.uniba.di.lacam.ml.knowledgeBasesHandler.*;
import it.uniba.di.lacam.ml.samplers.BalancedDataset;
import it.uniba.di.lacam.ml.utils.Triple;

/**
 * A class representing an evidential terminological random forest classifier
 * @author Giuseppe Rizzo
 *
 */
public class ETRFClassifier {
	

	private EvidentialTDTClassifier data;
	public ETRFClassifier(KnowledgeBase kb) {
		 data = new EvidentialTDTClassifier(kb);
	}

	/**
	 * The training procedure  
	 * @param posExs, the positive ex.s
	 * @param negExs, the neg ex.s
	 * @param undExs, the uncertain-membership ex.s
	 * @param dim, the  number of refinements generated per turn
	 * @param dimForest, the ensemble size
	 * @param prPos, priors for empty training subsets
	 * @param prNeg,priors for empty training subsets
	 * @return a collection of DSTDLTree
	 */
	public Ensemble<DSTDLTree> induceDLForest(ArrayList<Integer> posExs, ArrayList<Integer> negExs,	ArrayList<Integer> undExs, int dim,int dimForest, double prPos, double prNeg ){
		
		
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
		Ensemble<DSTDLTree> forest= new Ensemble<DSTDLTree>();
		for (int i = 0; i < dimForest; i++) {
			// examples used to induce a single tree
			ArrayList<Integer> posExsEns= splitting.get(i).getFirstElem();
			ArrayList<Integer> negExsEns= splitting.get(i).getSecondElem();
			ArrayList<Integer> undExsEns= splitting.get(i).getThirdElem();
			System.out.printf(" %d Training set composition: %d %d %d", i, posExsEns.size(),negExsEns.size(), undExsEns.size());
			DSTDLTree tree=data.induceDSTDLTree(posExsEns, negExsEns, undExsEns, dim, prPos, prNeg, null);
			forest.addClassifier(tree);

		}
		System.out.println("forest size: "+ forest.getSize());
		forest=prune(forest); // prune the ensemble

		return forest;
	}

	
	/**
	 * A pruning criterion of an ensemble based on the quantification of the conflict (according to Dempster-Shafer Theory)
	 * @param toPrune, the ennsemble to be pruned
	 * @return a pruned ensemble
	 */
	public Ensemble<DSTDLTree>  prune(Ensemble<DSTDLTree> toPrune){
		Ensemble<DSTDLTree>  newForest= new Ensemble<DSTDLTree>();
		double currentConflict=0.0d;
		final double nu=0.4d; 
		
		for(Object o: toPrune){
			DSTDLTree current= (DSTDLTree) o;
			ArrayList<MassFunction> collectLeaves = current.collectLeaves();
			MassFunction massFunction = collectLeaves.get(0);
					collectLeaves.remove(0);
			MassFunction[] others= collectLeaves.toArray(new MassFunction[collectLeaves.size()]);
			 MassFunction combine = massFunction.combine(RuleType.Dempster, others); 
			 double conflict = combine.getConflict(combine);
			//currentConflict += conflict; // auto-conflict
			 if (conflict < nu)
				 newForest.addClassifier(current);
			
		}
		
	
		return newForest;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int classifyEnsemble(int indTestEx, Ensemble<DSTDLTree> forest){
		MassFunction[] functions= new MassFunction[forest.getSize()];
		for (int tree=0; tree<forest.getSize(); tree++){
			functions[tree]=data.getBBA(indTestEx, forest.getClassifier(tree));
		}
		
		MassFunction bba= getBBA(functions);
		
		ArrayList<Integer> ipotesi= new ArrayList<Integer>();
		ipotesi.add(+1);
		
		double confirmationFunctionValuePos = bba.getConfirmationFunction(ipotesi);
		//		double confirmationFunctionValuePos = bba.calcolaBeliefFunction(ipotesi);
		// not concept
		ArrayList<Integer> ipotesi2= new ArrayList<Integer>();
		ipotesi2.add(-1);
		double confirmationFunctionValueNeg = bba.getConfirmationFunction(ipotesi2);
		//		double confirmationFunctionValueNeg = bba.calcolaBeliefFunction(ipotesi2);
		ArrayList<Integer> ipotesi3= new ArrayList<Integer>();
		ipotesi3.add(-1);
		ipotesi3.add(+1);
		double confirmationFunctionValueUnc = bba.getConfirmationFunction(ipotesi3);
		//		double confirmationFunctionValueUnc = bba.calcolaBeliefFunction(ipotesi3);

		if((confirmationFunctionValueUnc>confirmationFunctionValuePos)&&(confirmationFunctionValueUnc>confirmationFunctionValueNeg))
			if (confirmationFunctionValuePos>confirmationFunctionValueNeg)
				return +1;
			else if (confirmationFunctionValuePos<confirmationFunctionValueNeg)
				return -1;
			else  return 0;
		else if(confirmationFunctionValuePos>=confirmationFunctionValueNeg)
			return +1;
		else
			return -1;


	}

	@SuppressWarnings("unchecked")
	public	void classifyExamples(int indTestEx, @SuppressWarnings("rawtypes") Ensemble[] forests, int[] results, OWLClassExpression[] testConcepts, int...rclass) {

		for (int c=0; c < testConcepts.length; c++) {
			
			results[c] = classifyEnsemble(indTestEx, forests[c]);
			System.out.println(forests[c].printVotes());
		} // for c



	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MassFunction<Integer> getBBA(MassFunction... function) {
		MassFunction bba= function[0];

		MassFunction<Integer>[] others= new MassFunction[(function.length-1)];
		System.out.println("_____________BBA TO COMBINE______________________");
		System.out.println("BBA: "+bba);
		for(int i=1; i<function.length;i++){
			// applicare la regola di combinazione

			others[i-1]=function[i];
		}
		if(others.length>=1){
			bba=bba.combineDempster(others);

		}
		//  apply combination rule for BBA


		return bba;

	}


}