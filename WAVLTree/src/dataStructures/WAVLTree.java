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
/**
 * @author Eytan-c, Onoam
 *
 */
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
	 * returns true if and only if the tree is empty ============== Returns false if
	 * root is an inner node, and true if it is an outer leaf.
	 * 
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
	 * returns null ===============
	 * 
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
	 * 
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
	 * 
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
	 * k already exists in the tree. ================= First, create a new WAVLNode
	 * with OUTER_NODEs as children. Check if the tree is empty, and if true - set x
	 * to root and return 0 If tree isn't empty, use treeInsert to insert the node.
	 * If treeInsert found that the key is already in the tree - return -1 Else, x
	 * was already inserted in treeInsert, so we need to updateSizeUp(x), and then
	 * rebalance(x)
	 * 
	 * @Complexity O(treeInsert + updateSize + rebalance) = O(log n) WC, O(1)
	 *             amortized
	 * @param k - int key of WAVLNode to insert
	 * @param i - String info of WAVLNode to insert
	 * @return the number of rebalance operations after inserting x
	 */
	public int insert(int k, String i) {
		WAVLNode x = new WAVLNode(k, i, null, OUTER_NODE, OUTER_NODE);
		if (empty()) {
			this.root = x;
			return 0;
		} else {
			int counter = treeInsert(getRoot(), x);
			if (counter == -1) { // key k is already in the tree
				return counter; // counter = -1
			} else {
				return insertRebalance(x.getParent());
			}
		}
	}

	/**
	 * this method is called after inserting. Checks which rebalance case we are in
	 * and calls the appropriate rebalance helper-method.
	 * 
	 * @param x the father of the node that was inserted.
	 * @return the number of rebalance steps
	 */
	private int insertRebalance(WAVLNode x) {
		if (x == null) { // should only happen if we've reached the root
			return 0;
		}
		int ldiff = x.getRankDiff('l');
		int rdiff = x.getRankDiff('r');
		if (rdiff * ldiff != 0) {
			return 0; // tree is valid WAVL iff rdiff,ldiff!=0
		}
		// one of ldiff, rdiff is 0
		// choose which side is the problem side
		char side = ldiff == 0 ? 'l' : 'r';
		// case 1, including symmetry
		assert rdiff == 0 || ldiff == 0;
		if (rdiff + ldiff == 1) { // one is zero (established), the other is 1
			return iCaseOneRebalance(x);
		}
		assert ldiff == 2 || rdiff == 2;
		// case 2, established that x is (0,2) node
		if ((side == 'l' && x.getLeft().getRankDiff('l') == 1) || (side == 'r' && x.getRight().getRankDiff('r') == 1)) {
			return iCaseTwoRebalance(x, side);
		}
		// case 3, the only remaining option
		return iCaseThreeRebalance(x, side);
	}

	/**
	 * performs rebalancing after insertion, case 1
	 * 
	 * @param the "problematic" node (the one with the invalid rank difference)
	 * @return 1+number of rebalances done after (in case problem moved up)
	 * @Complexity O(logn) worst case, O(1) amortised, as in class.
	 */
	private int iCaseOneRebalance(WAVLNode x) {
		x.promote();
		return 1 + insertRebalance(x.getParent());
	}

	/**
	 * rebalances after insertion, case 2. also handles demotions
	 * 
	 * @param x    the node that needs to be rotated
	 * @param side which of x's children need to be rotated with it
	 * @return 2, 2 rebalance operations (demote and rotate)
	 */
	private int iCaseTwoRebalance(WAVLNode x, char side) {
		x.demote();
		if (side == 'l') {
			rotateRight(x);
		} else {
			rotateLeft(x);
		}
		return 2;
	}

	/**
	 * rebalances after insertion, case 3. also handles demotions
	 * 
	 * @param x    the node that needs to be double rotated
	 * @param side which direction (in terms of symmetry) needs to be rotated
	 * @return 5, 5 rebalance operations (2 demotes, 1 promote, 2 rotations)
	 */
	private int iCaseThreeRebalance(WAVLNode x, char side) {
		x.demote();
		if (side == 'l') {
			x.getLeft().demote();
			x.getLeft().getRight().promote();
			rotateLeft(x.getLeft());
			rotateRight(x);
		} else {
			x.getRight().demote();
			x.getRight().getLeft().promote();
			rotateRight(x.getRight());
			rotateLeft(x);
		}
		return 5;
	}

	/**
	 * performs the rebalancing of the tree after deletion calls appropriate helper
	 * methods
	 * 
	 * @param x the parent of the node that was deleted
	 * @return the number of rebalance steps taken
	 * @pre x.parent is not OUTERNODE
	 */
	private int deleteRebalance(WAVLNode x) {
		if (x == null) { // should only happen if we've reached the root
			return 0;
		}
		if (x.getRank() == 1 && x.isLeaf()) {// x is leaf
			return dCaseOneRebalance(x); // demotion, equivalent to other type of case-one
		}
		int ldiff = x.getRankDiff('l');
		int rdiff = x.getRankDiff('r');
		char side = ldiff == 3 ? 'r' : 'l'; // choose which side we work on
		if (Math.max(ldiff, rdiff) < 3) {
			return 0; // tree is valid WAVL, no rank 2 leaf, no rank diff>=3
		}
		assert Math.max(ldiff, rdiff) == 3;
		if (Math.min(ldiff, rdiff) == 2) {
			return dCaseOneRebalance(x);
		}
		assert Math.min(ldiff, rdiff) == 1;
		// x is confirmed as (3,1) node
		int[] grandChildDiffs = checkDiffs(x, side);
		if (grandChildDiffs[0] == 2 && grandChildDiffs[1] == 2) {
			return dCaseTwoRebalance(x, side);
		}
		// one diff or more isn't 2
		if (grandChildDiffs[1] == 1) {
			return dCaseThreeRebalance(x, side);
		}
		assert grandChildDiffs[1] == 2; // only option remaining
		return dCaseFourRebalance(x, side);
	}

	/**
	 * checks the differences for delete rebalancing as shown in the WAVL
	 * presentation, slide 47 the returned array stores the "outer" grandchild diff
	 * (same side as child) in index 1, and the "inner" grandchild diff (opposite
	 * from child) in index 0 e.g. if side='r', then outer is x.right.right and
	 * inner is x.right.left
	 * 
	 * @param x    the grandparent to check
	 * @param side which side's children need to be checked
	 * @return the differences in an array
	 */
	private int[] checkDiffs(WAVLNode x, char side) {
		int[] diffs = new int[2];
		if (side == 'r') {
			diffs[0] = x.getRight().getRankDiff('l');
			diffs[1] = x.getRight().getRankDiff('r');
		} else {
			diffs[0] = x.getLeft().getRankDiff('r');
			diffs[1] = x.getLeft().getRankDiff('l');
		}
		return diffs;
	}

	/**
	 * performs rebalance after deletion, case 1
	 * 
	 * @param x the problematic node (the one which has the illegal rank difference)
	 * @return 1 + the number of rebalance steps taken after, in case of non
	 *         terminal demotion.
	 * @Complexity O(logn) worst case, O(1) amortised (as shown in class)
	 */
	private int dCaseOneRebalance(WAVLNode x) {
		x.demote();

		return 1 + deleteRebalance(x.getParent());
	}

	/**
	 * performs rebalance after deletion, case 2
	 * 
	 * @param x    the problematic node
	 * @param side the side that needs to be demoted (not the 3 rank diff side)
	 * @return 2 + the number of rebalance steps taken after, in case of non
	 *         terminal demotion.
	 * @Complexity O(logn) worst-case, O(1) amortised (as shown in class)
	 */
	private int dCaseTwoRebalance(WAVLNode x, char side) {
		x.demote();
		if (side == 'r') {
			x.getRight().demote();
		} else {
			x.getLeft().demote();
		}
		return 2 + deleteRebalance(x.getParent());

	}

	/**
	 * performs rebalance after deletion, case 3 also handles demotions
	 * 
	 * @param x    the problematic node
	 * @param side the side that needs to be rotated (not the 3 rank diff side)
	 * @return 3 the number of rebalance steps.
	 * @Complexity O(1)
	 */
	private int dCaseThreeRebalance(WAVLNode x, char side) {
		x.demote();
		if (side == 'r') {
			x.getRight().promote();
			rotateLeft(x);
		} else {
			x.getLeft().promote();
			rotateRight(x);
		}
		if (x.isLeaf() && x.getRank() == 1) {
			x.demote();
		}
		return 3;
	}

	/**
	 * performs rebalance after deletion, case 4
	 * also handles demotions
	 * @param x    the problematic node
	 * @param side the side that needs to be demoted (not the 3 rank diff side)
	 * @return 7 the number of rebalance steps (3 demotes, 2 promotes, 2 rotations).
	 * @Complexity O(1)
	 *
	 */
	private int dCaseFourRebalance(WAVLNode x, char side) {
		x.demote();
		x.demote();
		if (side == 'r') {
			x.getRight().demote();
			x.getRight().getLeft().promote();
			x.getRight().getLeft().promote();
			rotateRight(x.getRight());
			rotateLeft(x);
		} else {
			x.getLeft().demote();
			x.getLeft().getRight().demote();
			x.getLeft().getRight().demote();
			rotateLeft(x.getLeft());
			rotateRight(x);
		}
		return 7;
	}

	/**
	 * performs right rotation. Does not handle demotions.
	 *
	 * @param x the node to be rotated
	 * @post x is y's right child, y is x's parent's child (same side as x was)
	 */
	private void rotateRight(WAVLNode x) {
		WAVLNode y = x.getLeft();
		if (x.getParent() != null && x.key > x.getParent().key) {
			x.getParent().setRight(y);
		} else if (x.getParent() != null) { // x is left child of its parent
			x.getParent().setLeft(y);
		}
		y.setParent(x.getParent());
		x.setLeft(y.getRight());
		y.setRight(x);
		x.setParent(y);
		x.updateSubtreeSize();
		y.updateSubtreeSize();
		if (x == this.getRoot()) {
			this.root = y;
		}
	}

	/**
	 * performs left rotation, does not handle demotions.
	 *
	 * @param x the node to rotate
	 * @post x is y's left child, y is x's parent's child (same side as x was)
	 */
	private void rotateLeft(WAVLNode x) {
		WAVLNode y = x.getRight();
		if (x.getParent() != null && x.key > x.getParent().key) {
			x.getParent().setRight(y);
		} else if (x.getParent() != null) { // y is left child of its parent
			x.getParent().setLeft(y);
		}
		y.setParent(x.getParent());
		x.setRight(y.getLeft());
		y.setLeft(x);
		x.setParent(y);
		x.updateSubtreeSize();
		y.updateSubtreeSize();
		if (x == this.getRoot()) {
			this.root = y;
		}

	}

	/**
	 * Function to insert the node into the tree. Uses treePosition to find parent
	 * to insert under (y)
	 * 
	 * @Complexity O(treePosition) = O(log n), n # nodes in tree
	 * @param root - WAVLNode root of tree to insert into
	 * @param z    - WAVLNode to insert
	 * @return int, for counting purposes
	 */
	private int treeInsert(WAVLNode root, WAVLNode z) {
		WAVLNode y = treePosition(root, z.getKey()); // parent to inser under
		if (z.getKey() == y.getKey()) { // z is already in the tree
			return -1;
		}
		z.setParent(y); // set z's parent

		// insert z into the right position and return
		if (z.getKey() < y.getKey()) {
			y.setLeft(z);
			return 0;
		} else {
			y.setRight(z);
			return 0;
		}
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were needed. returns -1 if an
<<<<<<< HEAD
	 * item with key k was not found in the tree. ================= First, perform a
	 * treeSearch (according to pseudo-code from slides) If treeSearch return an
	 * OUTER_NODE, then the node isn't in the tree If treeSearch return a normal
	 * node, we save it's parent for rebalancing, then remove(z), and finally
	 * rebalance the tree. remove and rebalance are used as private functions, for
	 * reabability, and to have rebalance count the number of rebalance ops.
	 * 
	 * @Complexity O(remove + rebalance) = O(updateSizeUp + rebalance) = = O(log n +
	 *             rebalance) worst case. Amortized - O(1)
=======
	 * item with key k was not found in the tree.
	 * =================
	 * First, perform a treeSearch (according to pseudo-code from slides)
	 * If treeSearch return an OUTER_NODE, then the node isn't in the tree.
	 * If the node is the root, we remove root. with specific function
	 * If treeSearch return a normal node, we save it's parent for rebalancing,
	 * then remove(z), and finally rebalance the tree.
	 * remove, removeRoot and rebalance are used as private functions,
	 * for reabability, and have rebalance count the number of rebalance ops.
	 * @Complexity O(remove + rebalance) = O(updateSizeUp + rebalance) =
	 * 				= O(log n + rebalance) worst case. Amortized - O(1)
>>>>>>> master
	 * @param k - int key to search for in the tree
	 * @return number of rebalancing operations.
	 */
	public int delete(int k) {
		WAVLNode z = treeSearch(getRoot(), k);
		if (z.getRank() == -1) {
			return -1;
		}
		if (z == getRoot()) {
			removeRoot();
			return deleteRebalance(getRoot());
		} else {
			WAVLNode y = z.getParent();
			remove(z);
			return deleteRebalance(y);
		}
	}

	/**
	 * remove a node from the tree. Three cases: 1) Node is a leaf 2) Node's
	 * successor is his right child 3) Node's successor is not his right child
	 * 
	 * @Complexity O(updateSizeUp) = O(log n) Each option does constant # operations
	 *             + updateSizeUp
	 * @param node
	 */
	private void remove(WAVLNode node) {
		WAVLNode succ;
		// Case 1
		// If leaf of tree, find side of parent and remove
<<<<<<< HEAD
		if (node.getRight().getRank() == -1 && node.getLeft().getRank() == -1) {
			// TODO Replace with IsLeaf() method
=======
		if (node.isLeaf()) {
			//TODO Replace with IsLeaf() method
>>>>>>> master
			removeLeaf(node); // O(1)
		} else { // Is an inner node
			succ = successor(node);
			// Case 2
			if (succ == node.getRight()) { // O(1)
				/*
				 * If succ is node's right child, then we need to: 1) make node's parent the
				 * parent of succ 2) make node's left child the child of succ 3) attach succ to
				 * node's parent based on side 4) update the size of succ (and this will update
				 * the size of parent) 5) set succ.rank to node.rank
				 */
				succ.setParent(node.getParent()); // (1)
				succ.setLeft(node.getLeft()); // (2)
				if (side(node) == 0) { // (3)
					node.getParent().setLeft(succ);
				} else if (side(node) == 1) {
					node.getParent().setRight(succ);
				}
				succ.setRank(node.getRank()); // (5)
				updateSizeUp(succ); // (4)
				// Case 3
			} else {
				/*
				 * If succ isn't node's right child then: 1) If succ has a right child, set it
				 * as the left child of succ's parent ## succ can't have a left child, ##
				 * because then it would be the successor 2) update the sizes starting from
				 * succ's right child ## this ensures that the size succ get's at stage 7 ## is
				 * the correct size. 3) set succ.parent to node.parent 4) set succ.right to
				 * node.right 5) set succ.left to node.left 6) set succ.rank to node.rank 7) set
				 * succ.size to node.size
				 */
				succ.getParent().setLeft(succ.getRight()); // (1)
				updateSizeUp(succ.getParent().getLeft()); // (2)
				succ.setParent(node.getParent()); // (3)
				succ.setRight(node.getRight()); // (4)
				succ.setLeft(node.getLeft()); // (5)
				succ.rank = node.getRank(); // (6)
				succ.size = node.getSubtreeSize(); // (7)
			}
		}
		node = null; // actually deleting the node, to be removed by GC

	}

	/**
<<<<<<< HEAD
	 * Function to remove leaf nodes. Used for readability. If the node has no
	 * parent, we don't do anything, because in remove we make the node null at the
	 * end.
	 * 
=======
	 * Removes the root of the tree.
	 * 4 cases:
	 * 1) The tree is of size 1 - just set the root to an empty node
	 * 2) The root has a left child and no right child -
	 * 			set the left child as the root
	 * 		## There won't be a case where the left child has children -
	 * 			because then an unbalanced tree
	 * 3) The successor for root is the right child:
	 * 		set the left child or right as the left child of root,
	 * 	    and set the new root
	 * 4) Same as case 3 from remove, just that instead of setting the parent,
	 *    set the root of the tree.
	 */
	private void removeRoot() {
		WAVLNode newRoot;
		// Case 1
		if (root.isLeaf()) {
			this.root = new WAVLNode();

		}
		// Case 2
		else if (getRoot().getRight().getRank() == WAVLNode.OUTER_NODE_RANK &&
				getRoot().getLeft().getRank() != WAVLNode.OUTER_NODE_RANK) {
			this.root = this.getRoot().getLeft();
		}
		// Case 3 + 4
		else {
			newRoot = successor(root);
			// Case 3
			if (newRoot == getRoot().getRight()) {
				newRoot.setLeft(getRoot().getLeft());
				this.root = newRoot;
				getRoot().getSubtreeSize();
			}
			// Case 4
			else {
				/*
				 * If newRoot isn't root's right child then:
				 * 1) If newRoot has a right child, set it as the
				 *    left child of newRoot's parent
				 * 		## succ can't have a left child,
				 * 		## because then it would be the successor
				 * 2) update the sizes starting from newRoot's right child
				 * 		## this ensures that the size newRoot get's at stage 7
				 * 	 	## is the correct size.
				 * 3) set newRoot.right to root.right
				 * 4) set newRoot.left to root.left
				 * 5) set newRoot.rank to root.rank
				 * 6) set newRoot.size to root.size
				 * 7) set newRoot as this.root
				 * */
				newRoot.getParent().setLeft(newRoot.getRight()); // (1)
				updateSizeUp(newRoot.getParent().getLeft()); // (2)
				newRoot.setRight(getRoot().getRight()); // (3)
				newRoot.setLeft(getRoot().getLeft()); // (4)
				newRoot.rank = getRoot().getRank(); // (5)
				newRoot.size = getRoot().getSubtreeSize(); // (6)
				this.root = newRoot; // (7)
			}
		}
	}

	/**
	 * Function to remove leaf nodes. Used for readability.
	 * If the node has no parent, we don't do anything, because in remove
	 * we make the node null at the end.
>>>>>>> master
	 * @Complexity O(updateSizeUp) = O(log n)
	 * @param node - the WAVLNode to remove
	 */
	private void removeLeaf(WAVLNode node) {
		switch (side(node)) {
		case 0:
			node.getParent().setLeft(OUTER_NODE);
			updateSizeUp(node.getParent());
			node.setParent(null);
			break;
		case 1:
			node.getParent().setRight(OUTER_NODE);
			updateSizeUp(node.getParent());
			node.setParent(null);
			break;
		default:
			break;
		}
	}

	/**
	 * Find the side on which the node is. Used in deletion to recognize cases
	 * 
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
		if (y.getParent() != null && y.key > y.getParent().key) {
			y.getParent().setRight(x);
		} else if (y.getParent() != null) { // y is left child of its parent
			y.getParent().setLeft(x);
		}
		x.setParent(y.getParent());
		y.setLeft(x.getRight());
		x.setRight(y);
		y.setParent(x);
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

		if (x.getParent() != null && x.key > x.getParent().key) {
			x.getParent().setRight(z);
		} else if (x.getParent() != null) { // y is left child of its parent
			x.getParent().setLeft(z);
		}
		z.setParent(x.getParent());
		x.setRight(z.getLeft());
		z.setLeft(x);
		x.setParent(z);
		return counter + 1;
	}

	/**
	 * Returns the node with the key directly following x. This does not deal with
	 * call successor on the maximum of the tree, because we don't meet this case in
	 * any other operation.
	 *
	 * Implement by peudo-code from class
	 * 
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
	 * Returns the node with the key directly preceding x. This does not deal with
	 * call predecessor on the minimum of the tree, because we don't meet this case
	 * in any other operation.
	 *
	 * Implement by peudo-code from class
	 * 
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
	 * the tree is empty ============= We use the private WAVLNode min to find the
	 * node with the minimal key in the subtree under node, and the return it's
	 * value
	 * 
	 * @Complexity O(log n) n the number of nodes in the tree
	 * @return the value of the node with the minimal key
	 */
	public String min() {
		return min(root).getValue();
	}

	/**
	 * implemented recursively
	 * 
	 * @Complexity O(log n) where n is the number of nodes in the subtree. The worst
	 *             case happens when the min is of depth = height.
	 * @param node the root of the current subtree
	 * @return info of minimal node in tree
	 */
	private WAVLNode min(WAVLNode node) {
		if (node.getLeft() == OUTER_NODE) {
			return node;
		}
		return min(node.getLeft());
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty ============= We use the private WAVLNode max to find the node
	 * with the maximal key in the subtree under node, and the return it's value
	 * 
	 * @Complexity O(log n) n the number of nodes in the tree
	 * @return the value of the node with the maximal key
	 */
	public String max() {
		return max(root).getValue();
	}

	/**
	 * Implemented recursively
	 * 
	 * @Complexity O(log n) where n is the number of nodes in the subtree. The worst
	 *             case happens when the max is of depth = height.
	 * @param node the root of the current subtree
	 * @return info of maximal node in the tree
	 */
	private WAVLNode max(WAVLNode node) {
		if (node.getRight() == OUTER_NODE) {
			return node;
		}
		return max(node.getRight());
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty. ===============
	 * 
	 * @Complexity this.min() = O(log n) successor - called n times, with an
	 *             amortized cost of O(1) per call total cost O(n) amortized total
	 *             cost O(n)
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
	 * respective keys, or an empty array if the tree is empty. ===============
	 * 
	 * @Complexity this.min() = O(log n) successor - called n times, with an
	 *             amortized cost of O(1) per call total cost O(n) amortized total
	 *             cost O(n)
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
	 * Returns the number of nodes in the tree. ================
	 *
	 * @Complexity O(1) given that the size of each node is updated at every insert,
	 *             delete, and rebalance
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
	 * Returns the root WAVL node, or null if the tree is empty =========
	 * 
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
	 * node minimal node's successor ===========
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
	 * Recursivly find the node with the i'th smalles value. If i < x.left.size,
	 * then the i'th smallest node is in the left subtree of x (because it has more
	 * than i nodes). We then need to keep on searching for the i'th smallest node.
	 * If i > x.left.size, then the i'th smallest node is in the right subtree of x.
	 * We then need to account for the x.left.size + 1 (all the nodes in order until
	 * x.right), and seach for the i - x.left.size + 1 index in the rigth subtree.
	 * 
	 * @Complexity O(log n) worst case where n is # of nodes in the tree. Because
	 *             the longest route from root to leaf is log n.
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

	public static int hight(WAVLNode n) {

		if (n == null || n.rank == WAVLNode.OUTER_NODE_RANK) {
			return 0;
		}

		return 1 + Math.max(hight(n.getRight()), hight(n.getLeft()));
	}

	public void print(WAVLNode node) {

		if (root == OUTER_NODE) {
			System.out.println("(XXXXXX)");
			return;
		}

		int height = hight(root); // PROBLEM??
		int width = (int) Math.pow(2, height - 1);

		// Preparing variables for loop.
		List<WAVLNode> current = new ArrayList<WAVLNode>(1), next = new ArrayList<WAVLNode>(2);
		current.add(root);

		final int maxHalfLength = 4;
		int elements = 1;

		StringBuilder sb = new StringBuilder(maxHalfLength * width);
		for (int i = 0; i < maxHalfLength * width; i++)
			sb.append(' ');
		String textBuffer;

		// Iterating through height levels.
		for (int i = 0; i < height; i++) {

			sb.setLength(maxHalfLength * ((int) Math.pow(2, height - 1 - i) - 1));

			// Creating spacer space indicator.
			textBuffer = sb.toString();

			// Print tree node elements
			for (WAVLNode n : current) {

				System.out.print(textBuffer);

				if (n == null) {

					System.out.print("        ");
					next.add(null);
					next.add(null);

				} else {

					System.out.printf("(" + Integer.toString(n.key) + "," + Integer.toString(n.rank) + ")	");
					next.add(n.getLeft());
					next.add(n.getRight());

				}

				System.out.print(textBuffer);

			}

			System.out.println();
			// Print tree node extensions for next level.
			if (i < height - 1) {

				for (WAVLNode n : current) {

					System.out.print(textBuffer);

					if (n == null)
						System.out.print("        ");
					else
						System.out.printf("%s      %s", n.getLeft() == null ? " " : "/",
								n.getRight() == null ? " " : "\\");

					System.out.print(textBuffer);

				}

				System.out.println();

			}

			// Renewing indicators for next run.
			elements *= 2;
			current = next;
			next = new ArrayList<WAVLNode>(elements);

		}

	}
	public static void notmain3(String[] args) {
		WAVLTree t = new WAVLTree();
		WAVLNode a = t.new WAVLNode(2, "a", t.root, t.OUTER_NODE, t.OUTER_NODE);
		WAVLNode b = t.new WAVLNode(1, "b", a, t.OUTER_NODE, t.OUTER_NODE);
		a.setLeft(b);
		WAVLNode c = t.new WAVLNode(6, "c", a, t.OUTER_NODE, t.OUTER_NODE);
		WAVLNode d = t.new WAVLNode(8, "d", c, t.OUTER_NODE, t.OUTER_NODE);
		WAVLNode e = t.new WAVLNode(4, "e", c, t.OUTER_NODE, t.OUTER_NODE);
		c.setRight(d);
		c.setLeft(e);
		a.setRight(c);
		System.out.format("father is %s, right is %s, left is %s %n %n", a.value,a.getRight().value,a.getLeft().value);
		System.out.format("one row down: from left to right %s %s %s %s %n", a.getLeft().getLeft().value,
				a.getLeft().getRight().value, a.getRight().getLeft().value, a.getRight().getRight().value);
		t.rotateLeft(a);
		t.root = c;
		WAVLNode r = t.root;
		System.out.format("father is %s, right is %s, left is %s %n %n", t.root.value, t.root.getRight().value,t.root.getLeft().value);
		System.out.format("one row down: from left to right %s %s %s %s %n", r.getLeft().getLeft().value,
			r.getLeft().getRight().value, r.getRight().getLeft().value, r.getRight().getRight().value);
		t.rotateRight(c);
		t.root = a;
		r = t.root;
		System.out.format("father is %s, right is %s, left is %s %n %n", t.root.value, t.root.getRight().value,t.root.getLeft().value);
		System.out.format("one row down: from left to right %s %s %s %s %n", r.getLeft().getLeft().value,
			r.getLeft().getRight().value, r.getRight().getLeft().value, r.getRight().getRight().value);
		
		
	}
	public static void notmain2(String[] args) {
		WAVLTree t = new WAVLTree();
	       while (true) {
	           System.out.println("(1) Insert");
	           System.out.println("(2) Delete");
	           System.out.println("(3) Break");


	           try {
	               BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	               String s = bufferRead.readLine();

	               if (Integer.parseInt(s) == 1) {
	                   System.out.print("Value to be inserted: ");
	                   int key =Integer.parseInt(bufferRead.readLine());
	                   t.insert(key, "AMEN");
	               }
	               else if (Integer.parseInt(s) == 2) {
	                   System.out.print("Value to be deleted: ");
	                   int key =Integer.parseInt(bufferRead.readLine());
	                   t.delete(key);
	               }
	               else if (Integer.parseInt(s) == 3) {
	            	   break;
	               }
	               else {
	                   System.out.println("Invalid choice, try again!");
	                   continue;
	               }
	               
	               t.print(t.root);
	           }
	           catch(IOException e) {
	               e.printStackTrace();
	           }
	       }
	}
	public static void notmain(String[] args) {
		int numOfTests = 1000;
		int maxOperationsInEachTest = 100;
		WAVLTester_Tamir tester = new WAVLTester_Tamir(maxOperationsInEachTest);
		for (int i = 0; i < numOfTests; ++i) {
			System.out.println(tester.RunNewTest());
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
			this.setParent(parent);
			this.setRight(right);
			this.setLeft(left);
			this.rank = rank;
			this.size = this.getSubtreeSize();
		}

		/**
		 * Calculates the difference between the node and node.side (right or left)
		 * 
		 * @param side the side to check
		 * @return the difference
		 * @Complexity O(1)
		 */
		public int getRankDiff(char side) {
			assert this.getRank() != OUTER_NODE_RANK;
			assert (this.getLeft() != null && this.getRight() != null);
			if (side == 'r')
				return getRank() - getRight().getRank();
			return getRank() - getLeft().getRank();
		}

		/**
		 * @pre not called on OUTER_NODE
		 * @return whether node is leaf
		 */
		public boolean isLeaf() {
			// TODO Auto-generated method stub
<<<<<<< HEAD
			return getRight().getRank() == OUTER_NODE_RANK && getLeft().getRank() == OUTER_NODE_RANK;
=======
			return getLeft().getRank() == OUTER_NODE_RANK && getRight().getRank() == OUTER_NODE_RANK;
>>>>>>> master
		}

		/**
		 * @deprecated constructor for building a WAVLNode item with only key and value
		 *             DEPRECATED: should use constructor with left and right children
		 *             (so we can use OUTER_NODE for left and right)
		 * @param key
		 * @param value
		 */
		public WAVLNode(int key, String value) {
			this(key, value, null, null, null, 0);
		}

		/**
		 * The constructor for adding with left and right children and parent. Use this
		 * constructor for adding a leaf to the tree.
		 * 
		 * @param parent the parent of the added leaf (insertion point)
		 * @param key    node's key
		 * @param value  node's value (info)
		 * @param right  should be OUTER_NODE when adding a leaf
		 * @param left   should be OUTER_NODE when adding a leaf
		 */
		public WAVLNode(int key, String value, WAVLNode parent, WAVLNode right, WAVLNode left) {
			this(key, value, parent, right, left, 0);
		}

		/**
		 * The default constructor for the WAVLNode class. Constructs an external node.
		 */
		public WAVLNode() {
			this(OUTER_NODE_KEY, "outerNODE", null, null, null, OUTER_NODE_RANK); //TODO: revert!
		}

		/**
		 * All the getters and setters for WAVLNode. All run in O(1).
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

		public void setLeft(WAVLNode left) {
			this.left = left;
		}

		public WAVLNode getRight() {
			return right;
		}

		public void setRight(WAVLNode right) {
			this.right = right;
		}

		public WAVLNode getParent() {
			return parent;
		}

		public void setParent(WAVLNode parent) {
			this.parent = parent;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		/**
		 * promote and demote WAVLNodes in tree.
		 * 
		 * @Complexity O(1).
		 */
		public void promote() {
			if (this.getRank() != -1) {				
				setRank(getRank() + 1);
			}
		}

		public void demote() {
			if (this.getRank() != -1) {
				setRank(getRank() - 1);				
			}
		}

		/**
		 * @Complexity O(1)
		 * @return boolean true - if node.rank != OUTER_NODE_RANK.
		 */
		public boolean isInnerNode() {
			return rank != OUTER_NODE_RANK;
		}

		/**
		 * This function is deterministic and not recursive. We assume that in
		 * insertions and deletions from trees, updating the sizes is done from the
		 * bottom up, and that each new node inserted has static OUTER_NODEs as
		 * children, and a size of 0. In addition, if the tree is set, all sizes should
		 * be up-to-date. =================
		 * 
		 * @Complexity O(1)
		 * @return the size of the subtree that has this as its root, including this.
		 */
		public int getSubtreeSize() {
			if (rank == OUTER_NODE_RANK) {
				return 0;
			}
			int lsize = 0;
			int rsize = 0;
			if (getLeft() != null) {
				lsize = getLeft().size;
			}
			if (getRight() != null) {
				rsize = getRight().size;
			}
			return rsize + lsize + 1;
		}

		/**
		 * updates the Node's subtree size in-place. Should be used after changes to the
		 * tree (insert, delete, rebalance). As explained in getSubtreeSize(), we assume
		 * that the update of sizes will be done from the bottom-up. ==================
		 * 
		 * @Complexity O(getSubtreeSize()) = O(1)
		 * @return void. update the size field of the WAVLNode object
		 */
		public void updateSubtreeSize() {
			size = getSubtreeSize();
		}
	}

}
