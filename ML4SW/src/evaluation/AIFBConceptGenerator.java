package evaluation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import utils.Couple;
/**
 * A snippet for the problem of affiliation for AIFB ontology
 * @author Utente
 *
 */
public class AIFBConceptGenerator extends ConceptGenerator {

	private OWLIndividual[] examples;
	public AIFBConceptGenerator(KnowledgeBase k) {
		super(k);
		// TODO Auto-generated constructor stub
	}


	public  Couple<OWLClassExpression[],OWLClassExpression[]> generateQueryConcept(){


		OWLClass owlClass = kb.getClasses()[40]; // research group
		OWLClass owlClass2 = kb.getClasses()[31]; // person
		OWLObjectProperty prop= kb.getRoles()[95];
		// considerare la propriet� no. 95

		Set<OWLNamedIndividual> instances = reasoner.getInstances(owlClass2, false).getFlattened();
		System.out.println(owlClass2 +" "+instances.size());
		examples= new OWLIndividual[instances.size()];
		examples=new ArrayList<OWLNamedIndividual>(instances).toArray(examples);

		// per ogni research group generare il concetto \exists R 

		Set<OWLNamedIndividual> researchGroup = reasoner.getInstances(owlClass, false).getFlattened(); // retrieval of research group
		OWLClassExpression[] queries= new OWLClassExpression[researchGroup.size()];
		OWLClassExpression[] negqueries= new OWLClassExpression[researchGroup.size()];
		OWLDataFactory dataFactory2 = kb.getDataFactory();
		int i=0;

		// genero  concetto \exists R.{owlIndividual}
		for (OWLIndividual owlIndividual : researchGroup) {

			queries[i]= dataFactory2.getOWLObjectSomeValuesFrom(prop, dataFactory2.getOWLObjectOneOf(owlIndividual));
			System.out.println("Queries: "+i+") "+queries[i]);
			i++;
		}

		
		// // genero  concetto \exists R.researchGroup-{owlIndividual}
		OWLIndividual[] inds= new OWLIndividual[researchGroup.size()];
		inds= researchGroup.toArray(inds);

		for (int j = 0; j < inds.length; j++) {

			Set<OWLIndividual> subset= new HashSet<OWLIndividual>();

			for (int j2 = 0; j2 < inds.length; j2++) {

				if (j2!=j)
					subset.add(inds[j2]);


			}

			negqueries[j]= dataFactory2.getOWLObjectSomeValuesFrom(prop, dataFactory2.getOWLObjectOneOf(subset));
			System.out.println("Neg Queries: "+j+") "+negqueries[j]);
			
			
			


		}
		// verifica se sono disgiunti
//		for(int k=0; k<queries.length;k++){
//			
//			System.out.println(reasoner.isDisjointWith( queries[k], negqueries[k])); // non sono disgiunti
//			
//		}
		
		Couple<OWLClassExpression[], OWLClassExpression[]> couple = new Couple <OWLClassExpression[], OWLClassExpression[]>();
		couple.setFirstElement(queries);
		couple.setSecondElement(negqueries);
		return couple;
	}


	public OWLIndividual[] getExamples() {
		return examples;
	}

}
