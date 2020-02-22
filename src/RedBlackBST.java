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
     * a new node, then calls balanceInsertion to maintain red black invariant.
     * @param object the object to insert
     * @param key the key associated with object to be inserted
     */
    @Override
    protected void insert(T object, K key) {
        Node last, next = root;
        int kCmp;
        do {
            kCmp = cmp(next.getKey(), key);
            next.incrementNodeCount(1); //TODO: increment only if tree does not already contain that key
            last = next;
            next = kCmp < 0 ? next.getRightChild() : next.getLeftChild();
        } while (kCmp != 0 && next != NODE_DNE);

        Node insert = new Node(last, object, key, Color.RED);

        if (kCmp < 0) last.setRightChild(insert);
        else if (kCmp > 0) last.setLeftChild(insert);

        if (kCmp == 0) last.pushValue(object);
        else balanceInsertion(insert);
    }

    /**
     * Deletes the first node in the subtree with key equal to the key parameter then
     * calls balanceDeletion to maintain red black invariant.
     * @param key the key associated with the object to be deleted
     * @return the node was that deleted, or NODE_DNE if no Node with the specified key exists
     */
    @Override
    protected Node delete(K key) {
        Node repl, curr = search(key, true); //TODO: decrement only if node will be fully deleted
        Color oc = curr.getColor();
        if (curr.valCount() > 1 || curr == NODE_DNE) return curr;

        if (curr.isLeaf()) {
            repl = NODE_DNE;
            repl.setParentNode(curr.getParent()); // temporarily set NODE_DNE's parent to enable traversing during balancing
            supplant(curr, NODE_DNE);
            System.out.println("1");
        } else if (curr.childCount() == 1) {
            repl = curr.getRightChild() == NODE_DNE ? curr.getLeftChild() : curr.getRightChild();
            supplant(curr, repl);
            System.out.println("2");
        } else {
            Node scr = localMin(curr.getRightChild()); // can also be localMax(n.getLeftChild)
            oc = scr.getColor();
            repl = scr.getRightChild();

            if (scr.getParent() != curr) {
                supplant(scr, scr.getRightChild());
                curr.getRightChild().setParentNode(scr); //
                scr.setRightChild(curr.getRightChild());
                repl.setParentNode(scr.getParent());
                System.out.println("Case 1");
            } else {
                System.out.println("Case 2");
                repl.setParentNode(scr);
            }

            scr.setNodeCount(curr.getNodeCount() - 1);
            supplant(curr, scr);

            scr.setColor(curr.getColor());
            curr.getLeftChild().setParentNode(scr);
            scr.setLeftChild(curr.getLeftChild());

            //if (repl == NODE_DNE) repl.setParentNode(scr);
            System.out.println(repl.getParent() == scr);
            System.out.println("3");
        }

        if (oc == Color.BLACK) balanceDeletion(repl);
        return curr;
    }

    /**
     * Recursively performs necessary rotations/recolorings.
     * Adapted from RB-Delete-Fixup CLRF section 1.3 pg. 326
     * @param in the node in which violations may have been introduced
     */
    private void balanceDeletion(Node in) {
        Node curr = in;
        while (curr != root && curr.getColor() == Color.BLACK) {
            System.out.println("cur DNE: " + (curr == NODE_DNE));
            System.out.println("cur parent: " + (curr.getParent()));
            Node sib = curr.isLeftChild() ? curr.getParent().getRightChild() :  curr.getParent().getLeftChild();


            if (sib.getColor() == Color.RED) { // Case 1: n's sibling, sib, is red
                sib.setColor(Color.BLACK);
                curr.getParent().setColor(Color.RED);
                if (curr.isLeftChild()) leftRotate(curr.getParent());
                else rightRotate(curr.getParent());
                sib = curr.isLeftChild() ? curr.getParent().getRightChild() :  curr.getParent().getLeftChild();
            }

            System.out.println("sib null child: " + (sib.getLeftChild() == NODE_DNE || sib.getRightChild() == NODE_DNE));
            System.out.println("sib dne: " + (sib == NODE_DNE));
            // Case 2: n's sibling, sib, is black and sib has two black children
            if (sib.getLeftChild().getColor() == Color.BLACK && sib.getRightChild().getColor() == Color.BLACK) {
                sib.setColor(Color.RED);
                curr = curr.getParent();
                System.out.println("curr == root" + (curr == root));
            } else if (curr.isLeftChild()) {
                if (sib.getRightChild().getColor() == Color.BLACK) { // Case 3: n's sibling, sib, is black and sib has a red left child
                    sib.getLeftChild().setColor(Color.BLACK);
                    sib.setColor(Color.RED);
                    rightRotate(sib);
                    sib = curr.getParent().getRightChild();
                }
                sib.setColor(curr.getParent().getColor()); // Case 4: n's sibling, sib, is black and sib has a red right child
                curr.getParent().setColor(Color.BLACK);
                if (sib.getRightChild() != NODE_DNE) sib.getRightChild().setColor(Color.BLACK);
                leftRotate(curr.getParent());
                curr = root;
            } else {
                if (sib.getLeftChild().getColor() == Color.BLACK) {
                    sib.getRightChild().setColor(Color.BLACK);
                    sib.setColor(Color.RED);
                    leftRotate(sib);
                    sib = curr.getParent().getLeftChild();
                }
                sib.setColor(curr.getParent().getColor());
                curr.getParent().setColor(Color.BLACK);
                if (sib.getLeftChild() != NODE_DNE) sib.getLeftChild().setColor(Color.BLACK);
                rightRotate(curr.getParent());
                curr = root;
            }
        }

        curr.setColor(Color.BLACK);
        NODE_DNE.setParentNode(null); // reset NODE_DNE's parent
    }

    /**
     * Recursively performs necessary rotations/recolorings.
     * Adapted from RB-Insert-Fixup CLRF section 1.3 pg. 316-322
     * @param in the node that was just inserted
     */
    private void balanceInsertion(Node in) {
        Node curr = in;
        while (curr.getParent().getColor() == Color.RED) {
            Node unc;
            if (curr.getGrandParent().getLeftChild() == curr.getParent())
                unc = curr.getGrandParent().getRightChild();
            else
                unc = curr.getGrandParent().getLeftChild();

            if (unc.getColor() == Color.RED) { // Case 1: Red uncle
                curr.getParent().setColor(Color.BLACK);
                unc.setColor(Color.BLACK);
                curr.getGrandParent().setColor(Color.RED);
                curr = curr.getGrandParent();
            } else if (curr.getParent().isLeftChild()) { // Case 2: Black uncle, n is left child
                if (curr.isRightChild()) { // Case 3: Black Uncle, n is right child
                    curr = curr.getParent();
                    leftRotate(curr);
                }
                curr.getParent().setColor(Color.BLACK);
                curr.getGrandParent().setColor(Color.RED);
                rightRotate(curr.getGrandParent());
            } else {
                if (curr.isLeftChild()) {
                    curr = curr.getParent();
                    rightRotate(curr);
                }
                curr.getParent().setColor(Color.BLACK);
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
        Node pr = n.getParent();
        if (n == root) {
            root = rc;
            rc.setParentNode(NODE_DNE);
        } else if (n.isLeftChild()) {
            pr.setLeftChild(rc);
            rc.setParentNode(pr);
        } else {
            pr.setRightChild(rc);
            rc.setParentNode(pr);
        }
        rc.setLeftChild(n);
        n.setParentNode(rc);
        n.setRightChild(rlc);
        if (rlc != NODE_DNE) rlc.setParentNode(n);
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
        Node pr = n.getParent();
        if (n == root) {
            root = lc;
            lc.setParentNode(NODE_DNE);
        } else if (n.isLeftChild()) {
            pr.setLeftChild(lc);
            lc.setParentNode(pr);
        } else {
            pr.setRightChild(lc);
            lc.setParentNode(pr);
        }
        lc.setRightChild(n);
        n.setParentNode(lc);
        n.setLeftChild(lrc);
        if (lrc != NODE_DNE) lrc.setParentNode(n);
    }
}
