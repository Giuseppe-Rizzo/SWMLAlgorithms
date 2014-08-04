package classifiers.trees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;

import classifiers.trees.models.DLTree;
import evaluation.Evaluation;

public abstract class AbstractTDTClassifier {
	
	protected KnowledgeBase kb;
	
	public AbstractTDTClassifier(KnowledgeBase k){
		kb=k;
		
	}
	
	public abstract DLTree induceDLTree(ArrayList<Integer> posExs, ArrayList<Integer> negExs,	ArrayList<Integer> undExs, 
			int dim, double prPos, double prNeg);
	
	
	
	
	public	void classifyExamples(int indTestEx, DLTree[] trees, int[] results, OWLDescription[] testConcepts, int...rclass) {

		for (int c=0; c < testConcepts.length; c++) {
			
			results[c] = classifyExample(indTestEx, trees[c]);
		} // for c



	}
	
	
	
	static double gini(double numPos, double numNeg, double prPos,
			double prNeg) {
			
				double sum = numPos+numNeg;
			
				double p1 = (numPos*Evaluation.M*prPos)/(sum+Evaluation.M);
				double p2 = (numNeg*Evaluation.M*prNeg)/(sum+Evaluation.M);
			
				return (1.0-p1*p1-p2*p2);
				//		return (1-Math.pow(p1,2)-Math.pow(p2,2))/2;
			}

	public int classifyExample(int indTestEx, DLTree tree) {
		Stack<DLTree> stack= new Stack<DLTree>();
		OWLDataFactory dataFactory = kb.getDataFactory();
		stack.add(tree);
		int result=0;
		boolean stop=false;
		while(!stack.isEmpty() && !stop){
			DLTree currentTree= stack.pop();
	
			OWLDescription rootClass = currentTree.getRoot();
			//			System.out.println("Root class: "+ rootClass);
			if (rootClass.equals(dataFactory.getOWLThing())){
				stop=true;
				result=+1;
	
			}
			else if (rootClass.equals(dataFactory.getOWLNothing())){
				stop=true;
				result=-1;
	
			}else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], rootClass))
				stack.push(currentTree.getPosSubTree());
			else if (kb.getReasoner().hasType(kb.getIndividuals()[indTestEx], dataFactory.getOWLObjectComplementOf(rootClass)))
				stack.push(currentTree.getNegSubTree());
			else {
				stop=true;
				result=0; // seguo entrambi i percorsi
	
			}
		};
	
	
		return result;
	
	}

	protected OWLDescription selectBestConcept(OWLDescription[] concepts, ArrayList<Integer> posExs, ArrayList<Integer> negExs,
			ArrayList<Integer> undExs, double prPos, double prNeg) {
			
				int[] counts;
			
				int bestConceptIndex = 0;
			
				counts = getSplitCounts(concepts[0], posExs, negExs, undExs);
				System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
						"#"+0, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);
			
				double bestGain = gain(counts, prPos, prNeg);
			
				System.out.printf("%+10e\n",bestGain);
			
				System.out.println(concepts[0]);
			
				for (int c=1; c<concepts.length; c++) {
			
					counts = getSplitCounts(concepts[c], posExs, negExs, undExs);
					System.out.printf("%4s\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t p:%d n:%d u:%d\t ", 
							"#"+c, counts[0], counts[1], counts[2], counts[3], counts[4], counts[5], counts[6], counts[7], counts[8]);
			
					double thisGain = gain(counts, prPos, prNeg);
					System.out.printf("%+10e\n",thisGain);
					System.out.println(concepts[c]);
					if(thisGain > bestGain) {
						bestConceptIndex = c;
						bestGain = thisGain;
					}
				}
			
				System.out.printf("best gain: %f \t split #%d\n", bestGain, bestConceptIndex);
				return concepts[bestConceptIndex];
			}

	private double gain(int[] counts, double prPos, double prNeg) {
	
		double sizeT = counts[0] + counts[1];
		double sizeF = counts[3] + counts[4];
		double sizeU = counts[6] + counts[7] + counts[2] + counts[5];
		double sum = sizeT+sizeF+sizeU;
	
		double startImpurity = gini(counts[0]+counts[3], counts[1]+counts[4], prPos, prNeg);
		double tImpurity = gini(counts[0], counts[1], prPos, prNeg);
		double fImpurity = gini(counts[3], counts[4], prPos, prNeg);
		double uImpurity = gini(counts[6]+counts[2], counts[7]+counts[5] , prPos, prNeg);		
	
		return startImpurity - (sizeT/sum)*tImpurity - (sizeF/sum)*fImpurity - - (sizeU/sum)*uImpurity;
	}

	public AbstractTDTClassifier() {
		super();
	}

	private int[] getSplitCounts(OWLDescription concept, ArrayList<Integer> posExs, ArrayList<Integer> negExs,
			ArrayList<Integer> undExs) {
				System.out.println("positive: "+posExs);
				int[] counts = new int[9];
				ArrayList<Integer> posExsT = new ArrayList<Integer>();
				ArrayList<Integer> negExsT = new ArrayList<Integer>();
				ArrayList<Integer> undExsT = new ArrayList<Integer>();
			
				ArrayList<Integer> posExsF = new ArrayList<Integer>();
				ArrayList<Integer> negExsF = new ArrayList<Integer>();
				ArrayList<Integer> undExsF = new ArrayList<Integer>();
			
				ArrayList<Integer> posExsU = new ArrayList<Integer>();
				ArrayList<Integer> negExsU = new ArrayList<Integer>();
				ArrayList<Integer> undExsU = new ArrayList<Integer>();
			
				splitGroup(concept,posExs,posExsT,posExsF,posExsU);
				splitGroup(concept,negExs,negExsT,negExsF,negExsU);	
				splitGroup(concept,undExs,undExsT,undExsF,undExsU);	
			
				counts[0] = posExsT.size(); 
				counts[1] = negExsT.size(); 
				counts[2] = undExsT.size(); 
				counts[3] = posExsF.size(); 
				counts[4] = negExsF.size();
				counts[5] = undExsF.size();
				counts[6] = posExsU.size(); 
				counts[7] = negExsU.size();
				counts[8] = undExsU.size();
				//		for(int i=0; i<counts.length;i++)
				//			System.out.println(counts[i]);
			
				return counts;
			
			}

	protected void split(OWLDescription concept, ArrayList<Integer> posExs, ArrayList<Integer> negExs, ArrayList<Integer> undExs,
			ArrayList<Integer> posExsT, ArrayList<Integer> negExsT, ArrayList<Integer> undExsT, ArrayList<Integer> posExsF, ArrayList<Integer> negExsF,
			ArrayList<Integer> undExsF) {
			
				ArrayList<Integer> posExsU = new ArrayList<Integer>();
				ArrayList<Integer> negExsU = new ArrayList<Integer>();
				ArrayList<Integer> undExsU = new ArrayList<Integer>();
			
				splitGroup(concept,posExs,posExsT,posExsF,posExsU);
				splitGroup(concept,negExs,negExsT,negExsF,negExsU);
				splitGroup(concept,undExs,undExsT,undExsF,undExsU);	
			
			}

	private void splitGroup(OWLDescription concept, ArrayList<Integer> nodeExamples, ArrayList<Integer> trueExs,
			ArrayList<Integer> falseExs, ArrayList<Integer> undExs) {
				OWLDescription negConcept = kb.getDataFactory().getOWLObjectComplementOf(concept);
			
				for (int e=0; e<nodeExamples.size(); e++) {
					int exIndex = nodeExamples.get(e);
					//			 System.out.println("-****"+ concept);
					if (kb.getReasoner().hasType(kb.getIndividuals()[exIndex], concept))
						trueExs.add(exIndex);
					else if (kb.getReasoner().hasType(kb.getIndividuals()[exIndex], negConcept))
						falseExs.add(exIndex);
					else
						undExs.add(exIndex);		
				}	
			
			
			
			}

	protected ArrayList<OWLDescription> generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {
	
		System.out.printf("Generating node concepts ");
		ArrayList<OWLDescription> rConcepts = new ArrayList<OWLDescription>(dim);
		OWLDescription newConcept;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				newConcept = kb.getRandomConcept();
	
				Set<OWLIndividual> individuals = (kb.getReasoner()).getIndividuals(newConcept, false);
				Iterator<OWLIndividual> instIterator = individuals.iterator();
				while (emptyIntersection && instIterator.hasNext()) {
					OWLIndividual nextInd = (OWLIndividual) instIterator.next();
					int index = -1;
					for (int i=0; index<0 && i<kb.getIndividuals().length; ++i)
						if (nextInd.equals(kb.getIndividuals()[i])) index = i;
					if (posExs.contains(index))
						emptyIntersection = false;
					else if (negExs.contains(index))
						emptyIntersection = false;
				}					
			} while (emptyIntersection);;
			rConcepts.add(newConcept);
			System.out.printf("%d ", c);
		}
		System.out.println();
	
		return rConcepts;
	}

}