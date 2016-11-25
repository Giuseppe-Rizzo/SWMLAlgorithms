import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;

import knowledgeBasesHandler.KnowledgeBaseForRoughConceptLearning;
import evaluation.ConceptGenerator;
import evaluation.Parameters;


public class Tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Parameters.loadParameters();
		KnowledgeBaseForRoughConceptLearning kb= new KnowledgeBaseForRoughConceptLearning(Parameters.urlOwlFile);
		ConceptGenerator qg= new ConceptGenerator(kb);
		
		//individuals
		OWLIndividual[] individuals = kb.getIndividuals();
		OWLDescription[] testConcepts=qg.generateQueryConcepts(Parameters.NUMGENCONCEPTS);
		
		OWLDescription[] negTestConcepts= new OWLDescription[testConcepts.length];
		for (int c=0; c<testConcepts.length; c++) 
			negTestConcepts[c] = kb.getDataFactory().getOWLObjectComplementOf(testConcepts[c]);
		
		
		System.out.println("LOW Approximation");
		kb.getClassMembershipResultLowApproximation(testConcepts, negTestConcepts, individuals);
		System.out.println("Upper Approximation");
		kb.getClassMembershipResultUpperApproximation(testConcepts, negTestConcepts, individuals);
		

	}

}
