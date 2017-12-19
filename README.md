SWMLAlgorithms
==============

Various implementation of machine learning algorithms for Semantic Web knowledge bases 

The software provides the implementation of the algorithms for inducing:
- Terminological Decision Trees [2,7,8]
- Terminological Random Forests [2,5]
- Evidential Terminological Decision Trees and Forests [1,3,4,6]
- kernelized perceptron 
- k-nearest neighbor [9]

in order to assess the class-membership of OWL individuals

Import the project.
==============

Clone the repository via GIT and import the project using Maven.

Running the software.
==============
For running the program you must to specify the parameters for the experiments in the file experiments.properties. The parameters to be specifiied are reported below:

- NUMGENCONCEPTS, the number of  artificial learning  problems  to be generated  in class-membership prediction tasks 
- PURITY_THRESHOLD, the purity threshold fdecision tree learning
- originalImbalance, the original imbalance ratio  (employed by TRF and ETRFs)
- SEED, the seed for random generator employed by the refinement operator
- NTREES, the forest size
- nonspecificityControl, a flag to impose a prepruning criterion over ETDT learning algoritgm
- design (CROSSVALIDATION/BOOTSTRAP), the design of the experiments
- task, the task to be solved (e.g. CLASSMEMBERSHIPREDICTION )
- BINARYCLASSIFICATION, a boolean to decide if the learning problem must be solved under CWA or OWA
- NFOLDS, the number of folds or run for the experiments
- algorithm, the algorithmn to be used in th experiments (e.g. classifiers.knn.QHDNN)
- urlOwlFile, the file containing the knowledge base (the software supports only  OWL file)
- M3, for the M-estimate probability
- samplingrate, the stratified sampling rate adopted by (E)TRFs 
- missingValueTreatmentForTDT, a boolean value to decide if the branches of a TDT must be navigated in parallel when an intermediate test return an unknown value 
- pruning, the pruning strategy for TDT (NOPRUNING/,REP/PEP)
- beam, the number of candidates generated via refinement operator
- refinementoperator, the refinement opertator to be employed (classifiers.refinementOperator.RefinementOperator)
- CCP, a flag to decide if the information gain must be calculated using the CCP
- mutagenicAsDataPropertyPredicti, a flag to decide the learning problme over CARCINOGENESIS ontology

Publications.
==============
[1] Giuseppe Rizzo, Nicola Fanizzi, Claudia d'Amato, Floriana Esposito:
Approximate classification with web ontologies through evidential terminological trees and forests. Int. J. Approx. Reasoning 92: 340-362 (2018)

[2]Giuseppe Rizzo, Claudia d'Amato, Nicola Fanizzi, Floriana Esposito:
Tree-based models for inductive classification on the Web Of Data. J. Web Sem. 45: 1-22 (2017)

[3] Giuseppe Rizzo, Claudia d'Amato, Nicola Fanizzi, Floriana Esposito:
Inductive Classification Through Evidence-Based Models and Their Ensembles. ESWC 2015: 418-433

[4]Giuseppe Rizzo, Claudia d'Amato, Nicola Fanizzi:
On the Effectiveness of Evidence-Based Terminological Decision Trees. ISMIS 2015: 139-149

[5]Giuseppe Rizzo, Claudia d'Amato, Nicola Fanizzi, Floriana Esposito:
Tackling the Class-Imbalance Learning Problem in Semantic Web Knowledge Bases. EKAW 2014: 453-468

[6] Giuseppe Rizzo, Claudia d'Amato, Nicola Fanizzi, Floriana Esposito:
Towards Evidence-Based Terminological Decision Trees. IPMU (1) 2014: 36-45

[7]Nicola Fanizzi, Claudia d'Amato, Floriana Esposito:
Towards the induction of terminological decision trees. SAC 2010: 1423-1427

[8] Nicola Fanizzi, Claudia d'Amato, Floriana Esposito:
Induction of Concepts in Web Ontologies through Terminological Decision Trees. ECML/PKDD (1) 2010: 442-457

[9]Claudia d'Amato, Nicola Fanizzi, Floriana Esposito:
Query Answering and Ontology Population: An Inductive Approach. ESWC 2008: 288-302



