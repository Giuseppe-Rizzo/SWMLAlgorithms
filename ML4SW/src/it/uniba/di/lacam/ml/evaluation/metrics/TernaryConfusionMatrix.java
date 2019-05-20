package it.uniba.di.lacam.ml.evaluation.metrics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

//import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;


import java.util.SortedSet;


import org.semanticweb.owlapi.model.OWLClassExpression;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.uniba.di.lacam.ml.evaluation.Evaluation;
import it.uniba.di.lacam.ml.evaluation.Parameters;
import it.uniba.di.lacam.ml.evaluation.designOfExperiments.AlgorithmName;
import it.uniba.di.lacam.ml.utils.MathUtils;

/**
 * Compute a ternary confusion matrix 
 * (not integrated yet in the experimental design)
 * @author Giuseppe
 *
 */
public class TernaryConfusionMatrix extends AbstractMetrics{

 private static Logger logger= LoggerFactory.getLogger(TernaryConfusionMatrix.class);

	public static final int POSITIVE=0;
	public static final int NEGATIVE=1;
	public static final int UNCERTAIN=2;
	
	int[][] confusionMatrix;
	

	public TernaryConfusionMatrix(){
		PrintStream stream;
		confusionMatrix = new int[3][3];
	}

	
	
	public int[][] computeConfusionMatrix(int[] labels, int[] rclasses){
		
		
		
		for (int l=0; l<labels.length; l++){
		if (labels[l] == rclasses[l]) { 
			//System.out.println("\t  Ground truth "+rclasses[l]+" Predicted "+ labels[l]+ ": matched");
			
			if (rclasses[l]==1) 
				confusionMatrix[POSITIVE][POSITIVE]++;
			else if (rclasses[l]==-1)
				confusionMatrix[NEGATIVE][NEGATIVE]++;
			else 
				confusionMatrix[UNCERTAIN][UNCERTAIN]++;
			
		}
		else if (Math.abs(labels[l] - rclasses[l])>1) { 
			//System.out.println("\t Ground truth "+rclasses[l]+" Predicted "+ labels[l]+ ":committed");
			if (rclasses[l]==-1)
				confusionMatrix[NEGATIVE][POSITIVE]++;
			else  //if (rclasses[l]==+1)
				confusionMatrix[POSITIVE][NEGATIVE]++;
					
		}
		else if (rclasses[l] != 0) {
			//System.out.println("\t Ground truth "+rclasses[l]+" Predicted "+ labels[l]+ ": omitted");
			if (rclasses[l]==-1)
				confusionMatrix[NEGATIVE][UNCERTAIN]++;
			else  //if (rclasses[l]==+1)
				confusionMatrix[POSITIVE][UNCERTAIN]++;
			
		}	
		else {
			//System.out.println("\t Ground truth "+rclasses[l]+" Predicted "+ labels[l]+ ": induced");
			if (labels[l]==-1)
				confusionMatrix[UNCERTAIN] [NEGATIVE]++;
			else  //if (rclasses[l]==+1)
				confusionMatrix[UNCERTAIN] [POSITIVE]++;
		}
		
		}
		return confusionMatrix;
	}
	
	
	
	
	public double getMatchRate(){
		int nExs = getnExs();
		  return ((double)confusionMatrix[POSITIVE][POSITIVE]+confusionMatrix[NEGATIVE][NEGATIVE]+confusionMatrix[UNCERTAIN][UNCERTAIN])/nExs; 
		
		
	}

	public double getCommissionRate(){
		int nExs = getnExs();
		  return ((double)confusionMatrix[POSITIVE][NEGATIVE]+confusionMatrix[NEGATIVE][POSITIVE])/nExs; 
		
		
	}

	public double getOmissionRate(){
		int nExs = getnExs();
		  return ((double)confusionMatrix[POSITIVE][UNCERTAIN]+confusionMatrix[NEGATIVE][UNCERTAIN])/nExs; 
		
		
	}

	public double getInductionRate(){
		int nExs = getnExs();
		  return ((double)confusionMatrix[UNCERTAIN][POSITIVE]+confusionMatrix[UNCERTAIN] [NEGATIVE])/nExs; 	
	}

	
	public double getPrecisionPos(){
		//int nExs=getnExs();
		return ((double)(confusionMatrix[POSITIVE][POSITIVE]+1)/(confusionMatrix[POSITIVE][POSITIVE]+confusionMatrix[POSITIVE][UNCERTAIN]+confusionMatrix[POSITIVE][NEGATIVE]+1));
	}
	
	public double getPrecisionNeg(){
		//int nExs=getnExs();
		return ((double)(confusionMatrix[NEGATIVE][NEGATIVE]+1)/(confusionMatrix[NEGATIVE][POSITIVE]+confusionMatrix[NEGATIVE][UNCERTAIN]+confusionMatrix[NEGATIVE][NEGATIVE]+1));
	}
	
	public double getPrecisionUnc(){
		//int nExs=getnExs();
		return ((double)(confusionMatrix[UNCERTAIN][UNCERTAIN]+1)/(confusionMatrix[UNCERTAIN][POSITIVE]+confusionMatrix[UNCERTAIN][UNCERTAIN]+confusionMatrix[UNCERTAIN][NEGATIVE]+1));
	}

	public double getPrecision(){
		System.out.println();
		double precisionNeg = getPrecisionNeg();
		double precisionPos = getPrecisionPos();
		double precisionUnc = getPrecisionUnc();
		//System.out.println(precisionNeg +"-"+precisionPos+ precisionUnc);
		return  (precisionNeg+precisionPos+precisionUnc)/3 ; // average on three classes
	
	}
	
	public double getRecall(){
		
		return (getTruePositiveRate()+getNegativeRate()+getTrueUncertainRate())/3;
	}
	
	private double getTrueUncertainRate() {
		return ((double)(confusionMatrix[UNCERTAIN][UNCERTAIN]+1)/(confusionMatrix[POSITIVE][UNCERTAIN]+confusionMatrix[UNCERTAIN][UNCERTAIN]+confusionMatrix[NEGATIVE][UNCERTAIN]+1));
		 
	}



	private double getTruePositiveRate() {
		// TODO Auto-generated method stub
		return ((double)(confusionMatrix[POSITIVE][POSITIVE]+1)/(confusionMatrix[POSITIVE][POSITIVE]+confusionMatrix[UNCERTAIN][POSITIVE]+confusionMatrix[NEGATIVE][POSITIVE]+1));
	}



	private double getNegativeRate() {
		// TODO Auto-generated method stub
		 return ((double)(confusionMatrix[NEGATIVE][NEGATIVE]+1)/(confusionMatrix[NEGATIVE][NEGATIVE]+confusionMatrix[UNCERTAIN][NEGATIVE]+confusionMatrix[POSITIVE][NEGATIVE]+1));
	}



	private int getnExs() {
		int nExs=0;
		for (int i = 0; i < confusionMatrix.length; i++) {
			for (int j = 0; j < confusionMatrix[i].length; j++) {
				if (confusionMatrix[i][j]>0)
					nExs+=confusionMatrix[i][j];
			}
		}
		return nExs;
	}
	
	
	
	
	
//	public void computeMetricsperIndividualperClass( int label, int rclass, Integer foundNum, Integer trueNum, Integer hitNum, Integer matchingNum, Integer commissionNum, Integer omissionNum, Integer inducedNum ){
//		
//			if (label == 1){
//				foundNum++;
//				//System.out.println("----"+foundNum[c]);
//				
//			}
//
//			if (rclass == +1){
//				trueNum++;
//				//System.out.println("----"+trueNum[c]);
//			}
//
//
//			if (label == rclass) { 
//				System.out.println("\t  Ground truth "+rclass+" Predicted "+ label+ ": matched");
//				++matchingNum;
//				if (rclass==1) 
//					++hitNum;
//			}
//			else if (Math.abs(label - rclass)>1) { 
//				System.out.println("\t Ground truth "+rclass+" Predicted "+ label+ ":committed");
//				++commissionNum;
//			}
//			else if (rclass != 0) {
//				System.out.println("\t Ground truth "+rclass+" Predicted "+ label+ ": omitted");
//				++omissionNum;
//			}	
//			else {
//				System.out.println("\t Ground truth "+rclass+" Predicted "+ label+ ": induced");
//				++inducedNum;
//			}
//
////		}
////	} 
//
//	}
	
	/*private void computeMetricsperIndividual( int[] labels, int[][] classification, int nOfConcepts, Integer te,int[] foundNum,int[] trueNum, int[] hitNum, int[] matchingNum, int[] commissionNum, int[] omissionNum, int[] inducedNum){
	System.out.println();
		for (int c=0; c < nOfConcepts; c++) {
			int rclass= classification[c][te];
			computeMetricsperIndividualperClass(labels[c], rclass, classification, c, te, foundNum, trueNum, hitNum, matchingNum, commissionNum, omissionNum, inducedNum);
			
		}
		
	}
	*/
	public void printResults(OWLClassExpression d, int nFolds){

	System.out.println("\n\n >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> OUTCOMES"+ d +"FOLD #"+nFolds);
	//stream.println("\n\n >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> OUTCOMES FOLD #"+fold);
		System.out.printf("\n %10s %10s %10s %10s %10s %10s\n", "matching", "commission", "omission", "induction","precision","recall");
				System.out.printf(" %10.3f %10.3f %10.3f %10.3f %10.3f %10.3f \n", getMatchRate(), getCommissionRate(), getOmissionRate(), getInductionRate(), getPrecision(), getRecall());
		
	
		
	}
	
	
	
	
	
	

	
}
