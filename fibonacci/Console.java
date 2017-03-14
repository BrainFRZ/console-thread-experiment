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
        STOPPED, PAUSED, RUNNING
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
     * Last starting term.
     */
    private int startTerm;


    /**
     * Console environment constructor. Launches the environment in a stopped state.
     */
    public Console() {
        state     = State.STOPPED;
        speed     = 0;
        maxValue  = 0;
        startTerm = 0;
        term0     = 0;
        term1     = 0;
    }
}
