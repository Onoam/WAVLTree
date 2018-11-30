/**
 *
 * WAVLTree
 *

 * An implementation of a WAVL Tree. (Haupler, Sen & Tarajan ‘15)
 *
 */
//test2.1
public class WAVLTree {
    private WAVLNode root;
    private int height;

    public WAVLTree() {
        this.root = new WAVLNode();
    }

    public WAVLTree(WAVLNode root){
        this.root = root;
        this.height = root.height;
    }

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		return false; // to be replaced by student code
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
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 */
	public String min() {
		return "42"; // to be replaced by student code
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 */
	public String max() {
		return "42"; // to be replaced by student code
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 */
	public int[] keysToArray() {
		int[] arr = new int[42]; // to be replaced by student code
		return arr; // to be replaced by student code
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		String[] arr = new String[42]; // to be replaced by student code
		return arr; // to be replaced by student code
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 */
	public int size() {
		return 42; // to be replaced by student code
	}

	/**
	 * public WAVLNode getRoot()
	 *
	 * Returns the root WAVL node, or null if the tree is empty
	 *
	 */
	public WAVLNode getRoot() {
		return null;
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
		public static final String OUTER_NODE_VALUE = "";
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
		 * The default constructor for the WAVLNode class.
		 * Constructs an external node.
		 */
		public WAVLNode() {
			this(OUTER_NODE_KEY, OUTER_NODE_VALUE, null, null, null, OUTER_NODE_RANK);
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

		public boolean isInnerNode() {
			return rank == OUTER_NODE_RANK;
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