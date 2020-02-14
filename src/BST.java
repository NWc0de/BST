/*
 * The main class declaration for the binary search tree data type.
 * Author: Spencer Little
 */

/**
 * An implementation of a basic (unbalanced) binary search tree.
 * Generic types: T the object corresponding to key K, which must be Comparable
 * @author Spencer Little
 */
public class BST<T, K extends Comparable> {

    protected final Node NODE_DNE = new Node(null, null, null);
    protected Node root = NODE_DNE;

    /**
     * Inserts a object/key pair into the BST. O(n)
     * @param object the object to insert
     * @param key the key for that object
     */
    public void put(T object, K key) {
        if (root == NODE_DNE) {
            root = new Node(NODE_DNE, object, key);
        } else {
            insert(object, key);
        }
    }

    /**
     * Gets the object associated with the specified key w/ binary search. O(n)
     * @param key the key associated with the desired object
     * @return the object in this BST associated with the provided key
     */
    public T get(K key) {
        Node data = search(key, false);
        return data == NODE_DNE ? null : data.getValue();
    }

    /**
     * Removes the node associated with the specified key. If there are multiple nodes
     * associated with that key, nodes are removed in the order they were inserted. O(n)
     * @param key the key associated with node to be removed
     */
    public T remove(K key) {
        Node rmv = delete(key);
        return rmv == NODE_DNE ? null : rmv.getValue();
    }

    /**
     * Gets the value associated with the Node containing the minimum key. O(n)
     * @return the object associated with the least key in the tree
     */
    public T min() {
        if (root == NODE_DNE) throw new IllegalStateException("Empty tree.");
        Node next = root;
        while (next.getLeftChild() != NODE_DNE) {
            next = next.getLeftChild();
        }
        return next.getValue();
    }

    /**
     * Gets the value associated with the Node containing the maximum key. O(n)
     * @return the object associated with the greatest key in the tree
     */
    public T max() {
        if (root == NODE_DNE) throw new IllegalStateException("Empty tree.");
        Node next = root;
        while (next.getRightChild() != NODE_DNE) {
            next = next.getRightChild();
        }
        return next.getValue();
    }

    /**
     * Returns the object associated with the nth smallest key. O(n)
     * @param n the rank of the desired element (least-greatest, not 1-sizeOfArray)
     * @return the element with the nth smallest key will be returned
     */
    public T select(int n) {
        if (n <= 0 || n > root.getNodeCount()) throw new IllegalArgumentException("Rank cannot be less than 0 or greater than the size of the BST.");
        Node rankN = select(n-1, root);
        return rankN.getValue();
    }

    public int size() { return root.getNodeCount(); }
    public boolean isEmpty() { return root == NODE_DNE; }
    public boolean contains(K key) { return search(key, false) != null; }

    /**
     * Gets the object associated with the next least (and not equal to) key (in relation to the parameter).
     * Minimum node has no predecessor. O(n)
     * @param key the key to compare against
     * @return the object associated with next least key, if it exists, if not null
     */
    public T predecessor(K key) {
        Node pred = nextLeastNode(search(key, false), false);
        return pred == NODE_DNE ? null : pred.getValue();
    }

    /**
     * Searches for the node corresponding to the given key (binary search). If multiple
     * such key-value pairs exist, the value associated with the first key-value pair
     * inserted will be returned. O(n)
     * @param key the key associated with desired node
     * @param dec flag to indicate whether to decrement node count during traversal
     * @return the Node associated with that Key, or NODE_DNE if none exists
     */
    private Node search(K key, boolean dec) {
        if (root == NODE_DNE) throw new IllegalStateException("Cannot search an empty tree.");
        Node next = root;
        int kCmp = cmp(root.getKey(), key);
        while(kCmp != 0 && next != NODE_DNE) {
            if (dec) next.decrementNodeCount(1);
            next = kCmp < 0 ? next.getRightChild() : next.getLeftChild();
            kCmp = cmp(next.getKey(), key);
        }
        return next;
    }

    /**
     * Recursively traverses the tree searching for the nth smallest key.
     * @param n the order of key to search for
     * @param next the Node to process (initially root)
     * @return the Node associated with the nth smallest key
     */
    private Node select(int n, Node next) {
        int lSize = next.getLeftChild().getNodeCount();
        if (n < lSize) return select(n, next.getLeftChild());
        else if (n > lSize) return select(n - lSize - 1, next.getRightChild());
        else return next;
    }

    /**
     * Inserts an object by finding it's place via binary search and creating
     * a new node. O(n)
     * @param object the object to insert
     * @param key the key associated with object to be inserted
     */
    private void insert(T object, K key) {
        Node next = root;
        Node last = root;
        int kCmp = cmp(root.getKey(), key);
        while(next != NODE_DNE) {
            kCmp = cmp(next.getKey(), key);
            next.incrementNodeCount(1);
            last = next;
            next = kCmp < 0 ? next.getRightChild() : next.getLeftChild();
        }

        Node insert = new Node(last, object, key);
        if (kCmp < 0) last.setRightChild(insert);
        else last.setLeftChild(insert);
    }

    /**
     * Deletes the first node in the subtree with key equal to the key parameter. O(n)
     * @param key the key associated with the object to be deleted
     * @return the node was that deleted, or NODE_DNE if no Node with the specified key exists
     */
    private Node delete(K key) {
        Node curr = search(key, true); // decrement while traversing during search
        if (curr == NODE_DNE) return curr;
        if (curr.isLeaf()) {
            deleteLeaf(curr);
        } else if (curr.childCount() == 1) {
            deleteNodeOC(curr);
        } else {
            deleteNodeTC(curr);
        }
        return curr;
    }

    /**
     * Deletes a leaf by removing the node's parent's reference to the node.
     * @param n the node to delete
     */
    private void deleteLeaf(Node n) {
        if (!n.isLeaf()) throw new IllegalArgumentException("Node is not leaf.");
        if (n.getParentNode().getRightChild() == n) {
            n.getParentNode().setRightChild(NODE_DNE);
        } else {
            n.getParentNode().setLeftChild(NODE_DNE);
        }
    }

    /**
     * Splices n out by replacing n with it's child node.
     * @param n the node to delete
     */
    private void deleteNodeOC(Node n) {
        if (n.childCount() != 1) throw new IllegalArgumentException("Node does not have one child.");
        Node child = n.getRightChild() == NODE_DNE ? n.getLeftChild() : n.getRightChild();
        if (n == root) {
            root = child;
            child.setParentNode(NODE_DNE);
        } else if (n.isRightChild()) {
            n.getParentNode().setRightChild(child);
            child.setParentNode(n.getParentNode());
        } else {
            n.getParentNode().setLeftChild(child);
            child.setParentNode(n.getParentNode());
        }
    }

    /**
     * Deletes a node by replacing n with it's leq predecessor. O(n)
     * @param n the object to be deleted
     */
    private void deleteNodeTC(Node n) {
        if (n.childCount() != 2) throw new IllegalArgumentException("Node does not have two children.");
        Node pred = nextLeastNode(n, true); // find next leq node
        splice(pred);
        pred.setNodeCount(n.getNodeCount() - 1);
        if (n == root) {
            root = pred;
            pred.setParentNode(NODE_DNE);
        } else if (n.isRightChild()) {
            n.getParentNode().setRightChild(pred);
            pred.setParentNode(n.getParentNode());
        } else {
            n.getParentNode().setLeftChild(pred);
            pred.setParentNode(n.getParentNode());
        }
        n.getLeftChild().setParentNode(pred);
        n.getRightChild().setParentNode(pred);
        pred.setRightChild(n.getRightChild());
        pred.setLeftChild(n.getLeftChild());
    }

    /**
     * Removes a node by joining it's parent and child. Assumes n does not have
     * right child, as this method is only called with the predecessor of a node
     * that has two children.
     * @param n the node to splice
     */
    private void splice(Node n) {
        if (n.isRightChild() && !n.isLeaf()) {
            n.getParentNode().setRightChild(n.getLeftChild());
            n.getLeftChild().setParentNode(n.getParentNode());
        } else if (n.isLeftChild() && !n.isLeaf()) {
            n.getParentNode().setLeftChild(n.getLeftChild());
            n.getLeftChild().setParentNode(n.getParentNode());
        } else if (n.isRightChild()) {
            n.getParentNode().setRightChild(NODE_DNE);
        } else {
            n.getParentNode().setLeftChild(NODE_DNE);
        }
    }

    /**
     * Returns the node with the next smallest key or the next smallest or
     * equal key if the eq flag is set.
     * @param n the node to compare against
     * @param eq flag to signify searching for the next smallest or equal key
     * @return the node with the next smallest key, or NODE_DNE if the node at key is min
     */
    private Node nextLeastNode(Node n, boolean eq) {
        Node nextLeast;
        if (n == NODE_DNE) throw new IllegalArgumentException("No node associated with key.");
        if ((eq && n.getLeftChild() == NODE_DNE) || (!eq && leftSubTreeEmpty(n))) {
            nextLeast = firstParentRL(n);
        } else if (eq) {
            nextLeast = localMax(n.getLeftChild());
        } else {
            nextLeast = localNeqMax(n.getLeftChild(), n);
        }
        return nextLeast;
    }

    /**
     * Determines whether the left subtree of the given node is "empty" meaning it is
     * either null or contains all nodes with keys equal to n.
     * @complexity O(height(n)) - height of the node n
     * @param n the node to begin the subtree search at
     * @return a boolean indicating whether the left subtree is empty or not
     */
    private boolean leftSubTreeEmpty(Node n) {
        if (n.getLeftChild() == NODE_DNE) return true;
        Node next = n.getLeftChild();
        while (next != NODE_DNE) { // if any left node != n, or any right node in the ST exists, not empty
            int lCmp = cmp(next.getKey(), n.getKey());
            if (lCmp != 0 || next.getRightChild() != NODE_DNE) return false;
            next = next.getLeftChild();
        }
        return true;
    }

    /**
     * Returns the maximum element in the subtree rooted at n.
     * @param n the root of the subtree to examine
     * @return the maximum element in the subtree rooted at n
     */
    private Node localMax(Node n) {
        if (n == NODE_DNE) throw new IllegalArgumentException("Tree is empty.");
        Node next = n;
        while (next.getRightChild() != NODE_DNE) {
            next = next.getRightChild();
        }
        return next;
    }

    /**
     * Returns the maximum element in n's left subtree, excluding values with key's equal to eq.
     * Assumes there is as least one key in n's left subtree that is not equal to eq.
     * @param n the root of the subtree to search
     * @param eq the node containing the key that will be excluded from the search
     * @return the node containing the maximum key in that subtree (excluding nodes with keys equal to nq)
     */
    private Node localNeqMax(Node n, Node eq) {
        if (n == NODE_DNE) throw new IllegalStateException("Empty tree.");
        if (eq==NODE_DNE || eq==null) throw new IllegalArgumentException("Equality node is null or DNE.");
        Node next = n;
        while (next.getRightChild() != NODE_DNE || nodeEq(next, eq)) { // until we find a node with no right child that is not equal to eq
            if (next.isLeaf() && nodeEq(next, eq)) return firstNeqParent(next, eq);
            if (nodeEq(next, eq) && next.getLeftChild() != NODE_DNE) next = next.getLeftChild(); // if node is equal, left subtree could contain max
            else next = next.getRightChild();
        }
        return next;
    }

    /**
     * Searchs up the tree from Node n for the first right turn (the first instance
     * where n or n's parent is a right link.
     * @param n the node to begin the search at
     * @return the first right parent of n, or null if n is the min
     */
    private Node firstParentRL(Node n) {
        Node next = n;
        while (next != root && !next.isRightChild()) {
            next = next.getParentNode();
        }
        if (next==root || cmp(next.getParentNode().getKey(), n.getKey()) >= 0) return NODE_DNE; // n was min
        return next.getParentNode();
    }

    /**
     * Returns the first parent node of n (or n itself) with a key that is not equal to eq.
     * Assumes there is a parent node of n (or n itself) with a key not equal to n.
     * @param n the node to begin with
     * @param eq the node to compare keys against
     * @return the first parent node of n (or n itself) with a key that is not equal to eq
     */
    private Node firstNeqParent(Node n, Node eq) {
        if (n == NODE_DNE) throw new IllegalStateException("Empty tree.");
        if (eq==NODE_DNE || eq==null) throw new IllegalArgumentException("Equality node is null or DNE.");
        Node next = n;
        while (nodeEq(next, eq)) {
            next = next.getParentNode();
        }
        return next;
    }

    /**
     * Compares to K key values by calling compareTo
     * @param key1 the first key to be compared
     * @param key2 the second key to be compared
     * @return 0 if key1 == key2, a negative int if key1 < key2, a positive int if key1 > key2
     */
    @SuppressWarnings("unchecked")
    private int cmp(K key1, K key2) {
        return key1.compareTo(key2);
    }

    private boolean nodeEq(Node n, Node m) {
        return cmp(n.getKey(), m.getKey()) == 0;
    }



    protected class Node {

        private Node parentNode;
        private Node leftChild = NODE_DNE;
        private Node rightChild = NODE_DNE;
        private T value;
        private K key;
        private int nodeCount; // size of subtree rooted at this node

        public Node(Node parentNode, T value, K key) {
            this.parentNode = parentNode;
            this.value = value;
            this.key = key;
            nodeCount = parentNode == null ? 0 : 1; // NODE_DNE has nodeCount 0
        }

        public Node getParentNode() {return parentNode;}
        public Node getLeftChild() {return leftChild;}
        public Node getRightChild() {return rightChild;}

        public void setParentNode(Node p) {parentNode = p;}
        public void setLeftChild(Node l) {leftChild = l;}
        public void setRightChild(Node r) {rightChild = r;}

        public T getValue() {return value;}
        public K getKey() {return key;}
        public int getNodeCount() {return nodeCount;}
        public void setNodeCount(int n) {nodeCount = n;}

        public void incrementNodeCount(int n) {nodeCount += n;}
        public void decrementNodeCount(int n) {nodeCount -= n;}

        public boolean isLeaf() {
            return rightChild == NODE_DNE && leftChild == NODE_DNE;
        }
        public boolean isRightChild() { return parentNode.rightChild == this; }
        public boolean isLeftChild() { return parentNode.leftChild == this; }
        public int childCount() {
            if (rightChild != NODE_DNE && leftChild != NODE_DNE) return 2;
            else if (rightChild != NODE_DNE || leftChild != NODE_DNE) return 1;
            else return 0;
        }

    }
}
