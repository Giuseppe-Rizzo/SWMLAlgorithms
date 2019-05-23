package it.uniba.di.lacam.ml.classifiers.knn;


import org.semanticweb.owlapi .apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import it.uniba.di.lacam.ml.classifiers.SupervisedLearnable;
import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;

public class QHDNN implements SupervisedLearnable {
	
	static final int POS = 0;	
	static final int BOH = 1;
	static final int NEG = 2;
	static final double EPSILON = 1E-4;
	static final double UNCERTAINTY_THRESHOLD = 0.7;
	private KnowledgeBase kb;
	private Integer[] neighbors;
	
	public QHDNN(KnowledgeBase  kb, int nOfConcepts){
		this.kb= kb;
	
		
	}
	
	/**
	 * Weighted majority vote procedure
	 * @param indTestEx
	 * @param neighboorEx
	 * @param testConcepts
	 * @param classificationResults
	 */
	public  void classify(int indTestEx, Integer[] neighboorEx, OWLClassExpression[] testConcepts, int[] classificationResults) {
		
		for (int c=0; c < testConcepts.length; c++) {
			
			double classWeight = 0;
			double negatedClassWeight = 0;
			double unknownClassWeight = 0;
						
			for (int ne = 0; ne<neighboorEx.length; ne++) {	
				
//				double addendum = 1/Math.pow(FeaturesDrivenDistance.simpleDistance(indTestEx,neighboorEx[e]),2);
				double addendum = 1;// (1+Float.MIN_VALUE)/(Float.MIN_VALUE+FeaturesDrivenDistance.simpleDistance(indTestEx,neighboorEx[ne]));
				OWLNamedIndividual owlNamedIndividual = (OWLNamedIndividual)(kb.getIndividuals()[neighboorEx[ne]]);
				OWLClassExpression owlClassExpression = testConcepts[c];
				OWLClassExpression negOwlClassExpression= kb.getDataFactory().getOWLObjectComplementOf(owlClassExpression);
				if (kb.getReasoner().isEntailed(kb.getDataFactory().getOWLClassAssertionAxiom(owlClassExpression,owlNamedIndividual)))
					classWeight += addendum;
			  else if (kb.getReasoner().isEntailed(kb.getDataFactory().getOWLClassAssertionAxiom(negOwlClassExpression,owlNamedIndividual))) 
					negatedClassWeight += addendum;
				else 
					unknownClassWeight += addendum;				
			}// for int ne
			
			if (classWeight >= negatedClassWeight)
				if (classWeight >= unknownClassWeight) 
					classificationResults[c] = 1;
				else 
					classificationResults[c] = 0;
			else if (negatedClassWeight >= unknownClassWeight)
				classificationResults[c] = -1;
			else 
				classificationResults[c] = 0;
			
		} // for c
		
//		return classificationResults;
	}

	
	
	


/**
 * Return the neighborhood of an example
 * @param indTestExample
 * @param trainingExamples
 * @param k
 * @return
 */
	private  Integer[] getNeighbors(int indTestExample, Integer[] trainingExamples, int k) {
		// restituzione dei vicini considerando la distanza semplice
		Integer[] neighborExs = new Integer[k];
		double[] dissNeighboor = new double[k];
		java.util.Arrays.fill(neighborExs, -1);
		java.util.Arrays.fill(dissNeighboor, Double.MAX_VALUE); // any value greater than the max dissimilarity would do
	
		for (int te=0; te<trainingExamples.length; te++) {
//			System.out.printf("...now comparing [%4d] to training example (%4d)",indTestExample, trainingExamples[i]);	
			double dValue = FeaturesDrivenDistance.simpleDistance(indTestExample, trainingExamples[te]);			
//			double dValue = dissimilarity(indTestExample, trainingExamples[i]);
			
			int pos = k;
//			double dCompare = dissimilarity(pos-1, indEx2Classify, dmatrix);
			while ((pos > 0) && (dValue < dissNeighboor[pos-1])) {
				if (pos<k) { 
					neighborExs[pos] = neighborExs[pos-1];
					dissNeighboor[pos] = dissNeighboor[pos-1];
				}
				pos--;
			}
			if (pos < k) {
				neighborExs[pos] = trainingExamples[te];
				dissNeighboor[pos] = dValue;
				}
		} // for i	
		
				
		return neighborExs;
	}

	@Override
	/**
	 * Lazy method: Store the neighbors 
	 */
	public void training(int[][] results, Integer[] trainingExs, OWLClassExpression[] testConcepts,
			OWLClassExpression[] negTestConcepts) {
		 // no training for lazy-learning methods
		System.out.print("No training phase");
		neighbors = trainingExs; // store the training set
		
		
	}

	@Override
	/**
	 * Classifiies the test ex.s
	 */
	public int[][] test(int f, Integer[] testExs, OWLClassExpression[] testConcepts) {
		// TODO Auto-generated method stub
		int [][] classificationresults = new int [testExs.length][testConcepts.length]; 
		
		int k= (int)Math.log(neighbors.length);
		for (int ex=0; ex<testExs.length;ex++){
			Integer[] neighbors2 = getNeighbors(testExs[ex], neighbors, k);
			//classification w.r.t.\ a set of target concept
			for (OWLClassExpression cl:testConcepts){
				classify(testExs[ex], neighbors2, testConcepts, classificationresults[ex]);
				
			}
		}
		return classificationresults;
		
	}

	@Override
	public double[] getComplexityValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
		
	
} // class



