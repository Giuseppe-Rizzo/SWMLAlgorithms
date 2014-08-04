package classifiers.evidentialAlgorithms.DempsterShafer;


import java.util.List;
import utils.Combination;
import utils.SetUtils;

/**
 * Classe che Rappresenta una bba
 * @author Giuseppe
 *
 * @param <S>
 * @param <T>
 */
public class MassFunction <T extends Comparable<? super T>> {
	private  List<T> frameOfDiscernement;//frame of Discernement
	private  List<List<T>> insiemePotenza;
	private double[] valori;// contiene i valori   assunti dalla funzione considerando un certo 
	// esempio, un individuo da classificare ed un frame of Discernement
	
	
	public static void setFrameOfDiscernement(){
		
	}
	
	/**
	 * Istanzia la funzione massa di probabilità per un certo esempio ed un certo individuo
	 * @param set
	 * @param individuo
	 * @param example
	 */
	public MassFunction(List<T> set){
		frameOfDiscernement=set;
		generaInsiemePotenza();
		valori= new double[insiemePotenza.size()];
		
	}
	/**
	 * Genera l'insieme potenza di un certo insieme
	 * @return
	 */
	public void  generaInsiemePotenza(){

		insiemePotenza=Combination.findCombinations(frameOfDiscernement);
	}
	
	
	/**
	 * Restituisce l'insieme potenza ottenuto a partire dal frame of discernement
	 * @return insieme potenza
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public  List<T>[] getSottoinsiemiFrame(){
		List[] result= new List[insiemePotenza.size()];
		int i=0;
		for(List<T> elem:insiemePotenza){
			
			result[i]=insiemePotenza.get(i);
			i++;
		}
		return result;
	}
	
	public List<T> getFrame(){
		return frameOfDiscernement;
		
	}
	/**
	 * Consente di attribuire il valore della bba per la categoria di appartenenza
	 * @param dissimilarity
	 * @param distanza 
	 */
	public void setValues(List<T> categoria,double value){
		int pos= SetUtils.cerca(categoria,insiemePotenza);
		valori[pos]=value;
		
		
	}
	
	
	/**
	 * Restituisce il valore assunto dalla funzione bba per una certa categoria
	 * @param categoria
	 * @return il valore assunto dalla funzione bba oppure NaN se la funzione non è definita
	 */
	public double getValue(List<T> categoria){
		//System.out.println(valori.get(categoria));
		int pos= SetUtils.cerca(categoria, insiemePotenza);
		return valori[pos];
	
	}
	
	
	public double getNonSpecificity(){
		double result=0;
		for(List<T> categoria: insiemePotenza){
			if(!categoria.isEmpty())
				result+=(valori[SetUtils.cerca(categoria, insiemePotenza)]*Math.log(categoria.size())); 
		}
//		System.out.println("Non-sp: "+result);
		return result;
	}
	
	
	public double getRandomnessMeasure(){
		double result=0.0;
		for (List<T> c: insiemePotenza){
			double pignisticValue=getPignisticTransformation(c);
			int posCategoria = SetUtils.cerca(c, insiemePotenza);
			 result+= -1* (valori[posCategoria]*Math.log(pignisticValue));
		
		}
		return result;
		
		
			
	}
	
	public double getPignisticTransformation(List<T> cl){
		// it works certainly for {-1,+1} as a frame of discernement
		 double result=0.0;
		for(T element: cl){
			double pignisticValueForElement=0; // initialization
			for(List<T> categoria: insiemePotenza){

				if(!categoria.isEmpty()){
					if (categoria.contains(element)){
						int posCategoria = SetUtils.cerca(categoria, insiemePotenza);
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
		for(List<T> categoria: insiemePotenza){
			if(!categoria.isEmpty())
				result-=(valori[SetUtils.cerca(categoria, insiemePotenza)]*Math.log(this.calcolaBeliefFunction(categoria))); 
		}
//		System.out.println("Non-sp: "+result);
		return result;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MassFunction applicaCombinazione(MassFunction function){
		MassFunction result= new MassFunction(frameOfDiscernement);
		double conflitto=calcolaPesoConflitto(function);
		// per l'iesima ipotesi dell'insieme potenza
		for(List<T> elem:insiemePotenza){
			int pos=SetUtils.cerca(elem, insiemePotenza);
			// trovo gli insiemi intersecanti ipotesi1 e ipotesi2
			for(List<T>ipotesi1: insiemePotenza){
				for(List<T>ipotesi2:insiemePotenza){
					List<T> ipotesi12=SetUtils.interseca(ipotesi1, ipotesi2);
						// se l'intersezione è quella che mi aspetto e non è vuota
						if(!(ipotesi12.isEmpty())&&(SetUtils.uguali(ipotesi12, elem))){
							SetUtils.cerca(ipotesi1, insiemePotenza);
							SetUtils.cerca(ipotesi2, insiemePotenza);
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
	 * applica la regola di combinazione di dempster a n funzioni(sfruttando l'associatività dell'operatore)
	 * @param function
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public MassFunction applicaCombinazione(MassFunction... function){
		if(function.length==0)
			throw new RuntimeException("Occorre almeno passare una funzione di massa");
		MassFunction result=this.applicaCombinazione(function[0]);
		// l'operazione sfrutta l'associatività della regola di Dempster
		for(int i=1;i<function.length;i++){
			// applico una regola non normalizzata fino alla n-1esima funzione
			
			
			result= result.applicaCombinazione(function[i]);
			
		}
		// faccio la normalizzazionesulla base del conflitto tra la combinata delle prime n-1 e l'ultima
		
	
		
		return result;
		
	}
	
	/**
	 * Implementa la regola di combinazione di Dubois-Prade
	 * @param function
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MassFunction<T> applicaCombinazioneDuboisPrade (MassFunction function){
		
		MassFunction<T> result= new MassFunction(frameOfDiscernement);
		
		// per l'iesima ipotesi dell'insieme potenza
		for(List<T> elem:insiemePotenza){
			int pos=SetUtils.cerca(elem, insiemePotenza);
			// trovo gli insiemi intersecanti ipotesi1 e ipotesi2
			for(List<T>ipotesi1: insiemePotenza){
				for(List<T>ipotesi2:insiemePotenza){
					List<T> ipotesi12=SetUtils.unisci(ipotesi1, ipotesi2);
						// se l'unione è quella che mi aspetto e non è vuota!ipotesi12.isEmpty()&&
						if((SetUtils.uguali(ipotesi12, elem))){
							SetUtils.cerca(ipotesi1, insiemePotenza);
							SetUtils.cerca(ipotesi2, insiemePotenza);
							double prodottoMasse=getValue(ipotesi1)*function.getValue(ipotesi2);	
							result.valori[pos]+=prodottoMasse;
							
						}
						
					}
					
				}
//				result.valori[pos]=result.valori[pos];
				
			}
			
		return result;
		
	}
	
	@SuppressWarnings("rawtypes")
	public MassFunction applicaCombinazioneDuboisPrade(MassFunction... function){
		if(function.length==0)
			throw new RuntimeException("Occorre almeno passare una funzione di massa");
		MassFunction result=this.applicaCombinazioneDuboisPrade(function[0]);
		// l'operazione sfrutta l'associatività della regola di Dempster
		for(int i=1;i<function.length;i++)
			result= result.applicaCombinazioneDuboisPrade(function[i]);
			
		
		
		
	
		
		return result;
		
	}
	
	
	
	/**
	 * Restituisce il peso del conflitto associato a due ipotesi
	 * @param function
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public double calcolaPesoConflitto(MassFunction function){
		double massaVuota=0;
		for(List<T> ipotesi1:insiemePotenza){
//			System.out.println("***************");
//			System.out.println("Ipotesi 1:"+ipotesi1);
			for(List<T> ipotesi2:insiemePotenza){
//				System.out.println("Ipotesi 2:"+ipotesi2);
				List<T>intersezione=SetUtils.interseca(ipotesi1,ipotesi2);
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
	 * Effettua il calcolo della  belief function
	 * @param ipotesi
	 * @return
	 */
	public double calcolaBeliefFunction(List<T> ipotesi){
		double bel_ipotesi=0;
		for(List<T> elem:insiemePotenza){
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
	 * Determina la plausibility function
	 * @param ipotesi
	 * @return
	 */
	public double calcolaPlausibilityFunction(List<T> ipotesi){
		// applicando la definizione abbiamo
		double pl_ipotesi=0;
		for(List<T> elem:insiemePotenza){
			
			if(!(SetUtils.interseca(ipotesi,elem)).isEmpty())
				// somma le masse
				pl_ipotesi+=getValue(elem);
//			System.out.println(pl_ipotesi);
		}
			
//		System.out.println("Plausibility"+pl_ipotesi);
		return pl_ipotesi;
		
		
	}
	/**
	 * calcola il valore della confirmation function
	 * @param ipotesi
	 * @return
	 */
	public double calcolaConfirmationFunction(List<T>ipotesi){
		return (calcolaBeliefFunction(ipotesi)+calcolaPlausibilityFunction(ipotesi)-1);
		
	}
	
	public String toString(){
		String res="";
		for(int i=0;i<insiemePotenza.size();i++){
			String string = ""+insiemePotenza.get(i)+valori[i];
			res+= string;
		}
		return res;
	}

	
	
	
	
	
}

