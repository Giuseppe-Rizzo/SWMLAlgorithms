package it.uniba.di.lacam.ml.evaluation.task.generators;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;

import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;
import it.uniba.di.lacam.ml.utils.Couple;

/**
 * Concept generator for Geoskills ontology
 * @author Giuseppe Rizzo
 *
 */
public class GeoSkillsGenerator extends ConceptGenerator {

	public GeoSkillsGenerator(KnowledgeBase k) {
		super(k);
		// TODO Auto-generated constructor stub
	}


	private OWLIndividual[] examples;

/**
 * Generate the queries and their complement
 * @return
 */
	public  Couple<OWLClassExpression[],OWLClassExpression[]> generateQueryConcept(){

		OWLClass domain=kb.getClasses()[495];  //EducationalLevel
		OWLClass range= kb.getClasses()[144];   //EducationalPathway
		OWLObjectProperty prop= kb.getRoles()[1]; // belongsToEducationalPathway
		Set<OWLIndividual> inds=EntitySearcher.getIndividuals(domain,kb.getOntology()).collect(Collectors.toSet());
		Set<OWLIndividual> fillers= new HashSet<OWLIndividual>();
		 OWLReasoner reasoner2 = kb.getReasoner();

		examples= new OWLIndividual[inds.size()];
		for (OWLIndividual ind:inds){
			if (ind instanceof OWLNamedIndividual)
			     fillers.addAll( reasoner2.getObjectPropertyValues((OWLNamedIndividual)ind, prop).getFlattened());			
		}
		// add all the observed values
		System.out.println("Domain:"+ inds.size());
		System.out.println("Fillers:"+ fillers.size());
    
	 examples= inds.toArray(examples);
		//
		ArrayList<OWLIndividual> indList= new ArrayList<OWLIndividual>(fillers);

		OWLClassExpression[] queries= new OWLClassExpression[fillers.size()]; // queries
		OWLClassExpression[] negQueries= new OWLClassExpression[fillers.size()]  ;// neg queries
		for(int i=0; i<indList.size();i++){
			OWLIndividual owlIndividual = indList.get(i);


			OWLClassExpression owlObjectOneOf = dataFactory.getOWLObjectOneOf(owlIndividual);
			
			OWLObjectSomeValuesFrom owlObjectSomeRestriction = dataFactory.getOWLObjectSomeValuesFrom(prop, owlObjectOneOf);
			queries[i]= owlObjectSomeRestriction;
			//			for  (OWLIndividual ind: inds){
			//				final boolean hasType = reasoner2.hasType(ind, owlObjectSomeRestriction);
			//				if (hasType)
			//					System.out.println(hasType);
			//			 

			OWLClassExpression owlcomplement=dataFactory.getOWLObjectComplementOf(owlObjectSomeRestriction);
			negQueries[i]= owlcomplement;
			int size = reasoner.getInstances(owlObjectSomeRestriction, false).getFlattened().size();
			int size2= inds.size()-size;
			System.out.println("Instances p:"+ size+" n:"+size2);
		}

		
 Couple<OWLClassExpression[], OWLClassExpression[]> couple = new Couple<OWLClassExpression[], OWLClassExpression[]>();
 OWLClassExpression[] toReturn={queries[2]};
 couple.setFirstElement(toReturn);
 OWLClassExpression[] toReturn2 ={negQueries[2]};
couple.setSecondElement(toReturn2);

		return couple;
	}


	public OWLIndividual[] getExamples() {
		return examples;
	}

}




