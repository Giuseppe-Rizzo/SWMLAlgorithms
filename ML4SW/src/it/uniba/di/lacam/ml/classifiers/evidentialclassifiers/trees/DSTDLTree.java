package it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.trees;


import java.util.ArrayList;
import org.semanticweb.owlapi.model.OWLClassExpression;

import it.uniba.di.lacam.ml.classifiers.evidentialclassifiers.dst.MassFunction;
import it.uniba.di.lacam.ml.classifiers.trees.models.*;
/**
 * A terminological decision tree with a BBA per node
 * @author Giuseppe
 *
 */
public class DSTDLTree extends AbstractTree implements EvidentialModel{

	private class DLNode {
		OWLClassExpression concept;	// node concept
		
		DSTDLTree pos; 			// positive decision subtree
		DSTDLTree neg; 	// negative decision subtree
		@SuppressWarnings("rawtypes")
		MassFunction m;
		@SuppressWarnings("rawtypes")
		public DLNode(OWLClassExpression c, MassFunction m) {
			concept = c;
			this.pos = this.neg = null; // node has no children
			this.m= m; // Dempster-Shafer extension
		}

//		public DLNode() {
//			concept = null;
////			this.pos = this.neg = null; // node has no children
//		}
		

		public String toString() {
			return this.concept.toString();
		}
		
		public Object clone(){
			DLNode cloned= new DLNode(concept,m);
			return cloned;
		}
		
	}
	

	private DLNode root; // Tree root
	
	
	public DSTDLTree () {
		this.root = null;
		
	
	}
	
	@SuppressWarnings("rawtypes")
	public DSTDLTree (OWLClassExpression c, MassFunction m) {		
		this.root = new DLNode(c,m);
	
	}

	/**
	 * 
	 * @param concept, the concept to be set into the root node
	 * @param m, the BBA for the concept
	 */
	@SuppressWarnings("rawtypes")
	public void setRoot(OWLClassExpression concept, MassFunction m) {
		this.root = new DLNode(concept, m);
//		this.root.concept = concept;
	}

	/**
	 * @return the root concept
	 */
	public OWLClassExpression getRoot() {
		return root.concept;
	}
	
	@SuppressWarnings("rawtypes")
	/**
	 * Return the BBA for the root node
	 * @return
	 */
	public MassFunction getRootBBA() {
		return root.m;
	}

  /**
   * Set the sub-tree for the positive branch
   * @param subTree
   */
	public void setPosTree(DSTDLTree subTree) {
		this.root.pos = subTree;
		
	}

	/**
	 * Set the sub-tree for the negative branch
	 * @param subTree
	 */
	public void setNegTree(DSTDLTree subTree) {
		
		this.root.neg = subTree;
		
	}
	
	public String toString() {
		if (root.pos == null && root.neg == null)
			return root.toString();
		else
			return root.concept.toString() + " ["+root.pos.toString()+"  "+root.neg.toString()+"]";
	}

	/**
	 * return the subtree for the positive branch
	 * @return the subtree
	 */
	public DSTDLTree getPosSubTree() {
		// TODO Auto-generated method stub
		return root.pos;
	}

	/**
	 * return the subtree for the negative branch
	 * @return the subtree
	 */
	public DSTDLTree getNegSubTree() {
		// TODO Auto-generated method stub
		return root.neg;
	}
	
	public Object clone(){
		DSTDLTree elem= new DSTDLTree();
		DLNode cloned= (DLNode)root.clone(); // copio il nodo
		elem.setRoot(cloned.concept, cloned.m);
		if (root.pos != null){ // copy the positive tree
		
			elem.root.pos= (DSTDLTree)(root.pos).clone();
			
		}
		if (root.neg!=null){ // copy the negative tree
			elem.root.neg=  (DSTDLTree)(root.neg).clone();
			
		}
		
		return elem;
	}
	
	
	
	/**
	 * Breadth first traversing of a tree
	 * @return
	 */
	private double getNodi(){
		// visita in ampiezza per effettuare il conteggio
		
		ArrayList<DLNode> lista = new ArrayList<DLNode>();
		double  num=0;
		if(root!=null){
			lista.add(root);
			while(!lista.isEmpty()){
				DLNode node= lista.get(0);
				lista.remove(0);
				num++;
				DLNode sx=null;
				if(node.pos!=null){
					sx= node.pos.root;
				 	if(sx!=null)
					 lista.add(sx);
				}
				if(node.neg!=null){
				 sx= node.neg.root;
				 if(sx!=null)
					 lista.add(sx);
				}
					 
			}
			
		}
		
		return num;
		
	}

	@Override
	/**
	 * Compute the complexity of the model
	 */
	public double getComplexityMeasure() {
		
		return getNodi();
	}

	/**
	 * Collect the BBAs installed into the  nodes
	 * @return 
	 */
	public ArrayList<MassFunction> collectLeaves() {
		ArrayList<MassFunction> bba = new ArrayList<MassFunction>();
		ArrayList<DLNode>lista=new ArrayList<DLNode>();
		if(root!=null){
			while(!lista.isEmpty()){
				DLNode node= lista.get(0);
				lista.remove(0);
				
				DLNode sx=null;
				
				if ((node.pos==null) &&(node.neg==null))
					bba.add(node.m); // add the bba 
				if(node.pos!=null){
					sx= node.pos.root;
				 	if(sx!=null)
					 lista.add(sx);
				}
				else
					
				if(node.neg!=null){
				 sx= node.neg.root;
				 if(sx!=null)
					 lista.add(sx);
				}
					 
			}
			
		}
		
		return bba;

		
	}
	

	
	
}
