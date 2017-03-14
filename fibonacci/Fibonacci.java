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

import java.util.Arrays;

/**
 * A utility class that statelessly calculates the Fibonacci sequence. By default, the first two
 * terms of the sequence are 0 and 1. Each following term is defined as the sum of the two terms
 * before it. The sequence may be generalized to start with different nonnegative initial terms.
 *
 * @Author      Terry Weiss
 * @Version     1.0, 14Mar2017
 */
public final class Fibonacci {

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
    public static int at(final int term, int a, int b) {
        if (term < 0) {
            throw new IllegalArgumentException("Term must be non-negative: " + term);
        }  else if (a < 0 || b < a) {
            throw new IllegalArgumentException("A and B must be positive, and B must be later "
                    + "in the sequence: a=" + a + " b=" + b);
        }


        if (term == 0) {
            return a;
        } else if (term == 1) {
            return b;
        } else if (b == 0) { // a <= b, so a must also be 0, so entire series will be 0
            return 0;
        }

        int val = 0;
        for (int i = 2; i <= term; ++i) {
            val = a + b;
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
    public static int at(final int term) {
        return at(term, 0, 1);
    }


    /**
     * Generates the next block of a sequence following two given values. The first term of the
     * block is the sum of <code>a</code> and <code>b</code>, and each following term is the sum of
     * the two before it. The two values <code>a</code> and <code>b</code> must be non-negative, and
     * <code>b</code> should be the value immediately after <code>a</code> in the sequence. There is
     * no validation beyond this, so it is possible to generate a block that could not legally
     * follow a block that ended with <code>a</code> and <code>b</code>. Blocks can be called
     * successively by reading the return array at index <code>length-2</code> for the next
     * <code>a</code> and <code>length-1</code> for the next <code>b</code>.
     *
     * @param   length  Length of the sequence block
     * @param   a       Two terms before block begins
     * @param   b       One term before block begins
     * @return          Array of values in the sequence block
     * @see             #sequence(int, int, int)
     */
    public static int[] nextBlock(final int length, int a, int b) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive: " + length);
        } else if (a < 0 || b < a) {
            throw new IllegalArgumentException("A and B must be non-negative, and B must be later "
                    + "in the sequence: a=" + a + " b=" + b);
        }

        int block[] = new int[length];
        int next;
        for (int i = 0; i < length; ++i) {
            next = a + b;
            a = b;
            b = next;
            block[i] = next;
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
    public static int[] sequence(final int length, int a, int b) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive: " + length);
        } else if (a < 0 || b < a) {
            throw new IllegalArgumentException("A and B must be non-negative, and B must be later "
                    + "in the sequence: a=" + a + " b=" + b);
        }


        int block[] = new int[length];
        block[0] = a;

        if (length >= 2) {
            block[1] = b;
        }

        if (length > 2) {
            int next;
            for (int i = 2; i < length; ++i) {
                next = a + b;
                a = b;
                b = next;
                block[i] = next;
            }
        }

        return block;
    }


    // Private constructor to prevent instantiation
    private Fibonacci() {}




    public static void main(String[] args) {
        // test at by finding first 10 terms
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; ++i) {
            sb.append(Fibonacci.at(i)).append(" ");
        }
        System.out.println(sb.toString());

        //test at when starting with different value
        sb.setLength(0);
        for (int i = 0; i < 10; ++i) {
            sb.append(Fibonacci.at(i, 5, 5)).append(" ");
        }
        System.out.println(sb.toString());

        //test building a block
        System.out.println(Arrays.toString(Fibonacci.sequence(10, 0, 1)));

        //test building a next block
        System.out.println(Arrays.toString(Fibonacci.nextBlock(10, 21, 34)));
    }
}
