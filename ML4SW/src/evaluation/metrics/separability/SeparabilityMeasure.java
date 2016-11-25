package evaluation.metrics.separability;

import knowledgeBasesHandler.KnowledgeBase;

import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntologyManager;

import evaluation.Parameters;

public class SeparabilityMeasure {
	private KnowledgeBase kb;
	private Reasoner reasoner;
	private OWLOntologyManager manager ;
	private OWLClass[] classes;
	private  OWLIndividual[] individuals;


	public SeparabilityMeasure(KnowledgeBase kb){
		this.kb=kb;	
		reasoner = kb.getReasoner();
		manager = reasoner.getManager();
		classes = kb.getClasses();
		individuals = kb.getIndividuals();
		System.out.println("Instances: "+individuals.length);

		FeaturesDrivenDistance.preLoadPi(Parameters.urlOwlFile, reasoner, manager, classes, individuals);

	} 


	public double computeDirectClassSeparabilityMeasure( int nOfConcepts){

		int[] nthConceptResults= kb.getClassMembershipResult()[nOfConcepts]; // classificationresult for nth query
		System.out.println("Concept Results: "+nthConceptResults.length);
		System.out.println("Length: "+FeaturesDrivenDistance.pi.length);

		double positive=0; 
		double negative=0;
		for (int i=0;i<individuals.length;i++){
			for(int j=0; j<individuals.length;j++){
				if ((nthConceptResults[i]+nthConceptResults[j])==2)
					positive+=FeaturesDrivenDistance.sqrDistance(i, j);
				else if ((nthConceptResults[i]+nthConceptResults[j])==-2)
					negative+= FeaturesDrivenDistance.sqrDistance(i, j);
			}
		}
		//scattermatrix positive
		double within=positive+ negative;

      double between=0;
		for (int i=0;i<individuals.length;i++){
			if  (nthConceptResults[i]==+1){
				for(int j=0; j<individuals.length;j++){
					if ((nthConceptResults[i]+nthConceptResults[j])==0)
						between+=FeaturesDrivenDistance.sqrDistance(i, j);
				}
			}
		}
 System.out.println("Within Positive: "+positive);
 System.out.println("Within Negative: "+negative);
 System.out.println("Between: "+between);
   return ( between - within);




	}

}
