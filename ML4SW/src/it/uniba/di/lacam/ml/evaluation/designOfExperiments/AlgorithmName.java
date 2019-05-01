package it.uniba.di.lacam.ml.evaluation.designOfExperiments;
/**
 * Enum type for the algorithms 
 * @author Giuseppe
 *
 */
public enum AlgorithmName {
	
	TerminologicalDecisionTree,
	DSTTerminologicalDecisionTree,
	TerminologicalRandomForests,
	DSTTerminologicalRandomForests,
	perceptron,
	knn;

	
	
	public String toString(){
		if (this.compareTo(TerminologicalDecisionTree)==0)
			
			return "it.uniba.di.lacam.ml.classifiers.trees.TerminologicalDecisionTreeInducer";
		
		else if (this.compareTo(DSTTerminologicalDecisionTree)==0)
			return  "it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.trees.DSTTerminologicalDecisionTreeInducer";
		
		else if (this.compareTo(TerminologicalRandomForests)==0)
			return  "it.uniba.di.lacam.ml.classifiers.ensemble.trfs.TerminologicalRandomForestsInducer";
		
		else if (this.compareTo(DSTTerminologicalRandomForests)==0)
			return "it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.etrfs.EvidentialTerminologicalRandomForestInducer";
		else if (this.compareTo(knn)==0)
			return "it.uniba.di.lacam.ml.classifiers.knn.QHDNN";
		else  if (this.compareTo(perceptron)==0)
			return "it.uniba.di.lacam.ml.classifiers.perceptron.Perceptron";
		
	
		return "";
	}

	public static AlgorithmName getClassifier(String name){
		
		if (name.compareTo("it.uniba.di.lacam.ml.classifiers.trees.TerminologicalDecisionTreeInducer")==0)
			return TerminologicalDecisionTree;
		else if (name.compareTo("it.uniba.di.lacam.ml.classifiers.ensemble.etrfs.TerminologicalRandomForestsInducer")==0)
			return TerminologicalRandomForests;
		else if (name.compareTo("it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.trees.DSTTerminologicalDecisionTreeInducer")==0)
			return DSTTerminologicalDecisionTree;
//		else if (name.compareTo("it.uniba.di.lacam.ml.classifiers.TerminologicalDecisionTreeInducer")==0)
//			return TerminologicalDecisionTree;
		else if (name.compareTo("it.uniba.di.lacam.ml.classifiers.knn.QHDNN")==0)
			return knn;
		else if (name.compareTo("it.uniba.di.lacam.ml.classifiers.perceptron.Perceptron")==0)
			return perceptron;
		else
			return DSTTerminologicalRandomForests;
		

		
		
		
		
	}
	
}
