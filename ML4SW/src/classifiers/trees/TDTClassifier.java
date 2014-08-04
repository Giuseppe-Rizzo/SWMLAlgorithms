package classifiers.trees;

import java.util.ArrayList;
import java.util.Stack;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owl.model.OWLDescription;

import utils.Couple;
import utils.Npla;
import classifiers.trees.models.DLTree;
import evaluation.Evaluation;

public class TDTClassifier extends AbstractTDTClassifier {


	public TDTClassifier(KnowledgeBase k){

		super(k);

	}




	public DLTree induceDLTree(ArrayList<Integer> posExs, ArrayList<Integer> negExs,	ArrayList<Integer> undExs, 
			int dim, double prPos, double prNeg) {		
		System.out.printf("Learning problem\t p:%d\t n:%d\t u:%d\t prPos:%4f\t prNeg:%4f\n", 
				posExs.size(), negExs.size(), undExs.size(), prPos, prNeg);


		Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double> examples = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExs, negExs, undExs, dim, prPos, prNeg);
		DLTree tree = new DLTree(); // new (sub)tree
		Stack<Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>> stack= new Stack<Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>>();
		Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> toInduce= new Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
		toInduce.setFirstElement(tree);
		toInduce.setSecondElement(examples);
		stack.push(toInduce);

		while(!stack.isEmpty()){
			System.out.printf("Stack: %d \n",stack.size());
			Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> current= stack.pop(); // extract the next element
			DLTree currentTree= current.getFirstElement();
			Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double> currentExamples= current.getSecondElement();
			// set of negative, positive and undefined example
			posExs=currentExamples.getFirst();
			negExs=currentExamples.getSecond();
			undExs=currentExamples.getThird();
			if (posExs.size() == 0 && negExs.size() == 0) // no exs
				if (prPos >= prNeg) { // prior majority of positives
					currentTree.setRoot(kb.getDataFactory().getOWLThing()); // set positive leaf
					System.out.println("true");
				}
				else { // prior majority of negatives
					currentTree.setRoot(kb.getDataFactory().getOWLNothing()); // set negative leaf
					System.out.println("false");
				}

			//		double numPos = posExs.size() + undExs.size()*prPos;
			//		double numNeg = negExs.size() + undExs.size()*prNeg;
			else{
				double numPos = posExs.size();
				double numNeg = negExs.size();
				double perPos = numPos/(numPos+numNeg);
				double perNeg = numNeg/(numPos+numNeg);

				if (perNeg==0 && perPos > Evaluation.PURITY_THRESHOLD) { // no negative
					currentTree.setRoot(kb.getDataFactory().getOWLThing()); // set positive leaf
					System.out.println("true");
				}
				else{
					if (perPos==0 && perNeg > Evaluation.PURITY_THRESHOLD) { // no positive			
						currentTree.setRoot(kb.getDataFactory().getOWLNothing()); // set negative leaf
						System.out.println("false");
					}		
					// else (a non-leaf node) ...
					else{
						OWLDescription[] cConcepts= new OWLDescription[0];
						ArrayList<OWLDescription> cConceptsL = generateNewConcepts(dim, posExs, negExs);
						System.out.println("Size: "+cConceptsL);
						//						cConceptsL= getRandomSelection(cConceptsL); // random selection of feature set

						cConcepts = cConceptsL.toArray(cConcepts);

						// select node concept
						OWLDescription newRootConcept = selectBestConcept(cConcepts, posExs, negExs, undExs, prPos, prNeg);

						ArrayList<Integer> posExsT = new ArrayList<Integer>();
						ArrayList<Integer> negExsT = new ArrayList<Integer>();
						ArrayList<Integer> undExsT = new ArrayList<Integer>();
						ArrayList<Integer> posExsF = new ArrayList<Integer>();
						ArrayList<Integer> negExsF = new ArrayList<Integer>();
						ArrayList<Integer> undExsF = new ArrayList<Integer>();

						split(newRootConcept, posExs, negExs, undExs, posExsT, negExsT, undExsT, posExsF, negExsF, undExsF);
						// select node concept
						currentTree.setRoot(newRootConcept);		
						// build subtrees

						//		undExsT = union(undExsT,);
						DLTree posTree= new DLTree();
						DLTree negTree= new DLTree(); // recursive calls simulation
						currentTree.setPosTree(posTree);
						currentTree.setNegTree(negTree);
						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla1 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExsT, negExsT, undExsT, dim, perPos, perNeg);
						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla2 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExsF, negExsF, undExsF, dim, perPos, perNeg);
						Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> pos= new Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						pos.setFirstElement(posTree);
						pos.setSecondElement(npla1);
						// negative branch
						Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> neg= new Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						neg.setFirstElement(negTree);
						neg.setSecondElement(npla2);
						stack.push(neg);
						stack.push(pos);
					}
				}
			}
		}
		return tree;

	}
}
