package it.uniba.di.lacam.ml.evaluation.task;

import org.semanticweb.owlapi.model.OWLClassExpression;

import it.uniba.di.lacam.ml.evaluation.task.generators.AIFBConceptGenerator;
import it.uniba.di.lacam.ml.knowledgeBasesHandler.KnowledgeBase;
import it.uniba.di.lacam.ml.utils.Couple;
/**
 * Class-membership prediction on AIFB ontology
 * @author Giuseppe
 *
 */
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
