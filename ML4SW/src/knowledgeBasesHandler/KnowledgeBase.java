package knowledgeBasesHandler;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.HermiT.Reasoner;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
//import com.hp.hpl.jena.reasoner.Reasoner;

import classifiers.knn.FeaturesDrivenDistance;
import evaluation.Parameters;
import evaluation.designOfExperiments.AlgorithmName;
/**
 *  a class for ontology interfacing
 */
public class KnowledgeBase implements IKnowledgeBase {
	static final double d = 0.3;
	//private String urlOwlFile = "file:///C:/Users/Giuseppe/Desktop//mod-biopax-example-ecocyc-glycolysis.owl";
	private String urlOwlFile = "file:///C:/Users/Giusepp/Desktop/Ontologie/GeoSkills.owl";
	private  OWLOntology ontology;
	protected  Reasoner reasoner;
	private  OWLOntologyManager manager;
	private  OWLClass[] allConcepts;
	private  OWLObjectProperty[] allRoles;
	private  OWLDataFactory dataFactory;
	private  OWLIndividual[] allExamples;
	/* Data property: propriet�, valori e domini*/
	private OWLLiteral[][] dataPropertiesValue;
	private  OWLDataProperty[] properties;
	private  OWLIndividual[][] domini;
	private int[][] classifications;
	public static  Random generator = new Random(2);;
	private  Random sceltaDataP= new Random(1);
	private  Random sceltaObjectP= new Random(1);
	public KnowledgeBase(String url) {
		urlOwlFile=url;
		ontology=initKB();

		// projection function and entropy computation for distance-based  methods
		if  (Parameters.algorithm.compareTo(AlgorithmName.knn)==0){
		    FeaturesDrivenDistance.computeFeatureEntropies(this, getDataFactory(), getClasses());
			FeaturesDrivenDistance.preLoadPi(this, getClasses(), getIndividuals());
		}


	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#initKB()
	 */
	@Override
	public   OWLOntology initKB() {



		manager = OWLManager.createOWLOntologyManager();        

		// read the file
		URI fileURI = URI.create(urlOwlFile);
		dataFactory = manager.getOWLDataFactory();
		OWLOntology ontology = null;
		try {
			SimpleIRIMapper mapper = new SimpleIRIMapper(IRI.create("http://semantic-mediawiki.org/swivt/1.0"),IRI.create("file:///C:/Users/Utente/Documents/Dataset/Dottorato/10.owl"));
			//			manager.addURIMapper();
			manager.addIRIMapper(mapper);
			ontology = manager.loadOntologyFromOntologyDocument(new File(fileURI));
			//			OWLImportsDeclaration importDeclaraton = dataFactory.getOWLImportsDeclarationAxiom(ontology, URI.create("file:///C:/Users/Utente/Documents/Dataset/10.owl"));
			//		   manager.makeLoadImportRequest(importDeclaraton);


		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}


		reasoner = new   Reasoner(ontology);//PelletReasoner(ontology, BufferingMode.NON_BUFFERING);

//		reasoner.getKB().realize();
		System.out.println("\nClasses\n-------");
		Set<OWLClass> classList = ontology.getClassesInSignature();
		allConcepts = new OWLClass[classList.size()];
		int c=0;
		for(OWLClass cls : classList) {
			if (!cls.isOWLNothing() && !cls.isAnonymous()) {
				allConcepts[c++] = cls;
				System.out.println(c +" - "+cls);
			}	        		
		}
		System.out.println("---------------------------- "+c);

		System.out.println("\nProperties\n-------");
		Set<OWLObjectProperty> propList = ontology.getObjectPropertiesInSignature();
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
		Set<OWLNamedIndividual> indList = ontology.getIndividualsInSignature();
		allExamples = new OWLIndividual[indList.size()];
		int i=0;
		for(OWLNamedIndividual ind : indList) {
			allExamples[i++] = ind;        		
		}
		System.out.println("---------------------------- "+i);

		System.out.println("\nKB loaded. \n");	
		return ontology;		

	}
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getClassMembershipResult(org.semanticweb.owl.model.OWLClassExpression[], org.semanticweb.owl.model.OWLIndividual[])
	 */
	@Override
	public int[][] getClassMembershipResult(OWLClassExpression[] testConcepts, OWLClassExpression[] negTestConcepts, OWLIndividual[] esempi){
		System.out.println("\nClassifying all examples ------ ");
		classifications = new int[testConcepts.length][esempi.length];
		System.out.print("Processed concepts ("+testConcepts.length+"): \n");

		for (int c=0; c<testConcepts.length; ++c) { 
			int p=0;
			int n=0;
			System.out.printf("[%d] ",c);
			for (int e=0; e<esempi.length; ++e) {			
				classifications[c][e] = 0;
				
				if (reasoner.isEntailed(dataFactory.getOWLClassAssertionAxiom(testConcepts[c],  esempi[e]))) {
					classifications[c][e] = +1;
					p++;

				}
				else{ 
					if (!Parameters.BINARYCLASSIFICATION){
						if (reasoner.isEntailed(dataFactory.getOWLClassAssertionAxiom(negTestConcepts[c],  esempi[e])) )
							classifications[c][e] = -1;
					}
					else
						classifications[c][e]=-1;
					
					n++;

				}
			}
			System.out.printf(": %d  %d \n",p,n);


		}
		return classifications;

	}

	public void setClassMembershipResult(int[][] classifications){
		
		this.classifications=classifications;
		
	}
	
	public int[][] getClassMembershipResult(){
		
		return classifications;
	}
	
	
	
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getRoleMembershipResult(org.semanticweb.owl.model.OWLObjectProperty[], org.semanticweb.owl.model.OWLIndividual[])
	 */
	@Override
	public int[][][] getRoleMembershipResult(OWLObjectProperty[] ruoli, OWLIndividual[]esempi){
		System.out.println("\nVerifyng all individuals' relationship ------ ");
		int[][][] correlati= new int [ruoli.length][esempi.length][esempi.length];
		// per ogni regola 
		for (int i=0;i<ruoli.length;i++){
			//per ogni esempio a

			for(int j=0;j<esempi.length;j++){

				//per ogni esempio b
				for(int k=0;k<esempi.length;k++){
					// verifico che l'esempio j � correlato all'esempio k rispetto alla regola i
					//System.out.println(regole[i]+" vs "+dataFactory.getOWLNegativeObjectPropertyAssertionAxiom(esempi[j], regole[i], esempi[k]).getProperty());
					correlati[i][j][k]=0;
					
					if(reasoner.isEntailed(dataFactory.getOWLObjectPropertyAssertionAxiom(ruoli[i], esempi[j], esempi[k])))
					{correlati[i][j][k]=1;
					//System.out.println(" Regola "+i+":   "+regole[i]+" Individui: "+i+" "+esempi[j]+" "+k+" "+esempi[k]+" "+correlati[i][j][k]);

					}
					else{
						correlati[i][j][k]=-1;
						//						System.out.println(" Regola "+regole[i]+" Individui: "+i+" "+esempi[j]+" "+k+" "+esempi[k]+" "+correlati[i][j][k]);
					}

				}
			}


		}
		return correlati;
	}

	public  void loadFunctionalDataProperties(){
		System.out.println("Data Properties--------------");

		Set<OWLDataProperty> propertiesSet = ontology.getDataPropertiesInSignature();

		Iterator<OWLDataProperty> iterator=propertiesSet.iterator();
		List<OWLDataProperty> lista= new ArrayList<OWLDataProperty>();
		while(iterator.hasNext()){
			OWLDataProperty corrente=iterator.next();
//			System.out.println(corrente+"-"+corrente.isFunctional(ontology));
			// elimino le propriet� non funzionali

//			if(corrente.isFunctional(ontology)){
				lista.add(corrente);
//				System.out.println(corrente+"-"+corrente.isFunctional(ontology));
//			}
		}




		properties=new OWLDataProperty[lista.size()];
		if(lista.isEmpty())
			throw  new RuntimeException("Non ci sono propriet� funzionali");
		lista.toArray(properties);
		//		System.out.println("\n Verifica cardinalit� del dominio....");


		domini=new OWLIndividual[properties.length][];
		dataPropertiesValue= new OWLLiteral[properties.length][];
		// per ogni propriet�...
		for(int i=0;i<properties.length;i++){

			domini[i]=new OWLIndividual[0];
			Map<OWLIndividual, Set<OWLLiteral>> prodottoCartesiano=getDatatypeAssertions(properties[i]);
			Set<OWLIndividual> chiavi=prodottoCartesiano.keySet();
			//			System.out.println("Dominio propriet�: "+chiavi);
			domini[i]=chiavi.toArray(domini[i]);// ottenimento individui facenti parte del dominio
			//			System.out.println("Cardinalit�: "+domini[i].length);
			//			System.out.println(properties[i]+"-"+ domini[i].length);
			dataPropertiesValue[i]= new OWLLiteral[domini[i].length];

			//... e  per l'elemento del dominio corrente...

			for(int j=0;j<domini[i].length;j++){

				//... determino il valore per una propriet� funzionale

				Set<OWLLiteral> valori=prodottoCartesiano.get(domini[i][j]);
				//				System.out.println(properties[i]+":    "+ i+" "+j+domini[i][j]+"----"+valori);
				OWLLiteral[] valoriArray=new OWLLiteral[0];
				valoriArray=valori.toArray(valoriArray);
				dataPropertiesValue[i][j]=valoriArray[0]; // la lunghezza � pari ad 1 perch� il valore possibile per 1 elemento � uno solo
				//				System.out.println(dataPropertiesValue[i][j]);

			}


		}



	}
	public Map<OWLIndividual, Set<OWLLiteral>> getDatatypeAssertions(OWLDataProperty dataProperty){
		//final Set<OWLDatatype> datatypesInSignature = ontology.getDatatypesInSignature();
		Map<OWLIndividual, Set<OWLLiteral>> asserzioni =  new HashMap<OWLIndividual, Set<OWLLiteral>>();
		for  (OWLIndividual ex: allExamples) {
		   Set<OWLLiteral> dataPropertyValues = reasoner.getDataPropertyValues((OWLNamedIndividual) ex, dataProperty);
		  asserzioni.put(ex, dataPropertyValues);
		}
		  
		  return asserzioni;


	}


	//********************METODI DI ACCESSO  ALLE COMPONENTI DELL'ONTOLOGIA*******************************//
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getRuoli()
	 */
	@Override
	public OWLObjectProperty[] getRoles(){
		return allRoles;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getClasses()
	 */
	@Override
	public OWLClass[] getClasses(){
		return allConcepts;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getIndividui()
	 */
	@Override
	public OWLIndividual[] getIndividuals(){

		return allExamples;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getDataProperties()
	 */
	@Override
	public OWLDataProperty[] getDataProperties(){
		return properties;
	}

	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getDomini()
	 */
	@Override
	public OWLIndividual[][] getDomains(){
		return domini;
	}
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getDataPropertiesValue()
	 */
	@Override
	public OWLLiteral[][] getDataPropertiesValue(){
		return dataPropertiesValue;

	}
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getURL()
	 */
	@Override
	public String getURL(){
		return urlOwlFile;
	}





	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getRandomProperty(int)
	 */
	@Override
	public int[] getRandomProperty(int numQueryProperty){

		int[] queryProperty= new int[numQueryProperty];
		int dataTypeProperty=0;
		while(dataTypeProperty<numQueryProperty ){

			int query=sceltaDataP.nextInt(properties.length);
			if (domini[query].length>1){
				queryProperty[dataTypeProperty]=query ;	// creazione delle dataProperty usate per il test
				dataTypeProperty++;

			}

		}
		return queryProperty;
	}
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getRandomRoles(int)
	 */
	@Override
	public int[] getRandomRoles(int numRegole){
		int[] regoleTest= new int[numRegole];
		// 1-genero casualmente un certo numero di regole sulla base delle
		//quali fare la classificazione
		for(int i=0;i<numRegole;i++)
			regoleTest[i]=sceltaObjectP.nextInt(numRegole);
		return regoleTest;

	}

	public Reasoner getReasoner(){
		return reasoner;
	}

	public OWLDataFactory getDataFactory() {
		return dataFactory;
	}

	public OWLOntology getOntology(){
		return ontology;
	}
	
	public void updateExamples(OWLIndividual[] individuals){
		allExamples=individuals;
	}
	
	/**
	 * Sceglie casualmente un concetto tra quelli generati
	 * @return il concetto scelto
	 */
	public OWLClassExpression getRandomConcept() {
		// sceglie casualmente uno tra i concetti presenti 
		OWLClassExpression newConcept = null;

		
		if (!Parameters.BINARYCLASSIFICATION){
			
			// case A:  ALC and more expressive ontologies
			do {
				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)];
				if (KnowledgeBase.generator.nextDouble() < 0.7) {
					OWLClassExpression newConceptBase = getRandomConcept();
					if (KnowledgeBase.generator.nextDouble() < 0.1) {
						
						if (KnowledgeBase.generator.nextDouble() <0) { // new role restriction
							OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
							//					OWLClassExpression roleRange = (OWLClassExpression) role.getRange;

							if (KnowledgeBase.generator.nextDouble() < 0.5)
								newConcept = dataFactory.getOWLObjectAllValuesFrom(role, newConceptBase);
							else
								newConcept = dataFactory.getOWLObjectSomeValuesFrom(role, newConceptBase);
						}
						else					
							newConcept = dataFactory.getOWLObjectComplementOf(newConceptBase);
					}
				} // else ext
				//				System.out.printf("-->\t %s\n",newConcept);
				//			} while (newConcept==null || !(reasoner.getIndividuals(newConcept,false).size() > 0));
			} while (!reasoner.isSatisfiable(newConcept));
		}else{
			// for less expressive ontologies ALE and so on (complemento solo per concetti atomici)
			do {
				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)];
				if (KnowledgeBase.generator.nextDouble() < d) {
					OWLClassExpression newConceptBase = getRandomConcept();
					if (KnowledgeBase.generator.nextDouble() < d)
						if (KnowledgeBase.generator.nextDouble() < 0.1) { // new role restriction
							OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
							//					OWLClassExpression roleRange = (OWLClassExpression) role.getRange;

							if (KnowledgeBase.generator.nextDouble() < d)
								newConcept = dataFactory.getOWLObjectAllValuesFrom(role, newConceptBase);
							else
								newConcept = dataFactory.getOWLObjectSomeValuesFrom(role, newConceptBase);
						}
				} // else ext
				else{ //if (KnowledgeBase.generator.nextDouble() > 0.8) {					
					newConcept = dataFactory.getOWLObjectComplementOf(newConcept);
				}
				//				System.out.printf("-->\t %s\n",newConcept);
				//			} while (newConcept==null || !(reasoner.getIndividuals(newConcept,false).size() > 0));
			} while (!reasoner.isSatisfiable(newConcept));
			
			
			
		}

		return newConcept;				
	}


}
