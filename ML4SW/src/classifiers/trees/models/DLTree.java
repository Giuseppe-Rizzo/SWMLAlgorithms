package classifiers.trees.models;



import org.semanticweb.owl.model.OWLDescription;


public class DLTree {

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


}
