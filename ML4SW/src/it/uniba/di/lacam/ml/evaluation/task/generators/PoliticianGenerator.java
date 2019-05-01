package it.uniba.di.lacam.ml.evaluation.task.generators;

import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;
import it.uniba.di.lacam.ml.utils.Couple;
/**
 * Generate the target concepts for the DBPedia fragments
 * @author Giuseppe
 *
 */
public class PoliticianGenerator extends ConceptGenerator {

	public PoliticianGenerator(KnowledgeBase k) {
		super(k);
		// TODO Auto-generated constructor stub
	}

	
	
	public  Couple<OWLClassExpression[],OWLClassExpression[]> generateQueryConcept(){


		OWLClass owlClass = kb.getClasses()[130]; // research group
		System.out.println(owlClass);
//		OWLClass owlClass2 = kb.getClasses()[31]; // person
//		OWLObjectProperty prop= kb.getRoles()[95];
//		// considerare la propriet� no. 95
//
		Set<OWLClassExpression> subClasses = owlClass.getSubClasses(kb.getOntology());
		
		ArrayList<OWLClassExpression> sub= new ArrayList<OWLClassExpression>(subClasses);
		
		
		
		OWLClassExpression owlClass2 = sub.get(0); // target concept
		subClasses.remove(owlClass2);
		OWLClassExpression owlClasses= kb.getDataFactory().getOWLObjectUnionOf(subClasses);
		
		Set<OWLNamedIndividual> instances = reasoner.getInstances(owlClass, false).getFlattened();
		allExamples = new OWLIndividual[instances.size()];
		allExamples=new ArrayList<OWLNamedIndividual>().toArray(allExamples);
//
//		// per ogni research group generare il concetto \exists R 
//
//		Set<OWLIndividual> researchGroup = reasoner.getIndividuals(owlClass, false); // retrieval of research group
		OWLClassExpression[] queries= {owlClass2};
		OWLClassExpression[] negqueries= {owlClasses};
//		OWLDataFactory dataFactory2 = kb.getDataFactory();
//		int i=0;
//
//		// genero  concetto \exists R.{owlIndividual}
//		for (OWLIndividual owlIndividual : researchGroup) {
//
//			queries[i]= dataFactory2.getOWLObjectSomeRestriction(prop, dataFactory2.getOWLObjectOneOf(owlIndividual));
//			System.out.println("Queries: "+i+") "+queries[i]);
//			i++;
//		}
//
//		
//		// // genero  concetto \exists R.researchGroup-{owlIndividual}
//		OWLIndividual[] inds= new OWLIndividual[researchGroup.size()];
//		inds= researchGroup.toArray(inds);
//
//		for (int j = 0; j < inds.length; j++) {
//
//			Set<OWLIndividual> subset= new HashSet<OWLIndividual>();
//
//			for (int j2 = 0; j2 < inds.length; j2++) {
//
//				if (j2!=j)
//					subset.add(inds[j2]);
//
//
//			}
//
//			negqueries[j]= dataFactory2.getOWLObjectSomeRestriction(prop, dataFactory2.getOWLObjectOneOf(subset));
//			System.out.println("Neg Queries: "+j+") "+negqueries[j]);
			
		Couple<OWLClassExpression[],OWLClassExpression[]> couple= new Couple<OWLClassExpression[],OWLClassExpression[]>();
		couple.setFirstElement(queries);
		couple.setSecondElement(negqueries);
		
		
		
		return couple;
			


		}

	
	public OWLIndividual[] getExamples() {
		return allExamples;
	}
}
