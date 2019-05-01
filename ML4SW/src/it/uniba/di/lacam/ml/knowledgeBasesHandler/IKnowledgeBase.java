package it.uniba.di.lacam.ml.knowledgeBasesHandler;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
/**
 * Abstract class for a KB
 * @author Giuseppe
 *
 */
public interface IKnowledgeBase {

	public abstract OWLOntology initKB();
//
//	/**
//	 * Restituisce i risultati della classificazione effettuata dal reasoner
//	 * @return matrice testConcepts.length x esempi.length
//	 */
//	public abstract int[][] getClassMembershipResult(
//			OWLDescription[] testConcepts, OWLIndividual[] esempi);

	/**
	 * Returns the classification results w.r.t. the target roles
	 * @param ruoli
	 * @param esempi
	 * @return
	 */
	public abstract int[][][] getRoleMembershipResult(
			OWLObjectProperty[] ruoli, OWLIndividual[] esempi);

	//********************METODI DI ACCESSO  ALLE COMPONENTI DELL'ONTOLOGIA*******************************//
	/**
	 * return the roles of an ontoloy
	 * @return
	 */
	public abstract OWLObjectProperty[] getRoles();

	/**
	 * Returns the concept names
	 * @return
	 */
	public abstract OWLClass[] getClasses();

	/**
	 * Restituisce gli individui dell'ontologia
	 * @return gli individui di un'ontologia
	 */
	public abstract OWLIndividual[] getIndividuals();

	public abstract OWLDataProperty[] getDataProperties();

	public abstract OWLIndividual[][] getDomains();

	public abstract OWLLiteral[][] getDataPropertiesValue();

	public abstract String getURL();

	/**
	 * Random choice of datatype prioperites
	 * @param numQueryProperty, un certo numero di propriet�
	 * @return
	 */
	public abstract int[] getRandomProperty(int numQueryProperty);

	public abstract int[] getRandomRoles(int numRegole);


 /**
  * determines the ground truth w.r.t the target concepts
  * @param testConcepts
  * @param negTestConcepts
  * @param esempi
  * @return
  */
	int[][] getClassMembershipResult(OWLClassExpression[] testConcepts,
			OWLClassExpression[] negTestConcepts, OWLIndividual[] esempi);

}