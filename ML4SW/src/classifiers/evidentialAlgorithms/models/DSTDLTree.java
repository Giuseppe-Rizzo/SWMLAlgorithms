package classifiers.evidentialAlgorithms.models;


import java.util.ArrayList;

import org.semanticweb.owl.model.OWLDescription;

import classifiers.evidentialAlgorithms.DempsterShafer.MassFunction;


import classifiers.trees.models.*;
public class DSTDLTree extends AbstractTree implements EvidentialModel{

	private class DLNode {
		OWLDescription concept;	// node concept
		
		DSTDLTree pos; 			// positive decision subtree
		DSTDLTree neg; 	// negative decision subtree
		@SuppressWarnings("rawtypes")
		MassFunction m;
		@SuppressWarnings("rawtypes")
		public DLNode(OWLDescription c, MassFunction m) {
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
	public DSTDLTree (OWLDescription c, MassFunction m) {		
		this.root = new DLNode(c,m);
	
	}

	/**
	 * @param root the root to set
	 */
	@SuppressWarnings("rawtypes")
	public void setRoot(OWLDescription concept, MassFunction m) {
		this.root = new DLNode(concept, m);
//		this.root.concept = concept;
	}

	/**
	 * @return the root
	 */
	public OWLDescription getRoot() {
		return root.concept;
	}
	
	@SuppressWarnings("rawtypes")
	public MassFunction getRootBBA() {
		return root.m;
	}


	public void setPosTree(DSTDLTree subTree) {
		this.root.pos = subTree;
		
	}

	public void setNegTree(DSTDLTree subTree) {
		
		this.root.neg = subTree;
		
	}
	
	public String toString() {
		if (root.pos == null && root.neg == null)
			return root.toString();
		else
			return root.concept.toString() + " ["+root.pos.toString()+"  "+root.neg.toString()+"]";
	}

	public DSTDLTree getPosSubTree() {
		// TODO Auto-generated method stub
		return root.pos;
	}

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
	public double getComplexityMeasure() {
		
		return getNodi();
	}
	

	
	
}
