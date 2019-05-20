


package it.uniba.di.lacam.ml.utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Generate the power set of a collection.
 */

public class Combination extends Object
{


    private Combination()
    {
        super();
    }


    
     
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    /**
     * Generate the combinations of the elements of a collection
     * @param elements
     * @return
     */
	public static <T extends Comparable<? super T>> List<List<T>> findCombinations(Collection elements)
    {
        List<List<T>> result = new ArrayList<List<T>>();
        
        for (int i = 0; i <= elements.size(); i++)
            result.addAll(findCombinations(elements, i));

        return result;
    }
    
    
    
    /**
     * Generate the combination of a collection of objects
     * @param elements,  the set
     * @param n
     * @return
     */
    public static <T extends Comparable<? super T>> List<List<T>> findCombinations(Collection<T> elements, int n)
    {
        List<List<T>> result = new ArrayList<List<T>>();
        
        if (n == 0)
        {
            result.add(new ArrayList<T>());
            
            return result;
        }
        
        List<List<T>> combinations = findCombinations(elements, n - 1);
        for (List<T> combination: combinations)
        {
            for (T element: elements)
            {
                if (combination.contains(element))
                {
                    continue;
                }
                
                List<T> list = new ArrayList<T>();

                list.addAll(combination);
                
                if (list.contains(element))
                    continue;
                
                list.add(element);
                Collections.sort(list);
                
                if (result.contains(list))
                    continue;
                
                result.add(list);
            }
        }
        
        return result;
    }


    
}


