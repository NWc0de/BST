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
    protected Node root;
    protected final Node NODE_DNE = new Node(null, null, null);

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
        Node next = root;
        int kCmp = cmp(root.getKey(), key);
        while(kCmp != 0 && next != NODE_DNE) {
            next = kCmp < 0 ? next.getRightChild() : next.getLeftChild();
            kCmp = cmp(next.getKey(), key);
        }
        return next == NODE_DNE ? null : next.getValue();
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
     * Compares to K key values by calling compareTo
     * @param key1 the first key to be compared
     * @param key2 the second key to be compared
     * @return 0 if key1 == key2, a negative int if key1 < key2, a positive int if key1 > key2
     */
    @SuppressWarnings("unchecked")
    private int cmp(K key1, K key2) {
        return key1.compareTo(key2);
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
    }
}
