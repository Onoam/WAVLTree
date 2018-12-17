package dataStructures;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import dataStructures.WAVLTree.WAVLNode;

/**
 *
 * WAVLTree
 *
 *
 * An implementation of a WAVL Tree. (Haupler, Sen & Tarajan ‘15)
 *
 */
//test2.1
public class WAVLTree {
	private WAVLNode root;
	public final WAVLNode OUTER_NODE = new WAVLNode();

	public WAVLTree(WAVLNode root) {
		this.root = root;
	}

	public WAVLTree() {
		this.root = new WAVLNode();
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 * ==============
	 * Returns false if root is an inner node, and true if it is an outer leaf.
	 * @Complexity O(1)
	 * @return boolean false if root is an inner node
	 **/
	public boolean empty() {
		return !this.root.isInnerNode();
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree otherwise,
	 * returns null
	 * ===============
	 * @Complexity O(treeSearch) = O(log n)
	 */
	public String search(int k) {
		WAVLNode item = treeSearch(getRoot(), k);
		if (item.getRank() == -1) {
			return null;
		} else {
			return item.getValue();
		}
	}


	/**
	 * Implementation of Tree-Search from slides, done deterministicly
	 * @Complexity O(log n) - where n # nodes in tree (tree height)
	 * @param x - WAVLNode
	 * @param k - int the key to look for
	 * @return WAVLNode - if not found, then returns a, OUTER_NODE
	 */
	private WAVLNode treeSearch(WAVLNode x, int k) {
		while (x.getRank() != -1) {
			if (k == x.getKey()) {
				return x;
			} else if (k < x.getKey()) {
				x = x.getLeft();
			} else {
				x = x.getRight();
			}
		}
		return x;
	}

	/**
	 * Implementation of Tree-Position from slides. Done deterministicly.
	 * @Complexity O(log n) - where n # nodes in tree (tree height)
	 * @param x - WAVLNode
	 * @param k - int the key to look for
	 * @return WAVLNode - return the last node encountered, or an existing node
	 */
	private WAVLNode treePosition(WAVLNode x, int k) {
		WAVLNode y = new WAVLNode();
		while (x.getRank() != -1) {
			y = x;
			if (k == x.getKey()) {
				return x;
			} else if (k < x.getKey()) {
				x = x.getLeft();
			} else {
				x = x.getRight();
			}
		}
		return y;
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the WAVL tree. the tree must remain
	 * valid (keep its invariants). returns the number of rebalancing operations, or
	 * 0 if no rebalancing operations were necessary. returns -1 if an item with key
	 * k already exists in the tree.
	 */
	public int insert(int k, String i) {
		WAVLNode x = new WAVLNode(k, i, null, OUTER_NODE, OUTER_NODE);
		if (empty()) {
			this.root = x;
			return 0;
		} else {
			int counter = treeInsert(getRoot(), x);
			if (counter == -1) {
				return counter;
			} else {
				return insertRebalance(x);
			}
		}
	}
	/**
	 * this method is called after inserting.
	 * Checks which rebalance case we are in and calls the appropriate
	 * rebalance helper-method.
	 * @param x the node that was inserted.
	 * @return the number of rebalance steps
	 */
	private int insertRebalance(WAVLNode x) {
		x = x.getParent(); // we actually work on the parent-child edge
		if (x == null) { // should only happen if we've reached the root
			return 0;
		}
		int ldiff = x.getRank() - x.getLeft().getRank();
		int rdiff = x.getRank() - x.getRight().getRank();
		if (rdiff * ldiff != 0) {
			return 0; // tree is valid WAVL iff rdiff,ldiff!=0
		}
		//one of ldiff, rdiff is 0
		//choose which side is the problem side
		char side = ldiff == 0 ? 'l': 'r';
		//case 1, including symmetry
		assert rdiff == 0 || ldiff == 0;
		if (rdiff + ldiff == 1) { // one is zero (established), the other is 1
			return iCaseOneRebalance(x);
		}
		assert ldiff == 2 || rdiff == 2;
		//case 2, established that x is (0,2) node
		if ((side == 'l' && x.getLeft().getRank()-x.getLeft().getLeft().getRank() == 1) ||
			 (side == 'r' && x.getRight().getRank() - x.getRight().getRight().getRank() == 1)) {
			return iCaseTwoRebalance(x, side);
		}
		//case 3, the only remaining option
		return iCaseThreeRebalance(x, side);
	}

	/**
	 * performs the rebalancing of the tree after deletion
	 * calls appropriate helper methods
	 * @param x the parent of the node that was deleted
	 * @return the number of rebalance steps taken
	 * @pre x.parent is not OUTERNODE
	 */
	private int deleteRebalance(WAVLNode x) {
		if (x == null) { // should only happen if we've reached the root
			return 0;
		}
		if (x.getRank() == 1 && x.isLeaf()) {//x is leaf
			return dCaseOneRebalance(x); //demotion, equivalent to other type of case-one
		}
		int ldiff = x.getRank() - x.getLeft().getRank();
		int rdiff = x.getRank() - x.getRight().getRank();
		char side = ldiff == 3? 'r':'l'; // choose which side we work on 
		if (Math.max(ldiff, rdiff)<3) {
			return 0; //tree is valid WAVL, no rank 2 leaf, no rank diff>=3
		}
		assert Math.max(ldiff, rdiff)==3;
		if (Math.min(ldiff, rdiff)== 2) {
			return dCaseOneRebalance(x);
		}
		assert Math.min(ldiff, rdiff)== 1;
		//x is confirmed as (3,1) node
		int[] grandChildDiffs = checkDiffs(x, side);
		if (grandChildDiffs[0] == 2 && grandChildDiffs[1] == 2) {
			return dCaseTwoRebalance(x, side);
		}
		//one diff or more isn't 2
		if (grandChildDiffs[1] == 1) {
			return dCaseThreeRebalance(x, side);
		}
		assert grandChildDiffs[1] == 2; // only option remaining
		return dCaseFourRebalance(x, side);
	}
	
	/**
	 * checks the differences as shown in the WAVL presentation, slide 47
	 * the returned array stores the "outer" grandchild diff (same side as child)
	 * in index 1, and the "inner" grandchild diff (opposite from child) in index 0
	 * e.g. if side='r', then outer is x.right.right and inner is x.right.left
	 * @param x the grandparent to check
	 * @param side which side's children need to be checked
	 * @return the differences in an array
	 */
	private int[] checkDiffs(WAVLNode x, char side) {
		int[] diffs = new int[2];
		if (side == 'r') {
			diffs[0] = x.getRight().getRank() - x.getRight().getLeft().getRank();
			diffs[1] = x.getRight().getRank() - x.getRight().getRight().getRank();
		}
		else {
			diffs[0] = x.getLeft().getRank() - x.getLeft().getRight().getRank();
			diffs[1] = x.getLeft().getRank() - x.getLeft().getLeft().getRank();
		}
		return diffs;
	}

	private int dCaseOneRebalance(WAVLNode x) {
		x.demote();
		
		return 1+deleteRebalance(x.getParent());
	}

	private int dCaseTwoRebalance(WAVLNode x, char side) {
		x.demote();
		if (side == 'r') {
			x.getRight().demote();
		}
		else {
			x.getLeft().demote();
		}
		return 1+deleteRebalance(x.getParent());
		
	}

	private int dCaseThreeRebalance(WAVLNode x, char side) {
		
		
		if (x.isLeaf() && x.getRank() == 1) {
			x.demote();
		}
		return 1;
	}

	private int dCaseFourRebalance(WAVLNode x, char side) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int iCaseOneRebalance(WAVLNode x) {
		x.promote();
		return 1+insertRebalance(x.getParent());
	}
	/**
	 * rebalances after insertion, case 2.
	 * @param x the node that needs to be rotated
	 * @param side which of x's children need to be rotated with it
	 * @return 1, 1 rebalance operation
	 */
	private int iCaseTwoRebalance(WAVLNode x, char side) {
		x.demote();
		if (side == 'l') {
			rotateRight(x, x.left);
		}
		else {
			rotateLeft(x, x.right);
		}
		return 1;
	}
	/**
	 * rebalances after insertion, case 3.
	 * @param x the node that needs to be double rotated
	 * @param side which direction (in terms of symmetry) needs to be rotated
	 * @return 2, 2 rebalance operations
	 */
	private int iCaseThreeRebalance(WAVLNode x, char side) {
		if (side == 'l') {
			rotateLeft(x.getLeft(), x.getLeft().getRight());
			rotateRight(x, x.getLeft());
		}
		return 2;
	}

	/**
	 * performs right rotation. Does not handle demotions.
	 *
	 * @param x       y's parent
	 * @param y       x's left child
	 * @post x is y's right child, y is x's parent's child (same side as x was)
	 */
	private void rotateRight(WAVLNode x, WAVLNode y) {
		if (x.parent != null && x.key > x.getParent().key) {
			x.parent.right = y;
		} else if (x.parent != null) { // y is left child of its parent
			x.parent.left = y;
		}
		y.parent = x.parent;
		x.left = y.right;
		y.right = x;
		x.parent = y;
		x.updateSubtreeSize();
		y.updateSubtreeSize();
	}

	/**
	 * performs left rotation, does not handle demotions.
	 *
	 * @param y       x's right child
	 * @param x       y's parent
	 * @post x is y's left child, y is x's parent's child (same side as x was)
	 */
	private void rotateLeft(WAVLNode x, WAVLNode y) {

		if (x.parent != null && x.key > x.getParent().key) {
			x.parent.right = y;
		} else if (x.parent != null) { // y is left child of its parent
			x.parent.left = y;
		}
		y.parent = x.parent;
		x.right = y.left;
		y.left = x;
		x.parent = y;
		x.updateSubtreeSize();
		y.updateSubtreeSize();

	}
	private int treeInsert(WAVLNode root, WAVLNode z) {
		WAVLNode y = treePosition(root, z.getKey());
		if (z.getKey() == y.getKey()) {
			return -1;
		}
		z.parent = y;
		if (z.getKey() < y .getKey()) {
			y.left = z;
			return 0;
		} else {
			y.right = z;
			return 0;
		}
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were needed. returns -1 if an
	 * item with key k was not found in the tree.
	 */
	public int delete(int k) {
		WAVLNode z = treeSearch(getRoot(), k);
		if (z.getRank() == -1) {
			return -1;
		}
		WAVLNode y = z.getParent();
		remove(z);
		return deleteRebalance(y);
	}

	/**
	 * remove a node from the tree.
	 * @Complexity O(updateSizeUp) = O(log n)
	 * 				Each option does constant # operations + updateSizeUp
	 * @param node
	 */
	private void remove(WAVLNode node) {
		WAVLNode succ;
		// If leaf of tree, find side of parent and remove
		if (node.getRight().getRank() == -1 && node.getLeft().getRank() == -1) {
			//TODO Replace with IsLeaf() method
			removeLeaf(node); // O(1)
		} else { // Is an inner node
			succ = successor(node);
			if (succ == node.getRight()) { // O(1)
				/*
				* If this is his right child, then we need to:
				* 1) make node's parent the parent of succ
				* 2) make node's left child the child of succ
				* 3) attach succ to node's parent based on side
				* 4) update the size of succ (and this will update
				*    the size of parent)
				* */
				succ.parent = node.getParent();
				succ.left = node.getLeft();
				if (side(node) == 0) {
					node.getParent().left = succ;
				} else if (side(node) == 1) {
					node.getParent().right = succ;
				}
			} else {
				/*
				* If this isn't his right child then:
				* 1) If he has a right child, set it as the left child of succ's parent
				* 2) set succ.right and succ.left to node.right and node.left repectively
				* 3) set succ.parent to node.parent*/
				succ.getParent().left = succ.getRight();
				succ.parent = node.getParent();
				succ.right = node.getRight();
				succ.left = node.getLeft();
				succ.rank = node.getRank();
				succ.size = node.getSubtreeSize();
			}
		}
		node = null;

	}

	/**
	 * Function to remove leaf nodes. Used for readability.
	 * If the node has no parent, we don't do anything, because in remove
	 * we make the node null at the end.
	 * @Complexity O(updateSizeUp) = O(log n)
	 * @param node - the WAVLNode to remove
	 */
	private void removeLeaf(WAVLNode node) {
		switch (side(node)) {
			case 0:
				node.getParent().left = OUTER_NODE;
				updateSizeUp(node.getParent());
				node.parent = null;
				break;
			case 1:
				node.getParent().right = OUTER_NODE;
				updateSizeUp(node.getParent());
				node.parent = null;
				break;
			default:
				break;
		}
	}

	/**
	 * Find the side on which the node is.
	 * Used in deletion to recognize cases
	 * @Complexity O(1)
	 * @param node - WAVLNODE to check what side of child it is.
	 * @return 0 if left, 1 if right, -1 if has no parent
	 */
	private int side(WAVLNode node) {
		WAVLNode parent = node.getParent();
		if (parent != null) {
			if (parent.getLeft() == node) {
				return 0;
			} else {
				return 1;
			}
		}
		return -1;
	}

	/**
	 * If a node is removed ot inserted, update the sizes up the tree.
	 *
	 * @Complexity worst case O(log n) where n is # nodes in tree (height of tree)
	 * @param node
	 */
	private void updateSizeUp(WAVLNode node) {
		node.updateSubtreeSize(); // update the size of the first node
		// Then, updates the sizes up the tree
		WAVLNode parent = node.getParent();
		// If we reached the root, then we stop.
		// This ensures that we update the the root last, and then stop.
		while (node != this.getRoot()) {
			node = parent;
			node.updateSubtreeSize();
			parent = node.getParent();
		}
	}

	/**
	 * performs right rotation. Does not handle demotions.
	 *
	 * @param x       y's left child
	 * @param y       x's parent
	 * @param counter the rebalance counter
	 * @return counter increased by 1 (1 rebalance operation)
	 * @post y is x's right child, x is y's parent's child (same side as y was)
	 */
	private int rotateRight(WAVLNode x, WAVLNode y, int counter) {
		if (y.parent != null && y.key > y.getParent().key) {
			y.parent.right = x;
		} else if (y.parent != null) { // y is left child of its parent
			y.parent.left = x;
		}
		x.parent = y.parent;
		y.left = x.right;
		x.right = y;
		y.parent = x;
		return counter + 1;
	}

	/**
	 * performs left rotation, does not handle demotions.
	 *
	 * @param z       x's right child
	 * @param x       z's parent
	 * @param counter the rebalance counter
	 * @return counter increased by 1 (1 rebalance operation)
	 * @post x is z's left child, z is x's parent's child (same side as x was)
	 */
	private int rotateLeft(WAVLNode z, WAVLNode x, int counter) {

		if (x.parent != null && x.key > x.getParent().key) {
			x.parent.right = z;
		} else if (x.parent != null) { // y is left child of its parent
			x.parent.left = z;
		}
		z.parent = x.parent;
		x.right = z.left;
		z.left = x;
		x.parent = z;
		return counter + 1;
	}

	/**
	 * Returns the node with the key directly following x.
	 * This does not deal with call successor on the maximum of the tree,
	 * because we don't meet this case in any other operation.
	 *
	 * Implement by peudo-code from class
	 * @Complexity O(log n) worst case, where n is the number of nodes in the tree
	 * @param x WAVLNode A node in the tree
	 * @return y The WAVLNode with the following key
	 */
	private WAVLNode successor(WAVLNode x) {
		if (x.getRight().getRank() != -1) {
			return min(x.getRight());
		} else {
			WAVLNode y = x.getParent();
			while (y != null && x == y.getRight()) {
				x = y;
				y = x.getParent();
			}
			return y;
		}
	}


	/**
	 * Returns the node with the key directly preceding x.
	 * This does not deal with call predecessor on the minimum of the tree,
	 * because we don't meet this case in any other operation.
	 *
	 * Implement by peudo-code from class
	 * @Complexity O(log n) worst case, where n is the number of nodes in the tree
	 * @param x WAVLNode A node in the tree
	 * @return y The WAVLNode with the preceding key
	 */
	private WAVLNode predecessor(WAVLNode x) {
		if (x.getLeft().getRank() != -1) {
			return max(x.getLeft());
		} else {
			WAVLNode y = x.getParent();
			while (y != null && x == y.getLeft()) {
				x = y;
				y = x.getParent();
			}
			return y;
		}
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 * =============
	 * We use the private WAVLNode min to find the node with the minimal key
	 * in the subtree under node, and the return it's value
	 * @Complexity O(log n) n the number of nodes in the tree
	 * @return the value of the node with the minimal key
	 */
	public String min() {
		return min(root).getValue();
	}

	/**
	 * implemented recursively
	 * @Complexity O(log n) where n is the number of nodes in the subtree.
	 * 				The worst case happens when the min is of depth = height.
	 * @param node the root of the current subtree
	 * @return info of minimal node in tree
	 */
	private WAVLNode min(WAVLNode node) {
		if (node.left == OUTER_NODE) {
			return node;
		}
		return min(node.left);
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 * =============
	 * We use the private WAVLNode max to find the node with the maximal key
	 * in the subtree under node, and the return it's value
	 * @Complexity O(log n) n the number of nodes in the tree
	 * @return the value of the node with the maximal key
	 */
	public String max() {
		return max(root).getValue();
	}

	/**
	 * Implemented recursively
	 * @Complexity O(log n) where n is the number of nodes in the subtree.
	 * 				The worst case happens when the max is of depth = height.
	 * @param node the root of the current subtree
	 * @return info of maximal node in the tree
	 */
	private WAVLNode max(WAVLNode node) {
		if (node.right == OUTER_NODE) {
			return node;
		}
		return max(node.right);
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 * ===============
	 * @Complexity
	 * 		this.min() = O(log n)
	 * 		successor - called n times, with an amortized cost of O(1) per call
	 * 			total cost O(n) amortized
	 * 		total cost O(n)
	 * @return arr int[] sorted array of the keys of tree nodes
	 */
	public int[] keysToArray() {
		int[] arr = new int[root.size];
		if (!this.empty()) {
			WAVLNode current = this.min(root);
			int i = 0;
			while (i < root.size) {
				arr[i] = current.getKey();
				current = successor(current);
				i++;
			}
		}
		return arr;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 * ===============
	 * @Complexity
	 * 		this.min() = O(log n)
	 * 		successor - called n times, with an amortized cost of O(1) per call
	 * 			total cost O(n) amortized
	 * 		total cost O(n)
	 * @return arr String[] sorted array of the keys of tree nodes
	 */
	public String[] infoToArray() {
		String[] arr = new String[root.size];
		int[] count = new int[1];
		if (!this.empty()) {
			WAVLNode current = this.min(root);
			int i = 0;
			while (i < root.size) {
				arr[i] = current.getValue();
				current = successor(current);
				i++;
			}
		}
		return arr; // to be replaced by student code
	}



	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 * ================
	 *
	 * @Complexity O(1) given that the size of each node is updated at
	 * 					 every insert, delete, and rebalance
	 * @return the size of the root node.
	 */
	public int size() {
		if (empty()) {
			return 0; // to be replaced by student code
		} else {
			return getRoot().size;
		}
	}

	/**
	 * public WAVLNode getRoot()
	 *
	 * Returns the root WAVL node, or null if the tree is empty
	 * =========
	 * @Complexity O(empty) = O(1)
	 * @return the root of the tree
	 */
	public WAVLNode getRoot() {
		if (this.empty()) {
			return null;
		} else {
			return this.root;
		}
	}

	/**
	 * public int select(int i)
	 *
	 * Returns the value of the i'th smallest key (return -1 if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key Example
	 * 2: select(size()) returns the value of the node with maximal key Example 3:
	 * select(2) returns the value 2nd smallest minimal node, i.e the value of the
	 * node minimal node's successor
	 * ===========
	 *
	 * @Complexity O(selectNode) = O(log n), where n is # of nodes in the tree.
	 * @param i - int index to search for
	 * @return String - the value of the i'th smallest node
	 */
	public String select(int i) {
		if (empty()) {
			return null;
		} else {
			WAVLNode x = selectNode(getRoot(), i - 1);
			return x.getValue();
		}
	}

	/**
	 * Recursivly find the node with the i'th smalles value.
	 * If i < x.left.size, then the i'th smallest node is in the left
	 * 		subtree of x (because it has more than i nodes).
	 * 		We then need to keep on searching for the i'th smallest node.
	 * If i > x.left.size, then the i'th smallest node is in the right
	 * 		subtree of x. We then need to account for the x.left.size + 1
	 * 		(all the nodes in order until x.right), and seach for the
	 * 		i - x.left.size + 1 index in the rigth subtree.
	 * @Complexity O(log n) worst case where n is # of nodes in the tree.
	 * 				Because the longest route from root to leaf is log n.
	 * @param x - WAVLNode the root of the subtree to find the node in
	 * @param i - int the index to search for.
	 * @return The node with the i'th smallest value
	 */
	private WAVLNode selectNode(WAVLNode x, int i) {
		int r = x.getLeft().getSubtreeSize();
		if (i == r) {
			return x;
		} else if (i < r) {
			return selectNode(x.getLeft(), i);
 		} else {
			return selectNode(x.getRight(), i - r - 1);
		}
	}

	/**
	 * public class WAVLNode
	 */
	//
	public class WAVLNode {
		public static final int OUTER_NODE_RANK = -1;
		public static final String OUTER_NODE_VALUE = ""; // not used
		public static final int OUTER_NODE_KEY = -1;
		private int key;
		private String value;
		private WAVLNode parent;
		private WAVLNode right;
		private WAVLNode left;
		private int rank;
		private int size; // the size field needs to be updated after inserts or rebalances.

		public WAVLNode(int key, String value, WAVLNode parent, WAVLNode right, WAVLNode left, int rank) {
			this.key = key;
			this.value = value;
			this.parent = parent;
			this.right = right;
			this.left = left;
			this.rank = rank;
			this.size = this.getSubtreeSize();
		}
		/**
		 * @pre not called on OUTER_NODE
		 * @return whether node is leaf
		 */
		public boolean isLeaf() {
			// TODO Auto-generated method stub
			return getLeft().getRank() == OUTER_NODE_RANK && getLeft().getRank() == OUTER_NODE_RANK;
		}
		/**
		 * constructor for building a WAVLNode item with only key and value
		 * DEPRECATED: should use constructor with left and right children
		 * (so we can use OUTER_NODE for left and right)
		 * @param key
		 * @param value
		 */

		public WAVLNode(int key, String value) {
			this(key, value, null, null, null, 0);
		}
		/**
		 * The constructor for adding with left and right children and parent.
		 * Use this constructor for adding a leaf to the tree.
		 * @param parent the parent of the added leaf (insertion point)
		 * @param key node's key
		 * @param value node's value (info)
		 * @param right should be OUTER_NODE when adding a leaf
		 * @param left should be OUTER_NODE when adding a leaf
		 */
		public WAVLNode(int key, String value, WAVLNode parent, WAVLNode right, WAVLNode left) {
			this(key, value, parent, right, left, 0);
		}

		/**
		 * The default constructor for the WAVLNode class. Constructs an external node.
		 */
		public WAVLNode() {
			this(OUTER_NODE_KEY, null, null, null, null, OUTER_NODE_RANK);
		}

		/**
		 * All the getters and setters for WAVLNode.
		 * All run in O(1).
		 */
		public int getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public WAVLNode getLeft() {
			return left;
		}

		public WAVLNode getRight() {
			return right;
		}

		public WAVLNode getParent() {
			return parent;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		/**
		 * promote and demote WAVLNodes in tree.
		 * @Complexity O(1).
		 */
		public void promote() {
			setRank(getRank() + 1);
		}

		public void demote() {
			setRank(getRank() - 1);
		}

		/**
		 * @Complexity O(1)
		 * @return boolean true - if node.rank != OUTER_NODE_RANK.
		 */
		public boolean isInnerNode() {
			return rank != OUTER_NODE_RANK;
		}

		/**
		 * This function is deterministic and not recursive.
		 * We assume that in insertions and deletions from trees,
		 * updating the sizes is done from the bottom up, and that each
		 * new node inserted has static OUTER_NODEs as children, and
		 * a size of 0.
		 * In addition, if the tree is set, all sizes should be up-to-date.
		 * =================
		 * @Complexity O(1)
		 * @return the size of the subtree that has this as its root, including this.
		 */
		public int getSubtreeSize() {
			if (rank == OUTER_NODE_RANK) {
				return 0;
			}
			int lsize = 0;
			int rsize = 0;
			if (left != null) {
				lsize = left.size;
			}
			if (right != null) {
				rsize = right.size;
			}
			return rsize + lsize + 1;
		}
		/**
		 * updates the Node's subtree size in-place.
		 * Should be used after changes to the tree (insert, delete, rebalance).
		 * As explained in getSubtreeSize(), we assume that the update of
		 * sizes will be done from the bottom-up.
		 * ==================
		 * @Complexity O(getSubtreeSize()) = O(1)
		 * @return void. update the size field of the WAVLNode object
		 */
		public void updateSubtreeSize() {
			size = getSubtreeSize();
		}
	}

}
