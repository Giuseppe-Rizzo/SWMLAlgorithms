package it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.dst;


import java.util.List;

import it.uniba.di.lacam.ml.utils.Combination;
import it.uniba.di.lacam.ml.utils.SetUtils;

/**
 * Class for representing a BBA 
 * @author Giuseppe
 * @param <S>
 * @param <T>
 */
public class MassFunction <T extends Comparable<? super T>> {
	private  List<T> frameOfDiscernement;//frame of Discernement
	private  List<List<T>> getPowerSet;
	private double[] valori;// contiene i valori   assunti dalla funzione considerando un certo 
	// esempio, un individuo da classificare ed un frame of Discernement
	
	
	public static void setFrameOfDiscernement(){
		
	}
	
	/**
	 * Constructor 
	 * @param set
	 * @param individuo
	 * @param example
	 */
	public MassFunction(List<T> set){
		frameOfDiscernement=set;
		generatePowerSet();
		valori= new double[getPowerSet.size()];
		
	}
	/**
	 * Generate the powerset of a set
	 * @return
	 */
	public void  generatePowerSet(){

		getPowerSet=Combination.findCombinations(frameOfDiscernement);
	}
	
	
	/**
	 * Return the power set of the frame of discernment
	 * @return the power set of the frame 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public  List<T>[] getFrameSubsets(){
		List[] result= new List[getPowerSet.size()];
		int i=0;
		for(List<T> elem:getPowerSet){
			
			result[i]=getPowerSet.get(i);
			i++;
		}
		return result;
	}
	
	public List<T> getFrame(){
		return frameOfDiscernement;
		
	}
	/**
	 * Assigns the mass function value for a set of hypotheses
	  * @param category
	 * @param value 
	 */
	public void setValues(List<T> hypothesis,double value){
		int pos= SetUtils.find(hypothesis,getPowerSet);
		valori[pos]=value;
		
		
	}
	
	
	/**
	 * Return the value for the set of hypotheses
	 * @param categoria, the set of hypootheses
	 * @return
	 */
	public double getValue(List<T> categoria){
		//System.out.println(valori.get(categoria));
		int pos= SetUtils.find(categoria, getPowerSet);
		return valori[pos];
	
	}
	
	/**
	 * Return one of uncertainty measure according to the DST
	 * @param measure the uncertainty measure (i.e., non specificity, confusion or dissonance measure)
	 * @return t
	 */
	public double getUncMeasure(TotalUncertainty measure){
		if (measure.compareTo(TotalUncertainty.nonspecificity)==0)
		return getNonSpecificity();
		else if (measure.compareTo(TotalUncertainty.confusion)==0)
			return getConfusionMeasure();
		else
			return getRandomnessMeasure();
			
		
	}
	/**
	 * Computes the  non-specificity measure of mass function 
	 * @return
	 */
	public double getNonSpecificity(){
		double result=0;
		for(List<T> categoria: getPowerSet){
			if(!categoria.isEmpty())
				result+=(valori[SetUtils.find(categoria, getPowerSet)]*Math.log(categoria.size())); 
		}
//		System.out.println("Non-sp: "+result);
		return result;
	}
	
	/**
	 * Compute a randomenss measure using the pignistic transformation of a BBA
	 * @return
	 */
	public double getRandomnessMeasure(){
		double result=0.0;
		for (List<T> c: getPowerSet){
			double pignisticValue=getPignisticTransformation(c);
			int posCategoria = SetUtils.find(c, getPowerSet);
			 result+= -1* (valori[posCategoria]*Math.log(pignisticValue));
		
		}
		return result;
		
		
			
	}
	/**
	 * Compute the value for a set of hypotheses adopting the pignistic transformation of a BBA (i.e. transforming a BBA in  probability function)
	 * @param cl
	 * @return
	 */
	public double getPignisticTransformation(List<T> cl){
		// it works certainly for {-1,+1} as a frame of discernement
		 double result=0.0;
		for(T element: cl){
			double pignisticValueForElement=0; // initialization
			for(List<T> categoria: getPowerSet){

				if(!categoria.isEmpty()){
					if (categoria.contains(element)){
						int posCategoria = SetUtils.find(categoria, getPowerSet);
						pignisticValueForElement += valori[posCategoria]/categoria.size();
					}
				}

			}
			result+=pignisticValueForElement;
			
		}
		return result;
	}
	
	
	public double getGlobalUncertaintyMeasure(){
		
		double nonSpecificity= this.getNonSpecificity();
		double randomness= this.getRandomnessMeasure();
		final double LAMBDA= 0.1;
		double result= ((1-LAMBDA)*nonSpecificity)+(LAMBDA*randomness);
		return result;
		
	}
	
	/**
	 * The method computes a confusion measure described in Smarandache et.al as discordant measure
	 * @return
	 */
	public double getConfusionMeasure(){
		double result=0;
		for(List<T> categoria: getPowerSet){
			if(!categoria.isEmpty())
				result-=(valori[SetUtils.find(categoria, getPowerSet)]*Math.log(this.getBeliefValue(categoria))); 
		}
//		System.out.println("Non-sp: "+result);
		return (result);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MassFunction combines(MassFunction function){
		MassFunction result= new MassFunction(frameOfDiscernement);
		double conflitto=getConflict(function);
		// per l'iesima ipotesi dell'insieme potenza
		for(List<T> elem:getPowerSet){
			int pos=SetUtils.find(elem, getPowerSet);
			// trovo gli insiemi intersecanti ipotesi1 e ipotesi2
			for(List<T>ipotesi1: getPowerSet){
				for(List<T>ipotesi2:getPowerSet){
					List<T> ipotesi12=SetUtils.intersection(ipotesi1, ipotesi2);
						// se l'intersezione � quella che mi aspetto e non � vuota
						if(!(ipotesi12.isEmpty())&&(SetUtils.equal(ipotesi12, elem))){
							SetUtils.find(ipotesi1, getPowerSet);
							SetUtils.find(ipotesi2, getPowerSet);
							double prodottoMasse=getValue(ipotesi1)*function.getValue(ipotesi2)/conflitto;	
							result.valori[pos]+=prodottoMasse;
							
						}
//						System.out.println("Valori"+pos+"----"+result.valori[pos]);
					}
					
				}
					
			}
			
		return result;
			
		}
		
		
		
	/**
	 * Combines n BBAs according to one of the combination rules (Desmpster, Dubois-Prade, Mixing)
	 * @param type, the rules to be applied
	 * @param functions
	 * @return
	 */
	public MassFunction combine(RuleType type, MassFunction...functions){
		if (type.compareTo(RuleType.Dempster)==0)
		return combineDempster(functions);
		else
			if (type.compareTo(RuleType.DuboisPrade)==0)
				return combineDuboisPrade(functions);
				else
					return combineMixing(functions);
	}
	
	
	/**
	 * combine n function according to the Dempster rule
	 * @param function
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public MassFunction combineDempster(MassFunction... function){
		if(function.length==0)
			throw new RuntimeException("You can combine 2 or more BBAs");
		MassFunction result=this.combines(function[0]);
		// l'operazione sfrutta l'associativit� della regola di Dempster
		for(int i=1;i<function.length;i++){
			// applico una regola non normalizzata fino alla n-1esima funzione
			
			
			result= result.combines(function[i]);
			
		}
		// faccio la normalizzazionesulla base del conflitto tra la combinata delle prime n-1 e l'ultima
		
	
		
		return result;
		
	}
	
	/**
	 * Combines the BBAs according to the mixing rule
	 * @param function
	 * @return
	 */
	public MassFunction<T> combineMixing (MassFunction... function){
		
		MassFunction<T> result= new MassFunction(frameOfDiscernement);
		double[] values= new double[result.getFrameSubsets().length];
		for (MassFunction f: function){
			for (int i=0; i<values.length;i++){
				values[i]+=f.valori[i]; 
			}

		}
	
		for (int i=0; i<values.length;i++){
			values[i]/=function.length; // compute the average with weight 2=1 
		}

		result.valori=values;
		
		return result;
		
	}
	
	/**
	 * Dubois-Prade Combination rule for two BBAs
	 * @param function
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MassFunction<T> combineDuboisPrade (MassFunction function){
		
		MassFunction<T> result= new MassFunction(frameOfDiscernement);
		
		// per l'iesima ipotesi dell'insieme potenza
		for(List<T> elem:getPowerSet){
			int pos=SetUtils.find(elem, getPowerSet);
			// trovo gli insiemi intersecanti ipotesi1 e ipotesi2
			for(List<T>ipotesi1: getPowerSet){
				for(List<T>ipotesi2:getPowerSet){
					List<T> ipotesi12=SetUtils.union(ipotesi1, ipotesi2);
						// se l'unione � quella che mi aspetto e non � vuota!ipotesi12.isEmpty()&&
						if((SetUtils.equal(ipotesi12, elem))){
							SetUtils.find(ipotesi1, getPowerSet);
							SetUtils.find(ipotesi2, getPowerSet);
							double prodottoMasse=getValue(ipotesi1)*function.getValue(ipotesi2);	
							result.valori[pos]+=prodottoMasse;
							
						}
						
					}
					
				}
//				result.valori[pos]=result.valori[pos];
				
			}
			
		return result;
		
	}
	
	/**
	 * dubois-Prade combination rule for 2 or more functions
	 * @param functions
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public MassFunction combineDuboisPrade(MassFunction... function){
		if(function.length==0)
			throw new RuntimeException("Please, provide a function");
		MassFunction result=this.combineDuboisPrade(function[0]);
		// l'operazione sfrutta l'associativit� della regola di Dempster
		for(int i=1;i<function.length;i++)
			result= result.combineDuboisPrade(function[i]);	
		return result;
		
	}
	
	
	
	/**
	 * compute the conflict of  a mass function
	 * @param function
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public double getConflict(MassFunction function){
		double massaVuota=0;
		for(List<T> ipotesi1:getPowerSet){
//			System.out.println("***************");
//			System.out.println("Ipotesi 1:"+ipotesi1);
			for(List<T> ipotesi2:getPowerSet){
//				System.out.println("Ipotesi 2:"+ipotesi2);
				List<T>intersezione=SetUtils.intersection(ipotesi1,ipotesi2);
				if(!intersezione.isEmpty()){
//					System.out.println("Intersezione vuota");
					massaVuota+= (getValue(ipotesi1)*function.getValue(ipotesi2));
//					System.out.println(massaVuota);
				}
				
				
			}
	//Peso Conflitto:0.8778599068789572		
			
		}
	
		return (massaVuota);
	}
	
	
	/**
	 * Compute the  belief function value for a set of hypotheses
	 * @param ipotesi
	 * @return
	 */
	public double getBeliefValue(List<T> ipotesi){
		double bel_ipotesi=0;
		for(List<T> elem:getPowerSet){
			// per ogni sottoinsieme non vuotodi ipotesi
			if(!elem.isEmpty()&& ipotesi.containsAll(elem)){
				// somma le masse
//				System.out.println("m("+elem+")="+bel_ipotesi);
				bel_ipotesi+=getValue(elem);
				
			}
		}
//			System.out.println("Belief:"+bel_ipotesi);
		
		return bel_ipotesi;
	}
	/**
	 * Compute the   plausibility function value for a set of hypotheses
	 * @param ipotesi
	 * @return
	 */
	public double getPlausibilityFunction(List<T> ipotesi){
		// applicando la definizione abbiamo
		double pl_ipotesi=0;
		for(List<T> elem:getPowerSet){
			
			if(!(SetUtils.intersection(ipotesi,elem)).isEmpty())
				// somma le masse
				pl_ipotesi+=getValue(elem);
//			System.out.println(pl_ipotesi);
		}
			
//		System.out.println("Plausibility"+pl_ipotesi);
		return pl_ipotesi;
		
		
	}
	/**
	 * compute the  confirmation function for a set of hypotheses
	 * @param ipotesi
	 * @return
	 */
	public double getConfirmationFunction(List<T>ipotesi){
		return (getBeliefValue(ipotesi)+getPlausibilityFunction(ipotesi)-1);
		
	}
	
	public String toString(){
		String res="";
		for(int i=0;i<getPowerSet.size();i++){
			String string = ""+getPowerSet.get(i)+valori[i];
			res+= string;
		}
		return res;
	}

	
	
	
	
	
}

