package knowledgeBasesHandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.SimpleURIMapper;

import evaluation.Parameters;
/**
 *  una classe per rappresentare una Knowledge base
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
	/* Data property: proprietà, valori e domini*/
	private OWLConstant[][] dataPropertiesValue;
	private  OWLDataProperty[] properties;
	private  OWLIndividual[][] domini;
	private int[][] classifications;
	public static  Random generator = new Random(2);;
	private  Random sceltaDataP= new Random(1);
	private  Random sceltaObjectP= new Random(1);
	public KnowledgeBase(String url) {
		urlOwlFile=url;
		ontology=initKB();

		// object property  Attribut-3AForschungsgruppe



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
			SimpleURIMapper mapper = new SimpleURIMapper(URI.create("http://semantic-mediawiki.org/swivt/1.0"),URI.create("file:///C:/Users/Utente/Documents/Dataset/Dottorato/10.owl"));
			//			manager.addURIMapper();
			manager.addURIMapper(mapper);
			ontology = manager.loadOntologyFromPhysicalURI(fileURI);
			//			OWLImportsDeclaration importDeclaraton = dataFactory.getOWLImportsDeclarationAxiom(ontology, URI.create("file:///C:/Users/Utente/Documents/Dataset/10.owl"));
			//		   manager.makeLoadImportRequest(importDeclaraton);


		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}


		reasoner = new Reasoner(manager);
		((Reasoner) reasoner).loadOntology(ontology);		

//		reasoner.getKB().realize();
		System.out.println("\nClasses\n-------");
		Set<OWLClass> classList = ontology.getReferencedClasses();
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
		allExamples = new OWLIndividual[indList.size()];
		int i=0;
		for(OWLIndividual ind : indList) {
			if (!ind.isAnonymous()) {
				allExamples[i++] = ind;
				//				System.out.println(ind);
			}	        		
		}
		System.out.println("---------------------------- "+i);

		System.out.println("\nKB loaded. \n");	
		return ontology;		

	}
	/* (non-Javadoc)
	 * @see it.uniba.di.lacam.fanizzi.IKnowledgeBase#getClassMembershipResult(org.semanticweb.owl.model.OWLDescription[], org.semanticweb.owl.model.OWLIndividual[])
	 */
	@Override
	public int[][] getClassMembershipResult(OWLDescription[] testConcepts, OWLDescription[] negTestConcepts, OWLIndividual[] esempi){
		System.out.println("\nClassifying all examples ------ ");
		classifications = new int[testConcepts.length][esempi.length];
		System.out.print("Processed concepts ("+testConcepts.length+"): \n");

		for (int c=0; c<testConcepts.length; ++c) { 
			int p=0;
			int n=0;
			System.out.printf("[%d] ",c);
			for (int e=0; e<esempi.length; ++e) {			
				classifications[c][e] = 0;
				if (reasoner.hasType(esempi[e],testConcepts[c])) {
					classifications[c][e] = +1;
					p++;

				}
				else{ 
					if (!Parameters.BINARYCLASSIFICATION){
						if (reasoner.hasType(esempi[e],negTestConcepts[c])) 
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
					// verifico che l'esempio j è correlato all'esempio k rispetto alla regola i
					//System.out.println(regole[i]+" vs "+dataFactory.getOWLNegativeObjectPropertyAssertionAxiom(esempi[j], regole[i], esempi[k]).getProperty());
					correlati[i][j][k]=0;
					if(reasoner.hasObjectPropertyRelationship(esempi[j], ruoli[i], esempi[k]))
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

		Set<OWLDataProperty> propertiesSet = reasoner.getDataProperties();

		Iterator<OWLDataProperty> iterator=propertiesSet.iterator();
		List<OWLDataProperty> lista= new ArrayList<OWLDataProperty>();
		while(iterator.hasNext()){
			OWLDataProperty corrente=iterator.next();
//			System.out.println(corrente+"-"+corrente.isFunctional(ontology));
			// elimino le proprietà non funzionali

//			if(corrente.isFunctional(ontology)){
				lista.add(corrente);
//				System.out.println(corrente+"-"+corrente.isFunctional(ontology));
//			}
		}




		properties=new OWLDataProperty[lista.size()];
		if(lista.isEmpty())
			throw  new RuntimeException("Non ci sono proprietà funzionali");
		lista.toArray(properties);
		//		System.out.println("\n Verifica cardinalità del dominio....");


		domini=new OWLIndividual[properties.length][];
		dataPropertiesValue= new OWLConstant[properties.length][];
		// per ogni proprietà...
		for(int i=0;i<properties.length;i++){

			domini[i]=new OWLIndividual[0];
			Map<OWLIndividual, Set<OWLConstant>> prodottoCartesiano=creazioneProdottoCartesianoDominioXValore(properties[i]);
			Set<OWLIndividual> chiavi=prodottoCartesiano.keySet();
			//			System.out.println("Dominio proprietà: "+chiavi);
			domini[i]=chiavi.toArray(domini[i]);// ottenimento individui facenti parte del dominio
			//			System.out.println("Cardinalità: "+domini[i].length);
			//			System.out.println(properties[i]+"-"+ domini[i].length);
			dataPropertiesValue[i]= new OWLConstant[domini[i].length];

			//... e  per l'elemento del dominio corrente...

			for(int j=0;j<domini[i].length;j++){

				//... determino il valore per una proprietà funzionale

				Set<OWLConstant> valori=prodottoCartesiano.get(domini[i][j]);
				//				System.out.println(properties[i]+":    "+ i+" "+j+domini[i][j]+"----"+valori);
				OWLConstant[] valoriArray=new OWLConstant[0];
				valoriArray=valori.toArray(valoriArray);
				dataPropertiesValue[i][j]=valoriArray[0]; // la lunghezza è pari ad 1 perchè il valore possibile per 1 elemento è uno solo
				//				System.out.println(dataPropertiesValue[i][j]);

			}


		}



	}
	public   Map<OWLIndividual, Set<OWLConstant>> creazioneProdottoCartesianoDominioXValore(OWLDataProperty dataProperty){
		Map<OWLIndividual, Set<OWLConstant>> asserzioni = reasoner.getDataPropertyAssertions(dataProperty);

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
	public OWLConstant[][] getDataPropertiesValue(){
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
		// TODO Auto-generated method stub
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
	public OWLDescription getRandomConcept() {
		// sceglie casualmente uno tra i concetti presenti 
		OWLDescription newConcept = null;

		
		if (!Parameters.BINARYCLASSIFICATION){
			
			// case A:  ALC and more expressive ontologies
			do {
				newConcept = allConcepts[KnowledgeBase.generator.nextInt(allConcepts.length)];
				if (KnowledgeBase.generator.nextDouble() < 0.7) {
					OWLDescription newConceptBase = getRandomConcept();
					if (KnowledgeBase.generator.nextDouble() < 0.1) {
						
						if (KnowledgeBase.generator.nextDouble() <0) { // new role restriction
							OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
							//					OWLDescription roleRange = (OWLDescription) role.getRange;

							if (KnowledgeBase.generator.nextDouble() < 0.5)
								newConcept = dataFactory.getOWLObjectAllRestriction(role, newConceptBase);
							else
								newConcept = dataFactory.getOWLObjectSomeRestriction(role, newConceptBase);
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
					OWLDescription newConceptBase = getRandomConcept();
					if (KnowledgeBase.generator.nextDouble() < d)
						if (KnowledgeBase.generator.nextDouble() < 0.1) { // new role restriction
							OWLObjectProperty role = allRoles[KnowledgeBase.generator.nextInt(allRoles.length)];
							//					OWLDescription roleRange = (OWLDescription) role.getRange;

							if (KnowledgeBase.generator.nextDouble() < d)
								newConcept = dataFactory.getOWLObjectAllRestriction(role, newConceptBase);
							else
								newConcept = dataFactory.getOWLObjectSomeRestriction(role, newConceptBase);
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
