/*
 * A set of unit tests covering the Red Black BST class.
 */

import org.junit.Assert;
import org.junit.Test;
import util.ListUtils;

import java.util.List;

/**
 * A set of unit tests covering the basic functions of the red black BST class.
 */
public class RebBlackBSTTests {

    @Test
    public void testInsert() {
        RedBlackTestBST<Integer, Integer> testBST = new RedBlackTestBST<>();
        List<Integer> ints = ListUtils.genIntList(1000);
        for (int i = 0; i < ints.size(); i++) {
            testBST.put(ints.get(i), ints.get(i));
            Assert.assertTrue(testBST.isValidBST());
        }
    }

    @Test
    public void testGet() {
        RedBlackTestBST<Integer, Integer> testBST = new RedBlackTestBST<>();
        List<Integer> elements = ListUtils.genIntList(1000, 1000);
        for (int i = 0; i < elements.size(); i++) {
            testBST.put(elements.get(i), elements.get(i));
        }
        for (int x : elements) {
            Assert.assertTrue(testBST.get(x) != null);
            Assert.assertEquals(testBST.get(x), (Integer) x);
            Assert.assertTrue(testBST.isValidBST());
        }
    }

    @Test
    public void testRemove() {
        RedBlackTestBST<Integer, Integer> testBST = new RedBlackTestBST<>();
        List<Integer> elements = ListUtils.genIntList(10, 10);
        for (int i = 0; i < elements.size(); i++) {
            testBST.put(elements.get(i), elements.get(i));
        }
        for (int x : elements) {
            Integer rmvd = testBST.remove(x);
            Assert.assertEquals(rmvd, Integer.valueOf(x));
            Assert.assertTrue(testBST.isValidBST());
        }
    }

    static class RedBlackTestBST<T, K extends Comparable> extends RedBlackBST<T, K> {

        private enum ORIENT {LEFT, RIGHT};

        /**
         * Recursively checks the BST property for every subtree (node) of the
         * BST.
         * @return a boolean indicating whether the BST property holds
         */
        public boolean isValidBST() {
            return isValidBST(root);
        }


        /**
         * Checks if the tree violates the BST property: every node is
         * greater than or equal to all nodes in it's left subtree and
         * less than all nodes in it's right subtree.
         * @complexity O(n^2)
         * @return true if this tree has the BST property, false if not
         */
        private boolean isValidBST(Node n) {
            if (root.getColor() != Color.BLACK) return false;
            boolean isValid = true;
            if (n == NODE_DNE) return isValid;

            Node l = n.getLeftChild();
            Node r = n.getRightChild();

            if (l != NODE_DNE) isValid = isValid && isValidSubTree(n, l, ORIENT.LEFT);
            if (r != NODE_DNE) isValid = isValid && isValidSubTree(n, r, ORIENT.RIGHT);

            if (n.getColor() == Color.RED) {
                isValid = isValid
                        && l.getColor() == Color.BLACK
                        && r.getColor() == Color.BLACK;
            }

            return isValid && isValidBST(l) && isValidBST(r);
        }

        /**
         * Recursively compares every element in one of n's subtree with n, assuring that the
         * BST property is preserved at node n (not throughout the entire tree).
         * @complexity O(n)
         * @param n all elements in n's left subtree will be compared to n
         * @param c the current child node under consideration
         * @return a boolean indicating if the subtree preserves the heap property
         */
        private boolean isValidSubTree(Node n, Node c, ORIENT dir) {
            //TODO: Is there a more efficient operation to check the BST property?
            //TODO: What is the time complexity for a balanced BST
            boolean validST = true;
            Node l = c.getLeftChild();
            Node r = c.getRightChild();
            if (l != NODE_DNE) {
                validST = validST
                        && validChild(n, l, dir)
                        && isValidSubTree(n, l, dir);
            }
            if (r != NODE_DNE) {
                validST = validST
                        && validChild(n, r, dir)
                        && isValidSubTree(n, r, dir);
            }
            return validST && validChild(n, c, dir);
        }

        /**
         * Compares two nodes with respect the right or left subtree property.
         * @param n the parent node
         * @param c the child node
         * @param dir the orientation, left returns n >= m, right returns n < m
         * @return if left is true return indicates whether n > m, if not return indicates whether n < m
         */
        private boolean validChild(Node n, Node c, ORIENT dir) {
            return dir == ORIENT.LEFT ? n.getKey().compareTo(c.getKey()) > 0 : n.getKey().compareTo(c.getKey()) < 0;
        }
    }
}
