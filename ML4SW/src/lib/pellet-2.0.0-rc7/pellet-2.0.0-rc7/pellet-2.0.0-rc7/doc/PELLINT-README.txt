Pellint
-------

Pellint (short for "pellet lint" command) is a lint tool for OWL ontologies 
that detects problematic modeling constructs (patterns) that may have an 
impact on the ontology's reasoning performance in Pellet and which, upon 
request, repairs them where possible. In addition, if the ontology is in 
RDF/XML format, Pellint also checks that all RDF resources are properly 
typed.                                       

Pellint currently supports detection of 9 easily configurable patterns.     
To find out more information on these patterns and how to configure them,   
please see PATTERNS.txt.                                                    

See "Extending Pellint" section below for more info about adding new        
patterns to Pellint.

License
-------

Pellint is available for use under the terms described in LICENSING.txt; to 
wit, the Affero GPL v. 3 software license.                                  

Installation
------------

Pellint requires Java 5.0 or later to run.

Unzip the Pellint distribution file into a directory of your choice. 

You can run Pellint command-line program using the script pellint.bat       
on Windows systems or pellint.sh on Unix systems.  Pellint expects the      
following arguments:                                                        

pellint.bat (or pellint.sh on Unix) [OPTIONS] ONTOLOGY
   ONTOLOGY       The input ontology file/URI to analyze

 OPTIONS:
   -f   FILE      Apply fixes (where applicable) on the found lints,
                  and save the new ontology to FILE
   -r             Analyze the root ontology only, not any of its imports
   -o [RDF|OWL]   RDF: only analyze RDF type declarations (RDF/XML only)
                  OWL: only analyze OWL axioms
   -v --version   Print the version information and exit
   -h --help      Print help message


Compiling
---------

If you would like to rebuild Pellint from source files Pellint comes with   
an Ant [1] build file. Packaging can easily be done by running the command  
"ant dist" from Pellint's root directory. The resulting jar file is named   
"pellint.jar" and put under dist/lib directory. The main class in the jar   
file is com.clarkparsia.pellint.Pellint.                                    

[1] http://ant.apache.org/


Extending Pellint
-----------------

The following is a step-by-step guide on how to implement your own pattern  
for use in Pellint.  For convenience <INSTALL> stands for your Pellint's    
root directory.                                                             

(1) Make sure you can build Pellint using "ant dist" as described above.

(2) Implement your pattern as a Java class in a new file:

  (2.1) If your pattern always matches against one single axiom, then       
  extend AxiomLintPattern and implement necessary methods, including a way  
  to repair a lint where possible.                                          

  (2.2) If your pattern needs to match against the entire ontology, then    
  implement OntologyLintPattern and its necessary methods, including a way  
  to repair a lint where possible.                                          

Refer to the Javadocs on these interfaces for further details.

(3) Place your new pattern file anywhere under <INSTALL>/src so that Ant    
can include it in the build.  Run "ant dist" to rebuild Pellint with the    
new pattern.                                                                

(4) Update <INSTALL>/dist/pellint.properties and add your new pattern to    
the configuration.  Please refer to the comments included in the head of    
this file for details.                                                      

(5) Run Pellint directly (not with .bat or .sh script) using the following  
command so that it runs the newly built Pellint and uses the modified       
configuration file:                                                         

    java -Dpellint.configuration=file:<INSTALL>/dist/pellint.properties -jar <INSTALL>/dist/lib/pellint.jar [OPTIONS] ONTOLOGY


Support
-------

For best-effort support of Pellint, Pellet, etc, please direct email        
inquiries to the Pellet Users list: pellet-users@lists.owldl.com.           
