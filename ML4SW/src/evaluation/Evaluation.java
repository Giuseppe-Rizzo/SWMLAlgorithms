package evaluation;

public interface Evaluation{

	/**
	 * Effettua la classificazione sui concetti
	 * @param nFolds
	 * @throws Exception 
	 */
	
	int NFOLDS = 10;
	int NTREES = 10;
	int NUMGENCONCEPTS = 15;
	int SEED = 2;
	double PURITY_THRESHOLD = 0.05;
	PruningType pruning=PruningType.NOPRUNING;
	boolean missingValueTreatmentForTDT= false;
	 double M = 3;	
	final String urlOwlFile = "file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Ontlogie_Non_vanno_bene/MDM0.73.owl";
	public abstract void bootstrap(int nFolds, String className) throws Exception; // bootstrap 

	public abstract void crossValidation(int nFolds, String Name); // leaveOneOut

}