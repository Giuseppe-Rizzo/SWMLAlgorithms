package evaluation.task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import java.util.Set;

import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;

import evaluation.ConceptGenerator;
import utils.Couple;

import knowledgeBasesHandler.KnowledgeBase;


public class GeoSkillsGenerator extends ConceptGenerator {

	public GeoSkillsGenerator(KnowledgeBase k) {
		super(k);
		// TODO Auto-generated constructor stub
	}


	private OWLIndividual[] examples;


	public  Couple<OWLDescription[],OWLDescription[]> generateQueryConcept(){

		OWLClass domain=kb.getClasses()[495];  //EducationalLevel
		OWLClass range= kb.getClasses()[144];   //EducationalPathway
		OWLObjectProperty prop= kb.getRoles()[1]; // belongsToEducationalPathway
		Set<OWLIndividual> inds=domain.getIndividuals(kb.getOntology());
		Set<OWLIndividual> fillers= new HashSet<OWLIndividual>();
		final Reasoner reasoner2 = kb.getReasoner();

		examples= new OWLIndividual[inds.size()];
		for (OWLIndividual ind:inds){
			fillers.addAll( reasoner2.getRelatedIndividuals(ind, prop));			
		}
		// add all the observed values
		System.out.println("Domain:"+ inds.size());
		System.out.println("Fillers:"+ fillers.size());
    
	 examples= inds.toArray(examples);
		//
		ArrayList<OWLIndividual> indList= new ArrayList<OWLIndividual>(fillers);

		OWLDescription[] queries= new OWLDescription[fillers.size()]; // queries
		OWLDescription[] negQueries= new OWLDescription[fillers.size()]  ;// neg queries
		for(int i=0; i<indList.size();i++){
			OWLIndividual owlIndividual = indList.get(i);


			OWLDescription owlObjectOneOf = dataFactory.getOWLObjectOneOf(owlIndividual);
			
			OWLObjectSomeRestriction owlObjectSomeRestriction = dataFactory.getOWLObjectSomeRestriction(prop, owlObjectOneOf);
			queries[i]= owlObjectSomeRestriction;
			//			for  (OWLIndividual ind: inds){
			//				final boolean hasType = reasoner2.hasType(ind, owlObjectSomeRestriction);
			//				if (hasType)
			//					System.out.println(hasType);
			//			 

			OWLDescription owlcomplement=dataFactory.getOWLObjectComplementOf(owlObjectSomeRestriction);
			negQueries[i]= owlcomplement;
			int size = reasoner.getIndividuals(owlObjectSomeRestriction, false).size();
			int size2= inds.size()-size;
			System.out.println("Instances p:"+ size+" n:"+size2);
		}

		
 Couple<OWLDescription[], OWLDescription[]> couple = new Couple<OWLDescription[], OWLDescription[]>();
 OWLDescription[] toReturn={queries[2]};
 couple.setFirstElement(toReturn);
 OWLDescription[] toReturn2 ={negQueries[2]};
couple.setSecondElement(toReturn2);

		return couple;
	}


	public OWLIndividual[] getExamples() {
		return examples;
	}

}




