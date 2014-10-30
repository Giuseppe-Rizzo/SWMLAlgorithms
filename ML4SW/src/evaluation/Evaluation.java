package evaluation;

import evaluation.designOfExperiments.AlgorithmName;
import evaluation.designOfExperiments.ExperimentalDesign;
import evaluation.task.Tasks;


/**
 * An interface for design of the experiments
 * @author Utente
 *
 */
public interface Evaluation{


    /* Experimental Design parameters */
	AlgorithmName algorithm= AlgorithmName.DSTTerminologicalRandomForests;
	double samplingrate= 1; //0.3; // 0.8 0.2 1
	int NFOLDS = 5;
	ExperimentalDesign design = ExperimentalDesign.CROSSVALIDATION;
	Tasks task= Tasks.CLASSMEMBERSHIPREDICTION; //AFFILIATIONPROBLEM; //POLITICIANPREDICTION; //MUTAGENICPREDICTION; //POLITICIANPREDICTION;//
	int NUMGENCONCEPTS = 15;
	int beam=3;
	int NTREES =30;
	int SEED = 2;
	double PURITY_THRESHOLD = 0.10;
	PruningType pruning=PruningType.REP;
	/* Tipo di classificazione */
	boolean missingValueTreatmentForTDT= true;
	boolean BINARYCLASSIFICATION=false;
	double M = 3;	
	boolean nonspecificityControl= false;
	//	final String urlOwlFile = "file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Ontlogie_Non_vanno_bene/MDM0.73.owl";
//C:/Users/Utente/Documents/Dataset/AltreOntologie
	final String urlOwlFile =  "file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Dataset/MDM0.732.owl"; //file:///C:/Users/Utente/Documents/Dataset/Dottorato/aifbportal_V2012-02-21_v1.owl"; //carcinogenesis.owl";//predident.owl";  //;            //"AltreOntologie/NTN-all.owl";  //"file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Dataset/mod-biopax-example-ecocyc-glycolysis.owl";
	public abstract void bootstrap(int nFolds, String className) throws Exception; // bootstrap 

	public abstract void crossValidation(int nFolds, String Name);

}