

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import knowledgeBasesHandler.KnowledgeBase;

import org.dllearner.core.ComponentInitException;
import org.dllearner.kb.OWLAPIOntology;
import org.dllearner.reasoning.FastInstanceChecker;
import org.dllearner.reasoning.OWLAPIReasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import evaluation.Evaluation;
import evaluation.Parameters;
import evaluation.task.*;
import evaluation.task.ClassMembershipPrediction;




public class Main {
	
static KnowledgeBase kb;
//	static int[][] classification;
 public  PrintStream console = System.out;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Parameters.loadParameters(); //loading from property file
kb = new KnowledgeBase(Parameters.urlOwlFile);
//		GeoSkillsGenerator gs= new GeoSkillsGenerator(kb);
//		gs.generateQueryConcept();
		
	
	        
//	OWLDataFactoryImpl owlDataFactoryImpl = new OWLDataFactoryImpl();
//	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//   OWLOntology ontology= null;
//	try {
//		//SimpleIRIMapper mapper = new SimpleIRIMapper(IRI.create("http://semantic-mediawiki.org/swivt/1.0"),IRI.create("file:///C:/Users/Utente/Documents/Dottorato/Dataset/Dottorato/10.owl"));
//		//			manager.addURIMapper();
//		//manager.addIRIMapper(mapper);
//
//		//ontology = manager.loadOntologyFromPhysicalURI(fileURI);
//		//org.semanticweb.owlapi.model.OWLImportsDeclaration importDeclaraton = owlDataFactoryImpl.getOWLImportsDeclaration(IRI.create("file:///C:/Users/Utente/Documents/Dottorato/Dataset/Dottorato/10.owl"));
//		//manager.makeLoadImportRequest(importDeclaraton);
//		ontology = manager.loadOntologyFromOntologyDocument(new FileInputStream("C:/Users/Utente/Documents/Dottorato/Dataset/Tesi_triennale/Ontologie/Dataset/biopax.owl"));
//	} catch (OWLOntologyCreationException | FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//   org.semanticweb.owlapi.model.IRI ontologyIRI = manager.getOntologyDocumentIRI(ontology);
//   OWLAPIOntology wrapper= new OWLAPIOntology(ontology);
//   
// FastInstanceChecker fi = new FastInstanceChecker(wrapper);
// fi.init();
//		Locale.setDefault(Locale.US);
//	DatasetGenerator dg= new  DatasetGenerator(kb);
//		dg.sampleIndividuals(500);

//	DatasetAcquisition da= new DatasetAcquisition();
//		final Set<String> readFromFile = da.readFromFile();
//		System.out.println(readFromFile.size()); 
		
		
		Evaluation vcm= null; 
		switch (Parameters.task) {
					
	case CLASSMEMBERSHIPREDICTION:
		kb = new KnowledgeBase(Parameters.urlOwlFile);
		vcm= new ClassMembershipPrediction(kb);
		break;
		
		case MUTAGENICPREDICTION:
			kb = new KnowledgeBase(Parameters.urlOwlFile);
			if (Parameters.mutagenicAsDataPropertyPrediction)
			vcm=new MutagenicPrediction(kb);
			else
				vcm=new MutagenicPrediction(kb,true);
		 break;

		case POLITICIANPREDICTION: 
			kb = new KnowledgeBase(Parameters.urlOwlFile);
			vcm= new PoliticianPrediction(kb);
			break;
			
		case BIBLICWOMANPREDICTION: 
			kb = new KnowledgeBase(Parameters.urlOwlFile);
			vcm= new BiblicWomanPrediction(kb);
			break;
			
			case GEOSKILLSPREDICTION: 
				kb = new KnowledgeBase(Parameters.urlOwlFile);
				vcm= new GeoSkillsPrediction(kb);
				break;
		 
		case AFFILIATIONPROBLEM:
			 kb = new KnowledgeBase(Parameters.urlOwlFile);
     		 vcm= new AffiliationPrediction(kb);
			break;
		default:
		//	kb =new KnowledgeBaseForRoughConceptLearning(Parameters.urlOwlFile);
			//vcm= new PUClassMembershipPrediction(kb);
  }
		
	String className =  Parameters.algorithm.toString(); 		// package name
	//
//		
//		
	if (Parameters.task.compareTo(Tasks.DATASETCREATION)!=0){
		switch (Parameters.design) {
		case BOOTSTRAP:
		 // n. of folds	
	vcm.bootstrap(10, className);
		break;
//		
	case CROSSVALIDATION:
			vcm.crossValidation(Parameters.NFOLDS,className); 
			break;
		case OVERLAPPINGMEASUREMENT:
			vcm.computeDirectClassSeparabilityMeasure();
		break;	
		}
	}
		
//System.out.println("\n\nEnding: "+Parameters.urlOwlFile);

//	} // main
	
	
	}

} // class DLTreeInducer