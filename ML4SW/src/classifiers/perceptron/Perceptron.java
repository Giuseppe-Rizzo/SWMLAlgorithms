package classifiers.perceptron;

import org.semanticweb.owlapi.model.OWLClassExpression;

import classifiers.SupervisedLearnable;
import classifiers.knn.FeaturesDrivenDistance;
import knowledgeBasesHandler.KnowledgeBase;

/**
 * Implementation of kernel perceptron with a kernel between individuals of a knowledge base
 * @author Utente
 *
 */
public class Perceptron implements SupervisedLearnable {
	
	
	private KnowledgeBase kb;

	public Perceptron(KnowledgeBase kb){
		
		this.kb=kb; 
	}

	@Override
	public void training(int[][] results, Integer[] trainingExs, OWLClassExpression[] testConcepts,
			OWLClassExpression[] negTestConcepts) {
		
		int[] misclassifications= new int[trainingExs.length]; // misclassification counter
		for (int j=0; j<trainingExs.length; j++){
			int label=0;
			double k=0;
			for(int i=0; i<trainingExs.length;i++){
				//pairwise comparison
				double kernelij=(1-FeaturesDrivenDistance.simpleDistance(trainingExs[j], trainingExs[i]));
				   // uncertain by default; 
				   k = (misclassifications[j]*results[i][0]*kernelij);
			}
			
			if (k>0) 
				  label=+1;
			  else if (k<0)
			     label=-1;
			  
			if ( results[trainingExs[j]][0]!=label)
				misclassifications[j]++;
			
			
		}
		
		
		
		

	}

	@Override
	public int[][] test(int f, Integer[] testExs, OWLClassExpression[] testConcepts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getComplexityValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
