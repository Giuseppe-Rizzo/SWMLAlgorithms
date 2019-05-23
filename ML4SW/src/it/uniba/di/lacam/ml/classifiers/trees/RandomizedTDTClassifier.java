package it.uniba.di.lacam.ml.classifiers.trees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.Stack;

import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;


import it.uniba.di.lacam.ml.classifiers.refops.RefinementOperator;
import it.uniba.di.lacam.ml.classifiers.trees.models.AbstractTree;
import it.uniba.di.lacam.ml.classifiers.trees.models.DLTree;
import it.uniba.di.lacam.ml.evaluation.Parameters;
import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;
import it.uniba.di.lacam.ml.utils.Couple;
import it.uniba.di.lacam.ml.utils.Npla;

public class RandomizedTDTClassifier  {

	KnowledgeBase k;

	public RandomizedTDTClassifier(KnowledgeBase k){
		this.k=k;

		//super(k);

	}



	static final OWLObjectRenderer renderer = new DLSyntaxObjectRenderer(); 

	// P for postive, N for negative, u for unknown memb. examples
	// L/R for left/right branch 
	static final int PL=0, NL=1, UL=2, PR=3, NR=4, UR=5;  

	
	/**
	 * TDT induction algorithm implementation
	 * 
	 * @param prob Learning problem
	 * @param father father concept
	 * @param posExs positive examples
	 * @param negExs negative examples
	 * @param undExs unknown m. examples
	 * @param nCandRefs 
	 * @param prPos
	 * @param prNeg
	 * @return
	 */
	public DLTree  induceDLTree	(OWLClassExpression father, 
				ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs, 
				int nCandRefs, double prPos, double prNeg) {		
		
		final double THRESHOLD = 0.05;
		
		System.out.printf("\n * Learning problem\t p:%d\t n:%d\t u:%d\t prPos:%4f\t prNeg:%4f\n", 
				posExs.size(), negExs.size(), undExs.size(), prPos, prNeg);
		
		DLTree tree = new DLTree (); // new (sub)tree		

		
		if (posExs.size() == 0 && negExs.size() == 0) // no exs
			if (prPos >= prNeg) { // prior majority of positives
				tree.setRoot(k.getDataFactory().getOWLThing()); // set positive leaf
				System.out.println("-----\nPOS leaf (prior)");
				return tree;
			}
			else { // prior majority of negatives
				tree.setRoot(k.getDataFactory().getOWLNothing()); // set negative leaf
				System.out.println("-----\nNEG leaf (prior)");
				return tree;
			}			
		
		
//		double numPos = posExs.size() + undExs.size()*prPos;
//		double numNeg = negExs.size() + undExs.size()*prNeg;
		double numPos = posExs.size();
		double numNeg = negExs.size();
		double perPos = numPos/(numPos+numNeg);
		double perNeg = numNeg/(numPos+numNeg);
		

		if (perNeg==0 && perPos > THRESHOLD) { // no negative
			tree.setRoot(k.getDataFactory().getOWLThing()); // set positive leaf
			System.out.println("-----\nPOS leaf (prior)");
			return tree;
			}
		else 
		if (perPos==0 && perNeg > THRESHOLD) { // no positive			
			tree.setRoot(k.getDataFactory().getOWLNothing()); // set negative leaf
			System.out.println("-----\nNEG leaf (thr)\n");
			return tree;
		}		
		// else (a non-leaf node) ...
		
		
		
		OWLClassExpression[] cConcepts = generateRefs(k, father, nCandRefs, posExs, negExs);
		
		// select node concept
		OWLClassExpression bestConcept = selectBestConcept(k, cConcepts, posExs, negExs, undExs, prPos, prNeg);
		
		ArrayList<Integer> posExsL = new ArrayList<Integer>();
		ArrayList<Integer> negExsL = new ArrayList<Integer>();
		ArrayList<Integer> undExsL = new ArrayList<Integer>();
		ArrayList<Integer> posExsR = new ArrayList<Integer>();
		ArrayList<Integer> negExsR = new ArrayList<Integer>();
		ArrayList<Integer> undExsR = new ArrayList<Integer>();
		
		split(k, bestConcept, 
				posExs, negExs, undExs, 
				posExsL, negExsL, undExsL, 
				posExsR, negExsR, undExsR);
		// select node concept
		tree.setRoot(bestConcept.getNNF());		
		// build subtrees
		
		
		tree.setPosTree(induceDLTree(bestConcept, posExsL, negExsL, undExsL, nCandRefs, prPos, prNeg));
		tree.setNegTree(induceDLTree( bestConcept.getComplementNNF(), posExsR, negExsR, undExsR, nCandRefs, prPos, prNeg));
				
		return tree;
	}



	/**
	 * routine selecting the best in a list (array) of refinements 
	 * @param prob
	 * @param concepts
	 * @param posExs
	 * @param negExs
	 * @param undExs
	 * @param prPos
	 * @param prNeg
	 * @return
	 */
	private static OWLClassExpression selectBestConcept(KnowledgeBase prob, OWLClassExpression[] concepts,
			ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs, 
			double prPos, double prNeg) {

		int[] counts;

		
		int bestConceptIndex = 0;
		
		counts = getSplitCounts(prob, concepts[0], posExs, negExs, undExs);
		System.out.printf("%4s\tp:%4d\t n:%4d\t u:%4d\t --- p:%4d\t n:%4d\t u:%4d\t ", 
				"#"+0, counts[PL], counts[NL], counts[UL], counts[PR], counts[NR], counts[UR]);
		
//		System.out.println(concepts[0]);		
		
		double bestGain = gain(counts);
		System.out.printf("%10f\n",bestGain);
			
		for (int c=1; c<concepts.length; c++) {
			
			counts = getSplitCounts(prob, concepts[c], posExs, negExs, undExs);
			System.out.printf("%4s\tp:%4d\t n:%4d\t u:%4d\t --- p:%4d\t n:%4d\t u:%4d\t ", 
					"#"+c, counts[PL], counts[NL], counts[UL], counts[PR], counts[NR], counts[UR]);
			
			double thisGain = gain(counts);
			System.out.printf("%10f\n",thisGain);
//			System.out.println(concepts[c]);
			
			if(thisGain > bestGain) {
				bestConceptIndex = c;
				bestGain = thisGain;
			}
		}
		
		System.out.printf("\n -------- best gain: %f \t split #%d\n %s\n\n", bestGain, bestConceptIndex, concepts[bestConceptIndex]);
		return concepts[bestConceptIndex];
	}



	
	/**
	 * @param counts
	 * @return
	 */
	private static double gain(int[] counts) {	

		// twoing
		
		double totL = counts[PL]+counts[NL]+0.001;
		double totR = counts[PR]+counts[NR]+0.001;
		double tot = totL+totR;
		double pPL = counts[PL]/totL, pPR = counts[PR]/totR, pNL = counts[NL]/totL,  pNR = counts[NR]/totR; 
		
		return (totL/tot)*(totR/tot)*
				Math.pow(Math.abs(pPL-pPR)/Math.abs(pPL+pPR)+Math.abs(pNL-pNR)/Math.abs(pNL+pNR),2); 
		
	}
	
//	private static double gini(double numPos, double numNeg, double numUnd) {
//		
//		double sum = numPos+numNeg;
//		
//		if (sum>0) {
//			double p1 = numPos/sum;
//			double p2 = numNeg/sum;
//			double p3 = numUnd/sum;
//
//			return (1.0-p1*p1-p2*p2);
//		}
//		else 
//			return (Double.MAX_VALUE);
//
//	}
	
	
	/**
	 * @param prob
	 * @param concept
	 * @param posExs
	 * @param negExs
	 * @param undExs
	 * @return
	 */
	private static int[] getSplitCounts(KnowledgeBase prob, OWLClassExpression concept, 
			ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs) {
		
		int[] counts = new int[6];

		ArrayList<Integer> posExsL = new ArrayList<Integer>();
		ArrayList<Integer> negExsL = new ArrayList<Integer>();
		ArrayList<Integer> undExsL = new ArrayList<Integer>();
		ArrayList<Integer> posExsR = new ArrayList<Integer>();
		ArrayList<Integer> negExsR = new ArrayList<Integer>();
		ArrayList<Integer> undExsR = new ArrayList<Integer>();
		
		splitGroup(prob, concept, posExs, posExsL, posExsR);
		splitGroup(prob, concept, negExs, negExsL, negExsR);	
		splitGroup(prob, concept, undExs, undExsL, undExsR);	
		
		counts[PL] = posExsL.size(); 
		counts[NL] = negExsL.size(); 
		counts[UL] = undExsL.size(); 
		counts[PR] = posExsR.size(); 
		counts[NR] = negExsR.size();
		counts[UR] = undExsR.size();
		
		return counts;
		
	}
	
	/**
	 * @param prob
	 * @param concept
	 * @param posExs
	 * @param negExs
	 * @param undExs
	 * @param posExsL
	 * @param negExsL
	 * @param undExsL
	 * @param posExsR
	 * @param negExsR
	 * @param undExsR
	 */
	private static void split(KnowledgeBase prob, OWLClassExpression concept,
			ArrayList<Integer> posExs,  ArrayList<Integer> negExs,  ArrayList<Integer> undExs,
			ArrayList<Integer> posExsL, ArrayList<Integer> negExsL,	ArrayList<Integer> undExsL, 
			ArrayList<Integer> posExsR,	ArrayList<Integer> negExsR, ArrayList<Integer> undExsR) {
				
		splitGroup(prob, concept, posExs, posExsL, posExsR);
		splitGroup(prob, concept, negExs, negExsL, negExsR);
		splitGroup(prob, concept, undExs, undExsL, undExsR);	
		
	}


	/**
	 * @param prob
	 * @param concept
	 * @param nodeExamples
	 * @param leftExs
	 * @param rightExs
	 */
	private static void splitGroup(KnowledgeBase  prob, OWLClassExpression concept, ArrayList<Integer> nodeExamples,
			ArrayList<Integer> leftExs, ArrayList<Integer> rightExs) {

		OWLClassExpression negConcept = prob.getDataFactory().getOWLObjectComplementOf(concept);
		
		for (int e=0; e<nodeExamples.size(); e++) {
			int exIndex = nodeExamples.get(e);
			if (prob.getReasoner().isEntailed(prob.getDataFactory().getOWLClassAssertionAxiom(concept, prob.getIndividuals()[exIndex])))
				leftExs.add(exIndex);
			else if (prob.getReasoner().isEntailed(prob.getDataFactory().getOWLClassAssertionAxiom(negConcept, prob.getIndividuals()[exIndex])))
				rightExs.add(exIndex);
			else { 
				leftExs.add(exIndex); 
				rightExs.add(exIndex); 
			}		
		}	
	}


	/**
	 * @param prob
	 * @param concept
	 * @param dim
	 * @param posExs
	 * @param negExs
	 * @return
	 */
	private static OWLClassExpression[] generateRefs(KnowledgeBase prob, OWLClassExpression concept, int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {
		
		System.out.printf("\nGenerating node concepts ");
		OWLClassExpression[] rConcepts = new OWLClassExpression[dim];
		OWLClassExpression newConcept, refinement;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				refinement = new RefinementOperator(prob).getRandomConcept(prob);
            	HashSet<OWLClassExpression> newConcepts = new HashSet<OWLClassExpression>();	            	
            	newConcepts.add(concept);
            	newConcepts.add(refinement);
            	newConcept = prob.getDataFactory().getOWLObjectIntersectionOf(newConcepts);
            	
//				Iterator<OWLIndividual> instIterator = reasoner.getIndividuals(newConcept, false).iterator();
//				emptyIntersection = reasoner.getIndividuals(newConcept, false).size()<1;
				emptyIntersection = !prob.getReasoner().isSatisfiable(newConcept);

//				while (emptyIntersection && instIterator.hasNext()) {
//					OWLIndividual nextInd = (OWLIndividual) instIterator.next();
//					int index = -1;
//					for (int i=0; index<0 && i<allIndividuals.length; ++i)
//						if (nextInd.equals(allIndividuals[i])) index = i;
//					if (posExs.contains(index))
//						emptyIntersection = false;
//					else if (negExs.contains(index))
//						emptyIntersection = false;
//				}					
			} while (emptyIntersection);
			rConcepts[c] = newConcept; // normalized ?
			System.out.printf("%d ", c);
		}
		System.out.println();
		
		return rConcepts;
	}

	/**
	 * recursive down through the tree model
	 * @param ind
	 * @param tree
	 * @return
	 */
	public  int classify(OWLIndividual ind, DLTree tree) {
		
		OWLClassExpression rootClass = tree.getRoot();
		
		if (rootClass.equals(k.getDataFactory().getOWLThing()))
			return +1;
		if (rootClass.equals(k.getDataFactory().getOWLNothing()))
			return -1;
		
		int r1=0, r2=0;
		
		if (k.getReasoner().isEntailed(k.getDataFactory().getOWLClassAssertionAxiom(rootClass, ind)))
			r1 = classify(ind, tree.getPosSubTree());
		else if (k.getReasoner().isEntailed(k.getDataFactory().getOWLClassAssertionAxiom(k.getDataFactory().getOWLObjectComplementOf(rootClass), ind)));
			r2 = classify(ind, tree.getNegSubTree());
 int cP=0, cn=0; //
		if (r1+r2==0) 
			if (Parameters.missingValueTreatmentForTDT){
			          cP+=classify(ind, tree.getPosSubTree());
			          cn-=classify(ind, tree.getNegSubTree());
			          if (cP>(-1*cn)) return +1; else if (cP<(-1*cn)) return -1; else return 0; // case of tie             
		     }
			else
			    return 0;
		else if (r1*r2==1) 
			return r1;
		else 
			return (r1!=0)? r1 : r2;
	}
 

	


}








