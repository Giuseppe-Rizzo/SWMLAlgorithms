package classifiers.refinementOperator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

import evaluation.Parameters;

public class RefinementOperator {


	KnowledgeBase kb;
	static final double d = 0.5;
	private OWLDescription[] allConcepts;
	private OWLObjectProperty[] allRoles;
	private OWLDataFactory dataFactory;
	public RefinementOperator(KnowledgeBase kb) {
		// TODO Auto-generated constructor stub
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
		if (!Parameters.BINARYCLASSIFICATION){
			// case A:  ALC and more expressive ontologies
			do {
				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)];
				if (KnowledgeBase.generator.nextDouble() < d) {
					OWLDescription newConceptBase =     getRandomConcept();
					if (KnowledgeBase.generator.nextDouble() < d) {

						if (KnowledgeBase.generator.nextDouble() <d) { // new role restriction
							OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
							//					OWLDescription roleRange = (OWLDescription) role.getRange
							if (KnowledgeBase.generator.nextDouble() < d)
								newConcept = dataFactory.getOWLObjectAllRestriction(role, newConceptBase);
							else
								newConcept = dataFactory.getOWLObjectSomeRestriction(role, newConceptBase);
						}
						else					
							newConcept = dataFactory.getOWLObjectComplementOf(newConceptBase);
					}
				}

					} while (!kb.getReasoner().isSatisfiable(newConcept));
		
			}
				else{
					// for less expressive ontologies ALE and so on (complemento solo per concetti atomici)
					do {
						newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)];
						if (KnowledgeBase.generator.nextDouble() < d) {
							OWLDescription newConceptBase =  getRandomConcept(); //dataFactory.getOWLThing(); //
							if (KnowledgeBase.generator.nextDouble() < d)
								if (KnowledgeBase.generator.nextDouble() < d) { // new role restriction
									OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
									//					OWLDescription roleRange = (OWLDescription) role.getRange;

									if (KnowledgeBase.generator.nextDouble() < d)
										newConcept = dataFactory.getOWLObjectAllRestriction(role, newConceptBase);
									else
										newConcept = dataFactory.getOWLObjectSomeRestriction(role, newConceptBase);
								}
						} // else ext
					else //if (KnowledgeBase.generator.nextDouble() > 0.8) {					
							newConcept = dataFactory.getOWLObjectComplementOf(newConcept);

					} while (!kb.getReasoner().isSatisfiable(newConcept));
				}

					return newConcept;				
				}

				public ArrayList<OWLDescription> generateNewConcepts(OWLDescription fatherConceptPop, int dim, ArrayList<Integer> posExs, ArrayList<Integer> negExs, boolean seed) {

					System.out.printf("Generating node concepts ");
					ArrayList<OWLDescription> rConcepts = new ArrayList<OWLDescription>(dim);
					if (!seed){
						OWLDescription newConcept=null;
						boolean emptyIntersection;
						for (int c=0; c<dim; c++) {
							do {
								emptyIntersection =  true;
								try{

									newConcept = dataFactory.getOWLObjectIntersectionOf(fatherConceptPop, getRandomConcept()).getNNF();
									Set<OWLIndividual> individuals;

									//				individuals = (kb.getReasoner()).getIndividuals(newConcept, true);
									//							Iterator<OWLIndividual> instIterator = individuals.iterator();
									//				while (emptyIntersection && instIterator.hasNext()) {
									//					OWLIndividual nextInd = (OWLIndividual) instIterator.next();
									//					int index = -1;
									//					for (int i=0; index<0 && i<kb.getIndividuals().length; ++i)
									//						if (nextInd.equals(kb.getIndividuals()[i])) index = i;
									//					if (posExs.contains(index))
									//						emptyIntersection = false;
									//					else if (negExs.contains(index))
									//						emptyIntersection = false;
									//				}	

									int index=0;
									while (emptyIntersection && index<posExs.size()){
										if (kb.getReasoner().hasType(kb.getIndividuals()[posExs.get(index)], newConcept));
										emptyIntersection=false;
										index++;
									}

									index=0;
									while (emptyIntersection && index<negExs.size()){
										if (kb.getReasoner().hasType(kb.getIndividuals()[negExs.get(index)], newConcept));
										emptyIntersection=false;
										index++;
									}
								}catch(Exception e){
									newConcept=null;
								}

							} while (emptyIntersection);
							if (newConcept !=null){
								System.out.println(newConcept==null);
								rConcepts.add(newConcept);
							}
							System.out.printf("%d ", c);
						}
						System.out.println();
					}
					else{
						OWLDescription concept= setSeed();

						System.out.println("Seed: "+ concept);
						if (concept==null){
							new RuntimeException("Seed concept not found");
						}
						rConcepts.add(concept);
					}

					return rConcepts;
				}


				private OWLDescription setSeed() {

					for (OWLDescription cl: allConcepts){
						if (cl.toString().compareToIgnoreCase(Parameters.conceptSeed)==0){		
							return cl;
						}

					}	
					return null;
				}



				public RefinementOperator(){};

			}
