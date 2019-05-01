package it.uniba.di.lacam.ml.evaluation.task;

import org.semanticweb.owlapi.model.OWLClassExpression;

import it.uniba.di.lacam.ml.evaluation.task.generators.GeoSkillsGenerator;
import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;
import it.uniba.di.lacam.ml.utils.Couple;

/**
 * The class membership prediciton task for the concepts generated via GeoskillsGenerator
 * @author Giuseppe
 *
 */

public class GeoSkillsPrediction extends ClassMembershipPrediction{

		
		
		public GeoSkillsPrediction (KnowledgeBase k){
			super();
			kb= k;
			GeoSkillsGenerator gen= new GeoSkillsGenerator(kb);
			
			Couple<OWLClassExpression[], OWLClassExpression[]> query= gen.generateQueryConcept();
			
			testConcepts= query.getFirstElement();
			System.out.println("-----"+testConcepts[0]);
			negTestConcepts=query.getSecondElement();
			allExamples= gen.getExamples();
			
			kb.updateExamples(allExamples);
			System.out.println("Number of instances"+kb.getIndividuals().length);
			classification=kb.getClassMembershipResult(testConcepts, negTestConcepts,allExamples);
			
			int pos=0;
			int neg=0;
			int und=0;
			
			for (int i=0; i<classification[0].length;i++){
				if (classification[0][i]==-1)
					neg++;
				else if (classification[0][i]==+1){
					pos++;

				}
				else
					und++;
			}

			System.out.printf("N: %d   P: %d  U:%d \n ", neg,pos, und);
			}

}
