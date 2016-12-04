package classifiers.refinementOperator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import knowledgeBasesHandler.KnowledgeBase;



import evaluation.Parameters;

public class RefinementOperator {


	KnowledgeBase kb;
	static final double d = 0.5;
	private OWLClassExpression[] allConcepts;
	private OWLObjectProperty[] allRoles;
	private OWLDataFactory dataFactory;
	public RefinementOperator(KnowledgeBase kb) {
		// TODO Auto-generated constructor stub
		this.kb=kb;
		allConcepts=kb.getClasses();
		allRoles=kb.getRoles();
		dataFactory = kb.getDataFactory();

	}



public OWLClassExpression getSubsumedRandomConcept(OWLClassExpression currentConcept) {
		
	Random generator = new Random ();
		OWLClassExpression newConcept = null;
				
		do {
			if (generator.nextDouble() < 0.5) 
				newConcept = allConcepts[generator.nextInt(allConcepts.length)];
			else {
				OWLClassExpression newConceptBase;
				if (generator.nextDouble() < 0.5) 
					newConceptBase = getRandomConcept(kb);
				else
					newConceptBase = allConcepts[generator.nextInt(allConcepts.length)];
				if (generator.nextDouble() < 0.5) { // new role restriction
					OWLObjectProperty role = allRoles[generator.nextInt(allRoles.length)];
//					OWLDescription roleRange = (OWLDescription) role.getRange;
					
					if (generator.nextDouble() < 0.5)
						newConcept = kb.getDataFactory().getOWLObjectAllValuesFrom(role, newConceptBase);
					else
						newConcept = kb.getDataFactory().getOWLObjectSomeValuesFrom(role, newConceptBase);
				}
				else					
					newConcept = kb.getDataFactory().getOWLObjectComplementOf(newConceptBase);
			} // else
//			System.out.printf("-->\t %s\n",newConcept);
//		} while (newConcept==null || !(reasoner.getIndividuals(newConcept,false).size() > 0));
		} while (!kb.getReasoner().isSatisfiable(newConcept)); 
//				|| !prob.reasoner.isEntailed(prob.dataFactory.getOWLSubClassOfAxiom(currentConcept,newConcept)));		
		
		return newConcept.getNNF();		
	}	



/**
 * @param prob
 * @return
 */
public OWLClassExpression getRandomConcept(KnowledgeBase k) {
	
	OWLClassExpression newConcept = null;
	Random generator= new Random();		
	do {
		if (generator.nextDouble() < 0.20) 
			newConcept = allConcepts[generator.nextInt(allConcepts.length)];
		else {
			OWLClassExpression newConceptBase;
			if (generator.nextDouble() < 0.2) 
				newConceptBase = getRandomConcept(k);
			else
				newConceptBase = allConcepts[generator.nextInt(allConcepts.length)];
			if (generator.nextDouble() < 0.75) { // new role restriction
				OWLObjectProperty role = allRoles[generator.nextInt(allRoles.length)];
//				OWLDescription roleRange = (OWLDescription) role.getRange;
				
				if (generator.nextDouble() < 0.5)
					newConcept = kb.getDataFactory().getOWLObjectAllValuesFrom(role, newConceptBase);
				else
					newConcept = kb.getDataFactory().getOWLObjectSomeValuesFrom(role, newConceptBase);
			}
			else					
				newConcept = kb.getDataFactory().getOWLObjectComplementOf(newConceptBase);
		} // else
//		System.out.printf("-->\t %s\n",newConcept);
	} while (newConcept==null || kb.getReasoner().getInstances(newConcept,false).getFlattened().size()==0);
//	} while (!prob.reasoner.isSatisfiable(newConcept));		
	
	return newConcept;		
}	

			}
