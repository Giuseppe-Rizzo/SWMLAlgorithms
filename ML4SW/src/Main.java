

import java.io.PrintStream;

import evaluation.Evaluation;
import evaluation.Parameters;
import evaluation.task.AffiliationPrediction;
import evaluation.task.MutagenicoPrediction;
import evaluation.task.ClassMembershipPrediction;
import evaluation.task.PoliticianGenerator;
import evaluation.task.PoliticianPrediction;

import knowledgeBasesHandler.KnowledgeBase;




public class Main {
	
static KnowledgeBase kb;
//	static int[][] classification;
 public  PrintStream console = System.out;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Parameters.loadParameters(); //loading from property file
		
//		Locale.setDefault(Locale.US);
		 kb = new KnowledgeBase(Parameters.urlOwlFile);
		
		Evaluation vcm= null; 
		switch (Parameters.task) {
		case CLASSMEMBERSHIPREDICTION:
			 vcm= new ClassMembershipPrediction(kb);
		break;
		
		case MUTAGENICPREDICTION:
			vcm=new MutagenicoPrediction(kb);
			
		 break;

		case POLITICIANPREDICTION: 
			vcm= new PoliticianPrediction(kb);
			break;
		 
		default:
			 vcm= new AffiliationPrediction(kb);
			break;
		}
		
		String className =  Parameters.algorithm.toString(); 		// package name
		
		
		switch (Parameters.design) {
		case BOOTSTRAP:
		
		vcm.bootstrap(Parameters.NFOLDS,className); // n. of folds		
		break;
		
		default:
			vcm.crossValidation(Parameters.NFOLDS,className); 
		
		break;	
		}
////		
	System.out.println("\n\nEnding: "+Parameters.urlOwlFile);

	} // main
	
	


} // class DLTreeInducer