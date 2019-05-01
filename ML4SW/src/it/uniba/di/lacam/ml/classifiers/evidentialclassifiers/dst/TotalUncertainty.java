package it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.dst;
/**
 * Enum type for the total uncertainty measure
 * @author Giuseppe
 *
 */
public enum TotalUncertainty {

	nonspecificity,
	dissonance,
	confusion;
	
	
	public String toString(){
		if (this.compareTo(confusion)==0)
			return "confusion";
		else if (this.compareTo(dissonance)==0)
			return "dissonance";
		else
			 return "confusion";
		
	}
	
	public TotalUncertainty  getMeasure(String name){
		
		if (name.compareTo("confusion")==0)
			return confusion;
		else if (name.compareTo("dissonance")==0)
			return TotalUncertainty.dissonance;
		else 
			return nonspecificity;
//		else if (name.compareTo("it.uniba.di.lacam.ml.classifiers.TerminologicalDecisionTreeInducer")==0)
//			return TerminologicalDecisionTree;
		
	} 
}
