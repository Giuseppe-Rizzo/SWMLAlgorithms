package classifiers.perceptron;

import org.semanticweb.owlapi.model.OWLClassExpression;

import classifiers.SupervisedLearnable;
import classifiers.knn.FeaturesDrivenDistance;
import knowledgeBasesHandler.KnowledgeBase;

/**
 * Implementation of kernel perceptron with a kernel between individuals of a knowledge base
 * @author GR
 *
 */
public class Perceptron implements SupervisedLearnable {


	private KnowledgeBase kb;
	private int[][]weights;
	private Integer[] trainingExs;
	private int[][]results;

	public Perceptron(KnowledgeBase kb,int nOfConcepts){

		this.kb=kb; 
	}

	@Override
	public void training(int[][] results, Integer[] trainingExs, OWLClassExpression[] testConcepts,
			OWLClassExpression[] negTestConcepts) {

		weights= new int[testConcepts.length][trainingExs.length];
		int[] misclassifications= new int[trainingExs.length]; // misclassification counter
		this.trainingExs= trainingExs;
		this.results= results;
		//max number of iterations

		for (int c=0;c<testConcepts.length;c++){ 
			
			for (int epochs=0;  epochs< 200; epochs++){
				for (int j=0; j<trainingExs.length; j++){
		//		System.out.println("Current examples: "+j);
					int label=0;
					double k=0;
					//compute the sum of the kernels 
					for(int i=0; i<trainingExs.length;i++){
			//			System.out.print( j+ "tr ex: "+trainingExs[i]);
						//pairwise comparison
						double kernelij=(1-FeaturesDrivenDistance.simpleDistance(trainingExs[j], trainingExs[i]));
						// uncertain by default; 
						k = (misclassifications[j]*results[c][trainingExs[j]]*kernelij);
					}

					if (k>0) 
						label=+1; 
					else if (k<0)
						label=-1;

					if ( results[c][trainingExs[j]]!=label) // results is a matrix nOfconcepts x number of instances
						misclassifications[j]++;
				}
			}

			weights[c]= misclassifications;

		}

	}

	@Override
	public int[][] test(int f, Integer[] testExs, OWLClassExpression[] testConcepts) {
		// TODO Auto-generated method stub
		int [][] labels= new int[testExs.length][testConcepts.length];
		double k=0;
		for (int c=0; c<testConcepts.length;c++) {// for each concept
			for (int i=0 ; i< testExs.length;i++) {// for each example	
				k=0;
				for (int j=0; j<trainingExs.length; j++){
					//compute the sum of the kernels for(int j=0; j<trainingExs.length;j++){					
					//pairwise comparison
					double kernelij=(1-FeaturesDrivenDistance.simpleDistance(testExs[i], trainingExs[j]));
					// uncertain by default; 
					k += (weights[c][j]*results[c][trainingExs[j]]*kernelij);
				}
			 if (k>0)
				  labels[i][c]= +1;
			 else if (k<0)
				 labels[i][c]= -1;
			}
		}


return labels;
	}





@Override
public double[] getComplexityValues() {
	// TODO Auto-generated method stub
	return null;
}

}
