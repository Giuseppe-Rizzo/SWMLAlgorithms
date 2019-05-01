package it.uniba.di.lacam.ml.evaluation.task.generators;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;
import it.uniba.di.lacam.ml.utils.Couple;

public class CarcinogesesisGenerator extends ConceptGenerator {
	private OWLIndividual[] examples;

	

	public CarcinogesesisGenerator(KnowledgeBase k) {
		
	 super(k);
		// TODO Auto-generated constructor stub
	}

	
	public  Couple<OWLClassExpression[],OWLClassExpression[]> generateQueryConcept(){
		
		kb.loadFunctionalDataProperties();
		
		OWLDataProperty[] dataProperties = kb.getDataProperties();
//		for (int i = 0; i < dataProperties.length; i++) {
//			System.out.println(dataProperties[i]);
//		}
		
	   OWLDataProperty query= dataProperties[14]; // ismutagenic
	   Set<OWLClassExpression> classesInSignature = query.getDomains(kb.getOntology());
	   HashSet<OWLIndividual> examples= new HashSet<OWLIndividual>();
	   System.out.println("classes in signature"+ classesInSignature);
	   for (OWLClassExpression c : classesInSignature) {
			examples.addAll((reasoner.getInstances(c, true).getFlattened()));
	}
	   System.out.println(examples.size());
	   this.examples= examples.toArray(new OWLIndividual[examples.size()]);
	   
	   OWLLiteral[][] propertyValue= kb.getDataPropertiesValue();
	   
	   HashSet<OWLLiteral> label= new HashSet<OWLLiteral>();
	   for (int i = 0; i < propertyValue[14].length; i++) {
		   label.add(propertyValue[14][i]);
	   }
	  
	   System.out.println(label);
		OWLLiteral[] lblarray= label.toArray(new OWLLiteral[2]);	
		
		for (int i = 0; i < lblarray.length; i++) {
			System.out.println(lblarray[i]);
		}
		
		OWLClassExpression owlDataSomeRestriction = kb.getDataFactory().getOWLDataSomeValuesFrom(query, kb.getDataFactory().getOWLDataOneOf(lblarray[1]));		
		OWLClassExpression negOwlDataSomeRestriction = kb.getDataFactory().getOWLDataSomeValuesFrom(query, kb.getDataFactory().getOWLDataOneOf(lblarray[0]));
		Couple<OWLClassExpression[], OWLClassExpression[]> couple = new Couple <OWLClassExpression[], OWLClassExpression[]>();
		OWLClassExpression[] queries={owlDataSomeRestriction};
		couple.setFirstElement(queries);
		OWLClassExpression[] negqueries={negOwlDataSomeRestriction};
		couple.setSecondElement(negqueries);
		return couple;
		
	}


	public OWLIndividual[] getExamples() {
		return examples;
	}

	
	
	
	
}
