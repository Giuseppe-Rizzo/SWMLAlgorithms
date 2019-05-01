package it.uniba.di.lacam.ml.evaluation.metrics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import it.uniba.di.lacam.ml.evaluation.Evaluation;
import it.uniba.di.lacam.ml.evaluation.Parameters;
import it.uniba.di.lacam.ml.evaluation.designOfExperiments.AlgorithmName;
import it.uniba.di.lacam.ml.utils.MathUtils;

public class GlobalPerformanceMetricsComputation extends AbstractMetrics{
	private PrintStream stream;
	@SuppressWarnings("unused")
	private double[][] posInstances;
	@SuppressWarnings("unused")
	private double[][] negInstances;
	@SuppressWarnings("unused")
	private double[][] uncInstances;
	double[][] totMatchingRate; // per OWLClass per fold
	double[][] totCommissionRate; // per OWLClass per fold
	double[][] totOmissionRate;// per OWLClass per fold
	double[][] totPrecision;
	double[][] totRecall;

	double[][] totInducedRate;

	public GlobalPerformanceMetricsComputation(int nOfConcepts,int nFolds){
		
		super();
		posInstances= new double[nOfConcepts][nFolds];
		negInstances= new double[nOfConcepts][nFolds];
		uncInstances= new double[nOfConcepts][nFolds];

		totMatchingRate=new double[nOfConcepts][nFolds]; // per OWLClass per fold
		totCommissionRate= new double[nOfConcepts][nFolds]; // per OWLClass per fold
		totOmissionRate= new double[nOfConcepts][nFolds]; // per OWLClass per fold
		totInducedRate= new double[nOfConcepts][nFolds]; // per OWLClass per fold
		totPrecision= new double[nOfConcepts][nFolds];
		totRecall= new double[nOfConcepts][nFolds];
		
		try {
			String string= "";
			if ((Parameters.algorithm.compareTo(AlgorithmName.TerminologicalDecisionTree)==0)||((Parameters.algorithm.compareTo(AlgorithmName.DSTTerminologicalDecisionTree)==0))){
			 string= "AccuracyEvaluation"+Parameters.algorithm+".txt";
			 
			}else if((Parameters.algorithm.compareTo(AlgorithmName.DSTTerminologicalRandomForests)==0) ||(Parameters.algorithm.compareTo(AlgorithmName.TerminologicalRandomForests)==0)){
				 string= "AccuracyEvaluation"+Parameters.algorithm+""+ Parameters.NTREES+"-"+Parameters.samplingrate+".txt";
			}else{
				
				string= "AccuracyEvaluation"+Parameters.algorithm+".txt";
			}
			stream= new PrintStream(new FileOutputStream(string), true);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} // append
	}

	public void computeMetricsperIndividualperClass( int label, int rclass, int[][] classification, int c, int te, int[] foundNum,int[] trueNum, int[] hitNum, int[] matchingNum, int[] commissionNum, int[] omissionNum, int[] inducedNum ){


			if (label == 1){
				foundNum[c]++;
				
				
			}

			if (rclass == +1){
				trueNum[c]++;
				//System.out.println("----"+trueNum[c]);
			}


			if (label == rclass) { 
				//System.out.println("\t"+rclass+"matched");
				++matchingNum[c];
				if (rclass==1) 
					++hitNum[c];
			}
			else if (Math.abs(label - rclass)>1) { 
				//System.out.println("\t "+rclass+"committed");
				++commissionNum[c];
			}
			else if (rclass != 0) {
				//System.out.println("\t"+rclass+" omitted");
				++omissionNum[c];
			}	
			else {
				//System.out.println("\t"+rclass+" induced");
				++inducedNum[c];
			}

//		}
//	} // for t - inPartition loop

	}
	
	public void computeMetricsperIndividual( int[] labels, int[][] classification, int nOfConcepts, int te,int[] foundNum,int[] trueNum, int[] hitNum, int[] matchingNum, int[] commissionNum, int[] omissionNum, int[] inducedNum){
	
		for (int c=0; c < nOfConcepts; c++) {
			int rclass= classification[c][te];
			computeMetricsperIndividualperClass(labels[c], rclass, classification, c, te, foundNum, trueNum, hitNum, matchingNum, commissionNum, omissionNum, inducedNum);
			
		}
		
	}
	
	public void computeMetricsPerFold(int fold, int labels[][], int[][] classification, int nOfConcepts, Integer[] testExs){
		
		int[] foundNum= new int[nOfConcepts];
		int[] trueNum= new int[nOfConcepts]; 
		int[] hitNum = new int[nOfConcepts];
		int[] matchingNum=new int[nOfConcepts]; 
		int[] commissionNum= new int[nOfConcepts];
		int[] omissionNum= new int[nOfConcepts] ;
		int[] inducedNum= new int[nOfConcepts] ;

		for (int te=0; te < testExs.length; te++ ) { 

			int indTestEx = testExs[te];
			computeMetricsperIndividual(labels[te], classification, nOfConcepts, indTestEx, foundNum, trueNum, hitNum, matchingNum, commissionNum, omissionNum, inducedNum);
		}
	System.out.println("\n\n >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> OUTCOMES FOLD #"+fold);
	stream.println("\n\n >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> OUTCOMES FOLD #"+fold);
		System.out.printf("\n%10s %10s %10s %10s %10s %10s %10s\n", "Query#",  "matching", "commission", "omission", "induction","precision","recall");
		stream.printf("\n%10s %10s %10s %10s %10s %10s %10s\n", "Query#",  "matching", "commission", "omission", "induction","precision","recall");
		
		for (int c=0; c < nOfConcepts; c++) {
						
			totMatchingRate[c][fold] = matchingNum[c]/(double) testExs.length; 
			;
			
//
			totCommissionRate[c][fold] = commissionNum[c]/(double)testExs.length; 
			
			totOmissionRate[c][fold] = omissionNum[c]/(double)testExs.length;  
			totInducedRate[c][fold] = inducedNum[c]/(double)testExs.length;
			totPrecision[c][fold] = ((double)hitNum[c]+1)/(((double)foundNum[c])+1);
			totRecall[c][fold]= ((double)hitNum[c]+1)/(((double)trueNum[c])+1);
			System.out.printf("%10d %10.3f %10.3f %10.3f %10.3f %10.3f %10.3f \n", c, totMatchingRate[c][fold], totCommissionRate[c][fold], totOmissionRate[c][fold], totInducedRate[c][fold],totPrecision[c][fold], totRecall[c][fold]);
			stream.printf("%10d %10.3f %10.3f %10.3f %10.3f %10.3f %10.3f \n", c, totMatchingRate[c][fold], totCommissionRate[c][fold], totOmissionRate[c][fold], totInducedRate[c][fold], totPrecision[c][fold], totRecall[c][fold]);
		}

	
		
	}
	public void  computeOverAllResults(int nOfConcepts) {
		
		double[] matchingStdDev = new double[nOfConcepts]; // per OWLClass per fold
		@SuppressWarnings("unused")
		double[] commissionStdDev = new double[nOfConcepts]; // per OWLClass per fold
		@SuppressWarnings("unused")
		double[] omissionStdDev = new double[nOfConcepts]; // per OWLClass per fold
		@SuppressWarnings("unused")
		double[] inducedStdDev = new double[nOfConcepts]; // per OWLClass per fold

		double[] matchingAvgArray= new double[nOfConcepts]; // per OWLClass per fold
		double[] commissionAvgArray = new double[nOfConcepts]; // per OWLClass per fold
		double[] omissionAvgArray = new double[nOfConcepts]; // per OWLClass per fold
		double[] inducedAvgArray = new double[nOfConcepts]; // per OWLClass per fold
		double[] precisionAvgArray = new double[nOfConcepts]; // per OWLClass per fold
		double[] recallAvgArray = new double[nOfConcepts]; 
		
		
		
		System.out.println("\n\n\n @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ OVERALL OUTCOMES");
		stream.println("\n\n\n @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ OVERALL OUTCOMES");
		System.out.printf("\n%10s %10s %10s %10s %10s %10s %10s\n", "Query#",  "matching", "commission", "omission", "induction","precision","recall");
		stream.printf("\n%10s %10s %10s %10s %10s %10s %10s\n", "Query#",  "matching", "commission", "omission", "induction","precision","recall");
		
		for (int c=0; c < nOfConcepts; c++) {
			// for each statistic
			double AvgMatching = MathUtils.avg(totMatchingRate[c]);
			matchingAvgArray[c]=AvgMatching;
			matchingStdDev[c] = MathUtils.stdDeviation(totMatchingRate[c]);


			double AvgCommission = MathUtils.avg(totCommissionRate[c]);
			commissionAvgArray[c]=AvgCommission;


			double avgOmission = MathUtils.avg(totOmissionRate[c]);
			omissionAvgArray[c]=avgOmission;


			double avgInduction = MathUtils.avg(totInducedRate[c]);
			inducedAvgArray[c]=avgInduction;
			
			double avgPrecision = MathUtils.avg(totPrecision[c]);
			precisionAvgArray[c]= avgPrecision;
			
			double avgRecall=MathUtils.avg(totRecall[c]);
			recallAvgArray[c]= avgRecall;

			System.out.printf("%10d %10.2f %10.2f %10.2f %10.2f \n", c, 
					AvgMatching*100, AvgCommission*100, avgOmission*100, avgInduction*100, avgPrecision*100, avgRecall*100);
			stream.printf("%10d %10.2f %10.2f %10.2f %10.2f \n", c, 
					AvgMatching*100, AvgCommission*100, avgOmission*100, avgInduction*100, avgPrecision*100, avgRecall*100);
		



		}
		System.out.println();
		stream.println();
		System.out.println("----------------------------------------------------------------------------------------------");
		stream.println("----------------------------------------------------------------------------------------------");
		
		// for each statistic

		double matchingAvg 		= MathUtils.avg(matchingAvgArray);
		double matchingSD= MathUtils.stdDeviation(matchingAvgArray);

		double commissionAvg 	= MathUtils.avg(commissionAvgArray);
		double commissionSD= MathUtils.stdDeviation(commissionAvgArray);


		double omissionAvg 		= MathUtils.avg(omissionAvgArray);
		double omissionSD= MathUtils.stdDeviation(omissionAvgArray);


		double inductionAvg 	= MathUtils.avg(inducedAvgArray);
		double inductionSD = MathUtils.stdDeviation(inducedAvgArray);

		double precisionAvg 	= MathUtils.avg(precisionAvgArray);
		double precisionSD = MathUtils.stdDeviation(precisionAvgArray);

		double recallAvg 	= MathUtils.avg(recallAvgArray);
		double recallSD = MathUtils.stdDeviation(recallAvgArray);


		System.out.printf("%10s %10.2f %10.2f %10.2f %10.2f %10.2f %10.2f \n", "avg Values", 
				matchingAvg*100, commissionAvg*100, omissionAvg*100, inductionAvg*100,precisionAvg*100,recallAvg*100);
		stream.printf("%10s %10.2f %10.2f %10.2f %10.2f %10.2f %10.2f \n", "avg Values", 
				matchingAvg*100, commissionAvg*100, omissionAvg*100, inductionAvg*100,precisionAvg*100,recallAvg*100);
		System.out.printf("%10s %10.2f %10.2f %10.2f %10.2f %10.2f %10.2f \n", "stdDev Values", 
				matchingSD*100, commissionSD*100, omissionSD*100, inductionSD*100,precisionSD*100,recallSD*100);
		stream.printf("%10s %10.2f %10.2f %10.2f %10.2f %10.2f %10.2f \n", "stdDev Values", 
				matchingSD*100, commissionSD*100, omissionSD*100, inductionSD*100, precisionSD*100,recallSD*100);
		
	}
	
	
	
}
