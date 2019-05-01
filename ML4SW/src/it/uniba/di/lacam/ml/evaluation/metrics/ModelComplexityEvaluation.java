package it.uniba.di.lacam.ml.evaluation.metrics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import it.uniba.di.lacam.ml.evaluation.Evaluation;
import it.uniba.di.lacam.ml.utils.MathUtils;
/**
 * Estimate the complexity of the models and 
 * @author Giuseppe
 *
 */
public class ModelComplexityEvaluation extends AbstractMetrics {
	
	private PrintStream stream;
	
	private double[][] values;
	
	public ModelComplexityEvaluation (int nOfqueries, int nOfFolds){
		
		super();
		
		values= new double[nOfqueries][nOfFolds];
		
		
		try {
			stream= new PrintStream(new FileOutputStream("ModelComplexityLog.txt"), true);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} // append
	}

	/**
	 * Store the value
	 * @param currentQuery, the learning problme
	 * @param currentFold, the current fold
	 * @param value, the complexity value
	 */
	public void setValues(int currentQuery, int currentFold, double value){
		
		values[currentQuery][currentFold]= value;
		
	}
	
	/**
	 * Average the complexity values of the induced models
	 */
	public void computeModelComplexityPerformance(){
		System.out.println();
		System.out.println();
		System.out.println("MODEL COMPLEXITY EVALUATION");
		stream.println("MODEL COMPLEXITY EVALUATION");
		double[] results= new double[values.length];
		System.out.printf("%s       %s \n","Query","Number Of Nodes");
		stream.printf("%s       %s \n","Query","Number Of Nodes");
		for (int i=0; i<values.length;i++){
			
			results[i]=MathUtils.avg(values[i]);
			System.out.printf("%d       %10.3f \n ",i,results[i]);
			stream.printf("%d       %10.3f \n",i,results[i]);
			
		}
		System.out.println("--------------------------------------");
		stream.println("--------------------------------------");
		double avg= MathUtils.avg(results);
		System.out.printf("%s       %10.3f \n","Avg",avg);
		stream.printf("%s       %10.3f \n","Avg",avg);
		
		stream.close();
		
		
	}
	
}
