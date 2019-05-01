package it.uniba.di.lacam.ml.classifiers;


import org.semanticweb.owlapi.model.OWLClassExpression;
/**
 * A generic supervised learning algorithm
 * @author Giuseppe
 *
 */
public interface SupervisedLearnable {

	/**
	 * A method for training a generic supervised learning algorithm
	 * @param trainingExs, the set of training examples
	 * @param testConcepts, the set of query concept (the target concept providing the label for the training ex.s)
	 * @param negTestConcepts, the complement of query concepts
	 */
	public abstract void training(int[][] results,Integer[] trainingExs,
			OWLClassExpression[] testConcepts, OWLClassExpression[] negTestConcepts);

	
	
	/**
	 * A  method for testing of a supervised learning algorithm
	 * @param f,the current fold
	 * @param testExs, the test ex.s
	 * @param testConcepts, the target concepts/learning problems for which the classification is made
	 * @return the classification results
	 */
	public abstract int[][] test(int f, Integer[] testExs,
			OWLClassExpression[] testConcepts);

	
	public abstract double[] getComplexityValues();
}