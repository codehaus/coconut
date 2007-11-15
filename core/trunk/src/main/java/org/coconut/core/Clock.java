/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Clock is a.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class Clock {

    /** An instance of the {@link DefaultClock}. */
    public static final Clock DEFAULT_CLOCK = new DefaultClock();

    /**
     * 
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
     * Returns the current time.
     */
    public abstract long timestamp();

    public abstract long relativeTime();

    public boolean isPassed(long timestamp) {
        return isPassed(timestamp(), timestamp);
    }

    public long getDeadlineFromNow(long timeout, TimeUnit unit) {
        return getDeadlineFromNow(unit.toMillis(timeout));
    }

    public long getDeadlineFromNow(long timeoutMS) {
        return timestamp() + timeoutMS;
    }

    public static boolean isPassed(long currentTimeStamp, long timeStampToCheck) {
        return currentTimeStamp >= timeStampToCheck;
    }
}
