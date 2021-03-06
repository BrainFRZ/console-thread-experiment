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
     * Minimum period between blocks display is 200 milliseconds (0.2 seconds).
     *
     * @See TimeUnit#Milliseonds
     */
    public static final long MIN_PERIOD = 200;

    /**
     * Default period between blocks display is 1000 milliseconds (1 second).
     *
     * @See TimeUnit#Milliseonds
     */
    public static final long DEFAULT_PERIOD = 1000;

    /**
     * Default block size per display is 10 terms.
     */
    public static final int DEFAULT_BLOCK_SIZE = 5;


    /**
     * The sequence will not go higher than this value. Null will be indefinite.
     */
    private BigInteger maxValue;

    /**
     * Period between display of blocks in milliseconds. 0 will use {@link #MIN_PERIOD}.
     *
     * @See TimeUnit#Milliseonds
     */
    private long period;

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
        period     = DEFAULT_PERIOD;
        maxValue   = null;
        startTerm0 = BigInteger.ZERO;
        startTerm1 = BigInteger.ONE;
        term0      = BigInteger.ZERO;
        term1      = BigInteger.ONE;
        blockSize  = DEFAULT_BLOCK_SIZE;
    }



    private ArrayList<BigInteger> buildBlock(boolean isFirst) {
        ArrayList<BigInteger> block;

        if (isFirst) {
            block = Fibonacci.sequence(blockSize, startTerm0, startTerm1);
        } else {
            block = Fibonacci.nextBlock(blockSize, term0, term1);
        }
        term0 = block.get(blockSize - 2);
        term1 = block.get(blockSize - 1);

        return block;
    }

    private void exit() {
        sch.shutdownNow();
        state = State.EXIT;
    }

    private void pause() {
        state = State.PAUSED;
        futureBlock.cancel(false);
    }

    private void reset() {
        stop();
        setStart(BigInteger.ZERO, BigInteger.ONE);
        maxValue = null;
    }

    private void scheduleFutureBlock(long delay) {
        Runnable generateNextBlock = new Runnable() {
            @Override
            public void run() {
                System.out.println();
                ArrayList<BigInteger> block = buildBlock(false);

                printBlock(block, maxValue);
                if (maxValue != null && term1.compareTo(maxValue) >= 0)
                {
                    System.out.println("Sequence completed.");
                    stop();
                }

                System.out.print(state.prompt());
            }
        };

        if (futureBlock != null && !futureBlock.isDone()) {
            long currentDelay = futureBlock.getDelay(TimeUnit.MILLISECONDS);
            delay = Math.max(0, delay - currentDelay);
            futureBlock.cancel(true);
        }
        futureBlock = sch.scheduleAtFixedRate(generateNextBlock, delay, period,
                                                TimeUnit.MILLISECONDS);
    }

    private void setStart(BigInteger t0, BigInteger t1) {
        startTerm0 = t0;
        startTerm1 = t1;
    }

    private void start(boolean first) {
        try {
            ArrayList<BigInteger> firstBlock = buildBlock(first);
            state = State.RUNNING;
            System.out.println(state.prompt());
            printBlock(firstBlock, maxValue);
        } catch (IllegalArgumentException e) {
            System.out.println("Syntax: START [term1 term2]");
            System.out.println(e.getMessage());
            System.out.println("See HELP for more details.");
            return;
        }

        scheduleFutureBlock(period);
    }

    private void stop() {
        state = State.STOPPED;
        futureBlock.cancel(true);
    }



    static void printBlock(final ArrayList<BigInteger> block, final BigInteger max) {
        StringBuilder sb = new StringBuilder("");
        boolean add = true;

        BigInteger next;
        for (int i = 0; i < block.size() && add; ++i) {
            next = block.get(i);
            if (max != null && next.compareTo(max) > 0) {
                add = false;
            } else {
                sb.append(next).append(" ");
            }
        }

        String out = sb.toString().trim();
        if (!out.isEmpty()) {
            System.out.println(out);
        }
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
            case "help":
                cmdHelp();
                break;
            case "start":
                cmdStart(arg);
                break;
            case "max":
                cmdMax(arg);
                break;
            case "speed":
                cmdSpeed(arg);
                break;
            case "pause":
                cmdPause();
                break;
            case "stop":
                cmdStop();
                break;
            case "reset":
                cmdReset();
                break;
            case "restart":
                cmdRestart();
                break;
            case "exit":
                exit();
                break;
            default:
                System.out.println("Unrecognized command: " + cmd);
                break;
        }
    }

    private static void cmdHelp() {
        final String HELP =

        "    HELP        Lists each command and its description (this list)\n\n"                +

        "    MAX [#]     Sets process to end when past given value\n\n"                         +

        "    PAUSE       Pauses process if running, or message is displayed\n\n"                +

        "    RESET       Stops process and resets environment to default starting state\n\n"    +

        "    RESTART     If running or paused: immediately restarts from starting terms\n"      +
        "                If not running: message is displayed\n\n"                              +

        "    SPEED [#]   Changes the time in seconds allotted for each iteration. If no\n"      +
        "                value is given, the speed is reset to default.\n\n"                    +

        "    START [# #] If values are negative or the second is less than the first, an\n"     +
        "                    error message is displayed.\n"                                     +
        "                If stopped, starts at given value or 0 and 1.\n"                       +
        "                If running, starts program execution from given value. If\n"           +
        "                    no value is given, message is displayed\n"                         +
        "                If paused, process resumes at given value or last value.\n\n"          +

        "    STOP        Stops the process if running, or displays a message\n";

        System.out.println(HELP);
    }

    private void cmdMax(String arg) {
        if (arg.isEmpty()) {
            maxValue = null;
            System.out.println("Max value has been cleared.");
        }

        try {
            maxValue = new BigInteger(arg);
        } catch (NumberFormatException e) {
            System.out.println("Syntax: MAX [max value]");
            System.out.println("Max value must be an integer. See HELP for details.");
        }
    }

    private void cmdPause() {
        if (state != State.RUNNING) {
            System.out.println("No sequence is currently running.");
            return;
        }

        pause();
        System.out.println("Pausing sequence ...");
    }

    private void cmdReset() {
        reset();
        System.out.println("Environment reset.");
    }

    private void cmdRestart() {
        if (state == State.STOPPED) {
            System.out.println("No sequence is currently active.");
            return;
        }

        if (state == State.RUNNING) {
            System.out.println("Stopping current sequence ...");
            futureBlock.cancel(true);
        } else if (state == State.PAUSED) {
            setStart(startTerm0, startTerm1);
            System.out.println("Restarting current sequence ...");
        }

        start(true);
    }

    private void cmdSpeed(String arg) {
        double input;
        if (arg.isEmpty()) {
            input = MIN_PERIOD;
        } else {
            try {
                input = Double.parseDouble(arg);
            } catch (NumberFormatException e) {
                System.out.println("Syntax: SPEED [period]");
                System.out.println("Period must be a number. See HELP for details.");
                return;
            }
        }

        calculatePeriod(input);
        System.out.println("Speed changed to " + period + "ms.");

        if (state == State.RUNNING) {
            scheduleFutureBlock(period);
        }
    }

    private void cmdStart(String args) {
        if (args.isEmpty()) {
            if (state == State.RUNNING) {
                System.out.println("The sequence is already running. "
                                   + "Speed can be adjusted with SPEED.");
                return;
            }

            if (state == State.PAUSED) {
                System.out.println("Resuming sequence ...");
                setStart(term0, term1);
            } else if (state == State.STOPPED) {
                System.out.println("Starting standard Fibonacci sequence ...");
                startTerm0 = Fibonacci.DEFAULT_0;
                startTerm1 = Fibonacci.DEFAULT_1;
            }
        }

        else {
            String[] terms = args.split(" ");
            if (terms.length != 2) {
                System.out.println("Syntax: START [term1 term2]");
                return;
            }

            BigInteger t0, t1;
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
        }

        boolean firstBlock = state == State.STOPPED;
        start(firstBlock);
    }

    private void cmdStop() {
        if (state == State.STOPPED) {
            System.out.println("There is currently no sequence running.");
            return;
        }

        stop();
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

    private void calculatePeriod(double newPeriod) {
        period = (int)(newPeriod * 1000);

        if (period < MIN_PERIOD) {
            period = MIN_PERIOD;
        }
    }




    public static void main(String[] args) {
        Console console = new Console();
        console.run();
    }
}
