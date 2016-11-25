package evaluation.metrics.separability;

import java.net.URI;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

//import org.mindswap.pellet.jena.OWLReasoner;
import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

/*import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;*/

import uk.ac.manchester.cs.owl.OWLObjectSomeRestrictionImpl;
import uk.ac.manchester.cs.owl.OWLObjectAllRestrictionImpl;

import uk.ac.manchester.cs.owl.OWLObjectComplementOfImpl;
import uk.ac.manchester.cs.owl.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.OWLObjectUnionOfImpl;



public class FeatureConstruction {

	
	// Locations in Science
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/Science/Science.owl";
	
	// Locations in Science
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/Science/Locations.owl";
	
	// ISO
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/iso-19115.owl";
	
	// wine merged with food
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/wineMix.owl";
	
	// Trains excerpt from an ontology posted on Pellet's mailinglist
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/Trains.owl";

	//Percorso e namespace famiglia
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/DinastieConPiuFamiglieConCambioDiAsserzioniDiConcetti.owl";
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/famiglia-50.owl";
	
	//Percorso e namespace ntn
	//static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/ntn/NTMerged.owl";
	 static String urlOwlFile = "file:///C:/Users/Giuseppe/Desktop/Ontologie/fsm.owl";
	//Percorso e namespace University
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/university.owl";

	// ontologia fsm
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/fsm.owl";
	
	// ontologia model
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/model.owl";	
	
	//Percorso e namespace wine
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/wine.rdf";

	// ontologia financial abbreviata:  1000 individui 
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/financial-abbrev.owl";
	
	// ontologia servizi
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/cluster_new/Cluster_dataset_test.rdf-xml.owl";

//	// ontologia LUBM
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/OntologieLUBM/OntoPiccole/University0_1.owl";
	
//	// ontologia ofn
//	static String urlOwlFile = "file:///home/nico/Documenti/Sviluppo/ontologie/ofn.owl";
	
	// parametres
	private static final double minFitness = 0.80;
	private static final int maxGenerations = 10;
	private static final int nOffsprings = 32;
		
	

	public static short[][] pi;
	private static OWLOntologyManager manager;
	private static OWLDataFactory dataFactory;
	private static Reasoner reasoner;
	private static OWLOntology theKB;
	private static OWLClass[] allConcepts;
	private static OWLObjectProperty[] allRoles;
	private static OWLIndividual[] individuals;
	
	
//	private static double computeBestFitness(Concept[][] pools) {
//		double maxFitness = 0;
//		for (int p=0; p<pools.length; p++) {		
//			double currFitness = fitness(pools[p]);
//			if (currFitness > maxFitness)
//				maxFitness = currFitness;
//		}
//		return maxFitness;
//	}
//	

	/**
	 * @param args
	 * @throws OWLReasonerException 
	 */
public static void main(String[] args) throws OWLReasonerException {		
		theKB = initKB();
				
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
		final int initialPoolCardinality = (int) Math.round(Math.pow(individuals.length,1.0/3.0))+1;
		OWLDescription[] features = constructFeatures(initialPoolCardinality);		
		
		System.out.printf("FINAL FEATURES\n");
		int f=0;
		for (OWLDescription c : features) 
			System.out.printf("%4d. %s \n",f++,c);		
	} // main
	
	
	public static OWLDescription[] constructFeatures(int nOfFeatures) throws OWLReasonerException {
		
		OWLDescription[][] offsprings;
		OWLDescription[] currentPool = new OWLDescription[nOfFeatures];
		 
		for(int f=0; f < nOfFeatures; f++)
			currentPool[f] = getRandomConcept();		
		OWLDescription[] previousBestPool = currentPool;
		double currentFitness = 0, previousBestFitness = 0;
		
		boolean stopGeneration;
		do {
//			currentPool = initPopulation(poolCardinality);
			printPool(currentPool, 0);
			int generationNumber = 0;
			stopGeneration = false;
			
			pi = new short[currentPool.length][individuals.length];
			currentFitness = fitness(currentPool);
			while (!(currentFitness > minFitness || generationNumber == maxGenerations)) {
				offsprings = generateOffsprings(currentPool);
				currentPool = selectFromPopulation(offsprings);
				currentFitness = fitness(currentPool);
				generationNumber++;
				System.out.printf("\n////////////////// GENERATION %d ////////////////// Best fitness: %f \n\n", generationNumber, currentFitness);
			}
			if (currentFitness > previousBestFitness && currentFitness < minFitness) {
				previousBestPool = currentPool;
				previousBestFitness = currentFitness;
				currentPool = enlargePool(currentPool);
			} else {
				stopGeneration = true;
				System.out.printf("\n... Stopping... Best fitness: %f \n\n", currentFitness);
			}
		} while (!stopGeneration);
		return previousBestPool;
	}
	
	
	
	static OWLDescription[] enlargePool(OWLDescription[] oldPool) {
		System.out.printf("\n\n .... increasing the number of features .... \n\n");
		OWLDescription[] newPool = new OWLDescription[oldPool.length+1];
		for (int i=0; i<oldPool.length; i++)
			newPool[i] = oldPool[i];
		newPool[oldPool.length] = getRandomConcept();
		return newPool;
			
		}
	
	
	
	public static double fitness(OWLDescription[] pool) throws OWLReasonerException {
		setProjections(pool);
		long sum = 0;
			for (int i = 0; i<individuals.length; i++)
				for (int j = i+1; j<individuals.length; j++) {
					boolean agree = true;
					for (int k=0; agree & k<pi.length; ++k)
						agree = (pi[k][i] == pi[k][j]);
					sum += (agree ? 0 : 1);
				}	
		double fitness = (double)sum/((individuals.length * individuals.length)/2.0);
		System.out.println();
		return fitness;
	}

	

	public static OWLDescription[][] generateOffsprings(OWLDescription[] seedPool) {
		int p;
		Random randomGenerator = new Random(System.currentTimeMillis());
		OWLDescription[][] newPop = new OWLDescription[nOffsprings][];
		
		// no mutation
		newPop[0] = seedPool;
		
		// concept (negation) mutation
		for (p = 1; p <=nOffsprings/3; p++) {
//			System.out.printf("...generating offspring #%3d\n",p);
			newPop[p] = seedPool.clone();
		    int f = randomGenerator.nextInt(seedPool.length);
	    	newPop[p][f] = getRandomConcept();
//		    System.out.printf("...generated %s\n\n",newPop[p][f]);
		}
		
		// conjunctive mutation
		for (p = nOffsprings/3+1; p <=2*nOffsprings/3; p++) {
//			System.out.printf("...generating offspring #%3d\n",p);
			newPop[p] = seedPool.clone();
			int f = randomGenerator.nextInt(seedPool.length);
			OWLDescription newConjConcept; 
			do {
				HashSet conjConceptSet = new HashSet();
				conjConceptSet.add(seedPool[f]);
				conjConceptSet.add(getRandomConcept());
				newConjConcept = new OWLObjectIntersectionOfImpl(dataFactory,conjConceptSet);
	//			System.out.printf("...got stuck HERE ? %s \n",conjConceptSet);
			} while (!reasoner.isSatisfiable(newConjConcept));
			newPop[p][f] = newConjConcept;
//			System.out.printf("...generated %s\n\n",newConjConcept);
		}		
		
		// disjunctive mutation
		for (p = 2*nOffsprings/3+1; p < nOffsprings; p++) {
//			System.out.printf("...generating offspring #%3d\n",p);
			newPop[p] = seedPool.clone();
			int f = randomGenerator.nextInt(seedPool.length);
			OWLDescription newDisjConcept;
			do {
				HashSet disjConceptSet = new HashSet();
				disjConceptSet.add(seedPool[f]);
				disjConceptSet.add(getRandomConcept());
				newDisjConcept = new OWLObjectUnionOfImpl(dataFactory,disjConceptSet);
			} while(reasoner.isSubClassOf(newDisjConcept, seedPool[f]));
			newPop[p][f] = newDisjConcept;	
//			System.out.printf("...generated %s\n\n",newDisjConcept);
		}	
		return newPop;
}



//	private static double fitness(Concept[] pool) {
//		computeProjections(pool);
//		double fitness = 0.0;
//			for (int i = 0; i<individuals.length; i++)
//				for (int j = i+1; j<individuals.length; j++) {
//					fitness += (simpleDistance(i,j)>0?1:0);
//				}	
//		fitness = fitness/((individuals.length * individuals.length)/2.0);
//		System.out.println();
//		return fitness;
//	}
	
public static OWLOntology initKB() {
		
		System.out.println(urlOwlFile);
        manager = OWLManager.createOWLOntologyManager();        
        
        // read the file
        URI fileURI = URI.create(urlOwlFile);
        
        OWLOntology ontology = null;
		try {
			ontology = manager.loadOntologyFromPhysicalURI(fileURI);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		dataFactory = manager.getOWLDataFactory();
		reasoner = new Reasoner(manager);
		((Reasoner) reasoner).loadOntology(ontology);		
		
		System.out.println("\nClasses\n-------");
		Set<OWLClass> classList = ontology.getReferencedClasses();
		allConcepts = new OWLClass[classList.size()];
		int c=0;
        for(OWLClass cls : classList) {
			if (!cls.isOWLThing() && !cls.isOWLNothing() &&	!cls.isAnonymous()) {
				allConcepts[c++] = cls;
				System.out.println(cls);
			}	        		
		}
        System.out.println("---------------------------- "+c);

		System.out.println("\nProperties\n-------");
        Set<OWLObjectProperty> propList = ontology.getReferencedObjectProperties();
		allRoles = new OWLObjectProperty[propList.size()];
		int op=0;
        for(OWLObjectProperty prop : propList) {
			if (!prop.isAnonymous()) {
				allRoles[op++] = prop;
				System.out.println(prop);
			}	        		
		}
        System.out.println("---------------------------- "+op);
        
        System.out.println("\nIndividuals\n-----------");
        Set<OWLIndividual> indList = ontology.getReferencedIndividuals();
        individuals = new OWLIndividual[indList.size()];
        int i=0;
        for(OWLIndividual ind : indList) {
			if (!ind.isAnonymous()) {
				individuals[i++] = ind;
				System.out.println(ind);
			}	        		
		}
        System.out.println("---------------------------- "+i);
    
		System.out.println("\nKB loaded. \n");	
		return ontology;		
	}


	
	private static OWLDescription getRandomConcept() {
		Random generator = new Random();
		OWLDescription newConcept;

		do {
			if (Math.random() < 0.3) { // base case
				newConcept = allConcepts[generator.nextInt(allConcepts.length)];
			} else if (Math.random() < 0.5) { // base case
				newConcept = getRandomConcept();
				if (Math.random() < 0.5) { // new role restriction
					OWLObjectProperty role = allRoles[generator.nextInt(allRoles.length)];
					// OWLDescription roleRange = (OWLDescription)
					// role.getRange;
					OWLDescription selectedSubConcept = getRandomConcept();
					if (Math.random() < 0.5)
						newConcept = dataFactory.getOWLObjectAllRestriction(role, selectedSubConcept);
					else
						newConcept = dataFactory.getOWLObjectSomeRestriction(role, selectedSubConcept);
				}
			} else {
				OWLDescription newConceptBase = getRandomConcept();
				if (newConceptBase instanceof OWLObjectComplementOf)
					newConcept = ((OWLObjectComplementOf) newConceptBase).getOperand();
				else
					newConcept = dataFactory.getOWLObjectComplementOf(newConceptBase);
			}
		} while (newConcept==null || !reasoner.isSatisfiable(newConcept));

		return newConcept;
	}	


	
	public static void printPool(OWLDescription[] pool, int number){
		System.out.printf("Pool #%d \n", number);		
		for (int h=0; h < pool.length; ++h) 
			System.out.printf("%4d. %s \n",h, pool[h]);
		System.out.println("----------------------------------------------------------------------------------------");
	}


	static OWLDescription[] selectFromPopulation(OWLDescription[][] population) throws OWLReasonerException {		
		double maxFitness = fitness(population[0]);
		printPool(population[0],0);
		System.out.printf("fitness: %f\n\n", maxFitness);		
		int bestPop = 0;
		
		for (int p=1; p<population.length; p++) {		
			double currFitness = fitness(population[p]);
//			printPool(population[p],p);
			System.out.printf("fitness: %f\n\n", currFitness);
			if (currFitness > maxFitness) {
				bestPop = p;
				maxFitness = currFitness;
			}		
		}
		return population[bestPop];		
	}

	
	
	public static void setProjections(OWLDescription[] pool) throws OWLReasonerException{		
		for (int f=0; f < pool.length; ++f) {
			OWLDescription negatedConcept = new OWLObjectComplementOfImpl(dataFactory, pool[f]);

			for (int i=0; i < individuals.length; i++) {
				// case: ind is not an instance of h
					if 	(reasoner.hasType(individuals[i],pool[f],false)) 
						pi[f][i] = 0;
					else {
						// case: ind is not an instance of h
						if (reasoner.hasType(individuals[i],negatedConcept,false))	
							pi[f][i] = 2;
						else
							// case unknown membership
							pi[f][i] = 1;
					}
			}
		}
	}
	


	public static OWLDescription[] optimizeGP(String urlOwlFile, String[] nSpace) throws OWLReasonerException {		
		theKB = initKB();
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
		final int initialPoolCardinality = (int) Math.round(Math.pow(individuals.length,1.0/3.0))+1;
		OWLDescription[] features = constructFeatures(initialPoolCardinality);		
		
		System.out.printf("FINAL FEATURES\n");
		int f=0;
		for (OWLDescription c : features) 
			System.out.printf("%4d. %s \n",f++,c);		
		return features;
	} // main

	
	
	public static OWLDescription[] optimizeGP
		(int nFeatures, OWLOntologyManager manager0, OWLDataFactory dataFactory0, Reasoner reasoner0,
			OWLOntology theKB0, OWLClass[] allConcepts0, OWLObjectProperty[] allRoles0,
			OWLIndividual[] individuals0) 
	throws OWLReasonerException {		
		
		manager = manager0;
		dataFactory = dataFactory0;
		reasoner = reasoner0;
		theKB = theKB0;
		allConcepts = allConcepts0;
		allRoles = allRoles0;
		individuals = individuals0;
		
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
//		final int initialPoolCardinality = (int) Math.round(Math.pow(individuals.length,1.0/3.0))+1;
		final int initialPoolCardinality = nFeatures;
		OWLDescription[] features = constructFeatures(initialPoolCardinality);		
		
		System.out.printf("FINAL FEATURES\n");
		int f=0;
		for (OWLDescription c : features) 
			System.out.printf("%4d. %s \n",f++,c);		
		return features;
	} // main
	
}