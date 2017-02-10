package classifiers;


import java.lang.reflect.InvocationTargetException;
	import java.util.ArrayList;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;

	

	import knowledgeBasesHandler.KnowledgeBase;


import classifiers.evidentialmodels.EvidentialTDTClassifier;
import classifiers.evidentialmodels.models.DSTDLTree;
import classifiers.refinementOperator.RefinementOperator;
import evaluation.Parameters;
/**
 * Wrapper for DSTTDTClassifier
 * @author Utente
 *
 */
public class DSTTerminologicalDecisionTreeInducer implements SupervisedLearnable {
	DSTDLTree[] trees; //for each query concept induce an ensemble
	EvidentialTDTClassifier cl;

	KnowledgeBase kb;
	int nOfConcepts;	
	public DSTTerminologicalDecisionTreeInducer( KnowledgeBase k, int nOfConcepts){
		this.nOfConcepts=nOfConcepts;
		kb=k;
		trees = new DSTDLTree [nOfConcepts]; 
		cl= new EvidentialTDTClassifier(kb);

	}

	/* (non-Javadoc)
	 * @see classifiers.SupervisedLearnable#training(java.lang.Integer[], org.semanticweb.owl.model.OWLClassExpression[], org.semanticweb.owl.model.OWLClassExpression[])
	 */
	@Override
	public void training(int[][] results, Integer[] trainingExs, OWLClassExpression[] testConcepts, OWLClassExpression[] negTestConcepts){
		RefinementOperator  op = null;
		try {
			op=  (RefinementOperator)(ClassLoader.getSystemClassLoader().loadClass(Parameters.refinementOperator)).getConstructor(KnowledgeBase.class).newInstance(kb);
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//		DLTree2[] forests = new DLTree2[testConcepts.length];
		Reasoner reasoner = kb.getReasoner();
		OWLIndividual[] allExamples= kb.getIndividuals();
		//		ArrayList<Triple<Integer, Integer, Integer>> testSetComposition= new ArrayList<Triple<Integer, Integer, Integer>>();
		for (int c=0; c<testConcepts.length; c++) {

			ArrayList<Integer> posExs = new ArrayList<Integer>();
			ArrayList<Integer> negExs = new ArrayList<Integer>();
			ArrayList<Integer> undExs = new ArrayList<Integer>();								

			System.out.printf("--- Query Concept #%d \n",c);
			// ha splittato in istanze negative, positive e incerte per un singolo albero
			for (int e=0; e<trainingExs.length; e++){

				if (this.kb.getReasoner().isEntailed(this.kb.getDataFactory().getOWLClassAssertionAxiom(testConcepts[c], kb.getIndividuals()[trainingExs[e]])))
					posExs.add(trainingExs[e]);
				else if ((kb.getReasoner().isEntailed(kb.getDataFactory().getOWLClassAssertionAxiom(kb.getDataFactory().getOWLObjectComplementOf(testConcepts[c]), kb.getIndividuals()[trainingExs[e]]))))
					negExs.add(trainingExs[e]);
				else
					undExs.add(trainingExs[e]);		
			}

			// queste istanze devono essere suddivise in istanze negative, positive e incerte sull'ensemble


			double prPos = (double)posExs.size()/(trainingExs.length);
			double prNeg = (double)negExs.size()/(trainingExs.length);



			System.out.println("Training set composition: "+ posExs.size()+" - "+ negExs.size()+"-"+undExs.size());
//
			double normSum = prPos+prNeg;
			if (normSum==0)	{ prPos=.5;	prNeg=.5; }
			else { prPos=prPos/normSum;	prNeg=prNeg/normSum; }

			System.out.printf("New learning problem prepared.\n",c);
			System.out.println("Learning phase ");

			trees[c] = cl.induceDSTDLTree(posExs, negExs, undExs, Parameters.beam,prPos, prNeg, op);

			//			System.out.println("forest "+c);
		    System.out.println(trees[c]);
			//			System.out.printf("--- forest #%d was induced. \n\n",c);

		}

	}


	/* (non-Javadoc)
	 * @see classifiers.SupervisedLearnable#test(int, java.lang.Integer[], org.semanticweb.owl.model.OWLClassExpression[])
	 */
	@Override
	public int[][] test(int f,Integer[] testExs,OWLClassExpression[] testConcepts) {
		int[][] labels= new int[testExs.length][nOfConcepts]; // classifier answers for each example and for each concept
		for (int te=0; te < testExs.length; te++ ) { 

			int indTestEx = testExs[te];

			System.out.print("\n\nFold #"+f);
			System.out.println(" --- Classifying Example " + (te+1) +"/"+testExs.length +" [" + indTestEx + "] " + kb.getIndividuals()[indTestEx]);

			int[] indClassifications = new int[nOfConcepts];
			//			cl.classifyExamplesTree(indTestEx, forests, indClassifications, testConcepts);
			cl.classifyExamplesDST(indTestEx, trees, indClassifications, testConcepts);

			labels[te]=indClassifications; 
			//						for (int i=0; i<testConcepts.length;i++){
			//			Triple<Integer, Integer, Integer> triple= testSetComposition.get(i);
			//			System.out.printf("Triple:  %d  %d  %d \n", triple.getFirstElem(), triple.getSecondElem(), triple.getThirdElem());
			//		
			//
			//		}


		}
		return labels;

	}

	@Override
	public double[] getComplexityValues() {
		double[] complexityValue= new double[trees.length]; // a measure to express the model complexity (e.g. the number of nodes in a tree)
		
		
		for(int i=0; i<trees.length; i++){
			
			double current=trees[i].getComplexityMeasure();
			complexityValue[i]= current;
			
		}
		
		
		return complexityValue;
	}

}

	
