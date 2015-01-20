package evaluation.designOfExperiments;

public enum AlgorithmName {
	
	TerminologicalDecisionTree,
	DSTTerminologicalDecisionTree,
	TerminologicalRandomForests,
	DSTTerminologicalRandomForests,
	RoughTerminologicalDecisionTree;
	
	
	public String toString(){
		if (this.compareTo(TerminologicalDecisionTree)==0)
			
			return "classifiers.TerminologicalDecisionTreeInducer";
		
		else if (this.compareTo(DSTTerminologicalDecisionTree)==0)
			return  "classifiers.DSTTerminologicalDecisionTreeInducer";
		
		else if (this.compareTo(TerminologicalRandomForests)==0)
			return  "classifiers.TerminologicalRandomForestsInducer";
		
		else if (this.compareTo(DSTTerminologicalRandomForests)==0)
			return "classifiers.EvidentialTerminologicalRandomForestInducer";
		else if (this.compareTo(RoughTerminologicalDecisionTree)==0)
			return "classifiers.RoughTerminologicalDecisionTreeInducer";
		
	
		return "";
	}

	public static AlgorithmName getClassifier(String name){
		
		if (name.compareTo("classifiers.TerminologicalDecisionTreeInducer")==0)
			return TerminologicalDecisionTree;
		else if (name.compareTo("classifiers.TerminologicalRandomForestsInducer")==0)
			return TerminologicalRandomForests;
		else if (name.compareTo("classifiers.DSTTerminologicalDecisionTreeInducer")==0)
			return DSTTerminologicalDecisionTree;
//		else if (name.compareTo("classifiers.TerminologicalDecisionTreeInducer")==0)
//			return TerminologicalDecisionTree;
		else if (name.compareTo("classifiers.RoughTerminologicalDecisionTreeInducer")==0)
			return RoughTerminologicalDecisionTree;
		else 
			return DSTTerminologicalRandomForests;
		

		
		
		
		
	}
	
}
