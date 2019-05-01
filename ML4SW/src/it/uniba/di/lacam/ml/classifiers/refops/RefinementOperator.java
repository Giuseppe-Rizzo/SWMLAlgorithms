package it.uniba.di.lacam.ml.classifiers.refops;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import it.uniba.di.lacam.ml.evaluation.Parameters;
import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;

/**
 * A refinement operator implementing  a kind of random walk on the concept space
 * @author Giuseppe Rizzo
 *
 */
public class RefinementOperator {


	KnowledgeBase kb;
	static final double d = 0.5;
	private OWLClassExpression[] allConcepts;
	private OWLObjectProperty[] allRoles;
	private OWLDataProperty[] allProperties;
	private OWLDataFactory dataFactory;
	public RefinementOperator(KnowledgeBase kb) {
		// TODO Auto-generated constructor stub
		this.kb=kb;
		allConcepts=kb.getClasses();
		allRoles=kb.getRoles();
		allProperties= kb.getDataProperties();
		dataFactory = kb.getDataFactory();

	}


/**
 * Generate a random concept subsuming the given one and added as a conjunct
 * @param currentConcept
 * @return
 */
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
				else if ((generator.nextDouble() < 0.75)){
					final OWLDataProperty owlDataProperty = allProperties[generator.nextInt(allProperties.length)];
					Set<OWLNamedIndividual> individualsInSignature = owlDataProperty.getIndividualsInSignature();
					ArrayList<OWLNamedIndividual> inds= new ArrayList<OWLNamedIndividual>(individualsInSignature);
					Set<OWLLiteral> dataPropertyValues = new HashSet<OWLLiteral>();
					for (OWLNamedIndividual i: inds){
					dataPropertyValues.addAll( i.getDataPropertyValues(owlDataProperty, kb.getOntology()));	
					}
					ArrayList<OWLLiteral> values= new ArrayList<OWLLiteral>(dataPropertyValues);
					if (!values.isEmpty())
					newConcept = kb.getDataFactory().getOWLDataHasValue(owlDataProperty, values.get(generator.nextInt(values.size())));
					else	
						newConcept = kb.getDataFactory().getOWLObjectComplementOf(newConceptBase); //in case there are no dataproperties
				}
				else if ((generator.nextDouble() < 0.9)){
					 OWLObjectProperty owlDataProperty = allRoles[generator.nextInt(allProperties.length)];
					Set<OWLNamedIndividual> individualsInSignature = owlDataProperty.getIndividualsInSignature();
					ArrayList<OWLIndividual> inds= new ArrayList<OWLIndividual>(individualsInSignature);
					Set<OWLIndividual> objValues = new HashSet<OWLIndividual>();
					for (OWLIndividual i: inds){
					objValues.addAll( i.getObjectPropertyValues(owlDataProperty, kb.getOntology()));	
					}
					ArrayList<OWLIndividual> values= new ArrayList<OWLIndividual>(objValues);
					if (!values.isEmpty())
					newConcept = kb.getDataFactory().getOWLObjectHasValue(owlDataProperty, values.get(generator.nextInt(values.size())));
					else	
						newConcept = kb.getDataFactory().getOWLObjectComplementOf(newConceptBase); //in case there are no dataproperties
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
			else	 if (generator.nextDouble() < 0.8)			
				newConcept = kb.getDataFactory().getOWLObjectComplementOf(newConceptBase);
		} // else
//		System.out.printf("-->\t %s\n",newConcept);
	} while (newConcept==null || kb.getReasoner().getInstances(newConcept,false).getFlattened().size()==0);
//	} while (!prob.reasoner.isSatisfiable(newConcept));		
	
	return newConcept;		
}	

			}
