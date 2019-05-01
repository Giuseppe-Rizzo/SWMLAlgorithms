package it.uniba.di.lacam.ml.utils;

import java.util.ArrayList;
import java.util.List;

public class SetUtils{

	
	
	 
/**
 * check of an element is in a set
 */	
	public static <T> boolean appartiene(T elem, List<T> lista){
		for(Object corrente:lista){
			if (elem.equals(corrente)){
				
				return true;
			}
			
		}
		return false;
		
	}

	/**
	 * Returns the intersections set
	 * @param <T>
	 * @param lista1
	 * @param lista2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  static <T> List<T> intersection(List<T> lista1, List<T> lista2){
		// controllo di quale delle due liste è piu lunga
		if(lista1.size()<=lista2.size()){
			List<T> intersezione= new ArrayList<T>();
			// se ogni elemento di lista2 è contenuto in lista 1 allora l'intersezione è lista 2
			for(Object elem: lista2){
				if(appartiene((T)elem, lista1)){
					intersezione.add((T)elem);
				}
					
				
			}
			
			return intersezione;
		}
		else
			return intersection(lista2,lista1);
	}
	/**
	 * returns the union of two sets
	 * @param <T>
	 * @param lista1
	 * @param lista2
	 * @return
	 */
	public static <T> List<T> union(List<T> lista1,List<T> lista2){
		
		List<T> result= intersection(lista1, lista2);// prendo gli elementi in comune
		// aggiungo gli elementi di lista1 che non sono presenti in intersezione
		for(T elem:lista1){
			if(!appartiene(elem,lista1)){
				result.add(elem);
			}
		}
		// aggiungo gli elementi di lista2 che non sono presenti in intersezione	
		for(T elem:lista2){
			if(!appartiene(elem,lista2)){
				result.add(elem);
			}
		}
		return result;
		
	}
	/**
	 * check equality 
	 * @param <T>
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static <T> boolean equal(List<T>l1, List<T> l2){
		// due insiemi sono uguali se uno è incluso nell'altro
		if (l1.containsAll(l2)&&l2.containsAll(l1))
			return true;
		
		return false;
	}
	
	/**
	 * find a set into the power set
	 * @param categoria, the set
	 * @param insiemePotenza,the power set
	 * @return
	 */
	public static <T> int find(List<T> categoria, List<List<T>> insiemePotenza){
		int pos=0;
		for(List<T> elem:insiemePotenza){
			
			if(equal(categoria,elem))
				return pos;
			else
				pos++;
		}
		return -1; // nel caso in cui non lo trova
		
		
	}

	/**
	 * find a set into the power set
	 * @param categoria, the set
	 * @param insiemePotenza,the power set
	 * @return
	 */
	public static <T> int cerca(List<T> lista, List<T>[] insiemePotenza){
		int pos=0;
		for(List<T> elem:insiemePotenza){
			
			if(equal(lista,elem))
				return pos;
			else
				pos++;
		}
		return -1; // nel caso in cui non lo trova
		
		
	}
	/**
	 * 
	 * @param lista, the singleton
	 * @return
	 */
	public static <T> T extractValueFromSingleton(List<T> lista){
		
		if (lista.size()!=1)
			throw new RuntimeException("la cardinalità e diversa da 1");
		return (lista.get(0));
		
		
	}
	public static <T> void insertValueinSingleton(List<T> lista,T elem){
		
		if (lista.size()!=0)
			throw new RuntimeException("Lista non vuota");
		lista.add(elem);
		
		
	}
	@SuppressWarnings("rawtypes")
	public static <T> List[] getSottoinsiemi(List<T>[] insiemePotenza, int cardinalita){
		if(cardinalita>Math.log10(insiemePotenza.length)/Math.log10(2))
			throw new RuntimeException("La cardinalità è maggiore di"+(Math.log10(insiemePotenza.length)/Math.log10(2)));
		
		List<List<T>> sottoinsiemi= new ArrayList<List<T>>();
		for(List<T> elem:insiemePotenza){
			if(elem.size()==cardinalita)
				sottoinsiemi.add(elem);
			
		}
		//converto tutto in un array
		List[] result= new List[sottoinsiemi.size()];
		for(int i=0;i<result.length;i++){
			result[i]=sottoinsiemi.get(i);
			
		}
		return result;
	} 
	

}
