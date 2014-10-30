package evaluation.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

import utils.Couple;
import knowledgeBasesHandler.KnowledgeBase;
import evaluation.ConceptGenerator;

public class PoliticianGenerator extends ConceptGenerator {

	public PoliticianGenerator(KnowledgeBase k) {
		super(k);
		// TODO Auto-generated constructor stub
	}

	
	
	public  Couple<OWLDescription[],OWLDescription[]> generateQueryConcept(){


		OWLClass owlClass = kb.getClasses()[130]; // research group
		System.out.println(owlClass);
//		OWLClass owlClass2 = kb.getClasses()[31]; // person
//		OWLObjectProperty prop= kb.getRoles()[95];
//		// considerare la proprietà no. 95
//
		Set<OWLDescription> subClasses = owlClass.getSubClasses(kb.getOntology());
		
		ArrayList<OWLDescription> sub= new ArrayList<OWLDescription>(subClasses);
		
		
		
		OWLDescription owlClass2 = sub.get(0); // target concept
		subClasses.remove(owlClass2);
		OWLDescription owlClasses= kb.getDataFactory().getOWLObjectUnionOf(subClasses);
		
		
		allExamples= new OWLIndividual[reasoner.getIndividuals(owlClass, false).size()];
		allExamples=reasoner.getIndividuals(owlClass, false).toArray(allExamples);
//
//		// per ogni research group generare il concetto \exists R 
//
//		Set<OWLIndividual> researchGroup = reasoner.getIndividuals(owlClass, false); // retrieval of research group
		OWLDescription[] queries= {owlClass2};
		OWLDescription[] negqueries= {owlClasses};
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
			
		Couple<OWLDescription[],OWLDescription[]> couple= new Couple<OWLDescription[],OWLDescription[]>();
		couple.setFirstElement(queries);
		couple.setSecondElement(negqueries);
		
		
		
		return couple;
			


		}

	
	public OWLIndividual[] getExamples() {
		return allExamples;
	}
}
