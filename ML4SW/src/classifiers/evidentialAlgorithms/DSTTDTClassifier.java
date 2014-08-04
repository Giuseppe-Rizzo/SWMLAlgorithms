package classifiers.evidentialAlgorithms;



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

import classifiers.evidentialAlgorithms.DempsterShafer.MassFunction;
import classifiers.evidentialAlgorithms.models.DSTDLTree;



public class DSTTDTClassifier {
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
			int dim, double prPos, double prNeg) {		
		int psize = posExs.size();
		int nsize = negExs.size();
		int usize = undExs.size();
		System.out.println(psize+ nsize+usize);
		System.out.printf("Learning problem\t p:%d\t n:%d\t u:%d\t prPos:%4f\t prNeg:%4f\n", 
				psize, nsize, usize, prPos, prNeg);

		DSTDLTree tree = new DSTDLTree(); // new (sub)tree
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
				tree.setRoot(dataFactory.getOWLThing(), mass); // set positive leaf
				return tree;
			}
			else { // prior majority of negatives
				tree.setRoot(dataFactory.getOWLNothing(),mass); // set negative leaf
				return tree;
			}

		//	double numPos = posExs.size() + undExs.size()*prPos;
		//	double numNeg = negExs.size() + undExs.size()*prNeg;
		double numPos = psize;
		double numNeg = nsize;
		double perPos = numPos/(numPos+numNeg);
		double perNeg = numNeg/(numPos+numNeg);
		if (perNeg==0 && perPos > THRESHOLD) { // no negative
			//			System.out.println("Thing as leaf");
			tree.setRoot(dataFactory.getOWLThing(), mass); // set positive lea
			return tree;
		}
		else 
			if (perPos==0 && perNeg > THRESHOLD) { // no positive	
				//				System.out.println("NoThing as leaf");
				tree.setRoot(dataFactory.getOWLNothing(), mass); // set negative leaf
				return tree;
			}		
		// else (a non-leaf node) ...
		if(mass.getNonSpecificity()<0.1){
			OWLDescription[] cConcepts = generateNewConcepts(dim, posExs, negExs); // genera i concetti sulla base degli esempi
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
			tree.setPosTree(induceDSTDLTree(posExsT, negExsT, undExsT, dim, prPos, prNeg));
			tree.setNegTree(induceDSTDLTree(posExsF, negExsF, undExsF, dim, prPos, prNeg));
		}else{

			if (perPos > perNeg) { // no negative
				tree.setRoot(dataFactory.getOWLThing(), mass); // set positive leaf
				System.out.println("true");
				return tree;
			}
			else {// no positive			
				tree.setRoot(dataFactory.getOWLNothing(), mass); // set negative leaf
				System.out.println("false");
				return tree;
			}		
		}	
		return tree;
	}

	



@SuppressWarnings({ "unchecked", "rawtypes" })
private void classifyExampleDST(List<Couple<Integer,MassFunction<Integer>>> list,int indTestEx, DSTDLTree tree, OWLDescription...testConcepts ) {

	OWLDescription rootClass = tree.getRoot(); 
	MassFunction m= tree.getRootBBA();
	//	System.out.println("BBA "+m);

	OWLDataFactory dataFactory = kb.getDataFactory();
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
	if (rootClass.equals(dataFactory.getOWLNothing())){
		//		System.out.println("Caso 2");
		Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
		result.setFirstElement(-1);
		result.setSecondElement(m);
		list.add(result);
//		if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testConcepts[0]))
//			tree.addPosExample(indTestEx);
//		else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testConcepts[0])))
//			tree.addNegExample(indTestEx);
//		else{
//			tree.addPosExample(indTestEx);
//			tree.addNegExample(indTestEx);
//		}

	}

	if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass)){
		//System.out.println(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass));
		if (tree.getPosSubTree()!=null){
			//			System.out.println("Caso 3");
//			if(testConcepts!=null){
//				if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testConcepts[0]))
//					tree.addPosExample(indTestEx);
//				else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testConcepts[0])))
//					tree.addNegExample(indTestEx);
//				else{
//					tree.addPosExample(indTestEx);
//					tree.addNegExample(indTestEx);
//				}
//			}
				classifyExampleDST( list, indTestEx, tree.getPosSubTree());	
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
		else
			if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass))){
				//			System.out.println(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass)));
				if (tree.getNegSubTree()!=null){
					classifyExampleDST(list,indTestEx, tree.getNegSubTree());
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
				System.out.println("---->");

				if (tree.getPosSubTree()!=null){
					//				System.out.println("Caso 7");

					classifyExampleDST(list, indTestEx, tree.getPosSubTree());
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
				if (tree.getNegSubTree()!=null){
					//				System.out.println("Caso 9");
					classifyExampleDST(list,indTestEx, tree.getNegSubTree());
				}
				else{
					Couple<Integer,MassFunction<Integer>> result=new Couple<Integer,MassFunction<Integer>>();
					result.setFirstElement(-1);
					result.setSecondElement(m);
					list.add(result);
					//					System.out.println("ADdded");
				}

			}
	
	//	System.out.println("Tree "+ tree);
}
	
	
	
	

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void classifyExamplesDST(int indTestEx, DSTDLTree[] trees, int[] results, OWLDescription[] testConcepts) {
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
		//  introduzione della mbisura di non specificità
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
		for(int i=0; i<counts.length;i++)
			System.out.println(counts[i]);
		
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
	public void prune(Integer[] pruningSet, DSTDLTree tree, DSTDLTree subtree,OWLDescription testConcept){
		Stack<DSTDLTree> stack= new Stack<DSTDLTree>();
		stack.add(tree);
		// array list come pila
		double nodes= tree.getNodi();
		if(nodes>1){
		while(!stack.isEmpty()){
			DSTDLTree current= stack.pop(); // leggo l'albero corrente

			System.out.println("Current: "+current);
			DSTDLTree pos= current.getPosSubTree();
			DSTDLTree neg= current.getNegSubTree();
			System.out.println("Current: "+pos+" ----- "+neg+"visited? "+current.isVisited());

			if(current.isVisited()){
				System.out.println("Valutazione");
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

	System.out.println("Tree: "+tree);
}
	
	
	public DSTDLTree searchFather(OWLDescription d, DSTDLTree tree){
		
		if(tree.getRoot()==null){
			return null;
			
		}
		else {
			
			ArrayList<DSTDLTree> lista= new ArrayList<DSTDLTree>();
			lista.add(tree); // inizializzazione
			
			while(!lista.isEmpty()){
				DSTDLTree current= lista.get(0);
				lista.remove(0);
					DSTDLTree pos= current.getPosSubTree();
					if(pos!=null){
						if(pos.getPosSubTree()!=null)
							if(pos.getPosSubTree().getRoot().equals(d))
								return pos.getPosSubTree();
							else
								lista.add(pos.getPosSubTree());
						if(pos.getNegSubTree()!=null)
							if(pos.getNegSubTree().getRoot().equals(d))
								return pos.getNegSubTree();
							else
								lista.add(pos.getNegSubTree());
					}
						
					
					DSTDLTree neg= current.getNegSubTree();
					
						if(neg!=null){
						if(neg.getPosSubTree()!=null)
							if(neg.getPosSubTree().getRoot().equals(d))
								return pos.getPosSubTree();
							else
								lista.add(neg.getPosSubTree());
						if(neg.getNegSubTree()!=null)
							if(neg.getNegSubTree().getRoot().equals(d))
								return neg.getNegSubTree();
							else
								lista.add(neg.getNegSubTree());
					
				}
			}
			
			return null;
		}
			
		
		
	}
	
	public void repBasedPruning(Integer[] pruningSet, DSTDLTree tree, OWLDescription testConcept){
//		System.out.println("Pruning Set"+ pruningSet.length);
//		
//		System.out.println(tree);
//		// we need to iterate the procedure of pruning until there is only the root of the tree or pruning is not applicable
//		boolean pruningPerformed= true;
//		DSTDLTree prunedTre=null;
//		while (pruningPerformed){
//			prunedTre = prune(pruningSet,tree, testConcept);
//			System.out.println(tree);
//			 if ((prunedTre.getPosSubTree()==null) &&(prunedTre.getNegSubTree()==null)){
//				 // root
//				 pruningPerformed= false;
//			 }else if(!(prunedTre.equals(tree))){
//				 // if the pruning has not been performed
//					 tree=prunedTre;
//			 }
//			 else {
//				 pruningPerformed=true;
//			 }
//			
//			
//		}
	}
	
	public int[] evaluationForPruning(Integer[] pruningset, DSTDLTree tree, OWLDescription testconcept){
		// step 1: classification
		int[] results= new int[pruningset.length];
		List<Couple<Integer, MassFunction<Integer>>> list=null;
		//for each element of the pruning set
		for (int element=0; element< pruningset.length; element++){
			//  per ogni elemento del pruning set
			list= new ArrayList<Couple<Integer,MassFunction<Integer>>>();
			System.out.println("Classifing: "+pruningset[element]);
			// versione modificata per supportare il pruning
			classifyExampleDSTforPruning(list,pruningset[element], tree,testconcept); // classificazione top down
			System.out.println("************************************************");

		}
		
		prune(pruningset, tree, tree, testconcept);
		
		
		

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
//			System.out.printf(tree+"%d %d %d %d \n", tree.getMatch(), tree.getCommission(),tree.getOmission(),tree.getInduction());
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
//					double confirmationFunctionValueUnc = pooled.calcolaConfirmationFunction(ipotesi3);
////				double confirmationFunctionValueUnc = bba.calcolaBeliefFunction(ipotesi3);
//			
//				System.out.println(confirmationFunctionValuePos+ " vs. "+ confirmationFunctionValueNeg+ "vs." +confirmationFunctionValueUnc);
//
//				
//				}
				
				if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept))
					tree.setOmission(0);
				else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept)))
					tree.setOmission(0);
				else{
					tree.setMatch(0);
				}
				
				
				
				// per trattare il mondo aperto al nodo tramite la Dempster-Shafer 
				// aggrego tutte le BBA delle foglie raggiunte fino a  quel momento + quella del nuovo nodo che considererò come foglia
				// in questo modo simulo che il nodo sia una foglia
//				MassFunction<Integer> bba=m;
//				
//				MassFunction<Integer>[] others= new MassFunction[list.size()];
//				System.out.println("_____________BBA TO COMBINE______________________");
//				System.out.println("BBA: "+bba);
//				for(int i=0; i<list.size();i++){
//					MassFunction next=list.get(i).getSecondElement();
//					// applicare la regola di combinazione
//					
//					others[i]=next;
//				}
//				if(others.length>=1){
//					bba=bba.applicaCombinazione(others);
//					
//				}// combino con tutte le altre BBA
//				//concept
//				ArrayList<Integer> ipotesi= new ArrayList<Integer>();
//				ipotesi.add(+1);
//				double confirmationFunctionValuePos = bba.calcolaConfirmationFunction(ipotesi);
////				double confirmationFunctionValuePos = bba.calcolaBeliefFunction(ipotesi);
//				// not concept
//				ArrayList<Integer> ipotesi2= new ArrayList<Integer>();
//				ipotesi2.add(-1);
//				double confirmationFunctionValueNeg = bba.calcolaConfirmationFunction(ipotesi2);
////				double confirmationFunctionValueNeg = bba.calcolaBeliefFunction(ipotesi2);
//				ArrayList<Integer> ipotesi3= new ArrayList<Integer>();
//				ipotesi3.add(-1);
//				ipotesi3.add(+1);
//				double confirmationFunctionValueUnc = bba.calcolaConfirmationFunction(ipotesi3);
//				
//				//************** fase di valutazione e computo delle misure***************/
//				if((confirmationFunctionValueUnc>confirmationFunctionValuePos)&&(confirmationFunctionValueUnc>confirmationFunctionValueNeg))
//					if (confirmationFunctionValuePos>confirmationFunctionValueNeg){
//						
//						if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept))
//							tree.setMatch(0);
//						else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept)))
//							tree.setCommission(0);
//						else{
//							tree.setInduction(0);
//						}
//					}
//						
//					else if (confirmationFunctionValuePos<confirmationFunctionValueNeg)
//						if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept))
//							tree.setCommission(0);
//						else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept)))
//							tree.setMatch(0);
//						else{
//							tree.setInduction(0);
//						}
//						
//					else tree.setOmission(0);
//				else if(confirmationFunctionValuePos>=confirmationFunctionValueNeg)
//					if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept))
//						tree.setMatch(0);
//					else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept)))
//						tree.setCommission(0);
//					else{
//						tree.setInduction(0);
//					}
//				else{
//					if(kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], testconcept))
//						tree.setCommission(0);
//					else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(testconcept)))
//						tree.setMatch(0);
//					else{
//						tree.setInduction(0);
//					}
//				
//				}

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


