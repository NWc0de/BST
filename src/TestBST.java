/*
 * A set of tests covering the BST class.
 */


import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * A set of tests covering the basic functions of the BST class.
 */
public class TestBST {

    @Test
    public void testInsert() {
        BSTTestCase<Integer, Integer> testBST = new BSTTestCase<>();
        Random gen = new Random();
        for (int i = 0; i < 1000; i++) {
            Integer random = gen.nextInt();
            testBST.put(random, random);
            Assert.assertTrue(testBST.isValidBST());
        }
    }

    @Test
    public void testGet() {
        BSTTestCase<Integer, Integer> testBST = new BSTTestCase<>();
        List<Integer> elements = genIntList(1000, 1000);
        for (int i = 0; i < elements.size(); i++) {
            testBST.put(elements.get(i), elements.get(i));
        }
        for (int x : elements) {
            Assert.assertTrue(testBST.get(x) != null);
            Assert.assertEquals(testBST.get(x), x);
            Assert.assertTrue(testBST.isValidBST());
        }
    }

    @Test
    public void testRemove() {
        BSTTestCase<Integer, Integer> testBST = new BSTTestCase<>();
        List<Integer> elements = genIntList(1000, 1000);
        for (int i = 0; i < elements.size(); i++) {
            testBST.put(elements.get(i), elements.get(i));
        }
        for (int x : elements) {
            Integer rmvd = (Integer) testBST.remove(x);
            Assert.assertEquals(rmvd, Integer.valueOf(x));
            Assert.assertTrue(testBST.isValidBST());
        }
    }

    @Test
    public void testSelect() {
        List<Integer> testList = genUniqueList(10000, 10000);
        BSTTestCase<Integer, Integer> testBST = new BSTTestCase<>();
        for (int x : testList) {
            testBST.put(x, x);
        }
        Collections.sort(testList);
        for (int i = 1; i <= testList.size(); i++) {
            Assert.assertEquals(testList.get(i-1), testBST.select(i));
        }
    }

    @Test
    public void testPredecessor() {
        List<Integer> testList = genIntList(10000, 10000);
        BSTTestCase<Integer, Integer> testBST = new BSTTestCase<>();
        for (int x : testList) {
            testBST.put(x, x);
        }
        Collections.sort(testList);
        Assert.assertEquals(testBST.predecessor(testList.get(0)), null); // min should have no predecessor
        for (int i = 0; i < testList.size() - 1; i++) {
            Integer pred = testList.get(i);
            Integer curr = testList.get(i+1);
            if (pred.compareTo(curr) != 0) Assert.assertEquals(pred, testBST.predecessor(curr));
        }
    }

    @Test
    public void testMinMax() {
        List<Integer> testList;
        BSTTestCase<Integer, Integer> testBST;
        for (int i = 0; i < 1000; i++) {
            testList = genIntList(1000, 1000);
            testBST = new BSTTestCase<>();
            for (int x : testList) {
                testBST.put(x, x);
            }
            Collections.sort(testList);
            Integer expectedMin = testList.get(0);
            Integer expectedMax = testList.get(testList.size() - 1);
            Assert.assertEquals(expectedMax, testBST.max());
            Assert.assertEquals(expectedMin, testBST.min());
        }
    }

    /**
     * Generates a random List of Integers
     * @param count the desired number of Integer objects in the list
     * @param bound the maximum value for the Integers added to the list
     * @return an ArrayList containing count Integers with a max value of bound
     */
    private List<Integer> genIntList(int count, int bound) {
        List<Integer> userList = new ArrayList<Integer>();
        Random gen = new Random();
        for (int i = 0; i < count; i++) {
            userList.add(gen.nextInt(bound));
        }
        return userList;
    }

    /**
     * Genereates a random array of unique Integers (no duplicate values)
     * @param count the number of Integers to generate
     * @param bound the maximum value for the Integers added to the list
     * @return an ArrayList containing count Integers with a max value of bound
     */
    private List<Integer> genUniqueList(int count, int bound) {
        if (count > bound) throw new IllegalArgumentException("Not enough unique values to fill the array (bound too low).");
        List<Integer> userList = new ArrayList<>();
        Random gen = new Random();
        while (userList.size() != count) {
            Integer x = gen.nextInt(bound);
            while (userList.contains(x)) x = gen.nextInt(bound);
            userList.add(x);
        }
        return userList;
    }

    /**
     * A special class that adds a single method to the
     * BST class to check if the BST invariant is satisfied.
     */
    private final class BSTTestCase<T, K extends Comparable> extends BST {

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
         * @complexity O(n^2) (needs improvement)
         * @return true if this tree has the BST property, false if not
         */
        private boolean isValidBST(Node n) {
            boolean isValid = true;
            if (n == NODE_DNE) return isValid;

            Node l = n.getLeftChild();
            Node r = n.getRightChild();

            if (l != NODE_DNE) isValid = isValid && isValidSubTree(n, l, true);
            if (r != NODE_DNE) isValid = isValid && isValidSubTree(n, r, false);

            return isValid && isValidBST(l) && isValidBST(r);
        }

        /**
         * Recursively compares every element in one of n's subtree with n, assuring that the
         * heap property is preserved at node n (not throughout the entire tree).
         * @complexity O(n)
         * @param n all elements in n's left subtree will be compared to n
         * @param c the current child node under consideration
         * @return a boolean indicating if the subtree preserves the heap property
         */
        private boolean isValidSubTree(Node n, Node c, boolean left) {
            //TODO: Is there a more efficient operation to check the BST property?
            //TODO: What is the time complexity for a balanced BST
            boolean validST = true;
            Node l = c.getLeftChild();
            Node r = c.getRightChild();
            if (l != NODE_DNE) {
                validST = validST
                        && cmp(n, l, left)
                        && isValidSubTree(n, l, left);
            }
            if (r != NODE_DNE) {
                validST = validST
                        && cmp(n, r, left)
                        && isValidSubTree(n, r, left);
            }
            return validST && cmp(n, c, left);
        }

        /**
         * Compares to nodes with respect the right or left subtree property.
         * @param n the first node
         * @param m the second node
         * @param left if true return indicates whether n >= m, if not return indicates whether n < m
         * @return if left is true return indicates whether n >= m, if not return indicates whether n < m
         */
        private boolean cmp(Node n, Node m, boolean left) {
            return left ? n.getKey().compareTo(m.getKey()) >= 0 : n.getKey().compareTo(m.getKey()) < 0;
        }
    }

}
