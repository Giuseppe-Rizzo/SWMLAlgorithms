package it.uniba.di.lacam.ml.classifiers;
/**
 * A generic machine learnin model
 * @author Giuseppe
 *
 */
public abstract class AbstractModel {

	public AbstractModel() {
		
	}
	
	/**
	 * Provide a value representing a complexity measure
	 * @return
	 */
	public abstract double getComplexityMeasure();

}
