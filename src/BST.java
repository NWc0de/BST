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

    private int nodeCount;
    protected final Node NODE_DNE = new Node(null, null, null);
    protected Node root = NODE_DNE;

    /**
     * Inserts a object/key pair into the BST
     * @param object the object to insert
     * @param key the key for that object
     * @complexity O(n)
     */
    public void put(T object, K key) {
        if (nodeCount++ == 0) {
            root = new Node(NODE_DNE, object, key);
        } else {
            insert(object, key);
        }
    }

    /**
     * Gets the object associated with the specified key, if it exists in the
     * BST. If not, Node.DNE is returned.
     * @complexity O(n)
     * @param key the key associated with the desired object
     * @return the object in this BST associated with the provided key
     */
    public T get(K key) {
        //TODO: does the BST contain this key? what to return if not? exception?
        Node data = search(key);
        return data == NODE_DNE ? null : data.getValue();
    }

    /**
     * Gets the value associated with the Node containing the minimum key
     * @complexity O(n)
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
     * Gets the value associated with the Node containing the maximum key
     * @complexity O(n)
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
     * Gets the object associated with the next least key (in relation to the parameter).
     * Minimum node has no predecessor.
     * @param key the key to compare against
     * @return the object associated with next least key, if it exists, if not null
     */
    public T predecessor(K key) {
        Node pred = nextLeastNode(key);
        return pred == NODE_DNE ? null : pred.getValue();
    }

    /**
     * Searches for the node corresponding to the given key. If multiple such key-value pairs exist,
     * the value associated with the first key-value pair inserted will be returned
     * @param key the key associated with desired node
     * @return the Node associated with that Key, or NODE_DNE if none exists
     */
    private Node search(K key) {
        if (root == NODE_DNE) throw new IllegalStateException("Cannot search an empty tree.");
        Node next = root;
        int kCmp = cmp(root.getKey(), key);
        while(kCmp != 0 && next != NODE_DNE) {
            next = kCmp < 0 ? next.getRightChild() : next.getLeftChild();
            kCmp = cmp(next.getKey(), key);
        }
        return next;
    }

    /**
     * Inserts an object into the BST by finding it's place with binary search
     * and creating a new node for the object.
     * @complexity O(n)
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
     * Returns the node with the next smallest key
     * @param key the key to compare against
     * @return the node with the next smallest key, or NODE_DNE if the node at key is min
     */
    private Node nextLeastNode(K key) {
        Node curr = search(key);
        Node nextLeast;
        if (curr == NODE_DNE) throw new IllegalArgumentException("No node associated with key.");
        if (leftSubTreeEmpty(curr)) {
            nextLeast = firstParentRL(curr);
        } else {
            nextLeast = localNeqMax(curr.getLeftChild(), curr); //TODO: what if next least is equal to key?
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
     * Searchs up the tree from Node n for the first right turn (the first instance
     * where n or n's parent is a right link.
     * @param n the node to begin the search at
     * @return the first right parent of n, or null if n is the min
     */
    private Node firstParentRL(Node n) {
        Node next = n;
        Node prnt = n.getParentNode();
        while (prnt != root && prnt.getRightChild() != next) {
            next = prnt;
            prnt = prnt.getParentNode();
        }
        if (cmp(prnt.getKey(), n.getKey()) >= 0) prnt = NODE_DNE; // n was min
        return prnt;
    }

    /**
     * Returns the local max of n's left subtree, excluding values with key's equal to eq.
     * Assumes there is as least one key in n's left subtree that is not equal to eq
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
     * Returns the first parent node of n (or n itself) with a key that is not equal to eq.
     * Assumes there is a parent with a key not equal to n.
     * @param n the node to begin with
     * @param eq the node to compare keys against
     * @return the first parent node of n with a key that is not equal to eq
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

    /**
     * Checks to nodes for equality
     */
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

        /**
         * Basic constructor to initialize a Node
         * @param parentNode the parent of this node
         * @param value the value associated with this node
         * @param key they key associated with this node
         */
        public Node(Node parentNode, T value, K key) {
            this.parentNode = parentNode;
            this.value = value;
            this.key = key;
            nodeCount = 1;
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

        public void incrementNodeCount(int n) {nodeCount += n;}
        public void decrementNodeCount(int n) {nodeCount -= n;}

        public boolean isLeaf() {
            return rightChild == NODE_DNE && leftChild == NODE_DNE;
        }
    }
}
