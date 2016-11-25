	
	
	
	package classifiers;

	import org.semanticweb.owl.model.OWLDescription;


	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileOutputStream;
	import java.io.PrintStream;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.HashSet;

	import org.mindswap.pellet.owlapi.Reasoner;
	import org.semanticweb.owl.model.OWLIndividual;

	import utils.Generator;

	import knowledgeBasesHandler.KnowledgeBase;
import knowledgeBasesHandler.KnowledgeBaseForRoughConceptLearning;


import classifiers.PUlearning.RoughTDT;
	import classifiers.trees.TDTClassifier;
	import classifiers.trees.models.DLTree;
	import evaluation.Parameters;
import evaluation.PruningType;
	/**
	 * Wrapper for TDTClassifier
	 * @author Utente
	 *
	 */
	public class RoughTerminologicalDecisionTreeInducer  implements SupervisedLearnable {
		DLTree[] lowerTrees; //for each query concept induce an ensemble
		DLTree[] upperTrees;
		boolean classificationResultsAvailable;
		RoughTDT cl;
		static PrintStream stream;
		KnowledgeBaseForRoughConceptLearning kb;
		int nOfConcepts;	
		public RoughTerminologicalDecisionTreeInducer( KnowledgeBase k, int nOfConcepts){
			this.nOfConcepts=nOfConcepts;
			kb=(KnowledgeBaseForRoughConceptLearning)k; // cast for knowledge base 
			lowerTrees = new DLTree [nOfConcepts]; 
			upperTrees= new DLTree[nOfConcepts];
			cl= new RoughTDT(kb);
			
			try {
				File f= new File("Models.txt");
				if(!f.exists())
				  stream= new PrintStream(new FileOutputStream("Models.txt"), true);
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			} // append

		}

		/* (non-Javadoc)
		 * @see classifiers.SupervisedLearnable#training(java.lang.Integer[], org.semanticweb.owl.model.OWLDescription[], org.semanticweb.owl.model.OWLDescription[])
		 */
		@Override
		public void training(int[][] results,Integer[] trainingExs, OWLDescription[] testConcepts, OWLDescription[] negTestConcepts){
			// flag for avoidance instance check tests
			if (!classificationResultsAvailable){
				RoughTDT.setClassificationResultsForLowerUpperApprox(kb, testConcepts, negTestConcepts, kb.getIndividuals());
				classificationResultsAvailable=true;
			}
			
			Reasoner reasoner = kb.getReasoner();
			OWLIndividual[] allExamples= kb.getIndividuals();

			//		ArrayList<Triple<Integer, Integer, Integer>> testSetComposition= new ArrayList<Triple<Integer, Integer, Integer>>();
			int length = testConcepts!=null?testConcepts.length:1;
			//int[][] results= kb.getClassMembershipResult();
			for (int c=0; c<length; c++) {

				cl.training(trainingExs, c, testConcepts, negTestConcepts);
//				stream.println(trees[c]);
				lowerTrees[c]=cl.getLowerApproximation();
				upperTrees[c]=cl.getUpperApproximation();
				System.out.printf("--- tree #%d was induced. \n\n",c);

			}



		}


		/* (non-Javadoc)
		 * @see classifiers.SupervisedLearnable#test(int, java.lang.Integer[], org.semanticweb.owl.model.OWLDescription[])
		 */
		@Override
		public int[][] test(int f,Integer[] testExs,OWLDescription[] testConcepts) {
			int[][] labels= new int[testExs.length][nOfConcepts]; // classifier answers for each example and for each concept
			for (int te=0; te < testExs.length; te++ ) { 

				int indTestEx = testExs[te];

				System.out.print("\n\nFold #"+f);
				System.out.println(" --- Classifying Example " + (te+1) +"/"+testExs.length +" [" + indTestEx + "] " + kb.getIndividuals()[indTestEx]);

				int[] indClassifications = new int[nOfConcepts];
				//			cl.classifyExamplesTree(indTestEx, forests, indClassifications, testConcepts);
				
				for (int c=0; c<testConcepts.length;c++){
					indClassifications[c]=cl.classifyExample(indTestEx, lowerTrees[c], upperTrees[c]);

				}
				labels[te]=indClassifications; 
				


			}
			return labels;

		}

		@Override
		public double[] getComplexityValues() {
			
//			double[] complexityValue= new double[trees.length]; // a measure to express the model complexity (e.g. the number of nodes in a tree)
//			
//			
//			for(int i=0; i<trees.length; i++){
//				
//				double current=trees[i].getComplexityMeasure();
//				complexityValue[i]= current;
//				
//			}
//			
//			
//			return complexityValue;
			return null;
		}

	
	
	
	

}
