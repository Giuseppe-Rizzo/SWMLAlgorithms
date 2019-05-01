package it.uniba.di.lacam.ml.evaluation.task;


import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;

import it.uniba.di.lacam.ml.classifiers.SupervisedLearnable;
import it.uniba.di.lacam.ml.evaluation.CrossValidation;
import it.uniba.di.lacam.ml.evaluation.Evaluation;
import it.uniba.di.lacam.ml.evaluation.Parameters;
import it.uniba.di.lacam.ml.evaluation.designOfExperiments.AlgorithmName;
import it.uniba.di.lacam.ml.evaluation.metrics.GlobalPerformanceMetricsComputation;
import it.uniba.di.lacam.ml.evaluation.metrics.ModelComplexityEvaluation;
import it.uniba.di.lacam.ml.evaluation.task.generators.ConceptGenerator;
import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;
import it.uniba.di.lacam.ml.utils.Couple;
import it.uniba.di.lacam.ml.utils.Generator;




/**
 * Class-membership prediciton task and the related evaluation
 * @author Giuseppe
 *
 */
public class ClassMembershipPrediction implements Evaluation {
	protected static KnowledgeBase kb;
	protected OWLIndividual[] allExamples;
	protected OWLClassExpression[] testConcepts;
	protected OWLClassExpression[] negTestConcepts;
	//	private OWLClass[] concetti;
	protected int[][] classification;

	//	private String urlOwlFile;
	static final int QUERY_NB = 30;

	static final double THRESHOLD = 0.05;
	public PrintStream console = System.out;
	//	private RegolaCombinazione regola;

	public ClassMembershipPrediction(){



	}
	public ClassMembershipPrediction(KnowledgeBase k){

		kb=k;
		allExamples=kb.getIndividuals();
		ConceptGenerator qg= new ConceptGenerator(kb);
		testConcepts=qg.generateQueryConcepts(Parameters.NUMGENCONCEPTS);

		negTestConcepts=new OWLClassExpression[testConcepts.length];
		for (int c=0; c<testConcepts.length; c++) 
			negTestConcepts[c] = kb.getDataFactory().getOWLObjectComplementOf(testConcepts[c]);
		// Classification wrt all query concepts
		System.out.println("\nClassifying all examples ------ ");
		kb.getReasoner();
		//concetti=kb.getClasses();
		//		urlOwlFile=kb.getURL();
		classification=kb.getClassMembershipResult(testConcepts, negTestConcepts,allExamples);

	}


	/* (non-Javadoc)
	 * @see forest.Evaluation#bootstrap(int)
	 */
	@Override
	public  void bootstrap( int nFolds, String className ) throws Exception {
		System.out.println(nFolds+"-fold BOOTSTRAP Experiment on ontology: ");	

		Class<?> classifierClass =ClassLoader.getSystemClassLoader().loadClass(className);
		int nOfConcepts = testConcepts!=null?testConcepts.length:1;

		GlobalPerformanceMetricsComputation gpmc = new GlobalPerformanceMetricsComputation(nOfConcepts,nFolds);
		ModelComplexityEvaluation mce = new ModelComplexityEvaluation(nOfConcepts,nFolds);

		// main loop on the folds
		int[] ntestExs = new int[nFolds];
		for (int f=0; f< nFolds; f++) {			

			System.out.print("\n\nFold #"+f);
			System.out.println(" **************************************************************************************************");

			Set<Integer> trainingExsSet = new HashSet<Integer>();

			Set<Integer> testingExsSet = new HashSet<Integer>();
			for (int r=0; r<allExamples.length; r++) 
				trainingExsSet.add(Generator.generator.nextInt(allExamples.length));

			for (int r=0; r<allExamples.length; r++) {
				if (! trainingExsSet.contains(r)) 
					testingExsSet.add(r);
			}			
			// splitting in growing and pruning set (70-30 ratio)

			Integer[] trainingExs = new Integer[0];
			Integer[] testExs = new Integer[0];
			trainingExs = trainingExsSet.toArray(trainingExs);
			testExs = testingExsSet.toArray(testExs);
			//			pruningSet=pruningExsSet.toArray(pruningSet);
			ntestExs[f] = testExs.length;
			//			System.setOut(new PrintStream("C:/Users/Utente/Documents/biopax.txt"));

			// training phase: using all examples but those in the f-th partition
			System.out.println("Training is starting...");


			
			SupervisedLearnable cl=  (SupervisedLearnable)(classifierClass.getConstructor(KnowledgeBase.class, int.class)).newInstance(kb,nOfConcepts);
			int[][] results= kb.getClassMembershipResult();
			
			cl.training(results, trainingExs, testConcepts, negTestConcepts);

			// store model complexity it.uniba.di.lacam.ml.evaluation
			double[] complexityValues= cl.getComplexityValues();
			if (testConcepts!=null){
				for (int i=0; i<testConcepts.length;i++){
					if (Parameters.algorithm==AlgorithmName.TerminologicalDecisionTree)
						mce.setValues(i, f, complexityValues[i]);
					else
						mce.setValues(i, f, 0);

				}
			}


			//			}
//			System.out.println("End of Training.\n\n");
//
			int[][] labels=cl.test(f, testExs, testConcepts);

			gpmc.computeMetricsPerFold(f, labels, classification, nOfConcepts, testExs);

		} // for f - fold look


		gpmc.computeOverAllResults(nOfConcepts);
		mce.computeModelComplexityPerformance();
	} // bootstrap DLDT induction	



	public  void stratifiedBootstrap( int nFolds, String className ) throws Exception {
		System.out.println(nFolds+"-fold BOOTSTRAP Experiment on ontology: ");	

		Class<?> classifierClass =ClassLoader.getSystemClassLoader().loadClass(className);
		int nOfConcepts = testConcepts!=null?testConcepts.length:1;

		GlobalPerformanceMetricsComputation gpmc = new GlobalPerformanceMetricsComputation(nOfConcepts,nFolds);
		ModelComplexityEvaluation mce = new ModelComplexityEvaluation(nOfConcepts,nFolds);
		ArrayList<Couple<Set<Integer>,Set<Integer>>> splits= new ArrayList<Couple<Set<Integer>,Set<Integer>>>();
		int[][] labels=kb.getClassMembershipResult();
		for (int c=0; c<nOfConcepts;c++){
			int[] label= labels[c]; // labels for the current concept
			ArrayList<Integer> positive= new ArrayList<Integer>();
			ArrayList<Integer> negative= new ArrayList<Integer>();
			ArrayList<Integer> uncertain= new ArrayList<Integer>();
		
			for (int i=0; i<label.length;i++){
				 if (label[i]==+1)
					 positive.add(i);
				 else if (label[i]==-1)
					 negative.add(i);
				 else
					 uncertain.add(i);
				
				
			}
			
			
			
			
			for (int f=0; f< nFolds; f++) {			

				System.out.print("\n\nFold #"+f);
				System.out.println(" **************************************************************************************************");

				Set<Integer> trainingExsSet = new HashSet<Integer>();
				Set<Integer> testingExsSet = new HashSet<Integer>();

				for (int r=0; r<allExamples.length; r++){ 
					trainingExsSet.add(Generator.generator.nextInt(allExamples.length));
				}

				for (int r=0; r<allExamples.length; r++) {
					if (! trainingExsSet.contains(r)) 
						testingExsSet.add(r);
				}			
			}

		}
		
		
		// main loop on the folds
		int[] ntestExs = new int[nFolds];
		for (int f=0; f< nFolds; f++) {			

			System.out.print("\n\nFold #"+f);
			System.out.println(" **************************************************************************************************");

			Set<Integer> trainingExsSet = new HashSet<Integer>();
			Set<Integer> testingExsSet = new HashSet<Integer>();
			
			for (int r=0; r<allExamples.length; r++){ 
				trainingExsSet.add(Generator.generator.nextInt(allExamples.length));
			}

			for (int r=0; r<allExamples.length; r++) {
				if (! trainingExsSet.contains(r)) 
					testingExsSet.add(r);
			}			
			// splitting in growing and pruning set (70-30 ratio)

			Integer[] trainingExs = new Integer[0];
			Integer[] testExs = new Integer[0];
			trainingExs = trainingExsSet.toArray(trainingExs);
			testExs = testingExsSet.toArray(testExs);
			//			pruningSet=pruningExsSet.toArray(pruningSet);
			ntestExs[f] = testExs.length;
			//			System.setOut(new PrintStream("C:/Users/Utente/Documents/biopax.txt"));

			// training phase: using all examples but those in the f-th partition
			System.out.println("Training is starting...");

			SupervisedLearnable cl=  (SupervisedLearnable)(classifierClass.getConstructor(KnowledgeBase.class, int.class)).newInstance(kb,nOfConcepts);
			int[][] results= kb.getClassMembershipResult();
			
			cl.training(results, trainingExs, testConcepts, negTestConcepts);

			// store model complexity it.uniba.di.lacam.ml.evaluation
			double[] complexityValues= cl.getComplexityValues();
			for (int i=0; i<testConcepts.length;i++){
				if (Parameters.algorithm==AlgorithmName.TerminologicalDecisionTree)
				       mce.setValues(i, f, complexityValues[i]);
				else
					  mce.setValues(i, f, 0);

			}



			//			}
//			System.out.println("End of Training.\n\n");
//
//			int[][] labels=cl.test(f, testExs, testConcepts);
//
//			gpmc.computeMetricsPerFold(f, labels, classification, nOfConcepts, testExs);

		} // for f - fold look


		gpmc.computeOverAllResults(nOfConcepts);
		mce.computeModelComplexityPerformance();
	} // bootstrap DLDT induction	




	/* (non-Javadoc)
	 * @see forest.Evaluation#crossValidation(int)
	 */
	@Override
	public void crossValidation(int nFolds, String className) {
		System.out.println(nFolds+"-fold CROSS VALIDATION Experiment on ontology:");		
		Class<?> classifierClass = null;
		try {
			classifierClass = ClassLoader.getSystemClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		int nExs = allExamples.length;		

		CrossValidation cv = new CrossValidation(nFolds,nExs);

		//	OWLClassExpression[] testConcepts = allConcepts;
		int nTestConcepts = testConcepts!=null?testConcepts.length:1;

		GlobalPerformanceMetricsComputation gbpmc= new GlobalPerformanceMetricsComputation(nTestConcepts,nFolds);
		ModelComplexityEvaluation mce = new ModelComplexityEvaluation(nTestConcepts,nFolds);

		// main loop on the folds
		for (int f=0; f< nFolds; f++) {			


			System.out.print("\n\nFold #"+f);
			System.out.println(" **************************************************************************************************");

			Integer[] trainingExs = cv.getTrainingExs(f);




			SupervisedLearnable cl=null;
			try {
				cl = (SupervisedLearnable)(classifierClass.getConstructor(KnowledgeBase.class, int.class)).newInstance(kb,nTestConcepts);
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			}
			int[][] results= kb.getClassMembershipResult();
			cl.training(results,trainingExs, testConcepts, negTestConcepts);

			// store model complexity it.uniba.di.lacam.ml.evaluation
			double[] complexityValues= cl.getComplexityValues();
			for (int i=0; i<nTestConcepts;i++){
				
				if (Parameters.algorithm==AlgorithmName.TerminologicalDecisionTree)
				       mce.setValues(i, f, complexityValues[i]);
				else
					  mce.setValues(i, f, 0);
			}
//
//
//
			ArrayList<Integer> currentList= new ArrayList<Integer>();
//
//			// keep track of all  test examples in the current split
			for (int te=0; te < cv.nPerFold; te++ ) { 

				int indTestEx = cv.getIndex(f,te);
				if (indTestEx != cv.UNASSIGNED) {

					currentList.add(indTestEx);

				}

			}
			Integer[] currentFold = new Integer[currentList.size()];
			currentFold= currentList.toArray(currentFold);
//
			
//			for(int c=0; c<testConcepts.length;c++){
//				Set<Integer> posExs= new HashSet<Integer>();
//				Set<Integer> negExs=new HashSet<Integer>();
//				Set<Integer> undExs= new HashSet<Integer>();
//				for (int e=0; e<currentFold.length; e++){
//
//					
//					if (kb.getReasoner().hasType(allExamples[trainingExs[e]], testConcepts[c]))
//						posExs.add(trainingExs[e]);
//					else if (kb.getReasoner().hasType(allExamples[trainingExs[e]], negTestConcepts[c]))
//						negExs.add(trainingExs[e]);
//					else
//						undExs.add(trainingExs[e]);
//				}
//				System.out.println("Test set composition: "+ posExs.size()+" - "+ negExs.size()+"-"+undExs.size());
//			}
//
			int[][] labels=cl.test(f, currentFold, testConcepts);




			gbpmc.computeMetricsPerFold(f, labels, classification, nTestConcepts, currentFold);

		} // for f - fold look

		gbpmc.computeOverAllResults(nTestConcepts);
		mce.computeModelComplexityPerformance();



	}
	@Override
	public void computeDirectClassSeparabilityMeasure() {
		// TODO Auto-generated method stub
		
	} 

}
