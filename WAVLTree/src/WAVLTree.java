import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
	 * * * * * * * * *
	 * Complexity - same as select(). Worst case O(log n)
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
	 * Select the node with key k.
	 * If node doesn't exist, return external node.
	 * Complexity - O(log n). Upper bounded by height of tree.
	 * @param k int - key of node to select
	 * @return current WAVLNode - the node with key k, or external node.
	 */
//	private WAVLNode select(int k) {
//		WAVLNode current = getRoot();
//		WAVLNode parent;
//		while (current.getRank() != -1) {
//			if (k == current.getKey()) {
//				return current;
//			} else if (k < current.getKey()) {
//				parent = current;
//				current = current.getLeft();
//			} else {
//				parent = current;
//				current = current.getRight();
//			}
//		}
//		return current;
//	}

	/**
	 * If this is legal, this can stop us from having 2 functions that do the same thing.
	 * @param k
	 * @return
	 */
//	private WAVLNode[] findNode(int k) {
//		WAVLNode current = getRoot();
//		WAVLNode parent = current.getParent();
//		WAVLNode[] array = new WAVLNode[2];
//		while (current.getRank() != -1) {
//			if (k == current.getKey()) {
//				WAVLNode[0] = current;
//				WAVLNode[1] = parent;
//				return array;
//			} else if (k < current.getKey()) {
//				parent = current;
//				current = current.getLeft();
//			} else {
//				parent = current;
//				current = current.getRight();
//			}
//		}
//		WAVLNode[0] = current;
//		WAVLNode[1] = parent;
//		return array;
//	}

	/**
	 * Finds the WAVLNode underwhich to insert a new WAVLNode with key k.
	 * Complexity - O(log n). Upper bounded by height of tree.
	 * @param k int - the new key that we want to insert
	 * @return parent WAVLNode - the WAVLNode underwhich to insert.
	 */
//	private WAVLNode findInsertParent(int k) {
//		WAVLNode current = getRoot();
//		WAVLNode parent;
//		while (current.getRank() != -1) {
//			if (k == current.getKey()) {
//				return current;
//			} else if (k < current.getKey()) {
//				parent = current;
//				current = current.getLeft();
//			} else {
//				parent = current;
//				current = current.getRight();
//			}
//		}
//		return parent;
//	}

	/**
	 * Implementation of Tree-Search from slides, done deterministicly
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
//		if (x.getRank() == -1 || k == x.getKey()) {
//			return x;
//		} else {
//			if (k < x.getKey()) {
//				return treeSearch(x.getLeft(), k);
//			} else {
//				return treeSearch(x.getRight(), k);
//			}
//		}
	}

	/**
	 * Implementation of Tree-Position from slides. Done deterministicly.
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
	 * ======================
	 *
	 * @param k
	 * @param i
	 * @return
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
				return rebalance(x);
			}
		}
	}

	private int rebalance(WAVLNode x) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 *
	 * @param root
	 * @param z
	 * @return
	 */
	private int treeInsert(WAVLNode root, WAVLNode z) {
		WAVLNode y = treePosition(root, z.getKey());
		if (z.getKey() == y.getKey()) {
			return -1;
		}
		z.parent = y;
		if (z.getKey() < y .getKey()) {
			y.left = z;
			updateSizeUp(y);
			return 0;
		} else {
			y.right = z;
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
	 * ===============
	 *
	 * @param k
	 * @return
	 */
	public int delete(int k) {
		WAVLNode z = treeSearch(getRoot(), k);
		if (z.getRank() == -1) {
			return -1;
		}
		WAVLNode y = successor(z);
		remove(z); // This function deals with the updating the sizes
		return rebalance(y);
	}

	/**
	 *
	 * @param node
	 */
	private void remove(WAVLNode node) {
		WAVLNode succ;
		// If leaf of tree, find side of parent and remove
		if (node.getRight().getRank() == -1 && node.getLeft().getRank() == -1) {
			removeLeaf(node); // This deals with updating the sizes.
		} else { // Is an inner node
			succ = successor(node);
			if (succ == node.getRight()) {
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
				updateSizeUp(succ);
			} else {
				/*
				* If succ isn't node's right child then:
				* 1) if succ has a right child, set it as the left child of succ's parent
				* 2) update succ's parent's size
				* 3) set succ.right and succ.left to node.right and node.left repectively
				* 4) set succ.parent to node.parent
				* 5) set succ's size to node's size
				* (node's size got updated in stage 2, so it is set correctly)
				* */
				succ.getParent().left = succ.getRight();
				updateSizeUp(succ.getParent());
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
	 *
	 * @param node
	 */
	private void removeLeaf(WAVLNode node) {
		// In each case, we switch the node in the appropraite side of the
		// parent with an outer node, update the subtreeSize of the parent,
		// and then remove the parent from the node.
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
	 *
	 * @param node
	 * @return
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
	 * @Complexity worst case O(log n) (height of tree)
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
	 */
	public String min() {
		return min(root).getValue();
	}

	/**
	 * implemented recursively
	 *
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
	 */
	public String max() {
		return max(root).getValue();
	}

	/**
	 * implemented recursively
	 *
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
	 * * * * * * * * * *
	 * Complexity:
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
	 * * * * * * * * * *
	 * Complexity:
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
		if (empty()) {
			return -1;
		}
		String[] info = infoToArray();
		return info[i-1];
	}

	public void print(WAVLNode node, int height) {
		// originally meant to get height from a method, i.e. height(root);
	       if(root == OUTER_NODE) {
	           System.out.println("(XXXXXX)");
	           return;
	       }

	       //int height = height(root); //PROBLEM??
	       int width = (int)Math.pow(2, height-1);

	       // Preparing variables for loop.
	       List<WAVLNode> current = new ArrayList<WAVLNode>(1),
	           next = new ArrayList<WAVLNode>(2);
	       current.add(root);

	       final int maxHalfLength = 4;
	       int elements = 1;

	       StringBuilder sb = new StringBuilder(maxHalfLength*width);
	       for(int i = 0; i < maxHalfLength*width; i++)
	           sb.append(' ');
	       String textBuffer;

	       // Iterating through height levels.
	       for(int i = 0; i < height; i++) {

	           sb.setLength(maxHalfLength * ((int)Math.pow(2, height-1-i) - 1));

	           // Creating spacer space indicator.
	           textBuffer = sb.toString();

	           // Print tree node elements
	           for(WAVLNode n : current) {

	               System.out.print(textBuffer);

	               if(n == null) {

	                   System.out.print("        ");
	                   next.add(null);
	                   next.add(null);

	               } else {

	                   System.out.printf( "("+Integer.toString(n.key)+","+Integer.toString(n.rank)+")	");
	                   next.add(n.left);
	                   next.add(n.right);

	               }

	               System.out.print(textBuffer);

	           }

	           System.out.println();
	           // Print tree node extensions for next level.
	           if(i < height - 1) {

	               for(WAVLNode n : current) {

	                   System.out.print(textBuffer);

	                   if(n == null)
	                       System.out.print("        ");
	                   else
	                       System.out.printf("%s      %s",
	                               n.left == null ? " " : "/", n.right == null ? " " : "\\");

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

	   public static void main(String args[]) {
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
	               int height = 1; // change this
	               t.print(t.root, height);

	           }
	           catch(IOException e) {
	               e.printStackTrace();
	           }
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
		 */
		public void updateSubtreeSize() {
			size = getSubtreeSize();
		}
	}

}
