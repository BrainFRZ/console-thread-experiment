/*
 * The Console Thread Experiment attempts to implement GUI-like behavior in a console.
 * Copyright (C) 2017  Terry Weiss
 *
 * This file is part of the Console Thread Experiment.
 *
 * The Console Thread Experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * The Console Thread Experiment is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * The Console Thread Experiment.  If not, see <http://www.gnu.org/licenses/>.
 */

package fibonacci;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * A utility class that statelessly calculates the Fibonacci sequence. By default, the first two
 * terms of the sequence are 0 and 1. Each following term is defined as the sum of the two terms
 * before it. The sequence may be generalized to start with different nonnegative initial terms.
 *
 * Change Log:
 * v1.1, 15Mar2017, Terry Weiss:
 *     - Changed int representation to BigInteger. I didn't think how fast the sequence grew.
 *
 * @Author      Terry Weiss
 * @Version     1.1, 15Mar2017
 */
public final class Fibonacci {

    /**
     * Default term 0 is 0.
     */
    public static final BigInteger DEFAULT_0 = BigInteger.ZERO;

    /**
     * Default term 1 is 1.
     */
    public static final BigInteger DEFAULT_1 = BigInteger.ONE;

    /**
     * Calculates a specific term of the Fibonacci sequence with generalized starting terms.
     * If term, <code>a</code>, or <code>b</code> are negative, or <code>b</code> is less than
     * <code>a</code>, an {@link IllegalArgumentException} is thrown.
     *
     * @param   term    Specified term in sequence starting at 0
     * @param   a       First term of the sequence
     * @param   b       Second term of the sequence
     * @return          Value of the specified term
     */
    public static BigInteger at(final int term, BigInteger a, BigInteger b) {
        if (term < 0) {
            throw new IllegalArgumentException("Term must be non-negative: " + term);
        }  else if (a.compareTo(BigInteger.ZERO) == -1 || b.compareTo(a) == -1) {
            throw new IllegalArgumentException("Terms must be positive, and second term must be "
                    + "later in the sequence: term1=" + a + " term2=" + b);
        }


        if (term == 0) {
            return a;
        } else if (term == 1) {
            return b;
        } else if (b.equals(BigInteger.ZERO)) {
            // a <= b, so a must also be 0, so entire series will be 0
            return BigInteger.ZERO;
        }

        BigInteger val = BigInteger.ZERO;
        for (int i = 2; i <= term; ++i) {
            val = a.add(b);
            a = b;
            b = val;
        }

        return val;
    }

    /**
     * Calculates a specific term of the Fibonacci sequence starting with 0 and 1. If
     * <code>term</code> is negative, an {@link IllegalArgumentException} is thrown.
     *
     * @param   term
     * @return  Value of the specified term
     * @see     #at(int, int, int)
     */
    public static BigInteger at(final int term) {
        return at(term, BigInteger.ZERO, BigInteger.ONE);
    }


    /**
     * Generates the next block of a sequence following two given values up to a maximum. The first
     * term of the block is the sum of <code>a</code> and <code>b</code>, and each following term is
     * the sum of the two before it. The block will be of size <code>length</code> or until a term
     * is greater than <code>max</code>. The initial values <code>a</code> and <code>b</code> must
     * be non-negative, and <code>b</code> should be the value immediately after <code>a</code> in
     * the sequence. There is no validation beyond this, so it is possible to generate a block that
     * could not legally follow a block that ended with <code>a</code> and <code>b</code>. Blocks
     * can be called successively by reading the return array at index <code>length-2</code> for the
     * next <code>a</code> and <code>length-1</code> for the next <code>b</code>.
     *
     * @param   length  Length of the sequence block
     * @param   a       Two terms before block begins
     * @param   b       One term before block begins
     * @return          ArrayList of values in the sequence block
     * @see             #sequence(int, int, int)
     */
    public static ArrayList<BigInteger> nextBlock(final int length, BigInteger a, BigInteger b)
    {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive: " + length);
        } else if (a.compareTo(BigInteger.ZERO) == -1 || b.compareTo(a) == -1) {
            throw new IllegalArgumentException("term1 and term2 must be non-negative, and "
                    + "term2 must be later in the sequence: term1=" + a + " term2=" + b);
        }

        ArrayList<BigInteger> block = new ArrayList<>(length);
        BigInteger next = a.add(b);
        for (int i = 0; i < length; ++i) {
            next = a.add(b);
            a = b;
            b = next;
            block.add(next);
        }

        return block;
    }


    /**
     * Generates a sequence starting with two given values. The first term of sequence is
     * <code>a</code> and the second term is <code>b</code>. Each following term is the sum of
     * the two before it. The two values <code>a</code> and <code>b</code> must be non-negative.
     *
     * @param   length  Length of the sequence block
     * @param   a       First term of sequence
     * @param   b       Second term of sequence
     * @return          Array of values in the sequence block
     * @see             #nextBlock(int, int, int)
     */
    public static ArrayList<BigInteger> sequence(final int length, BigInteger a, BigInteger b) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive: " + length);
        } else if (a.compareTo(BigInteger.ZERO) == -1 || b.compareTo(a) == -1) {
            throw new IllegalArgumentException("A and B must be non-negative, and B must be later "
                    + "in the sequence: a=" + a + " b=" + b);
        }


        ArrayList<BigInteger> block = new ArrayList<>(length);
        block.add(a);

        if (length >= 2) {
            block.add(b);
        }

        if (length > 2) {
            BigInteger next;
            for (int i = 2; i < length; ++i) {
                next = a.add(b);
                a = b;
                b = next;
                block.add(next);
            }
        }

        return block;
    }


    // Private constructor to prevent instantiation
    private Fibonacci() {}
}
