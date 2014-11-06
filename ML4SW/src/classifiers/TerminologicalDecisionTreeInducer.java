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


import classifiers.trees.TDTClassifier;
import classifiers.trees.models.DLTree;
import evaluation.Parameters;
import evaluation.PruningType;
/**
 * Wrapper for TDTClassifier
 * @author Utente
 *
 */
public class TerminologicalDecisionTreeInducer implements SupervisedLearnable {
	DLTree[] trees; //for each query concept induce an ensemble
	
	TDTClassifier cl;
	static PrintStream stream;
	KnowledgeBase kb;
	int nOfConcepts;	
	public TerminologicalDecisionTreeInducer( KnowledgeBase k, int nOfConcepts){
		this.nOfConcepts=nOfConcepts;
		kb=k;
		trees = new DLTree [nOfConcepts]; 
		cl= new TDTClassifier(kb);
		
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
	public void training(Integer[] trainingExs, OWLDescription[] testConcepts, OWLDescription[] negTestConcepts){

		Reasoner reasoner = kb.getReasoner();
		OWLIndividual[] allExamples= kb.getIndividuals();
		//		DLTree2[] forests = new DLTree2[testConcepts.length];
		HashSet<Integer> trainingExsSet= new HashSet<Integer>(Arrays.asList(trainingExs));
		Integer[] pruningExs= new Integer[0];
		if (Parameters.pruning==PruningType.REP){
			int pruningSetDimension = (int)3*(trainingExsSet.size()/10); // pruning set 30% of training set
			System.out.println("Dimension: "+ pruningSetDimension);
			int r=0;
			HashSet<Integer> pruningExsSet=new HashSet<Integer>();
			while (r<pruningSetDimension){
				int elem= Generator.generator.nextInt(allExamples.length);
				if(trainingExsSet.contains(elem)){
					trainingExsSet.remove(elem);
					pruningExsSet.add(elem);
					r++;
				}

			}
			
			pruningExs=pruningExsSet.toArray(pruningExs);
		}


		//		ArrayList<Triple<Integer, Integer, Integer>> testSetComposition= new ArrayList<Triple<Integer, Integer, Integer>>();
		int length = testConcepts!=null?testConcepts.length:1;
		int[][] results= kb.getClassMembershipResult();
		for (int c=0; c<length; c++) {

			ArrayList<Integer> posExs = new ArrayList<Integer>();
			ArrayList<Integer> negExs = new ArrayList<Integer>();
			ArrayList<Integer> undExs = new ArrayList<Integer>();								

			System.out.printf("--- Query Concept #%d \n",c);

			splitting(trainingExs, results, c, posExs, negExs, undExs);

			// queste istanze devono essere suddivise in istanze negative, positive e incerte sull'ensemble


			double prPos = (double)posExs.size()/(trainingExs.length);
			double prNeg = (double)negExs.size()/(trainingExs.length);

			System.out.println("Training set composition: "+ posExs.size()+" - "+ negExs.size()+"-"+undExs.size());

			double normSum = prPos+prNeg;
			if (normSum==0)	{ prPos=.5;	prNeg=.5; }
			else { prPos=prPos/normSum;	prNeg=prNeg/normSum; }

			System.out.printf("New learning problem prepared.\n",c);
			System.out.println("Learning a tree ");


			trees[c] = cl.induceDLTree(posExs, negExs, undExs, Parameters.NUMGENCONCEPTS,prPos, prNeg);
//			stream.println(trees[c]);
			
			if (Parameters.pruning==PruningType.REP)
				cl.doREPPruning(pruningExs, trees[c], results[c]);
			else if (Parameters.pruning==PruningType.REP)
				cl.doPEPPruning(trainingExs, trees[c], results[c]);
			

			
			System.out.printf("--- tree #%d was induced. \n\n",c);

		}



	}

	public void splitting(Integer[] trainingExs, int[][] classifications,  int c, ArrayList<Integer> posExs,
			ArrayList<Integer> negExs, ArrayList<Integer> undExs) {
		// ha splittato in istanze negative, positive e incerte per un singolo albero
//		for (int e=0; e<trainingExs.length; e++){
//
//			if (reasoner.hasType(allExamples[trainingExs[e]], testConcepts[c]))
//				posExs.add(trainingExs[e]);
//			else {
//				if (!Evaluation.BINARYCLASSIFICATION){
//
//					if (reasoner.hasType(allExamples[trainingExs[e]], negTestConcepts[c]))
//						negExs.add(trainingExs[e]);
//					else
//						undExs.add(trainingExs[e]);
//				}
//				else
//					negExs.add(trainingExs[e]);
//			}
//		}
		
		for (int e=0; e<trainingExs.length; e++){
			
			if (classifications[c][trainingExs[e]]==+1)
				posExs.add(trainingExs[e]);
			else if (!Parameters.BINARYCLASSIFICATION){
				
				if (classifications[c][trainingExs[e]]==-1)
					negExs.add(trainingExs[e]);
				else
					undExs.add(trainingExs[e]);
				
			}
			else
				negExs.add(trainingExs[e]);
				
			
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
			cl.classifyExamples(indTestEx, trees, indClassifications, testConcepts);

			labels[te]=indClassifications; 
			


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
