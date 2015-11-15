/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.lang;

import java.util.Random;

/**
 * Math utilities.
 * 
 * @author Andras Belicza
 */
public class MathUtils {
    
    /**
     * Returns a random number in the range of <code>min..max</code>, both inclusive.
     * 
     * @param min min random number to return
     * @param max max random number to return
     * @param random {@link Random} to be used for random number generation
     * 
     * @return a random number in the range <code>min..max</code>, both inclusive
     * 
     * @throws IllegalArgumentException if <code>min</code> is greater than <code>max</code>
     */
    public static int random(final int min, final int max, final Random random) {
        if (min > max)
            throw new IllegalArgumentException("min cannot be greater than max!");
        
        return min + random.nextInt(max - min + 1);
    }
    
    /**
     * Chooses a random value from a specified array using the specified weights.
     * 
     * <p>
     * This solution chooses a random value according to the specified <code>weights</code> in O(N). In most
     * cases the algorithm doesn't even read the whole <code>weights</code> array. This is as good as it can
     * get. Maximum steps (weights accessed) is N, average number of steps is N/2.
     * </p>
     * 
     * <p>
     * The sum of the weights is expected to be <code>1</code>. If the sum of the weights is less than
     * <code>1</code>, this method <i>might</i> return <code>null</code> (with a probability of
     * <code>1-sum(weights)</code>).
     * </p>
     * 
     * <p>
     * On average the method will be faster if bigger weights are at the beginning of the <code>weights</code>
     * array.
     * </p>
     * 
     * @param <T> type of the values
     * 
     * @param values values to choose from
     * @param weights weights of the values to consider when choosing a random one; expected to sum up to
     *            <code>1</code>
     * 
     * @return a randomly chosen value; <code>null</code> is returned if input is empty array
     * 
     * @throws IllegalArgumentException if <code>values.length < weights.length</code>
     * 
     * @see #randomValue(Object[], int[], Random)
     */
    public static <T> T randomValue(final T[] values, final double[] weights) {
        if (values.length < weights.length)
            throw new IllegalArgumentException("values.length cannot be less than weights.length!");
        
        final double r = Math.random();
        
        double sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
            if (r < sum)
                return values[i];
        }
        
        // We can only get here if sum of weights is less than 1.
        // The probability of getting here is 1-sum
        return null;
    }
    
    /**
     * Chooses a random value from a specified array using the specified weights.
     * 
     * <p>
     * This solution chooses a random value according to the specified <code>weights</code> in O(N). Maximum
     * steps (weights accessed) is 2*N, average number of steps is N.
     * </p>
     * 
     * <p>
     * The weights are expected to be non-negative, and sum of the weights is expected to be positive. If the
     * sum of the weights is <code>0</code>, this method returns <code>null</code>.
     * </p>
     * 
     * <p>
     * On average the method will be faster if bigger weights are at the beginning of the <code>weights</code>
     * array.
     * </p>
     * 
     * @param <T> type of the values
     * 
     * @param values values to choose from
     * @param weights weights of the values to consider when choosing a random one
     * @param random {@link Random} to be used for random number generation
     * 
     * @return a randomly chosen value; <code>null</code> is returned if input is empty array or if sum of
     *         weights is <code>0</code>
     * 
     * @throws IllegalArgumentException if <code>values.length < weights.length</code> or if
     *             <code>weights</code> contains negative values
     * 
     * @see #randomValue(Object[], double[])
     */
    public static <T> T randomValue(final T[] values, final int[] weights, final Random random) {
        if (values.length < weights.length)
            throw new IllegalArgumentException("values.length cannot be less than weights.length!");
        
        int sum = 0;
        for (final int weight : weights) {
            if (weight < 0)
                throw new IllegalArgumentException("Weights cannot be negative: " + weight);
            sum += weight;
        }
        
        if (sum == 0)
            return null;
        
        final int r = random.nextInt(sum);
        
        sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
            if (r < sum)
                return values[i];
        }
        
        // We can never end up here
        return null;
    }
    
    /**
     * Compares to <code>double</code> values whether they are equal according to the specified precision.
     *
     * <p>
     * The formula:
     * 
     * <pre>
     * abs(2 * (d1 - d2) / (d1 + d2)) &lt; precision
     * </pre>
     * 
     * is used to test equality.
     * </p>
     * 
     * @param d1 first number to be compared
     * @param d2 second number to be compared
     * @param precision precision in which to declare the 2 numbers equal
     * 
     * @return <code>true</code> if the 2 <code>double</code> values are considered equal; <code>false</code>
     *         otherwise
     */
    public static boolean eq(final double d1, final double d2, final double precision) {
        return Math.abs(2 * (d1 - d2) / (d1 + d2)) < precision;
    }
    
}
