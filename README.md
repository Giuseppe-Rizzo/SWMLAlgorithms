SWMLAlgorithms
==============

Various implementation of machine learning algorithms for Semantic Web knowledge bases 

The software provides the implementation of the algorithms for inducing:
- Terminological Decision Trees
- Terminological Random Forests 
- Evidential Terminological Decision Trees and Forests
- kernelized perceptron 
- k-nearest neighbor

in order to assess the class-membership of OWL individuals

Import the project
==============

Clone the repository via GIT and import the project using Maven.

Running the software.
==============
For running the program you must to specify the parameters for the experiments in the file experiments.properties. The parameters to be specifiied are reported below:

NUMGENCONCEPTS, the number of  artificial learning  problems  to be generated  in class-membership prediction tasks 
PURITY_THRESHOLD, the purity threshold fdecision tree learning
originalImbalance, the original imbalance ratio  (employed by TRF and ETRFs)
SEED, the seed for random generator employed by the refinement operator
NTREES, the forest size
nonspecificityControl, a flag to impose a prepruning criterion over ETDT learning algoritgm
design (CROSSVALIDATION/BOOTSTRAP), the design of the experiments
task, the task to be solved (e.g. CLASSMEMBERSHIPREDICTION )
BINARYCLASSIFICATION, a boolean to decide if the learning problem must be solved under CWA or OWA
NFOLDS, the number of folds or run for the experiments
algorithm, the algorithmn to be used in th experiments (e.g. classifiers.knn.QHDNN)
urlOwlFile, the file containing the knowledge base (the software supports only  OWL file)
M3, for the M-estimate probability
samplingrate, the stratified sampling rate adopted by (E)TRFs 
missingValueTreatmentForTDT, a boolean value to decide if the branches of a TDT must be navigated in parallel when an intermediate test return an unknown value 
pruning, the pruning strategy for TDT (NOPRUNING/,REP/PEP)
bea, the number of candidates generated via refinement operator
refinementoperator, the refinement opertator to be employed (classifiers.refinementOperator.RefinementOperator)
CCP, a flag to decide if the information gain must be calculated using the CCP
mutagenicAsDataPropertyPredicti, a flag to decide the learning problme over CARCINOGENESIS ontology

==============



