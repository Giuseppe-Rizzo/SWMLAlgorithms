package classifiers.trees.models;



import java.util.ArrayList;

import org.semanticweb.owl.model.OWLDescription;



public class DLTree extends AbstractTree {

	private int match, omission, commission, induction;

	/* (non-Javadoc)
	 * @see classifiers.trees.models.AbstractTree#getMatch()
	 */
	@Override
	public int getMatch() {
		return match;
	}

	/* (non-Javadoc)
	 * @see classifiers.trees.models.AbstractTree#setMatch(int)
	 */
	@Override
	public void setMatch(int match) {
		this.match++;
	}

	/* (non-Javadoc)
	 * @see classifiers.trees.models.AbstractTree#getOmission()
	 */
	@Override
	public int getOmission() {
		return omission;
	}

	/* (non-Javadoc)
	 * @see classifiers.trees.models.AbstractTree#setOmission(int)
	 */
	@Override
	public void setOmission(int omission) {
		this.omission++;
	}

	/* (non-Javadoc)
	 * @see classifiers.trees.models.AbstractTree#getCommission()
	 */
	@Override
	public int getCommission() {
		return commission;
	}

	/* (non-Javadoc)
	 * @see classifiers.trees.models.AbstractTree#setCommission(int)
	 */
	@Override
	public void setCommission(int commission) {
		this.commission++;
	}

	/* (non-Javadoc)
	 * @see classifiers.trees.models.AbstractTree#getInduction()
	 */
	@Override
	public int getInduction() {
		return induction;
	}

	/* (non-Javadoc)
	 * @see classifiers.trees.models.AbstractTree#setInduction(int)
	 */
	@Override
	public void setInduction(int induction) {
		this.induction++;
	}

	private class DLNode {

		OWLDescription concept;		// node concept
		DLTree pos; 			// positive decision subtree
		DLTree neg; 			// negative decision subtree

		public DLNode(OWLDescription c) {
			concept = c;
			this.pos = this.neg = null; // node has no children
		}

		//		public DLNode() {
		//			concept = null;
		////			this.pos = this.neg = null; // node has no children
		//		}


		public String toString() {
			return this.concept.toString();
		}

	}


	private DLNode root; // Tree root


	public DLTree () {
		this.root = null;
	}

	public DLTree (OWLDescription c) {		
		this.root = new DLNode(c);
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(OWLDescription concept) {
		this.root = new DLNode(concept);
		//		this.root.concept = concept;
	}

	/**
	 * @return the root
	 */
	public OWLDescription getRoot() {
		return root.concept;
	}


	public void setPosTree(DLTree subTree) {
		this.root.pos = subTree;

	}

	public void setNegTree(DLTree subTree) {

		this.root.neg = subTree;

	}

	public String toString() {
		if (root==null)
			return null;
		if (root.pos == null && root.neg == null)
			return root.toString();
		else
			return root.concept.toString() + " ["+root.pos.toString()+" "+root.neg.toString()+"]";
	}

	public DLTree getPosSubTree() {
		return root.pos;
	}

	public DLTree getNegSubTree() {

		return root.neg;
	}
	

	public double getNodi(){
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

}
