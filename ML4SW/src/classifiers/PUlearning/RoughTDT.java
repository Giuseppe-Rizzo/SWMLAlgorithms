package classifiers.PUlearning;

import java.util.ArrayList;
import java.util.Stack;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;

//import knowledgeBasesHandler.KnowledgeBaseForRoughConceptLearning;
import classifiers.TerminologicalDecisionTreeInducer;
import classifiers.refinementOperator.RefinementOperator;
import classifiers.trees.TDTClassifier;
import classifiers.trees.models.DLTree;
import evaluation.Parameters;
/**
 * A class for learn a pair of TDTs for lower and upper approximation
 * @author Utente
 *
 */
public class RoughTDT {

	private TDTClassifier lowerApproximation; 
	private TDTClassifier upperApproximation;

	private DLTree lowerTree;
	private DLTree upperTree;

	private KnowledgeBaseForRoughConceptLearning kb;
	private static int[][] classificationLA;
	private static int[][] classificationUA;
	private static int[][] classification;


	public RoughTDT(KnowledgeBaseForRoughConceptLearning kb){

		this.kb=kb;
		lowerApproximation= new TDTClassifier(kb);
		upperApproximation= new TDTClassifier(kb);
	}


/**
 * A method to keep track of classification results in ternary settings as well as for lower and upper approximation
 * @param kb
 * @param testConcepts
 * @param negTestConcepts
 * @param esempi
 */
	public static void setClassificationResultsForLowerUpperApprox(KnowledgeBaseForRoughConceptLearning kb, OWLDescription[] testConcepts, OWLDescription[] negTestConcepts, OWLIndividual[] esempi) {
		// TODO Auto-generated constructor stub

		classification= kb.getClassMembershipResult();
		classificationLA=kb.getClassMembershipResultLowApproximation(testConcepts, negTestConcepts, esempi);
		classificationUA=kb.getClassMembershipResultUpperApproximation(testConcepts, negTestConcepts, esempi);

	}


	public void training(Integer[] trainingExs,int c, OWLDescription[] testConcepts, OWLDescription[] negTestConcepts){

		// we enrich the set of negative examples according to the classification result
		RefinementOperator op= new RefinementOperator(kb);

		Integer[] copyTr= trainingExs.clone();

		ArrayList<Integer> unlabeledExs= new ArrayList<Integer>();

		for (int i=0; i<trainingExs.length;i++){
			if (classification[c][copyTr[i]]==0){
				unlabeledExs.add(copyTr[i]);
				copyTr[i]=-1;  // remove from the training example;

			}
		}
		int numIterations=0;

		do{
			// learning for lower approximation
			ArrayList<Integer> posExsLA= new ArrayList<Integer>();
			ArrayList<Integer> negExsLA= new ArrayList<Integer>();
			ArrayList<Integer> undExsLA= new ArrayList<Integer>();
			ArrayList<Integer> posExsUA= new ArrayList<Integer>();
			ArrayList<Integer> negExsUA= new ArrayList<Integer>();
			ArrayList<Integer> undExsUA= new ArrayList<Integer>();


			// per ogni concetto di query occorre addestrare il PUlearner
			splitting(copyTr, classificationLA,c,posExsLA,negExsLA,undExsLA);
			double prPos = (double)posExsLA.size()/(trainingExs.length);
			double prNeg = (double)negExsLA.size()/(trainingExs.length);

			System.out.println("Training set composition: "+ posExsLA.size()+" - "+ negExsLA.size()+"-"+undExsLA.size());

			double normSum = prPos+prNeg;
			if (normSum==0)	{ prPos=.5;	prNeg=.5; }
			else { prPos=prPos/normSum;	prNeg=prNeg/normSum; }

			System.out.printf("New learning problem prepared.\n",c);
			System.out.println("Learning a tree ");


			lowerTree= lowerApproximation.induceDLTree(posExsLA, negExsLA, negExsLA, Parameters.beam, prPos, prNeg, op);


			splitting(trainingExs, classificationUA,c,posExsUA,negExsUA,undExsUA);
			prPos = (double)posExsUA.size()/(trainingExs.length);
			prNeg = (double)negExsUA.size()/(trainingExs.length);

			System.out.println("Training set composition: "+ posExsUA.size()+" - "+ negExsUA.size()+"-"+undExsUA.size());

			normSum = prPos+prNeg;
			if (normSum==0)	{ prPos=.5;	prNeg=.5; }
			else { prPos=prPos/normSum;	prNeg=prNeg/normSum; }

			System.out.printf("New learning problem prepared.\n",c);
			System.out.println("Learning a tree ");

			// introdurre  il noise sull'upper approximation
			upperTree= lowerApproximation.induceDLTree(posExsUA, negExsUA, negExsUA, Parameters.beam, prPos, prNeg, op);

			ArrayList<Integer> unlabeledExsCloned=(ArrayList<Integer>) unlabeledExs.clone();
			for (int i=0; i<unlabeledExsCloned.size();i++){
				int label=classifyExample(unlabeledExsCloned.get(i), lowerTree, upperTree);
				negExsLA.add(unlabeledExsCloned.get(i));
				negExsUA.add(unlabeledExsCloned.get(i));
				unlabeledExs.remove(unlabeledExsCloned.get(i));

			}

			numIterations++;

		}while(!unlabeledExs.isEmpty()&& numIterations<10);
		// relabeling of unlabeled examples


	}

	public void splitting(Integer[] trainingExs, int[][] classifications,  int c, ArrayList<Integer> posExs,
			ArrayList<Integer> negExs, ArrayList<Integer> undExs) {

		for (int e=0; e<trainingExs.length; e++){
			if (trainingExs[e]!=-1){
				if (classifications[c][trainingExs[e]]==+1)
					posExs.add(trainingExs[e]);
				else
					negExs.add(trainingExs[e]);

			}
		}

		// splitting instances in positive, negative and uncertain instances
	}




	public DLTree getLowerApproximation(){

		return lowerTree;

	}

	public DLTree getUpperApproximation(){

		return upperTree;

	}







	public int classifyExample(int indTestEx, DLTree lower, DLTree upper) {
		int labelLower= lowerApproximation.classifyExample(indTestEx, lower);
		int labelUpper= upperApproximation.classifyExample(indTestEx, upper);

		if (labelLower==+1 &&(labelUpper==+1))
			return +1;
		else if (labelLower==-1 &&(labelUpper==+1))
			return 0;
		else if (labelLower==-1 &&(labelUpper==-1))
			return -1;
		else
			return -1; // as alternative it is possible to decide accordingly to prior knowledge // to implement

	}


}
