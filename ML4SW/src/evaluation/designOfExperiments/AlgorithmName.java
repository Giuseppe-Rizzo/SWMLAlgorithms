package evaluation.designOfExperiments;

public enum AlgorithmName {
	
	TerminologicalDecisionTree,
	DSTTerminologicalDecisionTree,
	TerminologicalRandomForests,
	DSTTerminologicalRandomForests;
	
	
	public String toString(){
		if (this.compareTo(TerminologicalDecisionTree)==0)
			
			return "classifiers.TerminologicalDecisionTreeInducer";
		
		else if (this.compareTo(DSTTerminologicalDecisionTree)==0)
			return  "classifiers.DSTTerminologicalDecisionTreeInducer";
		
		else if (this.compareTo(TerminologicalRandomForests)==0)
			return  "classifiers.TerminologicalRandomForestsInducer";
		
		else if (this.compareTo(DSTTerminologicalRandomForests)==0)
			return "classifiers.EvidentialTerminologicalRandomForestInducer";
			
		
	
		return "";
	}

}
