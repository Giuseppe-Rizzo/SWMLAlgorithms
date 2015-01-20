package classifiers.evidentialAlgorithms;



import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;

import utils.Couple;
import utils.Npla;

import classifiers.evidentialAlgorithms.DempsterShafer.MassFunction;
import classifiers.evidentialAlgorithms.models.DSTDLTree;
import classifiers.evidentialAlgorithms.models.EvidentialModel;
import classifiers.refinementOperator.RefinementOperator;
import classifiers.trees.models.AbstractTree;
import classifiers.trees.models.DLTree;
import evaluation.Parameters;



public class DSTTDTClassifier{
	//	RegolaCombinazione regola;
	static final double THRESHOLD = 0.05;
	static final double M = 3;	

	private KnowledgeBase kb;
	public DSTTDTClassifier(KnowledgeBase kb) {
		this.kb=kb;
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DSTDLTree induceDSTDLTree
	(ArrayList<Integer> posExs, ArrayList<Integer> negExs,	ArrayList<Integer> undExs, 
			int dim, double prPos, double prNeg, RefinementOperator op) {	
		
		if  (op == null){
			try {
				op=  (RefinementOperator)(ClassLoader.getSystemClassLoader().loadClass(Parameters.refinementOperator)).getConstructor(KnowledgeBase.class).newInstance(kb);
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double> examples = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExs, negExs, undExs, dim, prPos, prNeg);
		DSTDLTree tree = new DSTDLTree(); // new (sub)tree
		Stack<Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>> stack= new Stack<Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>>();
		Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> toInduce= new Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
		toInduce.setFirstElement(tree);
		toInduce.setSecondElement(examples);
		stack.push(toInduce);



		while (!stack.isEmpty()){


			// pop from the stack
			Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> current= stack.pop(); // extract the next element
			Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double> currentExamples= current.getSecondElement();
			// set of negative, positive and undefined example
			posExs=currentExamples.getFirst();
			negExs=currentExamples.getSecond();
			undExs=currentExamples.getThird();
			DSTDLTree currentTree= current.getFirstElement();

			int psize = posExs.size();
			int nsize = negExs.size();
			int usize = undExs.size();
			System.out.printf("Learning problem\t p:%d\t n:%d\t u:%d\t prPos:%4f\t prNeg:%4f\n", 
					psize, nsize, usize, prPos, prNeg);



			//build the BBA for the current node
			ArrayList<Integer> frame = new ArrayList<Integer>();
			frame.add(-1);
			frame.add(1);
			MassFunction mass= new MassFunction(frame);
			ArrayList<Integer> positive= new ArrayList<Integer>();
			positive.add(1);
			double positiveValue = (double)psize/(psize+ nsize+usize);
			if( (psize+ nsize+usize)==0){
				positiveValue= prPos;
			}
			mass.setValues(positive, positiveValue);
			ArrayList<Integer> negative= new ArrayList<Integer>();
			negative.add(-1);
			double negativeValue = (double)nsize/(psize+ nsize+usize);
			if( (psize+ nsize+usize)==0){
				negativeValue= prNeg;
			}
			mass.setValues(negative, negativeValue);
			double undValue = ((double)usize/(psize+ nsize+usize));

			if( (psize+ nsize+usize)==0){
				undValue= 0;
			}
			mass.setValues(frame, undValue);
			OWLDataFactory dataFactory = kb.getDataFactory();

			//	System.out.println("MASS: "+ positiveValue +", "+negativeValue+", "+undValue);
			//  ragionamento sui prior

			if (psize == 0 && nsize == 0) // no exs
				if (prPos >= prNeg) {
					//			System.out.println("8======D");// prior majority of positives
					currentTree.setRoot(dataFactory.getOWLThing(), mass); // set positive leaf
					//					return tree;
				}
				else { // prior majority of negatives
					currentTree.setRoot(dataFactory.getOWLNothing(),mass); // set negative leaf
					//					return tree;
				}
			else{
				//	double numPos = posExs.size() + undExs.size()*prPos;
				//	double numNeg = negExs.size() + undExs.size()*prNeg;
				double numPos = psize;
				double numNeg = nsize;
				double perPos = numPos/(numPos+numNeg);
				double perNeg = numNeg/(numPos+numNeg);
				if (perNeg==0 && perPos > THRESHOLD) { // no negative
					//			System.out.println("Thing as leaf");
					currentTree.setRoot(dataFactory.getOWLThing(), mass); // set positive lea
					//				return tree;
				}
				else if (perPos==0 && perNeg > THRESHOLD) { // no positive	
					//				System.out.println("NoThing as leaf");
					currentTree.setRoot(dataFactory.getOWLNothing(), mass); // set negative leaf
					//					return tree;
				}		
				else{

					if (!Parameters.nonspecificityControl){
						
								
							ArrayList<OWLDescription> generateNewConcepts = op.generateNewConcepts(Parameters.beam, posExs, negExs); // genera i concetti sulla base degli esempi
							OWLDescription[] cConcepts = new OWLDescription[generateNewConcepts.size()];
							
							cConcepts= generateNewConcepts.toArray(cConcepts);
							
							//	OWLDescription[] cConcepts = allConcepts;

						// select node couoncept
						Couple<OWLDescription,MassFunction> newRootConcept = selectBestConceptDST(cConcepts, posExs, negExs, undExs, prPos, prNeg);
						MassFunction refinementMass = newRootConcept.getSecondElement();

						System.out.println(newRootConcept.getFirstElement()+"----"+refinementMass);	
						ArrayList<Integer> posExsT = new ArrayList<Integer>();;
						ArrayList<Integer> negExsT =new ArrayList<Integer>();;
						ArrayList<Integer> undExsT =new ArrayList<Integer>();;
						ArrayList<Integer> posExsF =new ArrayList<Integer>();;
						ArrayList<Integer> negExsF =new ArrayList<Integer>();;
						ArrayList<Integer> undExsF = new ArrayList<Integer>();;


						split(newRootConcept.getFirstElement(), posExs, negExs, undExs, posExsT, negExsT, undExsT, posExsF, negExsF, undExsF);
						// select node concept

						currentTree.setRoot(newRootConcept.getFirstElement(), refinementMass);		

						//	undExsT = union(undExsT,
						//						tree.setPosTree(induceDSTDLTree(posExsT, negExsT, undExsT, dim, prPos, prNeg));
						//						tree.setNegTree(induceDSTDLTree(posExsF, negExsF, undExsF, dim, prPos, prNeg));

						DSTDLTree posTree= new DSTDLTree();
						DSTDLTree negTree= new DSTDLTree(); // recursive calls simulation
						currentTree.setPosTree(posTree);
						currentTree.setNegTree(negTree);
						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla1 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExsT, negExsT, undExsT, dim, perPos, perNeg);
						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla2 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExsF, negExsF, undExsF, dim, perPos, perNeg);
						Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> pos= new Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						pos.setFirstElement(posTree);
						pos.setSecondElement(npla1);
						// negative branch
						Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> neg= new Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						neg.setFirstElement(negTree);
						neg.setSecondElement(npla2);
						stack.push(neg);
						stack.push(pos);




					}
					else if(mass.getNonSpecificity()<0.1){
						OWLDescription[] cConcepts = generateNewConcepts(Parameters.beam, posExs, negExs); // genera i concetti sulla base degli esempi
						//	OWLDescription[] cConcepts = allConcepts;

						// select node couoncept
						Couple<OWLDescription,MassFunction> newRootConcept = selectBestConceptDST(cConcepts, posExs, negExs, undExs, prPos, prNeg);
						MassFunction refinementMass = newRootConcept.getSecondElement();

						System.out.println(newRootConcept.getFirstElement()+"----"+refinementMass);	
						ArrayList<Integer> posExsT = new ArrayList<Integer>();;
						ArrayList<Integer> negExsT =new ArrayList<Integer>();;
						ArrayList<Integer> undExsT =new ArrayList<Integer>();;
						ArrayList<Integer> posExsF =new ArrayList<Integer>();;
						ArrayList<Integer> negExsF =new ArrayList<Integer>();;
						ArrayList<Integer> undExsF = new ArrayList<Integer>();;


						split(newRootConcept.getFirstElement(), posExs, negExs, undExs, posExsT, negExsT, undExsT, posExsF, negExsF, undExsF);
						// select node concept

						tree.setRoot(newRootConcept.getFirstElement(), refinementMass);		

						//	undExsT = union(undExsT,
						//						tree.setPosTree(induceDSTDLTree(posExsT, negExsT, undExsT, dim, prPos, prNeg));
						//						tree.setNegTree(induceDSTDLTree(posExsF, negExsF, undExsF, dim, prPos, prNeg));


						DSTDLTree posTree= new DSTDLTree();
						DSTDLTree negTree= new DSTDLTree(); // recursive calls simulation
						currentTree.setPosTree(posTree);
						currentTree.setNegTree(negTree);
						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla1 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExsT, negExsT, undExsT, dim, perPos, perNeg);
						Npla<ArrayList<Integer>, ArrayList<Integer>, ArrayList<Integer>, Integer, Double, Double> npla2 = new Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>(posExsF, negExsF, undExsF, dim, perPos, perNeg);
						Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> pos= new Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						pos.setFirstElement(posTree);
						pos.setSecondElement(npla1);
						// negative branch
						Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>> neg= new Couple<DSTDLTree,Npla<ArrayList<Integer>,ArrayList<Integer>,ArrayList<Integer>, Integer, Double, Double>>();
						neg.setFirstElement(negTree);
						neg.setSecondElement(npla2);
						stack.push(neg);
						stack.push(pos);




					}else{

						if (perPos > perNeg) { // no negative
							currentTree.setRoot(dataFactory.getOWLThing(), mass); // set positive leaf
							//					return tree;
						}
						else {// no positive			
							currentTree.setRoot(dataFactory.getOWLNothing(), mass); // set negative leaf
							//					return tree;
						}	

					}

				}
			}	

		}


		return tree;
	}





	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void classifyExampleDST(List<Couple<Integer,MassFunction<Integer>>> list,int indTestEx, DSTDLTree tree, OWLDescription...testConcepts ) {

		
		//	System.out.println("BBA "+m);

		Stack<DSTDLTree> stack= new Stack<DSTDLTree>();
		stack.push(tree);
		
		OWLDataFactory dataFactory = kb.getDataFactory();
		while (!stack.isEmpty()){
			
			DSTDLTree currenttree=stack.pop();
			OWLDescription rootClass = currenttree.getRoot(); 
			MassFunction m= currenttree.getRootBBA();
		if (rootClass.equals(dataFactory.getOWLThing())){
			//		System.out.println("Caso 1");
			Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
			result.setFirstElement(+1);
			result.setSecondElement(m);
			list.add(result);
			//		if(testConcepts!=null){
			//			if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testConcepts[0]))
			//				tree.addPosExample(indTestEx);
			//			else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testConcepts[0])))
			//				tree.addNegExample(indTestEx);
			//			else{
			//				tree.addPosExample(indTestEx);
			//				tree.addNegExample(indTestEx);
			//			}
			//		}
		}
		else if (rootClass.equals(dataFactory.getOWLNothing())){
			//		System.out.println("Caso 2");
			Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
			result.setFirstElement(-1);
			result.setSecondElement(m);
			list.add(result);

		}		
		else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass)){
			//System.out.println(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass));
			if (currenttree.getPosSubTree()!=null){

//				classifyExampleDST( list, indTestEx, tree.getPosSubTree());	
				stack.push(currenttree.getPosSubTree());
				//			System.out.println("------");
			}
			else{
				//			System.out.println("Caso 4");
				Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
				result.setFirstElement(+1);
				result.setSecondElement(m);
				list.add(result);
				//			System.out.println("ADdded");
			}
		}
		else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass))){
				//			System.out.println(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass)));
				if (currenttree.getNegSubTree()!=null){
//					classifyExampleDST(list,indTestEx, tree.getNegSubTree());
					stack.push(currenttree.getNegSubTree());
					//				System.out.println("#######");
				}
				else{
					//				System.out.println("Caso 6"+ tree);
					Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
					result.setFirstElement(-1);
					result.setSecondElement(m);
					list.add(result);
					//				System.out.println("ADdded");
				}
			}
			else{
				//seguo entrambi i percorsi
				if (currenttree.getPosSubTree()!=null){
					//				System.out.println("Caso 7");

//					classifyExampleDST(list, indTestEx, tree.getPosSubTree());
					
					stack.push(currenttree.getPosSubTree());
				}
				else{
					//				System.out.println("Caso 8");
					Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
					result.setFirstElement(+1);
					result.setSecondElement(m);
					list.add(result);	
					//				System.out.println("ADdded");
				}
				System.out.println("---->");
				if (currenttree.getNegSubTree()!=null){
					//				System.out.println("Caso 9");
//					classifyExampleDST(list,indTestEx, tree.getNegSubTree());
					stack.push(currenttree.getNegSubTree());
				}
				else{
					Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
					result.setFirstElement(-1);
					result.setSecondElement(m);
					list.add(result);
					//					System.out.println("ADdded");
				}

			}

		
		}
		//	System.out.println("Tree "+ tree);
	}






	@SuppressWarnings({ })
	public void classifyExamplesDST(int indTestEx, DSTDLTree[] trees, int[] results, OWLDescription[] testConcepts) {
		int length = testConcepts!=null?testConcepts.length:1;
		for (int c=0; c < length; c++) {
			MassFunction<Integer> bba = getBBA(indTestEx, trees[c]);// combino con tutte le altre BBA
			predict(results, c, bba);

		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MassFunction<Integer> getBBA(int indTestEx, EvidentialModel tree) {
		DSTDLTree model= (DSTDLTree) tree;
		ArrayList<Couple<Integer, MassFunction<Integer>>> list;
		System.out.println("Tree \n"+ model);
		list= new  ArrayList<Couple<Integer,MassFunction<Integer>>>();
		classifyExampleDST(list,indTestEx, model);
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

		}
		return bba;
	}





	/**
	 * Implements the startegy to choose the class label
	 * @param results
	 * @param c
	 * @param bba
	 */
	private void predict(int[] results, int c, MassFunction<Integer> bba) {
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
			
			// commentare e decommentare qui per la classificazione con e senza forcing			
			//results[c]=0;

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


	@SuppressWarnings("rawtypes")
	private  Couple<OWLDescription, MassFunction> selectBestConceptDST(OWLDescription[] concepts,
			ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs, 
			double prPos, double prNeg) {

		int[] counts;

		int bestConceptIndex = 0;

		counts = getSplitCounts(concepts[0], posExs, negExs, undExs);
		System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
				"#"+0, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);

		//		double bestGain = gain(counts, prPos, prNeg);
		//  introduzione della mbisura di non specificit�
		int posExs2 = counts[0] + counts[1];
		int negExs2 = counts[3] + counts[4];
		int undExs2 = counts[6] + counts[7] + counts[2] + counts[5];
		System.out.println("Split: "+posExs2 +"---"+negExs2+"--"+undExs2);
		MassFunction<Integer> bestBba = getBBA(posExs2,negExs2,undExs2);

		double bestNonSpecificity = bestBba.getNonSpecificity();
		bestBba.getConfusionMeasure();
		System.out.printf("%+10e\n",bestNonSpecificity);

		System.out.println(concepts[0]);

		for (int c=1; c<concepts.length; c++) {

			counts = getSplitCounts(concepts[c], posExs, negExs, undExs);
			System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
					"#"+c, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);
			MassFunction<Integer> thisbba = getBBA(counts[0] + counts[1],counts[3] + counts[4],counts[6] + counts[7] + counts[2] + counts[5]);
			double thisNonSpecificity = thisbba.getNonSpecificity();
			thisbba.getGlobalUncertaintyMeasure();
			System.out.printf("%+10e\n",thisNonSpecificity);
			System.out.printf("%+10e\n",thisNonSpecificity);
			System.out.println(concepts[c]);
			if(thisNonSpecificity < bestNonSpecificity) {
				//			if(thisGlobalUncMeasure < bestTotaluncertaintyMeasure) {
				bestConceptIndex = c;
				bestNonSpecificity = thisNonSpecificity;
				bestBba= thisbba;
			}
		}

		System.out.printf("best gain: %f \t split #%d\n", bestNonSpecificity, bestConceptIndex);
		Couple<OWLDescription,MassFunction> name = new Couple<OWLDescription,MassFunction>();
		name.setFirstElement(concepts[bestConceptIndex]);
		name.setSecondElement(bestBba);
		return name;
	}


	private int[] getSplitCounts(OWLDescription concept, 
			ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs) {

		int[] counts = new int[9];
		System.out.println(concept+"-"+posExs+"-"+negExs+"-"+undExs);
		ArrayList<Integer> posExsT = new ArrayList<Integer>();
		ArrayList<Integer> negExsT = new ArrayList<Integer>();
		ArrayList<Integer> undExsT = new ArrayList<Integer>();

		ArrayList<Integer> posExsF = new ArrayList<Integer>();
		ArrayList<Integer> negExsF = new ArrayList<Integer>();
		ArrayList<Integer> undExsF = new ArrayList<Integer>();

		ArrayList<Integer> posExsU = new ArrayList<Integer>();
		ArrayList<Integer> negExsU = new ArrayList<Integer>();
		ArrayList<Integer> undExsU = new ArrayList<Integer>();

		splitGroup(concept,posExs,posExsT,posExsF,posExsU);
		splitGroup(concept,negExs,negExsT,negExsF,negExsU);	
		splitGroup(concept,undExs,undExsT,undExsF,undExsU);	

		counts[0] = posExsT.size(); 
		counts[1] = negExsT.size(); 
		counts[2] = undExsT.size(); 
		counts[3] = posExsF.size(); 
		counts[4] = negExsF.size();
		counts[5] = undExsF.size();
		counts[6] = posExsU.size(); 
		counts[7] = negExsU.size();
		counts[8] = undExsU.size();

		return counts;

	}


	private  void split(OWLDescription concept,
			ArrayList<Integer> posExs,  ArrayList<Integer> negExs,  ArrayList<Integer> undExs,
			ArrayList<Integer> posExsT, ArrayList<Integer> negExsT,	ArrayList<Integer> undExsT, 
			ArrayList<Integer> posExsF,	ArrayList<Integer> negExsF, ArrayList<Integer> undExsF) {

		ArrayList<Integer> posExsU = new ArrayList<Integer>();
		ArrayList<Integer> negExsU = new ArrayList<Integer>();
		ArrayList<Integer> undExsU = new ArrayList<Integer>();

		splitGroup(concept,posExs,posExsT,posExsF,posExsU);
		splitGroup(concept,negExs,negExsT,negExsF,negExsU);
		splitGroup(concept,undExs,undExsT,undExsF,undExsU);	

	}


	private void splitGroup(OWLDescription concept, ArrayList<Integer> nodeExamples,
			ArrayList<Integer> trueExs, ArrayList<Integer> falseExs, ArrayList<Integer> undExs) {
		OWLDescription negConcept = kb.getDataFactory().getOWLObjectComplementOf(concept);

		for (int e=0; e<nodeExamples.size(); e++) {
			int exIndex = nodeExamples.get(e);
			if (kb.getReasoner().hasType(kb.getIndividuals()[exIndex], concept))
				trueExs.add(exIndex);
			else if (kb.getReasoner().hasType(kb.getIndividuals()[exIndex], negConcept))
				falseExs.add(exIndex);
			else
				undExs.add(exIndex);		
		}	
	}


	private OWLDescription[] generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {

		System.out.printf("Generating node concepts ");
		OWLDescription[] rConcepts = new OWLDescription[dim];
		OWLDescription newConcept;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				newConcept = kb.getRandomConcept();

				Set<OWLIndividual> individuals = (kb.getReasoner()).getIndividuals(newConcept, false);
				Iterator<OWLIndividual> instIterator = individuals.iterator();
				while (emptyIntersection && instIterator.hasNext()) {
					OWLIndividual nextInd = (OWLIndividual) instIterator.next();
					int index = -1;
					for (int i=0; index<0 && i<kb.getIndividuals().length; ++i)
						if (nextInd.equals(kb.getIndividuals()[i])) index = i;
					if (posExs.contains(index))
						emptyIntersection = false;
					else if (negExs.contains(index))
						emptyIntersection = false;
				}					
			} while (emptyIntersection);;
			rConcepts[c] = newConcept;
			System.out.printf("%d ", c);
		}
		System.out.println();

		return rConcepts;
	}

	public MassFunction<Integer> getBBA(int posExs, int negExs,int undExs) {
		ArrayList<Integer> set = new ArrayList<Integer>();
		set.add(-1);
		set.add(1);
		MassFunction<Integer> mass= new MassFunction<Integer>(set);
		ArrayList<Integer> positive= new ArrayList<Integer>();
		positive.add(1);
		mass.setValues(positive,(double) posExs/(posExs+ negExs+undExs));
		ArrayList<Integer> negative= new ArrayList<Integer>();
		negative.add(-1);
		mass.setValues(negative, (double)negExs/(posExs+ negExs+undExs));
		mass.setValues(set, (double)undExs/(posExs+ negExs+undExs));

		return mass;

	}




	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void prune(Integer[] pruningSet, AbstractTree tree, AbstractTree subtree,OWLDescription testConcept){
		DSTDLTree treeDST= (DSTDLTree) tree;
		Stack<DSTDLTree> stack= new Stack<DSTDLTree>();
		stack.add(treeDST);
		// array list come pila
		double nodes= treeDST.getComplexityMeasure();
		if(nodes>1){
			while(!stack.isEmpty()){
				DSTDLTree current= stack.pop(); // leggo l'albero corrente

				System.out.println("Current: "+current);
				DSTDLTree pos= current.getPosSubTree();
				DSTDLTree neg= current.getNegSubTree();
				System.out.println("Current: "+pos+" ----- "+neg+"visited? "+current.isVisited());

				if(current.isVisited()){
					int comissionRoot=current.getCommission();
					int comissionPosTree= pos.getCommission();
					int comissionNegTree= neg.getCommission();
					int omissionRoot=current.getOmission();
					int omissionPosTree= pos.getOmission();
					int omissionNegTree= neg.getOmission();
					int inductionRoot=current.getInduction();
					int inductionPosTree= pos.getInduction();
					int inductionNegTree= neg.getInduction();

					int gainC=comissionRoot-(comissionPosTree+comissionNegTree);
					int gainO=omissionRoot-(omissionPosTree+omissionNegTree);
					int gainI=inductionRoot-(inductionPosTree+inductionNegTree);
					if((gainC==0)&&(gainO==0)&&(gainI<0)){

						MassFunction bba=current.getRootBBA();
						ArrayList<Integer> memership= new ArrayList<Integer>();
						memership.add(+1);
						double belC = bba.calcolaConfirmationFunction(memership);
						////								double confirmationFunctionValuePos = bba.calcolaBeliefFunction(ipotesi);
						//								// not concept
						ArrayList<Integer> nonmemership= new ArrayList<Integer>();
						nonmemership.add(-1);
						double belNonC = bba.calcolaBeliefFunction(nonmemership);
						bba.calcolaBeliefFunction(nonmemership);
						ArrayList<Integer> unkown= new ArrayList<Integer>();
						unkown.add(-1);
						unkown.add(+1);
						MassFunction newm= new MassFunction(unkown);
						//							 	// rimpiazzo rispetto alla classe di maggioranza
						if(belC<=belNonC){

							newm.setValues(nonmemership, (bba.getValue(memership)));
							newm.setValues(memership, bba.getValue(nonmemership));
							newm.setValues(unkown, bba.getValue(unkown));
							current.setRoot(kb.getDataFactory().getOWLObjectComplementOf(current.getRoot()),bba);
						}
						else{

							current.setRoot(current.getRoot(),bba);
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


	public int[] doREPPruning(Integer[] pruningset, DSTDLTree tree, OWLDescription testconcept){
		// step 1: classification

		System.out.println("Number of Nodes  Before pruning"+ tree.getComplexityMeasure());
		int[] results= new int[pruningset.length];
		List<Couple<Integer, MassFunction<Integer>>> list=null;
		//for each element of the pruning set
		for (int element=0; element< pruningset.length; element++){
			//  per ogni elemento del pruning set
			list= new ArrayList<Couple<Integer,MassFunction<Integer>>>();
			// versione modificata per supportare il pruning
			classifyExampleDSTforPruning(list,pruningset[element], tree,testconcept); // classificazione top down

		}

		prune(pruningset, tree, tree, testconcept);

		System.out.println("Number of Nodes  After pruning"+ tree.getComplexityMeasure());


		return results;
	}




	/**
	 * An ad-hoc modified classification procedure to support REP procedure 
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void classifyExampleDSTforPruning(List<Couple<Integer,MassFunction<Integer>>> list,int indTestEx, DSTDLTree tree, OWLDescription testconcept) {
		System.out.println(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept));
		System.out.printf(tree+ "[%d %d %d %d] \n", tree.getMatch(), tree.getCommission(),tree.getOmission(),tree.getInduction());
		OWLDescription rootClass = tree.getRoot(); 
		MassFunction m= tree.getRootBBA();
		//		System.out.println("BBA "+m);
		//		System.out.printf("%d %d %d %d \n", tree.getMatch(), tree.getCommission(),tree.getOmission(),tree.getInduction());
		OWLDataFactory dataFactory = kb.getDataFactory();
		if (rootClass.equals(dataFactory.getOWLThing())){
			//			System.out.println("Caso 1");
			Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
			result.setFirstElement(+1);
			result.setSecondElement(m);
			list.add(result);
			if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept))
				tree.setMatch(0);
			else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept)))
				tree.setCommission(0);
			else{
				tree.setInduction(0);
			}
			//
			//			}
		}

		if (rootClass.equals(dataFactory.getOWLNothing())){
			//			System.out.println("Caso 2");
			System.out.println("++++"+rootClass.equals(dataFactory.getOWLNothing()));
			Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
			result.setFirstElement(-1);
			result.setSecondElement(m);
			list.add(result);
			if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept)){
				System.out.println("c");
				tree.setCommission(0);
			}
			else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept))){
				System.out.println("+m");
				tree.setMatch(0);
			}
			else{
				System.out.println("i");
				tree.setInduction(0);
			}
			//			System.out.printf(tree+"%d %d %d %d \n", tree.getMatch(), tree.getCommission(),tree.getOmission(),tree.getInduction());
		}

		if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass)){
			//System.out.println(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass));
			if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept))
				tree.setMatch(0);
			else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept)))
				tree.setCommission(0);
			else{
				tree.setInduction(0);
			}

			if (tree.getPosSubTree()!=null){				
				classifyExampleDSTforPruning( list, indTestEx, tree.getPosSubTree(), testconcept);	
				//				System.out.println("------");
			}
			else{
				//				System.out.println("Caso 4");
				Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
				result.setFirstElement(+1);
				result.setSecondElement(m);
				list.add(result);
				//				System.out.println("ADdded");
			}
		}
		else
			if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass))){
				//				System.out.println(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass)));
				if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept))
					tree.setCommission(0);
				else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept)))
					tree.setMatch(0);
				else{
					tree.setInduction(0);
				}
				//				System.out.printf(tree+"%d %d %d %d \n", tree.getMatch(), tree.getCommission(),tree.getOmission(),tree.getInduction());
				if (tree.getNegSubTree()!=null){
					//					System.out.println("Caso 5");
					classifyExampleDSTforPruning(list,indTestEx, tree.getNegSubTree(), testconcept);
					//					System.out.println("#######");
				}
				else{
					//					System.out.println("Caso 6"+ tree);
					Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
					result.setFirstElement(-1);
					result.setSecondElement(m);
					list.add(result);
					//					System.out.println("ADdded");
				}
			}
			else{
				//seguo entrambi i percorsi
				System.out.println("---->");

				if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept))
					tree.setOmission(0);
				else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept)))
					tree.setOmission(0);
				else{
					tree.setMatch(0);
				}


				if (tree.getPosSubTree()!=null){
					//					System.out.println("Caso 7");
					//					m1=tree.getPosSubTree().getRootBBA();
					classifyExampleDSTforPruning(list, indTestEx, tree.getPosSubTree(), testconcept);
				}
				else{
					//					System.out.println("Caso 8");
					Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
					result.setFirstElement(+1);
					result.setSecondElement(m);
					list.add(result);	
					//					//					System.out.println("ADdded");
				}
				System.out.println("---->");
				if (tree.getNegSubTree()!=null){
					//			neg		System.out.println("Caso 9");
					//					m2=tree.getNegSubTree().getRootBBA();
					classifyExampleDSTforPruning(list,indTestEx, tree.getNegSubTree(), testconcept);
				}
				else{
					Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
					result.setFirstElement(-1);
					result.setSecondElement(m);
					list.add(result);
					//						System.out.println("ADdded");
				}

			}

		//		System.out.println("Tree "+ tree);
	}




}


