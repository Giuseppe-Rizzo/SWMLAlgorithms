package classifiers;

import org.semanticweb.owl.model.OWLDescription;

public interface SupervisedLearnable {

	public abstract void training(Integer[] trainingExs,
			OWLDescription[] testConcepts, OWLDescription[] negTestConcepts);

	public abstract int[][] test(int f, Integer[] testExs,
			OWLDescription[] testConcepts);

}