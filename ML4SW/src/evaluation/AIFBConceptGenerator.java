package evaluation;

import java.util.HashSet;

import java.util.Set;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import utils.Couple;

import knowledgeBasesHandler.KnowledgeBase;
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


	public  Couple<OWLDescription[],OWLDescription[]> generateQueryConcept(){


		OWLClass owlClass = kb.getClasses()[40]; // research group
		OWLClass owlClass2 = kb.getClasses()[31]; // person
		OWLObjectProperty prop= kb.getRoles()[95];
		// considerare la proprietà no. 95

		System.out.println(owlClass2 +" "+reasoner.getIndividuals(owlClass2, false).size());
		examples= new OWLIndividual[reasoner.getIndividuals(owlClass2, false).size()];
		examples=reasoner.getIndividuals(owlClass2, false).toArray(examples);

		// per ogni research group generare il concetto \exists R 

		Set<OWLIndividual> researchGroup = reasoner.getIndividuals(owlClass, false); // retrieval of research group
		OWLDescription[] queries= new OWLDescription[researchGroup.size()];
		OWLDescription[] negqueries= new OWLDescription[researchGroup.size()];
		OWLDataFactory dataFactory2 = kb.getDataFactory();
		int i=0;

		// genero  concetto \exists R.{owlIndividual}
		for (OWLIndividual owlIndividual : researchGroup) {

			queries[i]= dataFactory2.getOWLObjectSomeRestriction(prop, dataFactory2.getOWLObjectOneOf(owlIndividual));
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

			negqueries[j]= dataFactory2.getOWLObjectSomeRestriction(prop, dataFactory2.getOWLObjectOneOf(subset));
			System.out.println("Neg Queries: "+j+") "+negqueries[j]);
			
			
			


		}
		// verifica se sono disgiunti
//		for(int k=0; k<queries.length;k++){
//			
//			System.out.println(reasoner.isDisjointWith( queries[k], negqueries[k])); // non sono disgiunti
//			
//		}
		
		Couple<OWLDescription[], OWLDescription[]> couple = new Couple <OWLDescription[], OWLDescription[]>();
		couple.setFirstElement(queries);
		couple.setSecondElement(negqueries);
		return couple;
	}


	public OWLIndividual[] getExamples() {
		return examples;
	}

}
