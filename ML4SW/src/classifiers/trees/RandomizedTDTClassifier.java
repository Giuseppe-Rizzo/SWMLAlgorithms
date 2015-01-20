package classifiers.trees;

import java.util.ArrayList;

import java.util.Random;

import java.util.Stack;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;

import utils.Couple;
import utils.Npla;
import classifiers.refinementOperator.RefinementOperator;
import classifiers.trees.models.AbstractTree;
import classifiers.trees.models.DLTree;
import evaluation.Parameters;
import knowledgeBasesHandler.KnowledgeBase;

public class RandomizedTDTClassifier extends AbstractTDTClassifier{
//	public KnowledgeBase kb;
	public RandomizedTDTClassifier(KnowledgeBase kb) {
		
		super(kb);
		
	}
	
	
	
	public DLTree induceDLTree(ArrayList<Integer> posExs, ArrayList<Integer> negExs,	ArrayList<Integer> undExs, 
			int dim, double prPos, double prNeg, RefinementOperator op) {		
		System.out.printf("Learning problem\t p:%d\t n:%d\t u:%d\t prPos:%4f\t prNeg:%4f\n", 
				posExs.size(), negExs.size(), undExs.size(), prPos, prNeg);
		
		ArrayList<Integer> truePos= posExs;
		ArrayList<Integer> trueNeg= negExs;
		
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

				if (perNeg==0 && perPos > Parameters.PURITY_THRESHOLD) { // no negative
					currentTree.setRoot(kb.getDataFactory().getOWLThing()); // set positive leaf
					System.out.println("true");
				}
				else{
					if (perPos==0 && perNeg > Parameters.PURITY_THRESHOLD) { // no positive			
						currentTree.setRoot(kb.getDataFactory().getOWLNothing()); // set negative leaf
						System.out.println("false");
					}		
				// else (a non-leaf node) ...
					else{
						OWLDescription[] cConcepts= new OWLDescription[0];
						ArrayList<OWLDescription> cConceptsL = op.generateNewConcepts(dim, posExs, negExs);
						System.out.println("Size: "+cConceptsL);
						cConceptsL= getRandomSelection(cConceptsL); // random selection of feature set
					
						cConcepts = cConceptsL.toArray(cConcepts);

						// select node concept
						OWLDescription newRootConcept = Parameters.CCP?(selectBestConceptCCP(cConcepts, posExs, negExs, undExs, prPos, prNeg, truePos, trueNeg)):(selectBestConcept(cConcepts, posExs, negExs, undExs, prPos, prNeg));;

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


		

private ArrayList<OWLDescription> getRandomSelection(ArrayList<OWLDescription> refinement) {
	System.out.println("RANDOM SELECTION ");
	Random generator= new Random(1);
	ArrayList<OWLDescription> subset=new ArrayList<OWLDescription>();
	final int SUBSET_DIMENSION=(int) Math.sqrt(refinement.size());
	for (int i=0;i<SUBSET_DIMENSION;i++){
		int index=generator.nextInt(refinement.size());
		System.out.println("Next resampled feature"+refinement.get(index));
		subset.add((refinement.get(index)));
	
	}
	System.out.printf("Subset Feature: %d \n", subset.size());
	return subset;	
	
}

	public int classifyExample(int indTestEx, DLTree tree) {
		Stack<DLTree> stack= new Stack<DLTree>();
		OWLDataFactory dataFactory = kb.getDataFactory();
		stack.add(tree);
		int result=0;
		boolean stop=false;
		while(!stack.isEmpty() && !stop){
			DLTree currentTree= stack.pop();
			
			OWLDescription rootClass = currentTree.getRoot();
//			System.out.println("Root class: "+ rootClass);
			if (rootClass.equals(dataFactory.getOWLThing())){
				stop=true;
				result=+1;

			}
			else if (rootClass.equals(dataFactory.getOWLNothing())){
				stop=true;
				result=-1;

			}else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass))
				stack.push(currentTree.getPosSubTree());
			else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass)))
				stack.push(currentTree.getNegSubTree());
			else {
				stop=true;
				result=0; // seguo entrambi i percorsi

			}
		};
		 
		
		return result;

	}



	@Override
	public void prune(Integer[] pruningSet, AbstractTree tree,
			AbstractTree subtree) {
		// TODO Auto-generated method stub
		
	}



	
	
//	public	void classifyExamplesTree(int indTestEx, DLTree[] forests, int[] results, OWLDescription[] testConcepts, int...rclass) {
//
//		for (int c=0; c < testConcepts.length; c++) {
//			results[c] = classifyExample(indTestEx, forests[c]);
////			System.out.println(forests[c].printVotes());
//		} // for c
//
//
//
//	}

	/*public void classifyExamplesDST(int indTestEx, DSTDLTree[] trees, int[] results, OWLDescription[] testConcepts) {
		ArrayList<Couple<Integer,MassFunction<Integer>>> list=null;
		for (int c=0; c < testConcepts.length; c++) {
			System.out.println("Tree \n"+ trees[c]);
			list= new  ArrayList<Couple<Integer,MassFunction<Integer>>>();
			classifyExampleDST(list,indTestEx, trees[c]);
			// estraggo le BBA da tutte le foglie raggiunte
			System.out.println("Lista di foglie");
			System.out.println(list);
			MassFunction<Integer> bba=list.get(0).getSecondElement();

			MassFunction<Integer>[] others= new MassFunction[(list.size()-1)];
			System.out.println("_____________BBA TO COMBINE______________________");
			System.out.println("BBA: "+bba);
			for(int i=1; i<list.size();i++){
				MassFunction next=list.get(i).getSecondElement();
				// applicare la regola di combinazione

				others[i-1]=next;
			}
			if(others.length>=1){
				bba=bba.applicaCombinazione(others);

			}// combino con tutte le altre BBA
			//concept
			ArrayList<Integer> ipotesi= new ArrayList<Integer>();
			ipotesi.add(+1);
			double confirmationFunctionValuePos = bba.calcolaConfirmationFunction(ipotesi);
//			double confirmationFunctionValuePos = bba.calcolaBeliefFunction(ipotesi);
			// not concept
			ArrayList<Integer> ipotesi2= new ArrayList<Integer>();
			ipotesi2.add(-1);
			double confirmationFunctionValueNeg = bba.calcolaConfirmationFunction(ipotesi2);
//			double confirmationFunctionValueNeg = bba.calcolaBeliefFunction(ipotesi2);
			ArrayList<Integer> ipotesi3= new ArrayList<Integer>();
			ipotesi3.add(-1);
			ipotesi3.add(+1);
			double confirmationFunctionValueUnc = bba.calcolaConfirmationFunction(ipotesi3);
//			double confirmationFunctionValueUnc = bba.calcolaBeliefFunction(ipotesi3);

			System.out.println(confirmationFunctionValuePos+ " vs. "+ confirmationFunctionValueNeg+ "vs." +confirmationFunctionValueUnc);


			if((confirmationFunctionValueUnc>confirmationFunctionValuePos)&&(confirmationFunctionValueUnc>confirmationFunctionValueNeg))
				if (confirmationFunctionValuePos>confirmationFunctionValueNeg)
					results[c]=+1;
				else if (confirmationFunctionValuePos<confirmationFunctionValueNeg)
					results[c]=-1;
				else results[c]=0;
			else if(confirmationFunctionValuePos>=confirmationFunctionValueNeg)
				results[c]=+1;
			else
				results[c]=-1;

			System.out.println("Outcomes: "+ results[c]);

		}
	}
	 */

//	private  OWLDescription selectBestConcept(OWLDescription[] concepts,
//			ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs, 
//			double prPos, double prNeg) {
//
//		int[] counts;
//
//		int bestConceptIndex = 0;
//
//		counts = getSplitCounts(concepts[0], posExs, negExs, undExs);
//		System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
//				"#"+0, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);
//
//		double bestGain = gain(counts, prPos, prNeg);
//
//		System.out.printf("%+10e\n",bestGain);
//
//		System.out.println(concepts[0]);
//
//		for (int c=1; c<concepts.length; c++) {
//
//			counts = getSplitCounts(concepts[c], posExs, negExs, undExs);
//			System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
//					"#"+c, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);
//
//			double thisGain = gain(counts, prPos, prNeg);
//			System.out.printf("%+10e\n",thisGain);
//			System.out.println(concepts[c]);
//			if(thisGain > bestGain) {
//				bestConceptIndex = c;
//				bestGain = thisGain;
//			}
//		}
//
//		System.out.printf("best gain: %f \t split #%d\n", bestGain, bestConceptIndex);
//		return concepts[bestConceptIndex];
//	}

	


//	private double gain(int[] counts, double prPos, double prNeg) {
//
//		double sizeT = counts[0] + counts[1];
//		double sizeF = counts[3] + counts[4];
//		double sizeU = counts[6] + counts[7] + counts[2] + counts[5];
//		double sum = sizeT+sizeF+sizeU;
//
//		double startImpurity = gini(counts[0]+counts[3], counts[1]+counts[4], prPos, prNeg);
//		double tImpurity = gini(counts[0], counts[1], prPos, prNeg);
//		double fImpurity = gini(counts[3], counts[4], prPos, prNeg);
//		double uImpurity = gini(counts[6]+counts[2], counts[7]+counts[5] , prPos, prNeg);		
//
//		return startImpurity - (sizeT/sum)*tImpurity - (sizeF/sum)*fImpurity - - (sizeU/sum)*uImpurity;
//	}



//	private static double gini(double numPos, double numNeg, double prPos, double prNeg) {
//
//		double sum = numPos+numNeg;
//
//		double p1 = (numPos*Evaluation.M*prPos)/(sum+Evaluation.M);
//		double p2 = (numNeg*Evaluation.M*prNeg)/(sum+Evaluation.M);
//
//		return (1.0-p1*p1-p2*p2);
//		//		return (1-Math.pow(p1,2)-Math.pow(p2,2))/2;
//	}




//	private int[] getSplitCounts(OWLDescription concept, 
//			ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs) {
//		System.out.println("positive: "+posExs);
//		int[] counts = new int[9];
//		ArrayList<Integer> posExsT = new ArrayList<Integer>();
//		ArrayList<Integer> negExsT = new ArrayList<Integer>();
//		ArrayList<Integer> undExsT = new ArrayList<Integer>();
//
//		ArrayList<Integer> posExsF = new ArrayList<Integer>();
//		ArrayList<Integer> negExsF = new ArrayList<Integer>();
//		ArrayList<Integer> undExsF = new ArrayList<Integer>();
//
//		ArrayList<Integer> posExsU = new ArrayList<Integer>();
//		ArrayList<Integer> negExsU = new ArrayList<Integer>();
//		ArrayList<Integer> undExsU = new ArrayList<Integer>();
//
//		splitGroup(concept,posExs,posExsT,posExsF,posExsU);
//		splitGroup(concept,negExs,negExsT,negExsF,negExsU);	
//		splitGroup(concept,undExs,undExsT,undExsF,undExsU);	
//
//		counts[0] = posExsT.size(); 
//		counts[1] = negExsT.size(); 
//		counts[2] = undExsT.size(); 
//		counts[3] = posExsF.size(); 
//		counts[4] = negExsF.size();
//		counts[5] = undExsF.size();
//		counts[6] = posExsU.size(); 
//		counts[7] = negExsU.size();
//		counts[8] = undExsU.size();
////		for(int i=0; i<counts.length;i++)
////			System.out.println(counts[i]);
//
//		return counts;
//
//	}


//	private  void split(OWLDescription concept,
//			ArrayList<Integer> posExs,  ArrayList<Integer> negExs,  ArrayList<Integer> undExs,
//			ArrayList<Integer> posExsT, ArrayList<Integer> negExsT,	ArrayList<Integer> undExsT, 
//			ArrayList<Integer> posExsF,	ArrayList<Integer> negExsF, ArrayList<Integer> undExsF) {
//
//		ArrayList<Integer> posExsU = new ArrayList<Integer>();
//		ArrayList<Integer> negExsU = new ArrayList<Integer>();
//		ArrayList<Integer> undExsU = new ArrayList<Integer>();
//
//		splitGroup(concept,posExs,posExsT,posExsF,posExsU);
//		splitGroup(concept,negExs,negExsT,negExsF,negExsU);
//		splitGroup(concept,undExs,undExsT,undExsF,undExsU);	
//
//	}


//	private void splitGroup(OWLDescription concept, ArrayList<Integer> nodeExamples,
//			ArrayList<Integer> trueExs, ArrayList<Integer> falseExs, ArrayList<Integer> undExs) {
//		OWLDescription negConcept = kb.getDataFactory().getOWLObjectComplementOf(concept);
//
//		for (int e=0; e<nodeExamples.size(); e++) {
//			int exIndex = nodeExamples.get(e);
////			 System.out.println("-****"+ concept);
//			if (kb.getReasoner().hasType(kb.getIndividuals()[exIndex], concept))
//				trueExs.add(exIndex);
//			else if (kb.getReasoner().hasType(kb.getIndividuals()[exIndex], negConcept))
//				falseExs.add(exIndex);
//			else
//				undExs.add(exIndex);		
//		}	
	
	
	
	}


//	private ArrayList<OWLDescription> generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {
//
//		System.out.printf("Generating node concepts ");
//		ArrayList<OWLDescription> rConcepts = new ArrayList<OWLDescription>(dim);
//		OWLDescription newConcept;
//		boolean emptyIntersection;
//		for (int c=0; c<dim; c++) {
//			do {
//				emptyIntersection = false; // true
//				newConcept = kb.getRandomConcept();
//
//				Set<OWLIndividual> individuals = (kb.getReasoner()).getIndividuals(newConcept, false);
//				Iterator<OWLIndividual> instIterator = individuals.iterator();
//				while (emptyIntersection && instIterator.hasNext()) {
//					OWLIndividual nextInd = (OWLIndividual) instIterator.next();
//					int index = -1;
//					for (int i=0; index<0 && i<kb.getIndividuals().length; ++i)
//						if (nextInd.equals(kb.getIndividuals()[i])) index = i;
//					if (posExs.contains(index))
//						emptyIntersection = false;
//					else if (negExs.contains(index))
//						emptyIntersection = false;
//				}					
//			} while (emptyIntersection);;
//			rConcepts.add(newConcept);
//			System.out.printf("%d ", c);
//		}
//		System.out.println();
//
//		return rConcepts;
//	}





//	public void prune(Integer[] pruningSet, DSTDLTree tree, DSTDLTree subtree,OWLDescription testConcept){
//		ArrayList<DSTDLTree> lista;
//		Stack<DSTDLTree> stack= new Stack<DSTDLTree>();
//		boolean finished= false; // sono in fase di risalita
//		stack.add(tree);
//		// array list come pila
//		double nodes= tree.getNodi();
//		if(nodes>1){
//			while(!stack.isEmpty()){
//				DSTDLTree current= stack.pop(); // leggo l'albero corrente
//
//				System.out.println("Current: "+current);
//				DSTDLTree pos= current.getPosSubTree();
//				DSTDLTree neg= current.getNegSubTree();
//				System.out.println("Current: "+pos+" ----- "+neg+"visited? "+current.isVisited());
//
//				if(current.isVisited()){
//					System.out.println("Valutazione");
//					int comissionRoot=current.getCommission();
//					int comissionPosTree= pos.getCommission();
//					int comissionNegTree= neg.getCommission();
//					int omissionRoot=current.getOmission();
//					int omissionPosTree= pos.getOmission();
//					int omissionNegTree= neg.getOmission();
//					int inductionRoot=current.getInduction();
//					int inductionPosTree= pos.getInduction();
//					int inductionNegTree= neg.getInduction();
//
//					int gainC=comissionRoot-(comissionPosTree+comissionNegTree);
//					int gainO=omissionRoot-(omissionPosTree+omissionNegTree);
//					int gainI=inductionRoot-(inductionPosTree+inductionNegTree);
//					if((gainC==0)&&(gainO==0)&&(gainI<0)){
//
//						MassFunction bba=current.getRootBBA();
//						ArrayList<Integer> memership= new ArrayList<Integer>();
//						memership.add(+1);
//						double belC = bba.calcolaConfirmationFunction(memership);
//						////								double confirmationFunctionValuePos = bba.calcolaBeliefFunction(ipotesi);
//						//								// not concept
//						ArrayList<Integer> nonmemership= new ArrayList<Integer>();
//						nonmemership.add(-1);
//						double belNonC = bba.calcolaBeliefFunction(nonmemership);
//						double confirmationFunctionValueNeg = bba.calcolaBeliefFunction(nonmemership);
//						ArrayList<Integer> unkown= new ArrayList<Integer>();
//						unkown.add(-1);
//						unkown.add(+1);
//						MassFunction newm= new MassFunction(unkown);
//						//							 	// rimpiazzo rispetto alla classe di maggioranza
//						if(belC<=belNonC){
//
//							newm.setValues(nonmemership, (bba.getValue(memership)));
//							newm.setValues(memership, bba.getValue(nonmemership));
//							newm.setValues(unkown, bba.getValue(unkown));
//							current.setRoot(kb.getDataFactory().getOWLObjectComplementOf(current.getRoot()),bba);
//						}
//						else{
//
//							current.setRoot(current.getRoot(),bba);
//						}
//
//						current.setNegTree(null);
//						current.setPosTree(null);	
//
//
//
//					}
//				}
//				else{
//					current.setAsVisited();
//					stack.push(current); // rimetto in  pila  e procedo alle chiamate ricorsive
//					if(pos!=null){
//						if((pos.getNegSubTree()!=null)||(pos.getPosSubTree()!=null))
//							stack.push(pos);
//
//					}
//					if(neg!=null){
//						if((neg.getNegSubTree()!=null)||(neg.getPosSubTree()!=null))
//							stack.push(neg);
//
//					}
//				}
//
//			}				
//		}
//
//		System.out.println("Tree: "+tree);
//	}
//}