package it.uniba.di.lacam.ml.utils.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import it.uniba.di.lacam.ml.evaluation.Parameters;
import it.uniba.di.lacam.ml.evaluation.PruningType;

/**
 * Utility  class adopted for writing the configuration file from hard-coded parameters
 * (it can be deleted)
 * @author Giuseppe
 *
 */
public class PropertiesWriter {
	
	
	
	
	public PropertiesWriter(){
		
	}
	
	
	

	
	public void saveParamChanges() {
			    try {
			       Properties props = new Properties();
			       props.setProperty("algorithm", Parameters.algorithm.toString());
			       props.setProperty("samplingrate", ""+Parameters.samplingrate);
			       props.setProperty("originalImbalance", ""+Parameters.originalImbalance);
			       props.setProperty("NFOLDS", ""+Parameters.NFOLDS);
			       props.setProperty("ExperimentalDesign", Parameters.design.toString());
			       props.setProperty("Task", Parameters.task.toString());
			       props.setProperty("NUMGENCONCEPTS", ""+Parameters.NUMGENCONCEPTS);
			       props.setProperty("beam", ""+Parameters.beam);
			       props.setProperty("NUMTREES", ""+Parameters.NTREES);
			       props.setProperty("SEED", ""+Parameters.SEED);
			       props.setProperty("PURITY_THRESHOLD", ""+Parameters.PURITY_THRESHOLD);
			       props.setProperty("pruning", PruningType.NOPRUNING.toString());
			       props.setProperty("missingValueTreatmentForTDT", ""+Parameters.missingValueTreatmentForTDT);
			       props.setProperty("BINARYCLASSIFICATION", ""+Parameters.BINARYCLASSIFICATION);
			       props.setProperty("M", ""+Parameters.M);
			       props.setProperty("nonspecificityControl", ""+Parameters.nonspecificityControl);
			       props.setProperty("urlOwlFile", Parameters.urlOwlFile);
			       
			        File f = new File("experiments.properties");
			        OutputStream out = new FileOutputStream( f );
			        props.store(out, "Experimental Settings");
			    }
			    catch (Exception e ) {
			        e.printStackTrace();
			    }
			}
		

}
