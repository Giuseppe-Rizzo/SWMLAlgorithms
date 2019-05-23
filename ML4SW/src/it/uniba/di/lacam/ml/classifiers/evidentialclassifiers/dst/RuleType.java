package it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.dst;

/**
 * Enum type for the combination rule
 * @author Giuseppe Rizzo
 *
 */
public enum RuleType {

	Mixing,
	Dempster,
	DuboisPrade;
	
	
	public String toString(){
		if (this.compareTo(Mixing)==0)
			return "mixing";
		else if (this.compareTo(DuboisPrade)==0)
			return "dubois";
		else
			 return "dempster";
		
	}
	
	public static RuleType  getRule(String name){
		
		if (name.compareToIgnoreCase("dempster")==0)
			return Dempster;
		else if (name.compareToIgnoreCase("dubois")==0)
			return RuleType.DuboisPrade;
		else 
			return Mixing;
//		else if (name.compareTo("it.uniba.di.lacam.ml.classifiers.TerminologicalDecisionTreeInducer")==0)
//			return TerminologicalDecisionTree;
		
	} 
}
