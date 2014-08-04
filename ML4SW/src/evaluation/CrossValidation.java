package evaluation;

import java.util.Random;

public class CrossValidation {	
	
	public final int nOfFolds; 				//	number of folds
	public final int nOfExs; 				//	number of examples	
	public final int nOfPlaces; 			//	number of places in the mapping
	
	private Random generator;			//	random number generator
	private int exMapping[]; 				// array of the example indices 
	
	public final int nPerFold;
	public final int UNASSIGNED = -1;
	
	
	
	
	/**
	 * @param nonfp number of folds
	 * @param nonfp number of examples
	 */
	public CrossValidation(int nFolds, int nExs) {
		
		generator = new Random();
		
		nOfFolds =  nFolds;
		nOfExs = nExs;
		nPerFold = (int)Math.ceil((float)nExs/nFolds);
		nOfPlaces = nOfFolds*nPerFold;

		exMapping = new int[nOfPlaces];
		
		for (int p=0; p < nOfPlaces; p++) 
			exMapping[p] = UNASSIGNED;
		
		// random generation of a permutation of the integers in [0,nexs-1]
		for (int i=0; i<nExs; i++) {
			//	 find a random place for this i-th example index
			int rplace = Math.abs(generator.nextInt() % nExs); 
			while (exMapping[rplace] != UNASSIGNED) 
				rplace = (rplace + 1) % nExs;
			exMapping[rplace] = i;	
			System.out.printf("Ex: %4d -->  Pos: %4d\n",i,rplace);
		}
		System.out.println("No of folds: "+nOfFolds);
		System.out.println("No of examples: "+nExs);
		System.out.println("No of places: "+nOfPlaces);
		System.out.println("No of examples per fold: "+nPerFold);		
	}


/** 
 * create training set mappings of examples from other (nfolds-1) partitions
 * @param foldNo current fold number
 * @return training examples
 */
public Integer[] getTrainingExs(int foldNo) {

	int nTrainExs;
	if (foldNo<nOfFolds-1) { // for all but the last folds
		nTrainExs = (nOfFolds-1)*nPerFold - (nOfPlaces - nOfExs);
		System.out.printf("No of training examples %d - fold %d\n",nTrainExs,foldNo);
	} else { // last fold may contain fewer examples
		nTrainExs = (nOfFolds-1)*nPerFold;
		System.out.printf("No of training examples %d fold: %d\n",foldNo,nTrainExs);
	}
	
	Integer[] trainingExs = new Integer[nTrainExs];
	java.util.Arrays.fill(trainingExs, -1);
	int IndTrEx = 0;
	for (int f = 0; f<nOfFolds; f++)
		//è necessario ignorare la fold usata per il test
		if (foldNo != f)
			for (int t=f*nPerFold; t < (f+1)*nPerFold; t++) 
				if (exMapping[t] != UNASSIGNED) 
						{
							trainingExs[IndTrEx] = exMapping[t];
							IndTrEx++;
						}						

	if (IndTrEx != nTrainExs) System.exit(1); // incorrect number of tr. examples 
	return trainingExs;
	}



/**
 * 
 * @param fold fold number
 * @param n index in the fold 
 * @return example index
 */
	public int getIndex(int fold, int n) {
		if (n < nPerFold && fold < nOfFolds)
			return exMapping[nPerFold*fold+n];
		else 
			return UNASSIGNED;	
	}



}
