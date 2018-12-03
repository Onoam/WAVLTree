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
	public static final WAVLNode OUTER_NODE = new WAVLNode();

	public WAVLTree(WAVLNode root) {
		this.root = root;
	}

	public WAVLTree() {
		this(new WAVLNode());
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 **/
	public boolean empty() {
		/**
		 * Returns false if root is an inner node, and true if it is an outer leaf. Run
		 * time O(1)
		 **/
		return !this.root.isInnerNode();
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree otherwise,
	 * returns null
	 */
	public String search(int k) {
		return "42"; // to be replaced by student code
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
		return 42; // to be replaced by student code
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
		return 42; // to be replaced by student code
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
	 * @param x WAVLNode A node in the tree
	 * @return y The WAVLNode with the following key
	 */
	private WAVLNode successor(WAVLNode x) {
		if (x.getRight().getRank() != -1) {
			return min(x.right);
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
	 * @param x WAVLNode A node in the tree
	 * @return y The WAVLNode with the preceding key
	 */
	private WAVLNode predecessor(WAVLNode x) {
		if (x.getLeft().getRank() != -1) {
			return max(x.left);
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
	 */
	public String min() {
		return min(root);
	}

	/**
	 * implemented recursively
	 * 
	 * @param node the root of the current subtree
	 * @return info of minimal node in tree
	 */
	private String min(WAVLNode node) {
		if (node.left == OUTER_NODE) {
			return node.getValue();
		}
		return min(node.left);
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 */
	public String max() {
		return max(root);
	}

	/**
	 * implemented recursively
	 * 
	 * @param node the root of the current subtree
	 * @return info of maximal node in the tree
	 */
	private String max(WAVLNode node) {
		if (node.right == OUTER_NODE) {
			return node.getValue();
		}
		return max(node.right);
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 */
	public int[] keysToArray() {
		int[] arr = new int[root.size];
		int[] count = new int[1];
		if (!this.empty()) {
			keysToArrayRec(this, arr, count);
		}
		return arr;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		String[] arr = new String[root.size];
		int[] count = new int[1];
		if (!this.empty()) {
			infoToArrayRec(this, arr, count);
		}
		return arr; // to be replaced by student code
	}

	/**
	 * Used recursive inner function. Needs to pass a tree, the array that we're
	 * populating with keys and a coutner. The counter is to validate that we're
	 * adding values in the right places. Stopping conditions - if we reach an empty
	 * tree, we return without changing the array (hope this works like Python or
	 * JS) Otherwiase, we first run on the left side of the root, then add the root
	 * to the array and move the counter + 1, then move to the right. Complexity -
	 * We need to go through all the nodes in the tree (n) and we do a set number of
	 * O(1) operations for each, thus O(n)
	 */
	private String[] infoToArrayRec(WAVLTree tree, String[] array, int[] counter) {
		if (!tree.empty()) {
			return;
		} else {
			WAVLTree L = new WAVLTree(getRoot().getLeft());
			infoToArrayRec(L, array, counter);
			array[counter[0]] = getRoot().getValue();
			counter[0] = counter[0] + 1;
			WAVLTree R = new WAVLTree(getRoot().getLeft());
			infoToArrayRec(R, array, counter);
		}
	}

	/**
	 * Used recursive inner function. Needs to pass a tree, the array that we're
	 * populating with keys and a coutner. The counter is to validate that we're
	 * adding values in the right places. Stopping conditions - if we reach an empty
	 * tree, we return without changing the array (hope this works like Python or
	 * JS) Otherwiase, we first run on the left side of the root, then add the root
	 * to the array and move the counter + 1, then move to the right. Complexity -
	 * We need to go through all the nodes in the tree (n) and we do a set number of
	 * O(1) operations for each, thus O(n)
	 */
	private String[] keysToArrayRec(WAVLTree tree, String[] array, int[] counter) {
		if (!tree.empty()) {
			return;
		} else {
			WAVLTree L = new WAVLTree(getRoot().getLeft());
			keysToArrayRec(L, array, counter);
			array[counter[0]] = getRoot().getKey();
			counter[0] = counter[0] + 1;
			WAVLTree R = new WAVLTree(getRoot().getRight());
			keysToArrayRec(R, array, counter);
		}
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 */
	public int size() {
		/**
		 * @return the size of the root node. Complexity O(1), given that the size of
		 *         each node is updated at every insert, delete, and rebalance
		 */
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
	 *
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
	 *
	 */

	public String select(int i) {
		return null;
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

		public WAVLNode(int key, String value) {
			this(key, value, null, null, null, 1);
		}

		/**
		 * The default constructor for the WAVLNode class. Constructs an external node.
		 */
		public WAVLNode() {
			this(OUTER_NODE_KEY, null, null, null, null, OUTER_NODE_RANK);
		}

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

		public void promote() {
			setRank(getRank() + 1);
		}

		public void demote() {
			setRank(getRank() - 1);
		}

		public boolean isInnerNode() {
			return rank != OUTER_NODE_RANK;
		}

		/**
		 * 
		 * @return the size of the subtree that has this as its root, including this.
		 */
		public int getSubtreeSize() {
			int lsize = 0;
			int rsize = 0;
			if (left != null) {
				lsize = left.size;
			}
			if (right != null) {
				rsize = right.size;
			}
			return rsize + lsize + 1; //
		}
	}

}
