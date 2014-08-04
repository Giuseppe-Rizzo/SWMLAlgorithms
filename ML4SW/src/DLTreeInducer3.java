

import java.io.PrintStream;

import evaluation.Evaluation;
import evaluation.task.ClassMembershipPrediction;

import knowledgeBasesHandler.KnowledgeBase;






public class DLTreeInducer3 {
	
	
//	static final int QUERY_NB = 50;
//	static final int NFOLDS = 30;
//	static final int NUMGENCONCEPTS = 50;
//	static final double THRESHOLD = 0.05;
//	static final double M = 3;	
	
//	static Random generator = new Random(SEED);


//	static Reasoner reasoner;
//	static OWLOntologyManager manager;
//	static OWLDataFactory dataFactory;
//
//	static OWLOntology ontology;
//	
//	static OWLIndividual[] allExamples, trainingExamples;
//	static OWLClass[] allConcepts;
//	static OWLObjectProperty[] allRoles;
//	static OWLDescription[] testConcepts;
//	static OWLDescription[] negTestConcepts;
	static KnowledgeBase kb;
//	static int[][] classification;
 public  PrintStream console = System.out;

	// ontologia fsm
//static String urlOwlFile = "file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Dataset/mod-biopax-example-ecocyc-glycolysis.owl"; //humandisease2.owl";
//static String urlOwlFile="file:///C:/Users/Utente/Documents/Dataset/NTN-all.owl";
//static String urlOwlFile = "file:///C:/Users/Utente/Documents/Dataset/wine2.rdf";
// static String urlOwlFile = "file:///C:/Users/Utente/Downloads/LDMC_Task1_train.ttl";
//static String urlOwlFile = "file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Ontlogie_Non_vanno_bene/MDM0.73.owl";
//	static String urlOwlFile="file:///C:/Users/workspace/LUBM/University0_0.owl";
//static String urlOwlFile = "file:///C:/Users/Utente/Documents/Dataset/aifbportal_V2012-02-21_v1.owl"; //PREPRUNING
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
//		Locale.setDefault(Locale.US);
		 kb = new KnowledgeBase(Evaluation.urlOwlFile);
				
		Evaluation vcm= new ClassMembershipPrediction(kb);
        vcm.bootstrap(Evaluation.NFOLDS,"classifiers.Classifier2"); // n. of folds		
		System.out.println("\n\nEnding: "+Evaluation.urlOwlFile);


	} // main
	
	


} // class DLTreeInducer