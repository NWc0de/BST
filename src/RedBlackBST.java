/*
 * A parameterized generic implementation of a red black tree data structure.
 */

/**
 * A red black tree data structures that supports key-value pairs of a generic type.
 * @param <T> the type of the object to be inserted
 * @param <K> the type of the keys associated with the objects
 */
class RedBlackBST<T, K extends Comparable> extends BST<T, K> {


    /**
     * Inserts an object by finding it's place via binary search and creating
     * a new node, then calls balanceTree to maintain red black invariant - O(log(n))
     * @param object the object to insert
     * @param key the key associated with object to be inserted
     */
    @Override
    protected void insert(T object, K key) {
        Node next = root;
        Node last = root;
        int kCmp = cmp(root.getKey(), key);
        while(next != NODE_DNE) {
            kCmp = cmp(next.getKey(), key);
            next.incrementNodeCount(1);
            last = next;
            next = kCmp < 0 ? next.getRightChild() : next.getLeftChild();
        }

        Node insert = new Node(last, object, key, Color.RED);
        if (kCmp < 0) last.setRightChild(insert);
        else last.setLeftChild(insert);
        balanceInsertion(insert);
    }

    /**
     * Recursively performs necessary rotations/recolorings.
     * Adapted from RB-Insert-Fixup CLRF section 1.3 pg. 316-322
     * @param in the node that was just inserted
     */
    private void balanceInsertion(Node in) {
        Node curr = in;
        while (curr.getParentNode().getColor() == Color.RED) {
            Node unc;
            if (curr.getGrandParent().getLeftChild() == curr.getParentNode())
                unc = curr.getGrandParent().getRightChild();
            else
                unc = curr.getGrandParent().getLeftChild();

            if (unc.getColor() == Color.RED) { // Case 1: Red uncle
                curr.getParentNode().setColor(Color.BLACK);
                unc.setColor(Color.BLACK);
                curr.getGrandParent().setColor(Color.RED);
                curr = curr.getGrandParent();
            } else if (curr.getParentNode().isLeftChild()) { // Case 2-3: Black uncle
                if (curr.isRightChild()) {
                    curr = curr.getParentNode();
                    leftRotate(curr);
                }
                curr.getParentNode().setColor(Color.BLACK);
                curr.getGrandParent().setColor(Color.RED);
                rightRotate(curr.getGrandParent());
            } else {
                if (curr.isLeftChild()) {
                    curr = curr.getParentNode();
                    rightRotate(curr);
                }
                curr.getParentNode().setColor(Color.BLACK);
                curr.getGrandParent().setColor(Color.RED);
                leftRotate(curr.getGrandParent());
            }
        }
        root.setColor(Color.BLACK);
    }

    /**
     * Performs a "left rotation" around n meaning that n's right child
     * replaces n, n becomes the left child of it's right child, and n's
     * right child's previous left child becomes n's new right child.
     * @param n left most node of the left rotation
     */
    private void leftRotate(Node n) {
        Node rc = n.getRightChild();
        Node rlc = rc.getLeftChild();
        Node pr = n.getParentNode();
        if (n == root) {
            root = rc;
            rc.setParentNode(NODE_DNE);
        } else if (n.isLeftChild()) {
            pr.setLeftChild(rc);
        } else {
            pr.setRightChild(rc);
        }
        rc.setParentNode(pr);
        rc.setLeftChild(n);
        n.setRightChild(rlc);
        n.setParentNode(rc);
    }

    /**
     * Performs a "right rotation" around n meaning that n's left child
     * replaces n, n becomes the right child of it's left child, and n's
     * left child's previous right child becomes n's new left child.
     * @param n right most node of the right rotation
     */
    private void rightRotate(Node n) {
        Node lc = n.getLeftChild();
        Node lrc = lc.getRightChild();
        Node pr = n.getParentNode();
        if (n == root) {
            root = lc;
            lc.setParentNode(NODE_DNE);
        } else if (n.isLeftChild()) {
            pr.setLeftChild(lc);
        } else {
            pr.setRightChild(lc);
        }
        lc.setParentNode(pr);
        lc.setRightChild(n);
        n.setLeftChild(lrc);
        n.setParentNode(lc);
    }
}
