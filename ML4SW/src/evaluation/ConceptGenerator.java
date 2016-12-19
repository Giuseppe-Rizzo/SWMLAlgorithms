package evaluation;

import java.util.HashSet;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;



public class ConceptGenerator {
	protected KnowledgeBase kb;
	protected Reasoner reasoner;
	protected  OWLDataFactory dataFactory;
	protected  OWLIndividual[] allExamples;
	
	public ConceptGenerator(KnowledgeBase k){
		
		kb=k;
		reasoner= kb.getReasoner();
		dataFactory= kb.getDataFactory();
		allExamples= kb.getIndividuals();
	}
	
	
	public  OWLClassExpression[] generateQueryConcepts(int numConceptsToGenerate){
		System.out.println("\nQUERY GENERATION");
		OWLClassExpression[] queryConcepts = new OWLClassExpression[numConceptsToGenerate];
        final int minOfSubConcepts = 2;
        final int maxOfSubConcepts = 8;
        int numOfSubConcepts = 0;
        int i, j;

        int nExs = allExamples.length;
        
        OWLClassExpression nextConcept;
        OWLClassExpression complPartialConcept; 
        
        // cycle to build numConceptsToGenerate new query concepts
        for (i=0; i<numConceptsToGenerate; i++) {           
        	OWLClassExpression partialConcept; 
            numOfSubConcepts = minOfSubConcepts + KnowledgeBase.generator.nextInt(maxOfSubConcepts-minOfSubConcepts);
            int numPosInst, numNegInst;
            // build a single new query OWLClassExpression adding conjuncts or disjuncts
            do {
//            	take the first subConcept for builiding the query OWLClassExpression
                partialConcept = kb.getRandomConcept();
                
	            for (j=1; j < numOfSubConcepts; j++) {
	               
	            	HashSet<OWLClassExpression> newConcepts = new HashSet<OWLClassExpression>();	            	
	                newConcepts.add(partialConcept);
	                nextConcept = kb.getRandomConcept();
	                newConcepts.add(nextConcept);	                
	                
	                if (KnowledgeBase.generator.nextInt(4) == 0) 
	                	partialConcept = dataFactory.getOWLObjectIntersectionOf(newConcepts);
	                else
	                    partialConcept = dataFactory.getOWLObjectUnionOf(newConcepts);
	            } // for j
//            	System.out.println();
            	complPartialConcept = dataFactory.getOWLObjectComplementOf(partialConcept);
                
                numPosInst = reasoner.getInstances(partialConcept,false).getFlattened().size(); 
                numNegInst = reasoner.getInstances(complPartialConcept,false).getFlattened().size();
                
                System.out.printf("%s\n",partialConcept);
                System.out.printf("pos:%d (%3.1f)\t\t neg:%d (%3.1f)\t\t und:%d (%3.1f)\n",
                		numPosInst,numPosInst*100.0/nExs,
                		numNegInst,numNegInst*100.0/nExs,
                		(nExs-numNegInst-numPosInst), (nExs-numNegInst-numPosInst)*100.0/nExs);
            	
//            } while (!reasoner.isSatisfiable(partialConcept) || !reasoner.isSatisfiable(complPartialConcept));
//            } while (numPosInst > .8*nExs);	
        } while ((numPosInst*numNegInst == 0)||((numPosInst<10)||(numNegInst<10)));
// } while (numPosInst+numNegInst == 0 || numPosInst+numNegInst == nExs);
            //  || numPosInst+numNegInst == nExs
            //add the newly built OWLClassExpression to the list of all required query concepts
            queryConcepts[i] = partialConcept;
            System.out.printf("Query %d found\n\n", i);
//            System.out.printf("\n Query %3d.  pos:%6d  neg:%6d \n", i, numPosInst, numNegInst);
        }
        System.out.println();
        return queryConcepts;
	}

	
	
	
}
