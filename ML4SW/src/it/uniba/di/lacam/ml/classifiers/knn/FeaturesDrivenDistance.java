package it.uniba.di.lacam.ml.classifiers.knn;

//import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;


import org.semanticweb.HermiT.Reasoner;

//import org.mindswap.pellet.jena.OWLReasoner;

import org.semanticweb.owlapi.apibinding.OWLManager;
//import org.semanticweb.owlapi.;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import it.uniba.di.lacam.ml.kbhandler.KnowledgeBase;

//import uk.ac.manchester.cs.owl.OWLObjectComplementOfImpl;






/**
 * Feature Driven Pseudo-distance between two individuals
 * @author Giuseppe Rizzo
 *
 */
public class FeaturesDrivenDistance {

	public static short[][] pi;
	private static double[] featureEntropy;

	/*public static void computeFeatureEntropies(OWLReasoner reasoner, OntModel model, OntClass[] features) {

		int numIndA = reasoner.getIndividuals().size();
		featureEntropy = new double[features.length];
		double sum = 0;

		for (int f=0; f<features.length; f++) {

			OntClass complFeature = model.createComplementClass(null,features[f]);
			reasoner.load(model);

			int numPos = reasoner.getInstances(features[f]).size();
      	int numNeg = reasoner.getInstances(complFeature).size();
      	int numBoh = numIndA - numPos - numNeg;

      	double prPos = (numPos>0 ? (double)numPos/numIndA : Double.MIN_VALUE);
      	double prNeg = (numNeg>0 ? (double)numNeg/numIndA : Double.MIN_VALUE);
      	double prBoh = (numBoh>0 ? (double)numBoh/numIndA : Double.MIN_VALUE);        	

      	featureEntropy[f] = -(prPos * Math.log(prPos) + prNeg * Math.log(prNeg) + prBoh * Math.log(prBoh));
      	sum += featureEntropy[f];

		}		

		for (int f=0; f<features.length; f++) 
			featureEntropy[f] = featureEntropy[f]/sum;

	}*/

 /**
  * Entropy-based Weights for the features
  * @param kb, the knowledge base
  * @param df, the data factory
  * @param features, the concept committee adopted as features
  */
	public static void computeFeatureEntropies(KnowledgeBase kb, OWLDataFactory df, OWLClassExpression[] features) {

		int numIndA =kb.getIndividuals().length ;
		featureEntropy = new double[features.length];
		double sum = 0;

		for (int f=0; f<features.length; f++) {

			OWLClassExpression complFeature = df.getOWLObjectComplementOf(features[f]);

			int numPos = kb.getReasoner().getInstances(features[f],false).getFlattened().size();
			int numNeg = kb.getReasoner().getInstances(complFeature,false).getFlattened().size();
			int numBoh = numIndA - numPos - numNeg;

			double prPos = (numPos>0 ? (double)numPos/numIndA : Double.MIN_VALUE);
			double prNeg = (numNeg>0 ? (double)numNeg/numIndA : Double.MIN_VALUE);
			double prBoh = (numBoh>0 ? (double)numBoh/numIndA : Double.MIN_VALUE);        	

			featureEntropy[f] = -(prPos * Math.log(prPos) + prNeg * Math.log(prNeg) + prBoh * Math.log(prBoh));
			sum += featureEntropy[f];

		}		

		for (int f=0; f<features.length; f++) 
			featureEntropy[f] = featureEntropy[f]/sum;

	}



	/*public static void preLoadPi(String url, OWLReasoner reasoner, OntModel model, OntClass[] features, Resource[] allExamples) {

		String path = "";
		try {
			URI upath = new URI(url);
			path = upath.getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace(); 
		}
		File projFile = new File(path+".dat");
		if (projFile.exists()) {
			System.out.printf("Reading pi elements from file: %s\n",projFile);
			readProjections(projFile);
		}
		else {
			System.out.printf("Pre-computing %d x %d pi elements \n", features.length, allExamples.length);
			pi = new short[features.length][allExamples.length];
			for (int f=0; f < features.length; ++f) {
				System.out.printf("%4d. %50s", f, features[f].getLocalName());
				OntClass negfeature = model.createComplementClass(null, features[f]);
				reasoner.load(model);
				for (int i=0; i < allExamples.length; i++) {
						// case: ind is not an instance of h
						if 	(reasoner.isInstanceOf(allExamples[i], features[f])) 
							pi[f][i] = 0;
						else {
							// case: ind is not an instance of h							
							if (reasoner.isInstanceOf(allExamples[i],negfeature))	
								pi[f][i] = 2;
							else
								// case unknown membership
								pi[f][i] = 1;
						}
	//					System.out.print(".");
				}
				System.out.printf(" | completed. %5.1f%% \n", 100.0*(f+1)*allExamples.length / (features.length*allExamples.length)); 
			}
			System.out.println("-----------------------------------------------------------------------------------------------------------");
//			saveProjections(projFile);
//			System.out.printf("Saved pi elements to file: %s\n",projFile);
		}
	}*/

	/**
	 * 
	 * @param ind1 first individual index
	 * @param ind2 second individual index
	 * @param dim dimension of the comparison

	 * @return the (semi-)distance measure between the individuals
	 */	
	public static double sqrDistance(int ind1, int ind2) {
		double acc = 0;
		for (int h=0; h<pi.length; h++) {	
			acc += Math.pow(pi[h][ind1] - pi[h][ind2], 2); 
		}
		return (double)Math.sqrt(acc)/(2*pi.length);
	} // distance

	/**
	 * Simple distance without  weights 
	 * 
	 * @param ind1 index of the 1st individual
	 * @param ind2 index of the 2nd individual
	 *
	 * @return
	 */
	public static double simpleDistance(int ind1, int ind2) {
		double acc = 0;

		for (int f=0; f<pi.length; f++) {	
			acc += Math.abs(pi[f][ind1] - pi[f][ind2]); 
		}
		return acc/(2*pi.length); // divisione per 2 perche' doppi in pi
	} // distance


	public static double simpleEntropyDistance(int ind1, int ind2) {
		double acc = 0;

		for (int f=0; f<pi.length; f++) {	
			acc += featureEntropy[f] * Math.abs(pi[f][ind1] - pi[f][ind2]); 
		}
		return acc/(2*pi.length); // divisione per 2 perche' doppi in pi 
	} // distance


	//	private static double pi(URI ind, Concept h, KB theKB) {
	//		// case: ind is an instance of h
	//		if 	(theKB.instanceOf(ind,h)) return 0;
	//		
	//		// case: ind is not an instance of h
	//		Concept negH = NegatedConceptDescription.create(h);
	//		if (theKB.instanceOf(ind,negH))	return 1;
	//		
	//		// case unknown membership
	//		return (double)0.5;
	//	}	// pi


	static void saveProjections(File oFile) {

		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(oFile));
			oos.writeObject(pi);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	static void readProjections(File iFile) {

		ObjectInputStream ois;
		try {			
			ois = new ObjectInputStream(new FileInputStream(iFile));
			pi = (short[][]) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * Precompute the proections (actually multiplied by 2 and divided by the methods implementing the distance measure)
	 *  For a concept C: 0 if an individuals is not an instance of C, 2  if an individuals is an instance of C, 1 otherwise
	 * @param urlOwlFile
	 * @param kb
	 * @param features
	 * @param allExamples
	 */
	public static void preLoadPi(String urlOwlFile,KnowledgeBase kb,
			OWLClassExpression[] features, OWLIndividual[] allExamples) {
		// TODO Auto-generated method stub

		String path = "";
		try {
			URI upath = new URI(urlOwlFile);
			path = upath.getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace(); 
		}
		File projFile = new File(path+".dat");
		if (projFile.exists()) {
			System.out.printf("Reading pi elements from file: %s\n",projFile);
			readProjections(projFile);
			System.out.println(pi.length);

		}
		else {
			System.out.printf("Pre-computing %d x %d pi elements \n", features.length, allExamples.length);
			pi = new short[features.length][allExamples.length];

			for (int f=0; f < features.length; ++f) {
				System.out.printf("%4d. %50s", f, features[f]);

				OWLClassExpression negfeature = kb.getDataFactory().getOWLObjectComplementOf(features[f]);

				for (int i=0; i < allExamples.length; i++) {
					// case: ind is not an instance of h

					if 	(kb.getReasoner().isEntailed(kb.getDataFactory().getOWLClassAssertionAxiom(features[f], allExamples[i]))) 
						pi[f][i] = 0;
					else {
						// case: ind is not an instance of h							
						if ((kb.getReasoner().isEntailed(kb.getDataFactory().getOWLClassAssertionAxiom(negfeature, allExamples[i]))))
							pi[f][i] = 2;
						else
							// case unknown membership
							pi[f][i] = 1;
					}



					System.out.printf(" | completed. %5.1f%% \n", 100.0*(f+1)*allExamples.length / (features.length*allExamples.length)); 
				}
				System.out.println("-----------------------------------------------------------------------------------------------------------");
				saveProjections(projFile);
				//			System.out.printf("Saved pi elements to file: %s\n",projFile);
			}
		}
	}


 /**
  * Precompute the proections (actually multiplied by 2 and divided by the methods implementing the distance measure)
  *  For a concept C: 0 if an individuals is not an instance of C, 2  if an individuals is an instance of C, 1 otherwise
  * @param kb
  * @param features
  * @param individuals
  */
	public static void preLoadPi(KnowledgeBase kb,
			OWLClassExpression[] features, OWLIndividual[] individuals) {

		System.out.printf("Pre-computing %d x %d pi elements \n", features.length, individuals.length);
		pi = new short[features.length][individuals.length];

		for (int f=0; f < features.length; ++f) {
			System.out.printf("%4d. %50s", f, features[f]);

			OWLClassExpression negfeature = kb.getDataFactory().getOWLObjectComplementOf(features[f]);

			for (int i=0; i < individuals.length; i++) {
				// case: ind is not an instance of h

				if 	(kb.getReasoner().isEntailed(kb.getDataFactory().getOWLClassAssertionAxiom(features[f], individuals[i]))) 
					pi[f][i] = 0;
				else {
					// case: ind is not an instance of h							
					if ((kb.getReasoner().isEntailed(kb.getDataFactory().getOWLClassAssertionAxiom(negfeature, individuals[i]))))
						pi[f][i] = 2;
					else
						// case unknown membership
						pi[f][i] = 1;
				}
			
			}
			System.out.printf(" | completed. %5.1f%% \n", 100.0*(f+1)*individuals.length / (features.length*individuals.length)); 
			System.out.println("-----------------------------------------------------------------------------------------------------------");			
		}
	}
}	// class



