package classifiers.trees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Stack;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;

import utils.Couple;
import utils.Npla;


import classifiers.refinementOperator.RefinementOperator;
import classifiers.trees.models.AbstractTree;
import classifiers.trees.models.DLTree;
import evaluation.Parameters;

public class TDTClassifier extends AbstractTDTClassifier {


	public TDTClassifier(KnowledgeBase k){

		super(k);

	}



	public DLTree induceDLTree( ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs, 
			int dim, double prPos, double prNeg, RefinementOperator op) {		
		System.out.printf("Learning problem\t p:%d\t n:%d\t u:%d\t prPos:%4f\t prNeg:%4f\n", 
				posExs.size(), negExs.size(), undExs.size(), prPos, prNeg);
		ArrayList<Integer> truePos= posExs;
		ArrayList<Integer> trueNeg= negExs;
		Stack<OWLDescription> fatherConcepts= new Stack<OWLDescription>();
		
		
		Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double> examples = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExs, negExs, undExs, dim, prPos, prNeg);
		DLTree tree = new DLTree(); // new (sub)tree
		Stack<Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>> stack= new Stack<Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>>();
		Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> toInduce= new Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
		toInduce.setFirstElement(tree);
		toInduce.setSecondElement(examples);
		stack.push(toInduce);
		
		boolean setSeed=false; // set initial seed for concept refinement
		final OWLDataFactory dataFactory = super.kb.getDataFactory();
		while(!stack.isEmpty()){
			System.out.printf("Stack: %d \n",stack.size());
			 OWLDescription fatherConceptPop = fatherConcepts.isEmpty()?dataFactory.getOWLThing(): (fatherConcepts.pop())  ;
					 
					 //fatherConcepts.pop();  //
			
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
				}
				else { // prior majority of negatives
					currentTree.setRoot(kb.getDataFactory().getOWLNothing()); // set negative leaf
				}

			//		double numPos = posExs.size() + undExs.size()*prPos;
			//		double numNeg = negExs.size() + undExs.size()*prNeg;
			else{
				double numPos = posExs.size();
				double numNeg = negExs.size();
				double perPos = numPos/(numPos+numNeg);
				double perNeg = numNeg/(numPos+numNeg);
//				prPos=perPos;
//				prNeg=perNeg;

				if (perNeg==0 && perPos > Parameters.PURITY_THRESHOLD) { // no negative
					currentTree.setRoot(kb.getDataFactory().getOWLThing()); // set positive leaf

				}
				else{
					if (perPos==0 && perNeg > Parameters.PURITY_THRESHOLD) { // no positive			
						currentTree.setRoot(kb.getDataFactory().getOWLNothing()); // set negative leaf

					}		
					// else (a non-leaf node) ...
					else{
						OWLDescription[] cConcepts= new OWLDescription[0];
						ArrayList<OWLDescription> cConceptsL = op.generateNewConcepts(fatherConceptPop,dim, posExs, negExs, setSeed);
						//						cConceptsL= getRandomSelection(cConceptsL); // random selection of feature set
						setSeed=false;
						
						cConcepts = cConceptsL.toArray(cConcepts);
						// select node concept
						OWLDescription newRootConcept= null;
						if (cConceptsL.size()>1)
						 newRootConcept= Parameters.CCP?(selectBestConceptCCP(cConcepts, posExs, negExs, undExs, prPos, prNeg, truePos, trueNeg)):(selectBestConcept(cConcepts, posExs, negExs, undExs, prPos, prNeg));
						 else{
							newRootConcept= cConcepts[0]; // for the seed
						 }
						
						
						ArrayList<Integer> posExsT = new ArrayList<Integer>();
						ArrayList<Integer> negExsT = new ArrayList<Integer>();
						ArrayList<Integer> undExsT = new ArrayList<Integer>();
						ArrayList<Integer> posExsF = new ArrayList<Integer>();
						ArrayList<Integer> negExsF = new ArrayList<Integer>();
						ArrayList<Integer> undExsF = new ArrayList<Integer>();

						split(newRootConcept, posExs, negExs, undExs, posExsT, negExsT, undExsT, posExsF, negExsF, undExsF);
						// select node concept
						;
						if (fatherConceptPop!=null);{
								newRootConcept= dataFactory.getOWLObjectIntersectionOf(fatherConceptPop,newRootConcept);
						}
						currentTree.setRoot(newRootConcept);		
						
						
						// build subtrees

						//		undExsT = union(undExsT,);
						DLTree posTree= new DLTree();
						DLTree negTree= new DLTree(); // recursive calls simulation
						currentTree.setPosTree(posTree);
						currentTree.setNegTree(negTree);
						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla1 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExsT, negExsT, undExsT, dim, perPos, perNeg);
						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla2 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExsF, negExsF, undExsF, dim, perPos, perNeg);
						Couple<DLTree,Npla<ArrayLis<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> pos= new Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						pos.setFirstElement(posTree);
						pos.setSecondElement(npla1);
						
						// negative branch
						Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> neg= new Couple<DLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						neg.setFirstElement(negTree);
						neg.setSecondElement(npla2);
						// push
						stack.push(neg);
						stack.push(pos);
						fatherConcepts.push(dataFactory.getOWLObjectComplementOf(newRootConcept));
						fatherConcepts.push(newRootConcept);
					}
				}
			}
		}
		System.out.println("Induced tree: "+tree);
		return tree;

	}


	@Override
	public void prune(Integer[] pruningSet, AbstractTree tree,
			AbstractTree subtree) {



		DLTree treeDL= (DLTree) tree;

		Stack<DLTree> stack= new Stack<DLTree>();
		stack.add(treeDL);
		// array list come pila
		double nodes= treeDL.getComplexityMeasure();
		if(nodes>1){
			while(!stack.isEmpty()){
				DLTree current= stack.pop(); // leggo l'albero corrente

				DLTree pos= current.getPosSubTree();
				DLTree neg= current.getNegSubTree();
				System.out.println("Current: "+pos+" ----- "+neg+"visited? "+current.isVisited());

				if(current.isVisited()){
					System.out.println("Valutazione");
					int comissionRoot=current.getCommission();
					int comissionPosTree= pos.getCommission();
					int comissionNegTree= neg.getCommission();
					
					int gainC=comissionRoot-(comissionPosTree+comissionNegTree);

					if(gainC<0){

						int posExs=current.getPos();
						int negExs= current.getNeg();
						// rimpiazzo rispetto alla classe di maggioranza
						if(posExs<=negExs){

							current.setRoot(kb.getDataFactory()	.getOWLNothing());
						}
						else{

							current.setRoot(kb.getDataFactory()	.getOWLThing());
						}

						current.setNegTree(null);
						current.setPosTree(null);	



					}
				}
				else{
					current.setAsVisited();
					stack.push(current); // rimetto in  pila  e procedo alle chiamate ricorsive
					if(pos!=null){
						if((pos.getNegSubTree()!=null)||(pos.getPosSubTree()!=null))
							stack.push(pos);

					}
					if(neg!=null){
						if((neg.getNegSubTree()!=null)||(neg.getPosSubTree()!=null))
							stack.push(neg);

					}
				}

			}				
		}

	}

	public void prunePEP(Integer[] pruningSet, AbstractTree tree,
			AbstractTree subtree) {



		DLTree treeDL= (DLTree) tree;

		Stack<DLTree> stack= new Stack<DLTree>();
		stack.add(treeDL);
		// array list come pila
		
			while(!stack.isEmpty()){
				System.out.println("Print");
				DLTree current= stack.pop(); // leggo l'albero corrente

				List<DLTree> leaves= current.getFoglie();
				System.out.println("Print 2");
				
				   int commissionRoot= current.getCommission();
				   
				   int nExsForLeaves=0;
				   int commissions=0;
				
				   
					for (Iterator iterator = leaves.iterator(); iterator
							.hasNext();) {
						System.out.println("Print");
						DLTree dlTree = (DLTree) iterator.next();
						commissions+=dlTree.getCommission();
						nExsForLeaves=nExsForLeaves+current.getPos()+current.getNeg();
						
						
					} 
					nExsForLeaves+=2; // laplace correction
					commissions+=1;
					int gainC=commissionRoot-commissions;

					if(gainC<0){

						int posExs=current.getPos();
						int negExs= current.getNeg();
						// rimpiazzo rispetto alla classe di maggioranza
						if(posExs<=negExs){

							current.setRoot(kb.getDataFactory()	.getOWLNothing());
						}
						else{

							current.setRoot(kb.getDataFactory()	.getOWLThing());
						}

						current.setNegTree(null);
						current.setPosTree(null);	



					}
				else{
		
					DLTree pos=current.getPosSubTree();
					DLTree neg= current.getNegSubTree();
					if(pos!=null){
		
							stack.push(pos);

					}
					if(neg!=null){
						
							stack.push(neg);

					}
				}

			}				
		

	}
	
	
	
	
	
	
	/**
	 * Implementation of a REP-pruning algorithm for TDT
	 * @param pruningset
	 * @param tree
	 * @param results2
	 * @return
	 */
	public int[] doREPPruning(Integer[] pruningset, DLTree tree, int[] results2){
		// step 1: classification
		System.out.println("Number of Nodes  Before pruning"+ tree.getComplexityMeasure());
		int[] results= new int[pruningset.length];
		//for each element of the pruning set
		for (int element=0; element< pruningset.length; element++){
			//  per ogni elemento del pruning set
			// versione modificata per supportare il pruning
			classifyExampleforPruning(pruningset[element], tree,results2); // classificazione top down

		}

		prune(pruningset, tree, tree);
		System.out.println("Number of Nodes  After pruning"+ tree.getComplexityMeasure());

		return results;
	}
	
	
	public int[] doPEPPruning(Integer[] pruningset, DLTree tree, int[] results2){
		// step 1: classification
		System.out.println("Number of Nodes  Before pruning"+ tree.getComplexityMeasure());
		int[] results= new int[pruningset.length];
		//for each element of the pruning set
		for (int element=0; element< pruningset.length; element++){
			//  per ogni elemento del pruning set
			// versione modificata per supportare il pruning
			classifyExampleforPruning(pruningset[element], tree,results2); // classificazione top down

		}
        System.out.println("Classification for pruning");
		prunePEP(pruningset, tree, tree);
		System.out.println("Number of Nodes  After pruning"+ tree.getComplexityMeasure());

		return results;
	}
	
	
	
	
	
	
	
	





	/**
	 * Ad-hoc implementation for evaluation step in REP-pruning. the method count positive, negative and uncertain instances 
	 * @param indTestEx
	 * @param tree
	 * @param results2
	 * @return
	 */
	public int classifyExampleforPruning(int indTestEx, DLTree tree,int[] results2) {
		Stack<DLTree> stack= new Stack<DLTree>();
		OWLDataFactory dataFactory = kb.getDataFactory();
		stack.add(tree);
		int result=0;
		boolean stop=false;


		if (!Parameters.BINARYCLASSIFICATION){
			while(!stack.isEmpty() && !stop){
				DLTree currentTree= stack.pop();

				OWLDescription rootClass = currentTree.getRoot();
				//			System.out.println("Root class: "+ rootClass);
				if (rootClass.equals(dataFactory.getOWLThing())){
					if (results2[indTestEx]==+1){
						currentTree.setMatch(0);
						currentTree.setPos();
					}
					else if (results2[indTestEx]==-1){
						currentTree.setCommission(0);
						currentTree.setNeg(0);
					}else{
						currentTree.setInduction(0);
						currentTree.setUnd();
					}
					stop=true;
					result=+1;

				}
				else if (rootClass.equals(dataFactory.getOWLNothing())){

					if(results2[indTestEx]==+1){

						currentTree.setPos();
						currentTree.setCommission(0);
					}
					else if (results2[indTestEx]==-1){
						currentTree.setNeg(0);
						currentTree.setMatch(0);
					}
					else{
						currentTree.setUnd();
						currentTree.setInduction(0);
					}
					stop=true;
					result=-1;

				}else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass)){
					if(results2[indTestEx]==+1){
						currentTree.setMatch(0);
						currentTree.setPos();
					}else if (results2[indTestEx]==-1){
						currentTree.setCommission(0);
						currentTree.setNeg(0);
					}else{
						currentTree.setUnd();
						currentTree.setInduction(0);
					}
					stack.push(currentTree.getPosSubTree());

				}
				else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass))){

					if(results2[indTestEx]==+1){
						currentTree.setPos();
						currentTree.setCommission(0);
					}else if(results2[indTestEx]==-1){
						currentTree.setNeg(0);
						currentTree.setMatch(0);
					}else{
						currentTree.setUnd();
						currentTree.setInduction(0);
					}
					stack.push(currentTree.getNegSubTree());

				}
				else {
					if(results2[indTestEx]==+1){
						currentTree.setPos();
						currentTree.setInduction(0);
					}else if(results2[indTestEx]==-1){
						currentTree.setNeg(0);
						currentTree.setInduction(0);
					}else{
						currentTree.setUnd();
						currentTree.setMatch(0);
					}
					stop=true;
					result=0; 

				}
			};
		}else{
			
			while(!stack.isEmpty() && !stop){
				DLTree currentTree= stack.pop();

				OWLDescription rootClass = currentTree.getRoot();
				//			System.out.println("Root class: "+ rootClass);
				if (rootClass.equals(dataFactory.getOWLThing())){
					if(results2[indTestEx]==+1){
						currentTree.setMatch(0);
						currentTree.setPos();
					}
					else{
						currentTree.setCommission(0);
						currentTree.setNeg(0);
					}
					stop=true;
					result=+1;

				}
				else if (rootClass.equals(dataFactory.getOWLNothing())){

					if(results2[indTestEx]==+1){

						currentTree.setPos();
						currentTree.setCommission(0);
					}
					else {
						currentTree.setNeg(0);
						currentTree.setMatch(0);
					}
					
					stop=true;
					result=-1;

				} else {
					boolean hasType;
					try{
					hasType= kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass);
					}catch(Exception e){
						hasType=false;
					}
					if (hasType){
						if(results2[indTestEx]==+1){
							currentTree.setMatch(0);
							currentTree.setPos();
						}else{
							currentTree.setCommission(0);
							currentTree.setNeg(0);
						}
						stack.push(currentTree.getPosSubTree());

					}
					else {

						if(results2[indTestEx]==+1){
							currentTree.setPos();
							currentTree.setCommission(0);
						}else{
							currentTree.setNeg(0);
							currentTree.setMatch(0);
						}
						stack.push(currentTree.getNegSubTree());

					}
				}
				
			};
			
			
		}

		return result;

	}







}



