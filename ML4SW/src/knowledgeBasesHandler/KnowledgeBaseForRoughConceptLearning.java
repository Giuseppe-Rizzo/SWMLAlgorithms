package knowledgeBasesHandler;

import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;

import evaluation.Parameters;
/**
 * An extension of the class KnowledgeBase for supporting Rough Concept Learning 
 * @author Utente
 *
 */
public class KnowledgeBaseForRoughConceptLearning extends KnowledgeBase {
	
	private int[][] classificationsLA;
	private int[][] classificationsUA;
	

	public KnowledgeBaseForRoughConceptLearning(String url) {
		
		super(url);
		System.out.println("End kb for PU Learning------->");
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Assess the membership of individual for low approximation
	 * @param testConcepts
	 * @param negTestConcepts
	 * @param esempi
	 * @return the membership of individuals
	 */
	public int[][] getClassMembershipResultLowApproximation(OWLDescription[] testConcepts, OWLDescription[] negTestConcepts, OWLIndividual[] esempi){
		System.out.println("\nClassifying all examples ------ ");
		classificationsLA = new int[testConcepts.length][esempi.length];
		System.out.print("Processed concepts ("+testConcepts.length+"): \n");

		for (int c=0; c<testConcepts.length; ++c) { 
			int p=0;
			int n=0;
			System.out.printf("[%d] ",c);
			for (int e=0; e<esempi.length; ++e) {			
				classificationsLA[c][e] = 0;
				if (reasoner.hasType(esempi[e],testConcepts[c])) {
					classificationsLA[c][e] = +1;
					p++;

				}
				else{ 
				     classificationsLA[c][e]=-1;
					
					n++;
				}
			}
			System.out.printf(": %d  %d \n",p,n);


		}
		return classificationsLA;

	}


	/**
	 * Assess the membership of an individual for upper approximation
	 * @param testConcepts
	 * @param negTestConcepts
	 * @param esempi
	 * @return the membership of individuals
	 */
	public int[][] getClassMembershipResultUpperApproximation(OWLDescription[] testConcepts, OWLDescription[] negTestConcepts, OWLIndividual[] esempi){
		System.out.println("\nClassifying all examples ------ ");
		classificationsUA = new int[testConcepts.length][esempi.length];
		System.out.print("Processed concepts ("+testConcepts.length+"): \n");

		for (int c=0; c<testConcepts.length; ++c) { 
			int p=0;
			int n=0;
			System.out.printf("[%d] ",c);
			for (int e=0; e<esempi.length; ++e) {			
				classificationsUA[c][e] = 0;
				if (reasoner.hasType(esempi[e],negTestConcepts[c])) {
					classificationsUA[c][e] = -1;
					p++;

				}
				else{ 
				     classificationsUA[c][e]=+1;
					 n++;
				}
			}
			System.out.printf(": %d  %d \n",p,n);
		}
		return classificationsUA;

	}

	
	
	

}
