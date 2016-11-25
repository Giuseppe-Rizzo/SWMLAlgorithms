package evaluation.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

import utils.Couple;
import utils.Generator;
import knowledgeBasesHandler.KnowledgeBase;
import evaluation.ConceptGenerator;

public class PoliticianGenerator extends ConceptGenerator {

	public PoliticianGenerator(KnowledgeBase k) {
		super(k);
		// TODO Auto-generated constructor stub
	}

	
	
	public  Couple<OWLDescription[],OWLDescription[]> generateQueryConcept(){


		OWLClass owlClass = kb.getClasses()[130]; 
		
//		OWLDescription neg= (OWLDescription) kb.getReasoner().getComplementClasses(owlClass);
//		System.out.println(owlClass);
//		
		Set<OWLDescription> subClasses = owlClass.getSubClasses(kb.getOntology());
//		
		ArrayList<OWLDescription> sub= new ArrayList<OWLDescription>(subClasses);
//		
		OWLDescription owlClass2 = sub.get(0); // target concept
		subClasses.remove(owlClass2);
		OWLDescription owlClasses= kb.getDataFactory().getOWLObjectUnionOf(subClasses);
//		
		allExamples= new OWLIndividual[reasoner.getIndividuals(owlClass, false).size()];
		allExamples=reasoner.getIndividuals(owlClass, false).toArray(allExamples);
//		
		
		

		OWLDescription[] queries= {owlClass2};
		OWLDescription[] negqueries= {owlClasses};	
		Couple<OWLDescription[],OWLDescription[]> couple= new Couple<OWLDescription[],OWLDescription[]>();
		couple.setFirstElement(queries);
		couple.setSecondElement(negqueries);
		
		return couple;

		}

	
	public OWLIndividual[] getExamples() {
		return allExamples;
	}
}
