package evaluation.task;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owl.model.OWLDescription;

import evaluation.ConceptGenerator;
import evaluation.PULearningConceptGenerator;
import evaluation.Parameters;

/**
 * Class-membership prediction for PULearning
 * @author Utente
 *
 */
public class PUClassMembershipPrediction extends ClassMembershipPrediction {
	
	
	public PUClassMembershipPrediction(KnowledgeBase k){

		super();
		kb=k;
		allExamples=kb.getIndividuals();
		PULearningConceptGenerator qg= new PULearningConceptGenerator(kb);
		testConcepts=qg.generateQueryConcepts(Parameters.NUMGENCONCEPTS);
		negTestConcepts=new OWLDescription[testConcepts.length];
		for (int c=0; c<testConcepts.length; c++) 
			negTestConcepts[c] = kb.getDataFactory().getOWLObjectComplementOf(testConcepts[c]);
		// Classification wrt all query concepts
		System.out.println("\nClassifying all examples ------ ");
		kb.getReasoner();
		//concetti=kb.getClasses();
		//		urlOwlFile=kb.getURL();
		classification=kb.getClassMembershipResult(testConcepts, negTestConcepts,allExamples);

	}

	

}
