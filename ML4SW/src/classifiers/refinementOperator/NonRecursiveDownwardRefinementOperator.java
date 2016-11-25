package classifiers.refinementOperator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

import evaluation.Parameters;
import knowledgeBasesHandler.KnowledgeBase;

public class NonRecursiveDownwardRefinementOperator extends RefinementOperator{
	
	

	KnowledgeBase kb;
	static final double d = 0.3;
	private OWLDescription[] allConcepts;
	private OWLObjectProperty[] allRoles;
	private OWLDataFactory dataFactory;
	public NonRecursiveDownwardRefinementOperator(KnowledgeBase kb) {
		
		super();
	this.kb=kb;
	allConcepts=kb.getClasses();
	allRoles=kb.getRoles();
	dataFactory = kb.getDataFactory();
	
	}
	
	
	
	/**
	 * Sceglie casualmente un concetto tra quelli generati
	 * @return il concetto scelto
	 */
	public OWLDescription getRandomConcept() {
		// sceglie casualmente uno tra i concetti presenti 
		OWLDescription newConcept = null;
	
			// case A:  ALC and more expressive ontology
			do {
				
				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)]; // caso base della ricorsione 
				if (KnowledgeBase.generator.nextDouble() < d) {
					OWLDescription newConceptBase =   newConcept; //getRandomConcept();  // ricorsione
					if (KnowledgeBase.generator.nextDouble() < d) {
//						
						if (KnowledgeBase.generator.nextDouble() <d) { // new role restriction
							OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
//							//					OWLDescription roleRange = (OWLDescription) role.getRange;
//
							if (KnowledgeBase.generator.nextDouble() < d)
								newConcept = dataFactory.getOWLObjectAllRestriction(role, newConceptBase);
							else
								newConcept = dataFactory.getOWLObjectSomeRestriction(role, newConceptBase);
						}
						else					
							newConcept = dataFactory.getOWLObjectComplementOf(newConceptBase);
					}
					newConceptBase=newConcept;
				} // else ext
				//				System.out.printf("-->\t %s\n",newConcept);
				//			} while (newConcept==null || !(reasoner.getIndividuals(newConcept,false).size() > 0));
			} while (!kb.getReasoner().isSatisfiable(newConcept));
		

		return newConcept;				
	}
	
	public ArrayList<OWLDescription> generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {

		System.out.printf("Generating node concepts ");
		ArrayList<OWLDescription> rConcepts = new ArrayList<OWLDescription>(dim);
		OWLDescription newConcept;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				newConcept = getRandomConcept();

				Set<OWLIndividual> individuals = (kb.getReasoner()).getIndividuals(newConcept, false);
				Iterator<OWLIndividual> instIterator = individuals.iterator();
				while (emptyIntersection && instIterator.hasNext()) {
					OWLIndividual nextInd = (OWLIndividual) instIterator.next();
					int index = -1;
					for (int i=0; index<0 && i<kb.getIndividuals().length; ++i)
						if (nextInd.equals(kb.getIndividuals()[i])) index = i;
					if (posExs.contains(index))
						emptyIntersection = false;
					else if (negExs.contains(index))
						emptyIntersection = false;
				}					
			} while (emptyIntersection);
			rConcepts.add(newConcept);
			System.out.printf("%d ", c);
		}
		System.out.println();

		return rConcepts;
	}

	


	

}
