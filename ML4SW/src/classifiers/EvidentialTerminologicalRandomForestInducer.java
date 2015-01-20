package classifiers;

import java.util.ArrayList;

import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;

import knowledgeBasesHandler.KnowledgeBase;


import classifiers.ensemble.Ensemble;

import classifiers.evidentialAlgorithms.ETRFClassifier;
import classifiers.evidentialAlgorithms.models.DSTDLTree;

import evaluation.Parameters;

/**
 * Classifier wrapper for TRFs Predictive model
 * @author Utente
 *
 */
public class EvidentialTerminologicalRandomForestInducer implements SupervisedLearnable {
	Ensemble<DSTDLTree>[] forests; //for each query concept induce an ensemble
	ETRFClassifier cl;

	KnowledgeBase kb;
	int nOfConcepts;	
	@SuppressWarnings("unchecked")
	public EvidentialTerminologicalRandomForestInducer( KnowledgeBase k, int nOfConcepts){
		this.nOfConcepts=nOfConcepts;
		kb=k;
		forests = new Ensemble [nOfConcepts]; 
		cl= new ETRFClassifier(kb);

	}

	/* (non-Javadoc)
	 * @see classifiers.SupervisedLearnable#training(java.lang.Integer[], org.semanticweb.owl.model.OWLDescription[], org.semanticweb.owl.model.OWLDescription[])
	 */
	@Override
	public void training(int[][] results, Integer[] trainingExs, OWLDescription[] testConcepts, OWLDescription[] negTestConcepts){

		//		DLTree2[] forests = new DLTree2[testConcepts.length];
		Reasoner reasoner = kb.getReasoner();
		OWLIndividual[] allExamples= kb.getIndividuals();
		//		ArrayList<Triple<Integer, Integer, Integer>> testSetComposition= new ArrayList<Triple<Integer, Integer, Integer>>();
		ETRFClassifier cl= new ETRFClassifier(kb);
		for (int c=0; c<testConcepts.length; c++) {

			ArrayList<Integer> posExs = new ArrayList<Integer>();
			ArrayList<Integer> negExs = new ArrayList<Integer>();
			ArrayList<Integer> undExs = new ArrayList<Integer>();								

			System.out.printf("--- Query Concept #%d \n",c);

			// ha splittato in istanze negative, positive e incerte per un singolo albero
			for (int e=0; e<trainingExs.length; e++){

				if (reasoner.hasType(allExamples[trainingExs[e]], testConcepts[c]))
					posExs.add(trainingExs[e]);
				else if (reasoner.hasType(allExamples[trainingExs[e]], negTestConcepts[c]))
					negExs.add(trainingExs[e]);
				else
					undExs.add(trainingExs[e]);
			}

			// queste istanze devono essere suddivise in istanze negative, positive e incerte sull'ensemble


			double prPos = (double)posExs.size()/(trainingExs.length);
			double prNeg = (double)negExs.size()/(trainingExs.length);



			System.out.println("Training set composition: "+ posExs.size()+" - "+ negExs.size()+"-"+undExs.size());

			double normSum = prPos+prNeg;
			if (normSum==0)	{ prPos=.5;	prNeg=.5; }
			else { prPos=prPos/normSum;	prNeg=prNeg/normSum; }

			System.out.printf("New learning problem prepared.\n",c);
			System.out.println("Learning a forest ");


			forests[c] = cl.induceDLForest(posExs, negExs, undExs, Parameters.NUMGENCONCEPTS, Parameters.NTREES,prPos, prNeg);

			//			System.out.println("forest "+c);
			//			System.out.println(forests[c]);
					System.out.printf("--- forest #%d was induced. \n\n",c);

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

			int[] indClassifications = new int[testConcepts.length];
			//			cl.classifyExamplesTree(indTestEx, forests, indClassifications, testConcepts);
			cl.classifyExamples(indTestEx, forests, indClassifications, testConcepts);

			labels[te]=indClassifications; 
			

		}
		return labels;

	}

	@Override
	public double[] getComplexityValues() {
		// TODO Auto-generated method stub
		return null;
	}

}