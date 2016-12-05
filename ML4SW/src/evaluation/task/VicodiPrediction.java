package evaluation.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import knowledgeBasesHandler.KnowledgeBase;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;

import utils.Couple;


/**
 * Learning problem for Vicodi ontology
 * @author Utente
 *
 */
public class VicodiPrediction extends ClassMembershipPrediction{

	private OWLIndividual[] examples;
	
	public VicodiPrediction (KnowledgeBase k){
		super();
		kb= k;
		
	}
	public  Couple<OWLClassExpression[],OWLClassExpression[]> generateQueryConcept(){

		OWLClass domain=kb.getClasses()[43];  // TimeDependent
		OWLClass range= kb.getClasses()[86];    //Category
		OWLObjectProperty prop= kb.getRoles()[6];  // hasCategory
		Set<OWLIndividual> inds=domain.getIndividuals(kb.getOntology());
		Set<OWLIndividual> fillers= new HashSet<OWLIndividual>();
		 Reasoner reasoner2 = kb.getReasoner();

		examples= new OWLIndividual[inds.size()];
		
		//only four individuals
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


			final OWLDataFactory dataFactory = kb.getDataFactory();
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
			int size = reasoner2.getInstances(owlObjectSomeRestriction, false).getFlattened().size();
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