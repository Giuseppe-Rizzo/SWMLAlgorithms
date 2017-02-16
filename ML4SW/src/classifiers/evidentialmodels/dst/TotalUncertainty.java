package classifiers.evidentialmodels.dst;

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
//		else if (name.compareTo("classifiers.TerminologicalDecisionTreeInducer")==0)
//			return TerminologicalDecisionTree;
		
	} 
}
