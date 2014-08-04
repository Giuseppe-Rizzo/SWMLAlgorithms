package evaluation.metrics;

import utils.MathUtils;

public class GlobalPerformanceMetricsComputation {

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
		posInstances= new double[nOfConcepts][nFolds];
		negInstances= new double[nOfConcepts][nFolds];
		uncInstances= new double[nOfConcepts][nFolds];

		totMatchingRate=new double[nOfConcepts][nFolds]; // per OWLClass per fold
		totCommissionRate= new double[nOfConcepts][nFolds]; // per OWLClass per fold
		totOmissionRate= new double[nOfConcepts][nFolds]; // per OWLClass per fold
		totInducedRate= new double[nOfConcepts][nFolds]; // per OWLClass per fold
		totPrecision= new double[nOfConcepts][nFolds];
		totRecall= new double[nOfConcepts][nFolds];
	}

	public void computeMetricsperIndividualperClass( int label, int rclass, int[][] classification, int c, int te, int[] foundNum,int[] trueNum, int[] hitNum, int[] matchingNum, int[] commissionNum, int[] omissionNum, int[] inducedNum ){
//		for (int c=0; c < nOfConcepts; c++) {

//			int rclass = classification[c][te];
//			Triple<Integer,Integer, Integer> compositionConceptC= testSetComposition.get(c);


			// count the number of positive, negative and uncertain instances
//			if(rclass==1){
//				compositionConceptC.setFirstElem((compositionConceptC.getFirstElem())+1);
//			}else if (rclass==-1){
//				compositionConceptC.setSecondElem((compositionConceptC.getSecondElem())+1); //
//			}
//			else
//				compositionConceptC.setThirdElem((compositionConceptC.getThirdElem())+1);

			if (label == 1)
				++foundNum[c];


			if (rclass == +1)
				++trueNum[c];


			if (label == rclass) { 
				System.out.println("\t"+rclass+"matched");
				++matchingNum[c];
				if (rclass==1) 
					++hitNum[c];
			}
			else if (Math.abs(label - rclass)>1) { 
				System.out.println("\t "+rclass+"committed");
				++commissionNum[c];
			}
			else if (rclass != 0) {
				System.out.println("\t"+rclass+" omitted");
				++omissionNum[c];
			}	
			else {
				System.out.println("\t"+rclass+" induced");
				++inducedNum[c];
			}

//		}
//	} // for t - inPartition loop

	}
	
	public void computeMetricsperIndividual( int[] labels, int[][] classification, int nOfConcepts, int te,int[] foundNum,int[] trueNum, int[] hitNum, int[] matchingNum, int[] commissionNum, int[] omissionNum, int[] inducedNum){
	System.out.println();
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
		System.out.println("No esempi test: "+testExs.length);
		for (int te=0; te < testExs.length; te++ ) { 

			int indTestEx = testExs[te];
			computeMetricsperIndividual(labels[te], classification, nOfConcepts, indTestEx, foundNum, trueNum, hitNum, matchingNum, commissionNum, omissionNum, inducedNum);
		}
	System.out.println("\n\n >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> OUTCOMES FOLD #"+fold);
		System.out.printf("\n%10s %10s %10s %10s %10s %10s %10s\n", "Query#",  "matching", "commission", "omission", "induction","precision","recall");
		
		for (int c=0; c < nOfConcepts; c++) {
			System.out.println(matchingNum[c]+ "-"+ commissionNum[c]+ "-"+ omissionNum[c]+""+inducedNum[c]);
			
			totMatchingRate[c][fold] = matchingNum[c]/(double) testExs.length; 
			System.out.println(totMatchingRate[c][fold]);
			
//
			totCommissionRate[c][fold] = commissionNum[c]/(double)testExs.length; 
			System.out.println(totCommissionRate[c][fold]);
			totOmissionRate[c][fold] = omissionNum[c]/(double)testExs.length;  
			totInducedRate[c][fold] = inducedNum[c]/(double)testExs.length;
			System.out.printf("%10d %10.3f %10.3f %10.3f %10.3f \n", c, totMatchingRate[c][fold], totCommissionRate[c][fold], totOmissionRate[c][fold], totInducedRate[c][fold]);

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
		
		
		System.out.println("\n\n\n @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ OVERALL OUTCOMES");
		System.out.printf("\n%10s %10s %10s %10s %10s %10s %10s\n", "Query#",  "matching", "commission", "omission", "induction","precision","recall");

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

			System.out.printf("%10d %10.2f %10.2f %10.2f %10.2f \n", c, 
					AvgMatching*100, AvgCommission*100, avgOmission*100, avgInduction*100);
			//			accMatchingAvgs += AvgMatching;
			//			accCommissionAvgs += AvgCommission;
			//			accOmissionAvgs += avgOmission;
			//			accInductionAvgs += avgInduction;



		}
		System.out.println();

		System.out.println("----------------------------------------------------------------------------------------------");
		// for each statistic

		double matchingAvg 		= MathUtils.avg(matchingAvgArray);
		double matchingSD= MathUtils.stdDeviation(matchingAvgArray);

		double commissionAvg 	= MathUtils.avg(commissionAvgArray);
		double commissionSD= MathUtils.stdDeviation(commissionAvgArray);


		double omissionAvg 		= MathUtils.avg(omissionAvgArray);
		double omissionSD= MathUtils.stdDeviation(omissionAvgArray);


		double inductionAvg 	= MathUtils.avg(inducedAvgArray);
		double inductionSD = MathUtils.stdDeviation(inducedAvgArray);

		double globalAM = 0;
		double globalAMSD = 0;

		double globalCM = 0;
		double globalCMSD = 0;

		System.out.printf("%10s %10.2f %10.2f %10.2f %10.2f %10.2f %10.2f \n", "avg Values", 
				matchingAvg*100, commissionAvg*100, omissionAvg*100, inductionAvg*100,globalAM*100,globalCM*100);
		System.out.printf("%10s %10.2f %10.2f %10.2f %10.2f %10.2f %10.2f \n", "stdDev Values", 
				matchingSD*100, commissionSD*100, omissionSD*100, inductionSD*100,globalAMSD*100,globalCMSD*100);
		
	}
	
}
