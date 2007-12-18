/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A Clock is used to create timestamps and measure time in a deterministic manner. For
 * example, {@link DeterministicClock} which can be used while testing applications, to
 * make sure...
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class Clock {

    /** An instance of the {@link DefaultClock}. */
    public static final Clock DEFAULT_CLOCK = new DefaultClock();

    /**
     * The default implementation of Clock. {@link Clock#timestamp()} returns a value
     * obtained from {@link System#currentTimeMillis()}. {@link Clock#relativeTime()}
     * returns a value obtained from {@link System#nanoTime()}.
     */
    public final static class DefaultClock extends Clock implements Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -3343971832371995608L;

        /** {@inheritDoc} */
        public long timestamp() {
            return System.currentTimeMillis();
        }

        /** {@inheritDoc} */
        public long relativeTime() {
            return System.nanoTime();
        }
    }

    /**
     * DeterministicClock is useful for testing components that rely on time.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
     * @version $Id$
     */
    public static class DeterministicClock extends Clock implements Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -7045902747103949579L;

        /** The current timestamp. */
        private final AtomicLong timestamp = new AtomicLong();

        /** The current relative time. */
        private final AtomicLong relativeTime = new AtomicLong();

        /** {@inheritDoc} */
        @Override
        public long timestamp() {
            return timestamp.get();
        }

        /** Increments the current timestamp by 1. */
        public void incrementTimestamp() {
            timestamp.incrementAndGet();
        }

        /** Increments the current relative time by 1. */
        public void incrementRelativeTime() {
            relativeTime.incrementAndGet();
        }

        /**
         * Increments the current relative time by the specified amount.
         * 
         * @param amount
         *            the amount to increment the current relative time with
         */
        public void incrementRelativeTime(int amount) {
            relativeTime.addAndGet(amount);
        }

        /**
         * Increments the current timestamp by the specified amount.
         * 
         * @param amount
         *            the amount to increment the current timestamp with
         */
        public void incrementTimestamp(int amount) {
            timestamp.addAndGet(amount);
        }

        /** {@inheritDoc} */
        @Override
        public long relativeTime() {
            return relativeTime.get();
        }

        /**
         * Sets the current timestamp.
         * 
         * @param timestamp
         *            the timestamp to set
         */
        public void setTimestamp(long timestamp) {
            this.timestamp.set(timestamp);
        }

        /**
         * Sets the current relative time.
         * 
         * @param relativeTime
         *            the relativeTime to set
         */
        public void setRelativeTime(long relativeTime) {
            this.relativeTime.set(relativeTime);
        }
    }

    /**
     * Returns the current time in milliseconds. Note that while the unit of time of the
     * return value is a millisecond, the granularity of the value depends on the
     * underlying operating system and may be larger. For example, many operating systems
     * measure time in units of tens of milliseconds.
     * <p>
     * See the description of the class <code>Date</code> for a discussion of slight
     * discrepancies that may arise between "computer time" and coordinated universal time
     * (UTC).
     * 
     * @return the difference, measured in milliseconds, between the current time and
     *         midnight, January 1, 1970 UTC.
     * @see java.util.Date
     */
    public abstract long timestamp();

    /**
     * Returns the current value of the most precise available system timer, in
     * nanoseconds.
     * <p>
     * This method can only be used to measure elapsed time and is not related to any
     * other notion of system or wall-clock time. The value returned represents
     * nanoseconds since some fixed but arbitrary time (perhaps in the future, so values
     * may be negative). This method provides nanosecond precision, but not necessarily
     * nanosecond accuracy. No guarantees are made about how frequently values change.
     * Differences in successive calls that span greater than approximately 292 years (2<sup>63</sup>
     * nanoseconds) will not accurately compute elapsed time due to numerical overflow.
     * <p>
     * For example, to measure how long some code takes to execute:
     * 
     * <pre>
     * long startTime = System.nanoTime();
     * 
     * // ... the code being measured ...
     * long estimatedTime = System.nanoTime() - startTime;
     * </pre>
     * 
     * @return The current value of the system timer, in nanoseconds.
     */
    public abstract long relativeTime();

    public boolean isPassed(long timestamp) {
        return isPassed(timestamp(), timestamp);
    }

    public long getDeadlineFromNow(long timeout, TimeUnit unit) {
        return timestamp() + unit.toMillis(timeout);
    }

    public static boolean isPassed(long currentTimeStamp, long timeStampToCheck) {
        return currentTimeStamp >= timeStampToCheck;
    }
}
