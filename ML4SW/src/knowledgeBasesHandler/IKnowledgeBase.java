package knowledgeBasesHandler;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

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
	 * Restituisce i risultati della classificazione
	 * @param ruoli
	 * @param esempi
	 * @return
	 */
	public abstract int[][][] getRoleMembershipResult(
			OWLObjectProperty[] ruoli, OWLIndividual[] esempi);

	//********************METODI DI ACCESSO  ALLE COMPONENTI DELL'ONTOLOGIA*******************************//
	/**
	 * Restituisce i ruoli presenti nell'ontologia
	 * @return
	 */
	public abstract OWLObjectProperty[] getRoles();

	/**
	 * Restituisce i concetti primitivi dell'ontologia
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
	 * Scelta casuale di un certo numero di data properties funzionali
	 * @param numQueryProperty, un certo numero di propriet�
	 * @return
	 */
	public abstract int[] getRandomProperty(int numQueryProperty);

	public abstract int[] getRandomRoles(int numRegole);



	int[][] getClassMembershipResult(OWLClassExpression[] testConcepts,
			OWLClassExpression[] negTestConcepts, OWLIndividual[] esempi);

}