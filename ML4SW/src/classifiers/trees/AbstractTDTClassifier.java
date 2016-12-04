package classifiers.trees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;

import classifiers.refinementOperator.RefinementOperator;
import classifiers.trees.models.AbstractTree;
import classifiers.trees.models.DLTree;
import evaluation.Parameters;

public abstract class AbstractTDTClassifier {

	protected KnowledgeBase kb;

	public AbstractTDTClassifier(KnowledgeBase k){
		kb=k;

	}

	public abstract DLTree induceDLTree(ArrayList<Integer> posExs, ArrayList<Integer> negExs,	ArrayList<Integer> undExs, 
			int dim, double prPos, double prNeg, RefinementOperator op);




	public	void classifyExamples(int indTestEx, DLTree[] trees, int[] results, OWLClassExpression[] testConcepts, int...rclass) {

		int length = testConcepts!=null?testConcepts.length:1;
		for (int c=0; c < length; c++) {
			if (Parameters.missingValueTreatmentForTDT){
				ArrayList<Integer> list= new ArrayList<Integer>();
				results[c] = classifyExample(list,indTestEx, trees[c]);

			}
			else
				results[c] = classifyExample(indTestEx, trees[c]);

		} // for c



	}

	static double gini(double numPos, double numNeg, double prPos,
			double prNeg) {

		double sum = numPos+numNeg;

		double p1 = (numPos*Parameters.M*prPos)/(sum+Parameters.M);
		double p2 = (numNeg*Parameters.M*prNeg)/(sum+Parameters.M);

		return (1.0-p1*p1-p2*p2);
		//		return (1-Math.pow(p1,2)-Math.pow(p2,2))/2;
	}

	public int classifyExample(int indTestEx, DLTree tree) {
//
//
//		Stack<DLTree> stack= new Stack<DLTree>();
//		OWLDataFactory dataFactory = kb.getDataFactory();
//		stack.add(tree);
//		int result=0;
//		boolean stop=false;
//
//		if (!Parameters.BINARYCLASSIFICATION){
//			while(!stack.isEmpty() && !stop){
//				DLTree currentTree= stack.pop();
//
//				OWLClassExpression rootClass = currentTree.getRoot();
//				//			System.out.println("Root class: "+ rootClass);
//				if (rootClass.equals(dataFactory.getOWLThing())){
//					stop=true;
//					result=+1;
//
//				}
//				else if (rootClass.equals(dataFactory.getOWLNothing())){
//					stop=true;
//					result=-1;
//
//				}else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass))
//					stack.push(currentTree.getPosSubTree());
//				else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass)))
//					stack.push(currentTree.getNegSubTree());
//				else {
//					stop=true;
//					result=0; 
//
//				}
//
//			}
//		}else{
//			while(!stack.isEmpty() && !stop){
//				DLTree currentTree= stack.pop();
//
//				OWLClassExpression rootClass = currentTree.getRoot();
//				//			System.out.println("Root class: "+ rootClass);
//				if (rootClass.equals(dataFactory.getOWLThing())){
//					stop=true;
//					result=+1;
//
//				}
//				else if (rootClass.equals(dataFactory.getOWLNothing())){
//					stop=true;
//					result=-1;
//
//				}else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass))
//					stack.push(currentTree.getPosSubTree());
//				else 
//					stack.push(currentTree.getNegSubTree()); // for those kb having no full complement
//
//			}
//		}
//	
//
	return 0;

}


public int classifyExample(List<Integer> list, int indTestEx, DLTree tree) {
//	Stack<DLTree> stack= new Stack<DLTree>();
//	OWLDataFactory dataFactory = kb.getDataFactory();
//	stack.add(tree);
//	int result=0;
//	boolean stop=false;
//	while(!stack.isEmpty() && !stop){
//		DLTree currentTree= stack.pop();
//
//		OWLClassExpression rootClass = currentTree.getRoot();
//		//			System.out.println("Root class: "+ rootClass);
//		if (rootClass.equals(dataFactory.getOWLThing())){
//			//				stop=true;
//			result=+1;
//			list.add(result);
//
//		}
//		else if (rootClass.equals(dataFactory.getOWLNothing())){
//			//				stop=true;
//			result=-1;
//			list.add(result);
//
//		}else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass))
//			stack.push(currentTree.getPosSubTree());
//		else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass)))
//			stack.push(currentTree.getNegSubTree());
//		else {
//			//				stop=true;
//			result=0; 
//			stack.push(currentTree.getPosSubTree());
//			stack.push(currentTree.getNegSubTree());
//
//		}
//	};
//
//	int posFr= Collections.frequency(list, +1);
//	int negFr= Collections.frequency(list, -1);
//
//	if (posFr>negFr)
//		return +1;
//	else
//		return -1;

return 0;

}



protected OWLClassExpression selectBestConcept(OWLClassExpression[] concepts, ArrayList<Integer> posExs, ArrayList<Integer> negExs,
		ArrayList<Integer> undExs, double prPos, double prNeg) {

	int[] counts;

	int bestConceptIndex = 0;

	counts = getSplitCounts(concepts[0], posExs, negExs, undExs);
	System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
			"#"+0, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);

	double bestGain = gain(counts, prPos, prNeg);

	System.out.printf("%+10e\n",bestGain);

	System.out.println(concepts[0]);

	for (int c=1; c<concepts.length; c++) {

		counts = getSplitCounts(concepts[c], posExs, negExs, undExs);
		System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
				"#"+c, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);

		double thisGain = gain(counts, prPos, prNeg);
		System.out.printf("%+10e\n",thisGain);
		System.out.println(concepts[c]);
		if(thisGain > bestGain) {
			bestConceptIndex = c;
			bestGain = thisGain;
		}
	}

	System.out.printf("best gain: %f \t split #%d\n", bestGain, bestConceptIndex);
	return concepts[bestConceptIndex];
}

protected OWLClassExpression selectBestConceptCCP(OWLClassExpression[] concepts, ArrayList<Integer> posExs, ArrayList<Integer> negExs,
		ArrayList<Integer> undExs, double prPos, double prNeg, ArrayList<Integer> truePosExs, ArrayList<Integer> trueNegExs) {

	int[] counts;

	int bestConceptIndex = 0;

	counts = getSplitCounts(concepts[0], posExs, negExs, undExs);
	System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
			"#"+0, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);

	double minEntropy = CCP(counts, prPos, prNeg, truePosExs.size(), trueNegExs.size()); // recall improvement

	System.out.printf("%+10e\n",minEntropy);

	System.out.println(concepts[0]);

	for (int c=1; c<concepts.length; c++) {

		counts = getSplitCounts(concepts[c], posExs, negExs, undExs);
		System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
				"#"+c, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);

		double thisEntropy = CCP(counts, prPos, prNeg, truePosExs.size(), trueNegExs.size());
		System.out.printf("%+10e\n",thisEntropy);
		System.out.println(concepts[c]);
		if(thisEntropy < minEntropy) {
			bestConceptIndex = c;
			minEntropy = thisEntropy;
		}
	}

	System.out.printf("best gain: %f \t split #%d\n", minEntropy, bestConceptIndex);
	return concepts[bestConceptIndex];
}












private double CCP(int[] counts, double prPos, double prNeg, int sizePos,
		int sizeNeg) {
	// TODO Auto-generated method stub
	
	double cP = counts[0] + counts[1];
	double cN = counts[3] + counts[4];
	double cU = counts[6] + counts[7] + counts[2] + counts[5];
	double sum= cP+cN+cU;
	double c= sum!=0?cP+cN/sum:0;
	
	double sizeTP = counts[0]+1;
	double sizeFP = counts[1]+1;
	double sizeFN= counts[3]+1;
	double sizeTN= counts[4]+1;
	
	
	double tpr= (sizeTP+sizeFP)!=0?((sizeTP)/(sizeTP+sizeFP)):1;
	double fpr= (sizeFP+sizeTN)!=0?((sizeFP+0.5)/(sizeFP+sizeTN)):1;

	   double p1=(2-tpr-fpr)!=0?(1-tpr)/(2-tpr-fpr):1;
	   double p2=(2-tpr-fpr)!=0?(1-fpr)/(2-tpr-fpr):1;
	   System.out.println( "TPR:"+tpr+"--"+" FPR:"+ fpr+ " p1: "+ p1+" p2:"+p2);
	   double entropyCCP= (-(tpr+fpr)*((tpr/(tpr+fpr))*Math.log(tpr/(tpr+fpr))-(fpr/(tpr+fpr))*Math.log(fpr/(tpr+fpr)))
			   -(2-p1-p2)*(p1*Math.log(p1)-p2*Math.log(p2)));

	return entropyCCP;
}

private double gain(int[] counts, double prPos, double prNeg) {

	double sizeT = counts[0] + counts[1];
	double sizeF = counts[3] + counts[4];
	double sizeU = counts[6] + counts[7] + counts[2] + counts[5];
	double sum = sizeT+sizeF+sizeU;

	double startImpurity = gini(counts[0]+counts[3], counts[1]+counts[4], prPos, prNeg);
	double tImpurity = gini(counts[0], counts[1], prPos, prNeg);
	double fImpurity = gini(counts[3], counts[4], prPos, prNeg);
	double uImpurity = gini(counts[6]+counts[2], counts[7]+counts[5] , prPos, prNeg);		

	return (startImpurity - (sizeT/sum)*tImpurity - (sizeF/sum)*fImpurity - -(sizeU/sum)*uImpurity);
}


public AbstractTDTClassifier() {
	super();
}

private int[] getSplitCounts(OWLClassExpression concept, ArrayList<Integer> posExs, ArrayList<Integer> negExs,
		ArrayList<Integer> undExs) {
	
	int[] counts = new int[9];
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
	//		for(int i=0; i<counts.length;i++)
	//			System.out.println(counts[i]);

	return counts;

}

protected void split(OWLClassExpression concept, ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs,
		ArrayList<Integer> posExsT, ArrayList<Integer> negExsT, ArrayList<Integer> undExsT, ArrayList<Integer> posExsF, ArrayList<Integer> negExsF,
		ArrayList<Integer> undExsF) {

	ArrayList<Integer> posExsU = new ArrayList<Integer>();
	ArrayList<Integer> negExsU = new ArrayList<Integer>();
	ArrayList<Integer> undExsU = new ArrayList<Integer>();

	splitGroup(concept,posExs,posExsT,posExsF,posExsU);
	splitGroup(concept,negExs,negExsT,negExsF,negExsU);
	splitGroup(concept,undExs,undExsT,undExsF,undExsU);	

}

private void splitGroup(OWLClassExpression concept, ArrayList<Integer> nodeExamples, ArrayList<Integer> trueExs,
		ArrayList<Integer> falseExs, ArrayList<Integer> undExs) {
	OWLClassExpression negConcept = kb.getDataFactory().getOWLObjectComplementOf(concept);

	for (int e=0; e<nodeExamples.size(); e++) {
		int exIndex = nodeExamples.get(e);
		if (kb.getReasoner().isEntailed(kb.getDataFactory().getOWLClassAssertionAxiom(concept, kb.getIndividuals()[exIndex])))
			trueExs.add(exIndex);
		else if ((kb.getReasoner().isEntailed(kb.getDataFactory().getOWLClassAssertionAxiom(negConcept, kb.getIndividuals()[exIndex]))))
			falseExs.add(exIndex);
		else
			undExs.add(exIndex);		
	}	

}



public abstract void prune(Integer[] pruningSet, AbstractTree tree, AbstractTree subtree);
}
