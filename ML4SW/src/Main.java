

import java.io.PrintStream;

import evaluation.Evaluation;
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
		
//		Locale.setDefault(Locale.US);
		 kb = new KnowledgeBase(Evaluation.urlOwlFile);
		
		Evaluation vcm= null; 
		switch (Evaluation.task) {
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
		
		String className =  Evaluation.algorithm.toString(); 		// package name
		
		
		switch (Evaluation.design) {
		case BOOTSTRAP:
		
		vcm.bootstrap(Evaluation.NFOLDS,className); // n. of folds		
		break;
		
		default:
			vcm.crossValidation(Evaluation.NFOLDS,className); 
		
		break;	
		}
////		
	System.out.println("\n\nEnding: "+Evaluation.urlOwlFile);

	} // main
	
	


} // class DLTreeInducer