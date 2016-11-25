package classifiers.refinementOperator;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

import evaluation.Parameters;
import knowledgeBasesHandler.KnowledgeBase;

/**
 * A new refinement operator without random choice
 * @author Utente
 *
 */
public class NonRandomRefinementOperator extends RefinementOperator {

	public NonRandomRefinementOperator() {
		// TODO Auto-generated constructor stub
	}





	KnowledgeBase kb;
	static final double d = 0.3;
	private OWLDescription[] allConcepts;
	private OWLObjectProperty[] allRoles;
	private OWLDataFactory dataFactory;
	public NonRandomRefinementOperator(KnowledgeBase kb) {

		super();
		this.kb=kb;
		allConcepts=kb.getClasses();
		allRoles=kb.getRoles();
		dataFactory = kb.getDataFactory();

	}


	public OWLDescription getRandomConcept() {

		return null;
	}


	/**
	 * Sceglie casualmente un concetto tra quelli generati
	 * @return il concetto scelto
	 */
	public ArrayList<OWLDescription> getRandomConcept(int...parameter) {
		// sceglie casualmente uno tra i concetti presenti 
		OWLDescription newConcept = null;
		boolean stop=false;
		ArrayList<OWLDescription> toRefine;

		// case A:  ALC and more expressive ontology
		do {

			newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)]; // caso base della ricorsione 
			int refinementLength=0;
			final int MAXLENGTH=1;
			toRefine= new ArrayList<OWLDescription>();
			//getRandomConcept();  // ricorsione
			toRefine.add(newConcept);
			while (refinementLength<MAXLENGTH){
				ArrayList<OWLDescription> refinements= new ArrayList<OWLDescription>();
				while(!toRefine.isEmpty()){
					OWLDescription newConceptBase =   toRefine.get(0); // first element
					toRefine.remove(0);
					// new role restriction
					OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
					newConcept = dataFactory.getOWLObjectAllRestriction(role, newConceptBase);
					if (kb.getReasoner().isSatisfiable(newConcept))
						refinements.add(newConcept);
					newConcept = dataFactory.getOWLObjectSomeRestriction(role, newConceptBase);
					if (kb.getReasoner().isSatisfiable(newConcept))
						refinements.add(newConcept);

					newConcept = dataFactory.getOWLObjectComplementOf(newConceptBase);
					if (kb.getReasoner().isSatisfiable(newConcept))
						refinements.add(newConcept);
				}
				toRefine.addAll(refinements); // add all the refinements generated from the inner loop
				refinementLength++;

				//				newConceptBase=newConcept;

			}


			if (!toRefine.isEmpty()){ // check satisifiability of all possible refinements
				stop= true;
			}

		} while (!stop); // come stopparlo?


		return toRefine;				
	}

	public ArrayList<OWLDescription> generateNewConcepts(int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs) {

		System.out.printf("Generating node concepts ");
		ArrayList<OWLDescription> rConcepts = new ArrayList<OWLDescription>(dim);
		ArrayList<OWLDescription> newConcepts;
		boolean emptyIntersection;
		for (int c=0; c<dim; c++) {
			do {
				emptyIntersection = false; // true
				newConcepts = getRandomConcept(1);

				for(OWLDescription newConcept: newConcepts){


					Set<OWLIndividual> individuals = (kb.getReasoner()).getIndividuals(newConcept, false);
					Iterator<OWLIndividual> instIterator = individuals.iterator();
					
					int numIntersections=0;
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

					rConcepts.add(newConcept);

				}
			} while (emptyIntersection);
			System.out.printf("%d ", c);
		}
		System.out.println();

		return rConcepts;
	}






}



