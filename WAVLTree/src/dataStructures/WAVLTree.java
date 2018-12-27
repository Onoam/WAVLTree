package dataStructures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * WAVLTree
 *
 *
 * An implementation of a WAVL Tree. (Haupler, Sen & Tarajan ‘15)
 *
 * @author Eytan-c, Onoam
 *
 */
@SuppressWarnings("WeakerAccess")
public class WAVLTree {
	private WAVLNode root;
	public final WAVLNode OUTER_NODE = new WAVLNode();

	public WAVLTree(WAVLNode root) {
		this.root = root;
	}

	public WAVLTree() {
		this.root = OUTER_NODE;
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
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
	 *
	 * @Complexity O(treeSearch) = O(log n)
	 * @param k int key to search for
	 * @return value of node with key k, or null if it doesn't exists
	 */
	public String search(int k) {
		WAVLNode item = treeSearch(getRoot(), k);
		if (item == null) {
			return null;
		}
		if (item.getRank() == -1) {
			return null;
		} else {
			return item.getValue();
		}
	}

	/**
	 * Implementation of Tree-Search from slides, done deterministically
	 * @Complexity O(log n) - where n # nodes in tree (tree height)
	 * @param x WAVLNode
	 * @param k int the key to look for
	 * @return WAVLNode - if not found, then returns a, OUTER_NODE
	 */
	private WAVLNode treeSearch(WAVLNode x, int k) {
		if (x == null) {
			return OUTER_NODE;
		}
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
	 * Implementation of Tree-Position from slides. Done deterministically.
	 * @Complexity O(log n) - where n # nodes in tree (tree height)
	 * @param x WAVLNode
	 * @param k int the key to look for
	 * @return WAVLNode - return the last node encountered, or an existing node
	 */
	private WAVLNode treePosition(WAVLNode x, int k) {
		WAVLNode y = OUTER_NODE;
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
	 *
	 * First, create a new WAVLNode with OUTER_NODEs as children.
	 * Check if the tree is empty, and if true - set x to root and return 0
	 * If tree isn't empty, use treeInsert to insert the node.
	 * If treeInsert found that the key is already in the tree - return -1
	 * Else, x was already inserted in treeInsert, so we need to
	 * updateSizeUp(x), and then rebalance(x)
	 * @Complexity O(max{treeInsert, updateSizeUp, insertRebalance}) =
	 * 				O(log n) WC, O(1) amortized
	 * @param k int key of WAVLNode to insert
	 * @param i String info of WAVLNode to insert
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
	 * This method is called after inserting.
	 * Checks which rebalance case we are in and calls the appropriate
	 * rebalance helper-method. (Cases based on slides shown in class)
	 *
	 * @Complexity O(log n) worst case, O(1) amortised, as in class.
	 * @param x the parent of the node that was inserted.
	 * @return the number of rebalance steps
	 */
	private int insertRebalance(WAVLNode x) {
		if (x == null) { // should only happen if we've reached the root //TODO change to OUTER_NODE instaed of null?
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
		if ((side == 'l' && x.getLeft().getRankDiff('l') == 1) ||
				(side == 'r' && x.getRight().getRankDiff('r') == 1)) {
			return iCaseTwoRebalance(x, side);
		}
		// case 3, the only remaining option
		return iCaseThreeRebalance(x, side);
	}

	/**
	 * performs rebalancing after insertion, case 1 from slides
	 *
	 * @param x the "problematic" node (the one with the invalid rank difference)
	 * @return 1+number of rebalances done after (in case problem moved up)
	 * @Complexity O(log n) worst case, O(1) amortised, as in class.
	 */
	private int iCaseOneRebalance(WAVLNode x) {
		x.promote();
		return 1 + insertRebalance(x.getParent());
	}

	/**
	 * rebalances after insertion, case 2 from slides. also handles demotions
	 *
	 * @Complexity O(1)
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
	 * rebalances after insertion, case 3 from slides. also handles demotions
	 *
	 * @Complexity O(1)
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
	 * performs the rebalancing of the tree after deletion
	 * calls appropriate helper methods based on cases from slides
	 *
	 * @Complexity O(log n) worst case, O(1) amortized, as in class
	 * @param x the parent of the node that was deleted
	 * @return the number of rebalance steps taken
	 * @pre x.parent is not OUTER_NODE
	 */
	private int deleteRebalance(WAVLNode x) {
		if (x == null) { // should only happen if we've reached the root //TODO shouldn't be OUTER_NODE instead of null?
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
		int[] grandChildDiffs = new int[2];
		grandChildDiffs = checkDiffs(x, side);

		
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
	 * presentation, slide 47.
	 * The returned array stores the "outer" grandchild diff
	 * (same side as child) in index 1, and the "inner" grandchild diff
	 * (opposite from child) in index 0 e.g. if side='r', then outer is
	 * x.right.right and inner is x.right.left
	 *
	 * @Complexity O(1)
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
	 * performs rebalance after deletion, case 1 from slides
	 *
	 * @param x the problematic node (the one which has the illegal rank difference)
	 * @return 1 + the number of rebalance steps taken after, in case of non
	 *         terminal demotion.
	 * @Complexity O(log n) worst case, O(1) amortised (as shown in class)
	 */
	private int dCaseOneRebalance(WAVLNode x) {
		x.demote();

		return 1 + deleteRebalance(x.getParent());
	}

	/**
	 * performs rebalance after deletion, case 2 from slides
	 *
	 * @param x    the problematic node
	 * @param side the side that needs to be demoted (not the 3 rank diff side)
	 * @return 2 + the number of rebalance steps taken after, in case of non
	 *         terminal demotion.
	 * @Complexity O(log n) worst-case, O(1) amortised (as shown in class)
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
	 * performs rebalance after deletion, case 3 from slides
	 * also handles demotions
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
	 * performs rebalance after deletion, case 4 from slides
	 * also handles demotions
	 *
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
			x.getLeft().getRight().promote();
			x.getLeft().getRight().promote();
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
		y.getRight().setParent(x);
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
		y.getLeft().setParent(x);
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
	 * @param root WAVLNode root of tree to insert into
	 * @param z    WAVLNode to insert
	 * @return int, for counting purposes
	 */
	private int treeInsert(WAVLNode root, WAVLNode z) {
		WAVLNode y = treePosition(root, z.getKey()); // parent to insert under
		if (z.getKey() == y.getKey()) { // z is already in the tree
			return -1;
		}
		z.setParent(y); // set z's parent

		// insert z into the right position, update sizes of nodes and return
		if (z.getKey() < y.getKey()) { // insert to the left of y
			y.setLeft(z);
			updateSizeUp(y);
			return 0;
		} else { // insert to the right of y
			y.setRight(z);
			updateSizeUp(y);
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
	 *
	 * First, perform a treeSearch (according to pseudo-code from slides)
	 * If treeSearch return an OUTER_NODE, then the node isn't in the tree.
	 * If the node is the root, we remove root. with specific function
	 * If treeSearch return a normal node, we save it's parent for rebalancing,
	 * then remove(z), and finally rebalance the tree.
	 * remove, removeRoot and rebalance are used as private functions,
	 * for readability, and have rebalance count the number of rebalance ops.
	 * @Complexity O(remove + rebalance) = O(updateSizeUp + rebalance) =
	 * 				= O(log n + rebalance) worst case. Amortized - O(1)
	 * @param k int key to search for in the tree
	 * @return number of rebalancing operations.
	 */
	public int delete(int k) {
		WAVLNode z = treeSearch(getRoot(), k);
		if (z.getRank() == -1) {
			return -1;
		}
		if (z == getRoot()) {
			WAVLNode reb = removeRoot();
			if (reb == OUTER_NODE) {				
				return 0;
			}
			return deleteRebalance(reb);
		} else {
			WAVLNode y = remove(z);
			return deleteRebalance(y);
		}
	}

	/**
	 * remove a node from the tree, dealing with different edge cases.
	 * return the node from which to start rebalancing
	 *
	 * @Complexity Worst case O(updateSizeUp) = O(log n)
 	 * @param node node to be removed
	 * @return WAVLNode ret - the node that the rebalancing starts from
	 */
	private WAVLNode remove(WAVLNode node) {
		WAVLNode parent = node.getParent();
		WAVLNode ret;
		// Case 1 - node to remove is a leaf
		if (node.isLeaf()){
			ret = parent;
			removeLeaf(node);
		}
		// Case 2 - node to remove is unary with right child
		else if (node.getLeft() == OUTER_NODE) {
			parent.setChild(node.getRight(), side(node));
			node.getRight().setParent(parent);
			updateSizeUp(parent);
			ret = parent;
		}
		// Case 3 - node to remove is unary with left child
		else if (node.getRight() == OUTER_NODE) {
			parent.setChild(node.getLeft(), side(node));
			node.getLeft().setParent(parent);
			updateSizeUp(parent);
			ret = parent;
		}
		// Case 4 - node to remove is binary, successor(node) == node.right
		else  if (node == successor(node).getParent()){
			ret = successor(node);
			successorSwap(node);
		}
		// Case 5 - node to remove is binary, successor(node) != node.right
		else {
			ret = successor(node).getParent(); // TODO: nullcheck?
			successorSwap(node);
		}
		return ret; // node to return for rebalancing
	}

	/**
	 * //TODO added detailed operation description
	 * @param node node to swap with successor
	 * @Complexity O(1)
	 */
	private void successorSwap(WAVLNode node) {
		WAVLNode succ = successor(node);
		if (succ == this.getRoot()) {
			removeRoot();
		}
		else {
			remove(succ);
		}
		succ.setRank(node.getRank());
		succ.setLeft(node.getLeft());
		succ.getLeft().setParent(succ);
		succ.setRight(node.getRight());
		succ.getRight().setParent(succ);
		if (node != root && node == node.getParent().getLeft()){
			node.getParent().setLeft(succ);
		}
		else  if (node != root){
			node.getParent().setRight(succ);
		}
		succ.setParent(node.getParent());
		succ.updateSubtreeSize();
		if (node == root) {
			root = succ;
		}
	}

	/**
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
	 * @return WAVLNode to start rebalancing from
	 */
	private WAVLNode removeRoot() {
		WAVLNode newRoot;
		WAVLNode ret;
		WAVLNode currRoot = this.getRoot();
		// Case 1
		if (root.isLeaf()) {
			this.root = OUTER_NODE;
			return root;

		}
		// Case 2
		else if (getRoot().getRight().getRank() == WAVLNode.OUTER_NODE_RANK &&
				getRoot().getLeft().getRank() != WAVLNode.OUTER_NODE_RANK) {
			getRoot().getLeft().setParent(getRoot().getParent());
			this.root = this.getRoot().getLeft();
			return root;
		}
		// Case 3 + 4
		else {
			newRoot = successor(root);
			ret = newRoot.getParent(); //This is the node we need to rebalance on
			// Case 3
			if (newRoot == getRoot().getRight()) {
				newRoot.setLeft(getRoot().getLeft());
				newRoot.setParent(getRoot().getParent());
				this.root = newRoot;
				getRoot().getSubtreeSize();
			}
			// Case 4
			else {
				/*
				 * If newRoot isn't root's right child then:
				 * perform successor swap
				 *
				 * */
				successorSwap(root);
				this.root = newRoot; // (7)
			}
		}
		currRoot = null; //TODO check if this line can be deleted
		return ret;
	}

	/**
	 * Function to remove leaf nodes. Used for readability.
	 * We check the side of node, and accordingly:
	 * Set the child of of node.parent to OUTER_NODE,
	 * and updateSizeUp from node.parent.
	 * @Complexity O(updateSizeUp) = O(log n)
	 * @param node the WAVLNode to remove
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
	 * Find the side on which the node is.
	 * Used in deletion to recognize cases
	 * @Complexity O(1)
	 * @param node WAVLNODE to check what side of child it is.
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
	 * If node isn't the root of the tree, we go up with a while loop.
	 * Logic of loop:
	 * 		We find the parent of the node.
	 * 		While we haven't reached the root, we set node to parent
	 * 		(going up the tree) and then update the size of node, and
	 * 		find the next parent.
	 * 	Once we reached the root, we exit the loop without
	 * 	updating the size of root, so we update the size of the last parent.
	 * @Complexity worst case O(log n) where n is # nodes in tree (height of tree)
	 * @param node node to update size to
	 */
	private void updateSizeUp(WAVLNode node) {
		node.updateSubtreeSize(); // update the size of the first node

		// If node isn't the root, then we need to go up the tree
		if (node != this.getRoot()) {
			// Find the first parent
			WAVLNode parent = node.getParent();
			// If we reached the root, then we stop.
			// This ensures that we update the the root last, and then stop.
			while (parent != this.getRoot()) {
				node = parent; // go up the tree
				node.updateSubtreeSize(); // update the size of the node //TODO make sure this doesn't cause problems
				parent = node.getParent(); // find the next parent
			}
			// the last parent is root, update the size of parent
			parent.updateSubtreeSize();
		}
	}

	/**
	 * Returns the node with the key directly following x.
	 * This does not deal with call successor on the maximum of the tree,
	 * because we don't meet this case in any other operation.
	 *
	 * Implement by pseudo-code from class
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
	 * Returns the node with the key directly preceding x.
	 * This does not deal with call predecessor on the minimum of the tree,
	 * because we don't meet this case in any other operation.
	 *
	 * Implement by pseudo-code from class
	 *
	 * @Complexity O(log n) worst case, where n is the number of nodes in the tree
	 * @param x WAVLNode A node in the tree
	 * @return y The WAVLNode with the preceding key
	 */
	private WAVLNode predecessor(WAVLNode x) { //TODO method is never used - delete?
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
	 *
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
	 *
	 * @Complexity O(log n) where n is the number of nodes in the subtree. The worst
	 *             case happens when the min is of depth = height.
	 * @param node the root of the current subtree
	 * @return minimal node in tree
	 */
	private WAVLNode min(WAVLNode node) {
		if (node == OUTER_NODE || node.getLeft() == OUTER_NODE) {
			return node;
		}
		return min(node.getLeft());
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 *
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
	 *
	 * @Complexity O(log n) where n is the number of nodes in the subtree. The worst
	 *             case happens when the max is of depth = height.
	 * @param node the root of the current subtree
	 * @return maximal node in the tree
	 */
	private WAVLNode max(WAVLNode node) {
		if (node == OUTER_NODE || node.getRight() == OUTER_NODE) {
			return node;
		}
		return max(node.getRight());
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 *
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
	 *
	 * @Complexity
	 * 		this.min() = O(log n)
	 * 		successor - called n times, with an amortized cost of O(1) per call
	 * 			total cost O(n) amortized
	 * 		total cost O(n)
	 * @return arr String[] sorted array of the keys of tree nodes
	 */
	public String[] infoToArray() {
		String[] arr = new String[root.size];
		if (!this.empty()) {
			WAVLNode current = this.min(root);
			int i = 0;
			while (i < root.size) {
				arr[i] = current.getValue();
				current = successor(current);
				i++;
			}
		}
		return arr;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * @Complexity O(1) given that the size of each node is updated at every insert,
	 *             delete, and rebalance
	 * @return the size of the root node.
	 */
	public int size() {
		if (empty()) {
			return 0;
		} else {
			return getRoot().size;
		}
	}

	/**
	 * public WAVLNode getRoot()
	 *
	 * Returns the root WAVL node, or null if the tree is empty
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
	//TODO delete this
	@SuppressWarnings("RedundantCast")
	private boolean checkRank_rec(WAVLNode node)
	{
		if (node == null || (!node.isInnerNode() && node.getRank() == -1))
			return true;
		
		int leftRank = node.getLeft() != null ? ((WAVLNode)node.getLeft()).getRank() : -1;
		int rightRank = node.getRight() != null ? ((WAVLNode)node.getRight()).getRank() : -1;
		
		boolean isType1_1 = (node.getRank() - leftRank) == 1 && (node.getRank() - rightRank) == 1;
		boolean isType1_2 = (node.getRank() - leftRank) == 1 && (node.getRank() - rightRank) == 2;
		boolean isType2_1 = (node.getRank() - leftRank) == 2 && (node.getRank() - rightRank) == 1;
		boolean isType2_2 = (node.getRank() - leftRank) == 2 && (node.getRank() - rightRank) == 2 && !node.isLeaf();
		if (!isType1_1 && !isType1_2 && !isType2_1 && !isType2_2)
			return false;
		
		return checkRank_rec((WAVLNode)node.getLeft()) && checkRank_rec((WAVLNode)node.getRight());
	}
	/**
	 * public int select(int i)
	 *
	 * Returns the value of the i'th smallest key (return -1 if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key Example
	 * 2: select(size()) returns the value of the node with maximal key Example 3:
	 * select(2) returns the value 2nd smallest minimal node, i.e the value of the
	 * node minimal node's successor
	 *
	 *
	 * @Complexity O(selectNode) = O(log n), where n is # of nodes in the tree.
	 * @param i int index to search for
	 * @return the value of the i'th smallest node
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
	 * Recursively find the node with the i'th smallest value.
	 * If i < x.left.size, then the i'th smallest node is in the left
	 * 		subtree of x (because it has more than i nodes).
	 * 		We then need to keep on searching for the i'th smallest node.
	 * If i > x.left.size, then the i'th smallest node is in the right
	 * 		subtree of x. We then need to account for the x.left.size + 1
	 * 		(all the nodes in order until x.right), and search for the
	 * 		i - x.left.size + 1 index in the right subtree.
	 * @Complexity O(log n) worst case where n is # of nodes in the tree.
	 * 				Because the longest route from root to leaf is log n.
	 * @param x WAVLNode the root of the subtree to find the node in
	 * @param i int the index to search for.
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
	 *
	 * We decided to have each node with pointers for:
	 * key, value, parent node, right child, left child, rank and size
	 * In addition, we created static properties for OUTER_NODEs
	 */
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

		/**
		 * basic constructor for WAVLNode
		 * @param key int the key of the node
		 * @param value String the value of the node
		 * @param parent WAVLNode the parent of the node
		 * @param right WAVLNode right child
		 * @param left WAVLNode left child
		 * @param rank int the rank of the node
		 */
		public WAVLNode(int key, String value, WAVLNode parent, WAVLNode right, WAVLNode left, int rank) {
			this.key = key;
			this.value = value;
			this.setParent(parent);
			this.setRight(right);
			this.setLeft(left);
			this.rank = rank;
			this.size = this.getSubtreeSize();
		}

		//TODO delete this method if deprecated?
		/**
		 * @deprecated constructor for building a WAVLNode item with only key and value
		 *             DEPRECATED: should use constructor with left and right children
		 *             (so we can use OUTER_NODE for left and right)
		 * @param key int
		 * @param value String
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
		 * This means: parent, children, value = null, key, rank = -1 static
		 *
		 */
		public WAVLNode() {
			this(OUTER_NODE_KEY, null, null, null, null, OUTER_NODE_RANK);
		}

		/*
		 * All the getters and setters for WAVLNode.
		 * All run in O(1).
		 */

		/**
		 * getter for key
		 * @Complexity O(1)
		 * @return node's key
		 */
		public int getKey() {
			return key;
		}

		/**
		 * getter for value
		 * @Complexity O(1)
		 * @return node's value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * getter for left child
		 * @Complexity O(1)
		 * @return node's left child
		 */
		public WAVLNode getLeft() {
			return left;
		}

		/**
		 * Setter for left child
		 * @Complexity O(1)
		 * @param left WAVLNode to set as left child
		 */
		public void setLeft(WAVLNode left) {
			this.left = left;
		}

		/**
		 * getter for right child
		 * @Complexity O(1)
		 * @return node's right child
		 */
		public WAVLNode getRight() {
			return right;
		}

		/**
		 * Setter for right child
		 * @Complexity O(1)
		 * @param right WAVLNode to set as right child
		 */
		public void setRight(WAVLNode right) {
			this.right = right;
		}

		/**
		 * wrapper for setLeft, setRight
		 * @Complexity O(1)
		 * @param newChild WAVLNode to set as child
		 * @param side 0 for left, 1 for right
		 */
		public void setChild(WAVLNode newChild, int side) {
			if (side == 0) {
				setLeft(newChild);
			}
			else {
				setRight(newChild);
			}
		}

		/**
		 * getter for parent
		 * @Complexity O(1)
		 * @return node's parent
		 */
		public WAVLNode getParent() {
			return parent;
		}

		/**
		 * Setter for parents
		 * @Complexity O(1)
		 * @param parent WAVLNode to set as parent
		 */
		public void setParent(WAVLNode parent) {
			this.parent = parent;
		}

		/**
		 * getter for rank
		 * @Complexity O(1)
		 * @return node's rank
		 */
		public int getRank() {
			return rank;
		}

		/**
		 * Setter for rank
		 * @Complexity O(1)
		 * @param rank int to set as rank
		 */
		public void setRank(int rank) {
			if (this.getRank() != OUTER_NODE_RANK) {				
				this.rank = rank;
			}
		}

		/**
		 * promote rank of WAVLNodes in tree.
		 *
		 * @Complexity O(1).
		 */
		public void promote() {
			if (this.getRank() != OUTER_NODE_RANK) {
				setRank(getRank() + 1);
			}
		}

		/**
		 * demote rank of WAVLNodes in tree.
		 *
		 * @Complexity O(1).
		 */
		public void demote() {
			if (this.getRank() != OUTER_NODE_RANK) {
				setRank(getRank() - 1);
			}
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
			return getRight().getRank() == OUTER_NODE_RANK && getLeft().getRank() == OUTER_NODE_RANK;
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
		 * updates the Node's subtree size in-place.
		 * Should be used after changes to the tree (insert, delete, rebalance).
		 * As explained in getSubtreeSize(), we assume that the update of
		 * sizes will be done from the bottom-up.
		 *
		 * @Complexity O(getSubtreeSize()) = O(1)
		 */
		public void updateSubtreeSize() {
			size = getSubtreeSize();
		}
	}

}
