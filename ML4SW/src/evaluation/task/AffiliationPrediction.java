package evaluation.task;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owlapi.model.OWLClassExpression;

import utils.Couple;
import evaluation.AIFBConceptGenerator;

public class AffiliationPrediction extends ClassMembershipPrediction {
	
//	private static KnowledgeBase kb;
//	private OWLIndividual[] allExamples;
//	private OWLDescription[] testConcepts;
//	private OWLDescription[] negTestConcepts;
//	private int[][] classification;
	
	public AffiliationPrediction(KnowledgeBase k){
		super();
		kb= k;
		AIFBConceptGenerator gen= new AIFBConceptGenerator(kb);
		
		Couple<OWLClassExpression[], OWLClassExpression[]> query= gen.generateQueryConcept();
		
		testConcepts= query.getFirstElement();
		negTestConcepts=query.getSecondElement();
	
		allExamples= gen.getExamples();
		kb.updateExamples(allExamples);
		classification=kb.getClassMembershipResult(testConcepts, negTestConcepts,allExamples);
		
		
	
	}
	
	

}
