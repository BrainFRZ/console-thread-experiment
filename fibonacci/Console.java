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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;


/**
 * A console driver that generates Fibonacci sequences in a separate thread. The user may enter
 * commands during runtime as specified in the README file.
 *
 * @Author      Terry Weiss
 * @Version     1.0, 14Mar2017
 */
public class Console {

    /**
     * List of possible runtime states.
     */
    public enum State {
        STOPPED {
            @Override
            public String prompt() {
                return "\n > ";
            }
        },
        PAUSED {
            @Override
            public String prompt() {
                return "\n*> ";
            }
        },
        RUNNING {
            @Override
            public String prompt() {
                return "\n>> ";
            }
        },
        EXIT {
            @Override
            public String prompt() {
                return "";
            }
        };

        public abstract String prompt();
    }

    /**
     * Maximum number of blocks displayed per second is 5.
     */
    public static final int MAX_SPEED = 5;

    /**
     * Minimum delay is 1 second divided by max speed in milliseconds.
     *
     * @See TimeUnit#Milliseonds
     */
    public static final int MIN_DELAY = 1000 / MAX_SPEED;

    /**
     * Default block size per display is 10 terms.
     */
    public static final int DEFAULT_BLOCK_SIZE = 5;


    /**
     * The sequence will not go higher than this value. 0 will be {@link Integer#MAX_VALUE}.
     */
    private BigInteger maxValue;

    /**
     * Number of blocks displayed per second. 0 will use {@link #MAX_SPEED}.
     */
    private int speed;

    /**
     * Current state of runtime.
     */
    private State state;

    /**
     * Second-last term used.
     */
    private BigInteger term0;

    /**
     * Last term used.
     */
    private BigInteger term1;

    /**
     * Last starting first term.
     */
    private BigInteger startTerm0;

    /**
     * Last starting second term.
     */
    private BigInteger startTerm1;

    /**
     * Current block size.
     */
    private int blockSize;

    /**
     * Scanner utility object
     */
    private static final Scanner cin = new Scanner(System.in);

    /**
     * Executor thread object
     */
    private static final ScheduledThreadPoolExecutor sch = new ScheduledThreadPoolExecutor(1);

    /**
     * Future scheduled block generation
     */
    private ScheduledFuture futureBlock;



    /**
     * Console environment constructor. Launches the environment in a stopped state.
     */
    public Console() {
        state      = State.STOPPED;
        speed      = 0;
        maxValue   = null;
        startTerm0 = BigInteger.ZERO;
        startTerm1 = BigInteger.ONE;
        term0      = BigInteger.ZERO;
        term1      = BigInteger.ONE;
        blockSize  = DEFAULT_BLOCK_SIZE;
    }



    static void printBlock(final ArrayList<BigInteger> block) {
        int last = block.size() - 1;
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < last; ++i) {
            sb.append(block.get(i)).append(" ");
        }

        sb.append(block.get(last));

        System.out.println(sb.toString());
    }



    private void command(String input) {
        input = input.trim().toLowerCase();
        if (input.isEmpty()) {
            return;
        }

        String cmd, arg;
        int split = input.indexOf(' ');
        if (split == -1) { // no argument
            cmd = input;
            arg = "";
        } else {
            cmd = input.substring(0, split);
            arg = input.substring(split+1);
        }

        switch (cmd) {
            case "start":
                start(arg);
                break;
            case "pause":
                pause();
                break;
            case "stop":
                stop();
                break;
            case "exit":
                exit();
                break;
            default:
                System.out.println("Unrecognized command: " + cmd);
                break;
        }
    }

    private void exit() {
        sch.shutdownNow();
        state = State.EXIT;
    }

    private void pause() {
        if (state != State.RUNNING) {
            System.out.println("No sequence is currently running.");
            return;
        }

        System.out.println("Pausing sequence ...");
        // TODO: Pause process
        state = State.PAUSED;
    }

    private void start(String args) {
        if (args.isEmpty()) {
            if (state == State.RUNNING) {
                System.out.println("The sequence is already running. "
                                   + "Speed can be adjusted with SPEED.");
                return;
            }

            if (state == State.PAUSED) {
                System.out.println("Resuming sequence ...");
                startTerm0 = term0;
                startTerm1 = term1;
            } else if (state == State.STOPPED) {
                System.out.println("Starting standard Fibonacci sequence ...");
                startTerm0 = Fibonacci.DEFAULT_0;
                startTerm1 = Fibonacci.DEFAULT_1;
                term0      = startTerm0;
                term1      = startTerm1;
            }
        }

        else {
            String[] terms = args.split(" ");
            if (terms.length != 2) {
                System.out.println("Syntax: START [term1 term2]");
                return;
            }

            BigInteger t0 = BigInteger.ZERO, t1 = BigInteger.ONE;
            try {
                t0 = new BigInteger(terms[0]);
                t1 = new BigInteger(terms[1]);
            } catch (NumberFormatException e) {
                System.out.println("Syntax: START [term1 term2]");
                System.out.println("Terms must be integers. See HELP for more details.");
                return;
            }

            startTerm0 = t0;
            startTerm1 = t1;
            term0      = t0;
            term1      = t1;
        }

        try {
            ArrayList<BigInteger> firstBlock = Fibonacci.sequence(blockSize, startTerm0, startTerm1);
            term0 = firstBlock.get(blockSize - 2);
            term1 = firstBlock.get(blockSize - 1);
            state = State.RUNNING;
            System.out.print(state.prompt());
            printBlock(firstBlock);
        } catch (IllegalArgumentException e) {
            System.out.println("Syntax: START [term1 term2]");
            System.out.println(e.getMessage());
            System.out.println("See HELP for more details.");
            return;
        }

        Runnable generateNextBlock = new Runnable() {
            @Override
            public void run() {
                ArrayList<BigInteger> block = Fibonacci.nextBlock(blockSize, term0, term1, maxValue);
                System.out.println();
                printBlock(block);
                term0 = block.get(blockSize - 2);
                term1 = block.get(blockSize - 1);

                System.out.print(state.prompt());
            }
        };

        int period = calculatePeriod();
        if (futureBlock != null && !futureBlock.isDone()) {
            futureBlock.cancel(true);
        }
        futureBlock = sch.scheduleAtFixedRate(generateNextBlock, 0, period,
                                                TimeUnit.MILLISECONDS);
    }

    private void stop() {
        if (state == State.STOPPED) {
            System.out.println("There is currently no sequence running.");
            return;
        }

        state = State.STOPPED;
        term0 = startTerm0;
        term1 = startTerm1;
        futureBlock.cancel(true);
        System.out.println("The sequence has been stopped.");
    }



    public void run() {
        System.out.println("                          Fibonacci Sequence Generator");
        System.out.println("    Type HELP for more information.");

        while (state != State.EXIT) {
            System.out.print(state.prompt());
            String cmd = cin.nextLine();
            command(cmd);
        }
    }

    private int calculatePeriod() {
        int delay;

        if (speed == 0 || speed >= MAX_SPEED) {
            delay = 1000 / MAX_SPEED;
        } else {
            delay = 1000 / speed;
        }

        return delay;
    }




    public static void main(String[] args) {
        Console console = new Console();
        console.run();
    }
}
