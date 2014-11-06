package evaluation;



/**
 * An interface for design of the experiments
 * @author Utente
 *
 */
public interface Evaluation {


    public abstract void bootstrap(int nFolds, String className) throws Exception; // bootstrap 

	public abstract void crossValidation(int nFolds, String Name);

}