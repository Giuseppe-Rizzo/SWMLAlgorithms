package evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import classifiers.evidentialmodels.dst.RuleType;
import classifiers.evidentialmodels.dst.TotalUncertainty;
import evaluation.designOfExperiments.AlgorithmName;
import evaluation.designOfExperiments.ExperimentalDesign;
import evaluation.task.Tasks;

public class Parameters {

	/* Experimental Design parameters */
//	AlgorithmName algorithm = AlgorithmName.DSTTerminologicalRandomForests;
	private static Properties props;
	private static InputStream input;
	public static void loadParameters(){
		props= new Properties();
		try {
			input= new FileInputStream(new File("experiments.properties"));
			props.load(input);
			/*parameters*/
			algorithm =  AlgorithmName.getClassifier(props.getProperty("algorithm"));
			samplingrate = Double.parseDouble(props.getProperty("samplingrate"));
			originalImbalance =Double.parseDouble(props.getProperty("originalImbalance"));
			NFOLDS = Integer.parseInt(props.getProperty("NFOLDS"));
			design = ExperimentalDesign.valueOf(props.getProperty("design"));
			task = Tasks.valueOf( props.getProperty("task")); //AFFILIATIONPROBLEM; //POLITICIANPREDICTION; //MUTAGENICPREDICTION; //POLITICIANPREDICTION;//
			NUMGENCONCEPTS = Integer.parseInt(props.getProperty("NUMGENCONCEPTS"));
			beam = Integer.parseInt(props.getProperty("beam"));
			NTREES = Integer.parseInt(props.getProperty("NTREES"));
			SEED = Integer.parseInt(props.getProperty("SEED"));
			PURITY_THRESHOLD = Double.parseDouble( props.getProperty("PURITY_THRESHOLD"));
			pruning = PruningType.valueOf(props.getProperty("pruning")); //PruningType.NOPRUNING;
			ETDTheuristic=TotalUncertainty.valueOf(props.getProperty("ETDTHeuristic"));
			rule= RuleType.valueOf(props.getProperty("rule"));
			/* Tipo di classificazione */
			missingValueTreatmentForTDT = Boolean.parseBoolean(props.getProperty("missingValueTreatmentForTDT"));//true;
			BINARYCLASSIFICATION = Boolean.parseBoolean(props.getProperty("BINARYCLASSIFICATION"));//false;
			M = Double.parseDouble(props.getProperty("M"));
		    nonspecificityControl = Boolean.parseBoolean(props.getProperty("nonspecificityControl"));//false;
			//	final String urlOwlFile = "file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Ontlogie_Non_vanno_bene/MDM0.73.owl";
			//C:/Users/Utente/Documents/Dataset/AltreOntologie 
			urlOwlFile = "file:////"+props.getProperty("urlOwlFile");//"file:////home/mod-biopax-example-ecocyc-glycolysis.owl";//"file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Dataset/humandisease2.owl";//"file:////home/humandisease2.owl";//"file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Dataset/humandisease2.owl"; //file:///C:/Users/Utente/Documents/Dataset/Dottorato/aifbportal_V2012-02-21_v1.owl"; //carcinogenesis.owl";//predident.owl";  //;            //"AltreOntologie/NTN-all.owl";  //"file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Dataset/mod-biopax-example-ecocyc-glycolysis.owl";
			refinementOperator=props.getProperty("refinementoperator");
			CCP = Boolean.parseBoolean(props.getProperty("CCP"));
			mutagenicAsDataPropertyPrediction=Boolean.parseBoolean(props.getProperty("mutagenicAsDataPropertyPrediction"));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	public static  AlgorithmName algorithm;//AlgorithmName.DSTTerminologicalRandomForests;
	
	public static double samplingrate;//0.5; //0.3; // 0.8 0.2 1
	public static double originalImbalance;// 1;
	public static int NFOLDS; //10;
	public static ExperimentalDesign design;
	public static Tasks task ; //AFFILIATIONPROBLEM; //POLITICIANPREDICTION; //MUTAGENICPREDICTION; //POLITICIANPREDICTION;//
	public static int NUMGENCONCEPTS;
	public static int beam;
	public static int NTREES;
	public static int SEED;
	public static double PURITY_THRESHOLD;
	public static PruningType pruning; //PruningType.NOPRUNING;
	/* Tipo di classificazione */
	public static boolean missingValueTreatmentForTDT;//true;
	public static boolean BINARYCLASSIFICATION;//false;
	public static double M;
	public static TotalUncertainty ETDTheuristic;
	public static  RuleType rule;
	public static boolean nonspecificityControl;//false;
	//	final String urlOwlFile = "file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Ontlogie_Non_vanno_bene/MDM0.73.owl";
	//C:/Users/Utente/Documents/Dataset/AltreOntologie
	public static String urlOwlFile;//"file:////home/mod-biopax-example-ecocyc-glycolysis.owl";//"file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Dataset/humandisease2.owl";//"file:////home/humandisease2.owl";//"file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Dataset/humandisease2.owl"; //file:///C:/Users/Utente/Documents/Dataset/Dottorato/aifbportal_V2012-02-21_v1.owl"; //carcinogenesis.owl";//predident.owl";  //;            //"AltreOntologie/NTN-all.owl";  //"file:///C:/Users/Utente/Documents/Dataset/Tesi_triennale/Ontologie/Dataset/mod-biopax-example-ecocyc-glycolysis.owl";
	public static String refinementOperator;
	public static boolean CCP;
	public static boolean mutagenicAsDataPropertyPrediction;
	public static String conceptSeed="Compound";
}