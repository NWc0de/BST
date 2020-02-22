/*
 * A set of unit tests covering the Red Black BST class.
 */

import org.junit.Assert;
import org.junit.Test;
import util.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A set of unit tests covering the basic functions of the red black BST class.
 */
public class RebBlackBSTTests {

    @Test
    public void testInsert() {
        TestRedBlackBST<Integer, Integer> testBST = new TestRedBlackBST<>();
        List<Integer> ints = ListUtils.genIntList(1000);
        for (int i = 0; i < ints.size(); i++) {
            testBST.put(ints.get(i), ints.get(i));
            Assert.assertTrue(testBST.isValidRBST());
        }
    }

    @Test
    public void testGet() {
        TestRedBlackBST<Integer, Integer> testBST = new TestRedBlackBST<>();
        List<Integer> elements = ListUtils.genIntList(1000, 1000);
        for (int i = 0; i < elements.size(); i++) {
            testBST.put(elements.get(i), elements.get(i));
        }
        for (int x : elements) {
            Assert.assertTrue(testBST.get(x) != null);
            Assert.assertEquals(testBST.get(x), (Integer) x);
            Assert.assertTrue(testBST.isValidRBST());
        }
    }

    @Test
    public void testRemove() {
        TestRedBlackBST<Integer, Integer> testBST = new TestRedBlackBST<>();
        List<Integer> elements = ListUtils.genIntList(1000, 1000);
        System.out.println(elements.toString());
        for (int i = 0; i < elements.size(); i++) {
            testBST.put(elements.get(i), elements.get(i));
        }
        for (int x : elements) {
            Integer rmvd = testBST.remove(x);
            Assert.assertEquals(rmvd, Integer.valueOf(x));
            Assert.assertTrue(testBST.isValidRBST());
        }
    }

    private List<Integer> intArrayToList(int[] arr) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int x : arr) list.add(x);
        return list;
    }

    static class TestRedBlackBST<T, K extends Comparable> extends RedBlackBST<T, K> {

        private enum ORIENT {LEFT, RIGHT};

        /**
         * Recursively checks the BST property for every subtree (node) of the
         * BST.
         * @return a boolean indicating whether the BST property holds
         */
        public boolean isValidRBST() {
            if (root.getColor() != Color.BLACK) return false; // property 2
            return isValidRBST(root);
        }


        /**
         * Checks if the tree violates the BST property: every node is
         * greater than or equal to all nodes in it's left subtree and
         * less than all nodes in it's right subtree.
         * @complexity O(n^2)
         * @return true if this tree has the BST property, false if not
         */
        private boolean isValidRBST(Node n) {
            boolean isValid = true;
            if (n == NODE_DNE) return isValid;
            if (!isBlackHeightSymmetric(n)) return false; // property 5

            Node l = n.getLeftChild();
            Node r = n.getRightChild();

            if (n.getColor() == Color.RED) { // property 4
                isValid = isValid
                        && l.getColor() == Color.BLACK
                        && r.getColor() == Color.BLACK;
            }

            if (l != NODE_DNE) isValid = isValid && isValidSubTree(n, l, ORIENT.LEFT);
            if (r != NODE_DNE) isValid = isValid && isValidSubTree(n, r, ORIENT.RIGHT);

            return isValid && isValidRBST(l) && isValidRBST(r);
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
         * Tests Red Black BST property 5, that the number of black nodes on
         * any path from n to a leaf node is the same.
         * @param n the root of the subtree to analyze
         * @return a boolean indicating whether this subtree fulfilling property 5
         */
        private boolean isBlackHeightSymmetric(Node n) {
            List<Integer> pathLens = new ArrayList<>();
            getBlackPathLengths(pathLens, n, 0);
            if (pathLens.size() <= 1) return true;
            boolean eqPathLen = true;
            int prev = pathLens.get(0);
            for (int x : pathLens) {
                eqPathLen = eqPathLen && (prev == x);
                prev = x;
            }

            return eqPathLen;
        }

        /**
         * Recursively traverses all paths from the provided node to all leafs and fills the
         * provided list with each path length (only counting black nodes).
         * @param pathLens the list to fill with each path length
         * @param n the node to begin the traversal at
         * @param curr the current cumulative path length (should initially be 0 or 1)
         */
        private void getBlackPathLengths(List<Integer> pathLens, Node n, int curr) {
            int curLen = curr;
            if (n.getColor() == Color.BLACK) curLen++;
            if (n.isLeaf()) {
                pathLens.add(curLen);
                return;
            }
            if (n.getLeftChild() != NODE_DNE) getBlackPathLengths(pathLens, n.getLeftChild(), curLen);
            if (n.getRightChild() != NODE_DNE) getBlackPathLengths(pathLens, n.getRightChild(), curLen);
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
