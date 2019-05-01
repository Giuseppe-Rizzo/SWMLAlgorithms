package it.uniba.di.lacam.ml.classifiers.ensemble;

import java.util.ArrayList;
import java.util.Iterator;

import it.uniba.di.lacam.ml.classifiers.AbstractModel;

/**
 * A generic class for bagging ensemble
 * @author Giuseppe Rizzo
 *
 * @param <T>
 */
public class Ensemble<T> extends AbstractModel implements Iterable{
	
	protected ArrayList<T> classifiers;
	// counter for ensemble class-memebership
	private int countPos;
	private int countNeg;
	private int countUkn;
	

	public Ensemble (){
		classifiers= new ArrayList<T>();
	}
	
	
	public void addClassifier(T classifier){
		classifiers.add(classifier);
		
		
	}
	
	public void removeClassifier(T classifier){
		
		classifiers.add(classifier);
		
	}
	
	/**
	 * Return the i-th classifier
	 * @param i, the index for the classifier
	 * @return the classifier
	 */
	public T getClassifier(int i){
		
		 return classifiers.get(i);
		
		
	}
	/**
	 * Return the ensemble size
	 * @return the ensemble size
	 */
	public int getSize(){
		
		return classifiers.size();
	}
	
	/**
	 * Increase the counter for the label (+1,-1,0) given as an input
	 * @param classValue, the label 
	 */
	public void addCounter(int classValue){
		
		if(classValue==1)
			countPos++;
		else if(classValue== -1)
			countNeg++;
		else
			countUkn++;
	}
	
	public int getMajorityClass(){
		int results=0;
		if (countPos>=countNeg)
			if(countPos>=countUkn)
					results=+1;
			else
				results= 0;
		else
			if(countNeg>=countUkn)
				results=-1;
			else
				results=0;
		countPos=0;
		countNeg=0;
		countUkn=0;
		return results;
			
	}
	
	public String toString(){
		String result= "Ensemble \n";
		for(int i=0; i< this.getSize();i++){
			result+=classifiers.get(i).toString();
			result+="\n";
		}
		return result;
	}
	
	public String  printVotes(){
		
		return "Count Vote: "+ countPos+ "-"+ countNeg+ "-"+ countUkn;
		
	}


	@Override
	public double getComplexityMeasure() {

		return 0;
	}


	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		
		return classifiers.iterator();
	}
	
	

}
