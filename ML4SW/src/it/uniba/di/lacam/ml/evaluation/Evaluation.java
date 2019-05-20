package it.uniba.di.lacam.ml.evaluation;



/**
 * An interface for the design of the experiments
 * @author Giuseppe Rizzo
 *
 */
public interface Evaluation {

/**
 * 
 * @param nFolds, the number of bootstrap samples
 * @param className, the name of the algorithm adopted in the experiments
 * @throws Exception an exception due to the
 */
    public abstract void bootstrap(int nFolds, String className) throws Exception; // bootstrap 
    /**
     * 
     * @param nFolds, the number of the folds 
     * @param className, the name of the algorithm adopted in the experiments
     * @throws Exception an exception due to the
     */
	public abstract void crossValidation(int nFolds, String Name);

	public abstract void computeDirectClassSeparabilityMeasure();

}