/*
 * A utility class that provides data sets for the BST unit tests.
 */

package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An auxiliary class to provide integer lists for the unit tests.
 */
public class ListUtils {

    /**
     * Generates a random List of Integers
     * @param count the desired number of Integer objects in the list
     * @param bound the maximum value for the Integers added to the list
     * @return an ArrayList containing count Integers with a max value of bound
     */
    public static List<Integer> genIntList(int count, int bound) {
        List<Integer> userList = new ArrayList<Integer>();
        Random gen = new Random();
        for (int i = 0; i < count; i++) {
            userList.add(gen.nextInt(bound));
        }
        return userList;
    }

    /**
     * Generates a random List of Integers with no bounds
     * @param count the desired number of Integer objects in the list
     * @return an ArrayList containing count Integers with a max value of bound
     */
    public static List<Integer> genIntList(int count) {
        List<Integer> userList = new ArrayList<Integer>();
        Random gen = new Random();
        for (int i = 0; i < count; i++) {
            userList.add(gen.nextInt());
        }
        return userList;
    }

    /**
     * Genereates a random array of unique Integers (no duplicate values)
     * @param count the number of Integers to generate
     * @param bound the maximum value for the Integers added to the list
     * @return an ArrayList containing count Integers with a max value of bound
     */
    public static List<Integer> genUniqueList(int count, int bound) {
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
}
