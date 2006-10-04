/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.Serializable;

/**
 * A class used for prioritizing. 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Priority implements Serializable, Comparable<Priority> {

    //TODO define a maximum priority, usefull for determining which
    //bucket to put stuff into
    //TODO do we want log priorities?
    //Pro
    //Con
    /** serialVersionUID */
    private static final long serialVersionUID = 5279037606038599422L;

    public static final Priority HIGHEST = new Priority(1 << 12, "Highest");

    public static final Priority HIGH = new Priority(1 << 10, "High");

    public static final Priority ABOVE_NORMAL = new Priority(1 << 8, "Above-Normal");

    public static final Priority NORMAL = new Priority(1 << 6, "Normal");

    public static final Priority BELOW_NORMAL = new Priority(1 << 4, "Below-Normal");

    public static final Priority LOW = new Priority(1 << 2, "Low");

    public static final Priority LOWEST = new Priority(1, "Lowest");

    /* The actual priority */
    private final int priority;

    private final String name;

    public Priority(int priority) {
        this(priority, "Custom-Priority");
    }

    private Priority(int priority, String name) {
        if (priority < 0) {
            throw new IllegalArgumentException("priority must be positive");
        }
        this.priority = priority;
        this.name = name;
    }

    /**
     * Returns the priority as a integer
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof Priority)
                && ((Priority) other).priority == this.priority;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return priority;
    }

    @Override
    public String toString() {
        return "Priority = " + name;
    }

    /**
     * @param anotherPriority
     * @return
     */
    public int compareTo(Priority anotherPriority) {
        int thisPrio = this.priority;
        int anotherPrio = anotherPriority.priority;
        return (thisPrio < anotherPrio ? -1 : (thisPrio == anotherPrio ? 0 : 1));
    }
}
