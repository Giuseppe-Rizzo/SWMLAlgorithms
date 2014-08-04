package evaluation;

import java.util.HashSet;

import knowledgeBasesHandler.KnowledgeBase;

import org.mindswap.pellet.owlapi.Reasoner;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;

public class ConceptGenerator {
	private KnowledgeBase kb;
	private  Reasoner reasoner;
	private  OWLDataFactory dataFactory;
	private  OWLIndividual[] allExamples;
	
	public ConceptGenerator(KnowledgeBase k){
		
		kb=k;
		reasoner= kb.getReasoner();
		dataFactory= kb.getDataFactory();
		allExamples= kb.getIndividuals();
	}
	
	
	public  OWLDescription[] generateQueryConcepts(int numConceptsToGenerate){
		System.out.println("\nQUERY GENERATION");
		OWLDescription[] queryConcepts = new OWLDescription[numConceptsToGenerate];
        final int minOfSubConcepts = 2;
        final int maxOfSubConcepts = 8;
        int numOfSubConcepts = 0;
        int i, j;

        int nExs = allExamples.length;
        
        OWLDescription nextConcept;
        OWLDescription complPartialConcept; 
        
        // cycle to build numConceptsToGenerate new query concepts
        for (i=0; i<numConceptsToGenerate; i++) {           
        	OWLDescription partialConcept; 
            numOfSubConcepts = minOfSubConcepts + KnowledgeBase.generator.nextInt(maxOfSubConcepts-minOfSubConcepts);
            int numPosInst, numNegInst;
            // build a single new query OWLDescription adding conjuncts or disjuncts
            do {
//            	take the first subConcept for builiding the query OWLDescription
                partialConcept = kb.getRandomConcept();
                
	            for (j=1; j < numOfSubConcepts; j++) {
	               
	            	HashSet<OWLDescription> newConcepts = new HashSet<OWLDescription>();	            	
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
                
                numPosInst = reasoner.getIndividuals(partialConcept,false).size(); 
                numNegInst = reasoner.getIndividuals(complPartialConcept,false).size();
                
                System.out.printf("%s\n",partialConcept);
                System.out.printf("pos:%d (%3.1f)\t\t neg:%d (%3.1f)\t\t und:%d (%3.1f)\n",
                		numPosInst,numPosInst*100.0/nExs,
                		numNegInst,numNegInst*100.0/nExs,
                		(nExs-numNegInst-numPosInst), (nExs-numNegInst-numPosInst)*100.0/nExs);
            	
//            } while (!reasoner.isSatisfiable(partialConcept) || !reasoner.isSatisfiable(complPartialConcept));
//            } while (numPosInst > .8*nExs);	
            } while ((numPosInst*numNegInst == 0));//||((numPosInst<10)&&(numNegInst<10)));
//            } while (numPosInst+numNegInst == 0 || numPosInst+numNegInst == nExs);
            //  || numPosInst+numNegInst == nExs
            //add the newly built OWLDescription to the list of all required query concepts
            queryConcepts[i] = partialConcept;
            System.out.printf("Query %d found\n\n", i);
//            System.out.printf("\n Query %3d.  pos:%6d  neg:%6d \n", i, numPosInst, numNegInst);
        }
        System.out.println();
        return queryConcepts;
	}

	
	
	
}
