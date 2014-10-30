package evaluation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;


import utils.Couple;
import knowledgeBasesHandler.KnowledgeBase;

public class CarcinogesesisGenerator extends ConceptGenerator {
	private OWLIndividual[] examples;

	

	public CarcinogesesisGenerator(KnowledgeBase k) {
		
	 super(k);
		// TODO Auto-generated constructor stub
	}

	
	public  Couple<OWLDescription[],OWLDescription[]> generateQueryConcept(){
		
		kb.loadFunctionalDataProperties();
		
		OWLDataProperty[] dataProperties = kb.getDataProperties();
//		for (int i = 0; i < dataProperties.length; i++) {
//			System.out.println(dataProperties[i]);
//		}
		
	   OWLDataProperty query= dataProperties[14]; // ismutagenic
	   Set<OWLDescription> classesInSignature = query.getDomains(kb.getOntology());
	   HashSet<OWLIndividual> examples= new HashSet<OWLIndividual>();
	   System.out.println("classes in signature"+ classesInSignature);
	   for (OWLDescription c : classesInSignature) {
			examples.addAll((reasoner.getIndividuals(c, true)));
	}
	   System.out.println(examples.size());
	   this.examples= examples.toArray(new OWLIndividual[examples.size()]);
	   
	   OWLConstant[][] propertyValue= kb.getDataPropertiesValue();
	   
	   HashSet<OWLConstant> label= new HashSet<OWLConstant>();
	   for (int i = 0; i < propertyValue[14].length; i++) {
		   label.add(propertyValue[14][i]);
	   }
	  
	   System.out.println(label);
		OWLConstant[] lblarray= label.toArray(new OWLConstant[2]);	
		
		for (int i = 0; i < lblarray.length; i++) {
			System.out.println(lblarray[i]);
		}
		
		OWLDescription owlDataSomeRestriction = kb.getDataFactory().getOWLDataSomeRestriction(query, kb.getDataFactory().getOWLDataOneOf(lblarray[1]));		
		OWLDescription negOwlDataSomeRestriction = kb.getDataFactory().getOWLDataSomeRestriction(query, kb.getDataFactory().getOWLDataOneOf(lblarray[0]));
		Couple<OWLDescription[], OWLDescription[]> couple = new Couple <OWLDescription[], OWLDescription[]>();
		OWLDescription[] queries={owlDataSomeRestriction};
		couple.setFirstElement(queries);
		OWLDescription[] negqueries={negOwlDataSomeRestriction};
		couple.setSecondElement(negqueries);
		return couple;
		
	}


	public OWLIndividual[] getExamples() {
		return examples;
	}

	
	
	
	
}
