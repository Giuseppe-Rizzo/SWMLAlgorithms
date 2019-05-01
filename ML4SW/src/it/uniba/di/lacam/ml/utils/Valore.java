package it.uniba.di.lacam.ml.utils;
/**
 * A generic class to wrap a value  (it can be removed in the next release)
 * @author Giuseppe
 *
 * @param <T>
 */
public class Valore<T> {
	
	private T valore;
	boolean prior;
	public Valore(T valore,boolean prior){
		
		this.valore=valore;
		this.prior=prior;
	}
	
	public boolean isObtainedByPriors(){
		
		return prior;
		
	}
	
	public T getValue(){
		return valore;
		
	}
	public boolean isUndefined(){
		return valore==null;
	}
	
	

}
