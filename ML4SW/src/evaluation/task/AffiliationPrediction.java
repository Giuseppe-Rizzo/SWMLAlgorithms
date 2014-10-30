package evaluation.task;

import org.semanticweb.owl.model.OWLDescription;
import utils.Couple;

import evaluation.AIFBConceptGenerator;

import knowledgeBasesHandler.KnowledgeBase;

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
		
		Couple<OWLDescription[], OWLDescription[]> query= gen.generateQueryConcept();
		
		testConcepts= query.getFirstElement();
		negTestConcepts=query.getSecondElement();
	
		allExamples= gen.getExamples();
		kb.updateExamples(allExamples);
		classification=kb.getClassMembershipResult(testConcepts, negTestConcepts,allExamples);
		
		
	
	}
	
	

}
