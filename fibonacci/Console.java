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
     * Maximum number of blocks displayed per second.
     */
    public static final int MAX_SPEED = 5;


    /**
     * The sequence will not go higher than this value. 0 will be {@link Integer#MAX_VALUE}.
     */
    private int maxValue;

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
    private int term0;

    /**
     * Last term used.
     */
    private int term1;

    /**
     * Last starting first term.
     */
    private int startTerm0;

    /**
     * Last starting second term.
     */
    private int startTerm1;

    /**
     * Scanner utility object
     */
    private static final Scanner cin = new Scanner(System.in);



    /**
     * Console environment constructor. Launches the environment in a stopped state.
     */
    public Console() {
        state      = State.STOPPED;
        speed      = 0;
        maxValue   = 0;
        startTerm0 = 0;
        startTerm1 = 1;
        term0      = 0;
        term1      = 1;
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
            }
            else if (state == State.STOPPED) {
                System.out.println("Starting standard Fibonacci sequence ...");
                startTerm0 = Fibonacci.DEFAULT_0;
                startTerm1 = Fibonacci.DEFAULT_1;
            }

            state = State.RUNNING;
            // TODO: Start generator thread
        }

        else {
            String[] terms = args.split(" ");
            if (terms.length != 2) {
                System.out.println("Syntax: START [term1 term2]");
                return;
            }

            int t0 = 0, t1 = 1;
            try {
                t0 = Integer.parseInt(terms[0]);
                t1 = Integer.parseInt(terms[1]);
            } catch (NumberFormatException e) {
                System.out.println("Syntax: START [term1 term2]");
                System.out.println("Terms must be integers. See HELP for more details.");
//              return;
            }

/*
            startTerm0 = t0;
            startTerm1 = t1;
            try {
                TODO: Start generator thread
            } catch (IllegalArgumentException e) {
                System.out.println("Syntax: START [term1 term2]");
                System.out.println(e.getMessage());
                System.out.println("See HELP for more details.");
                return;
            }
*/
        }
    }

    private void stop() {
        if (state == State.STOPPED) {
            System.out.println("There is currently no sequence running.");
            return;
        }

        state = State.STOPPED;
        term0 = startTerm0;
        term1 = startTerm1;
        //TODO: stop sequence thread
        System.out.println("The sequence has been stopped.");
    }



    public void run() {
        System.out.println("                          Fibonacci Sequence Generator");
        System.out.println("    Type HELP for more information.");

        while (state != State.EXIT) {
            System.out.print(state.prompt());
            command(cin.nextLine());
        }
    }




    public static void main(String[] args) {
        Console console = new Console();
        console.run();
    }
}
