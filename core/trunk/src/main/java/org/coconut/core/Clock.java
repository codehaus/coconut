/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Clock is a
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class Clock {

    public static final Clock DEFAULT_CLOCK = new DefaultClock();

    static class DefaultClock extends Clock implements Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -3343971832371995608L;

        public long timestamp() {
            return System.currentTimeMillis();
        }

        public long relativeTime() {
            return System.nanoTime();
        }
    }

    /**
     * DeterministicClock is useful for testing components that rely on time.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
     * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
     */
    public static class DeterministicClock extends Clock implements Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -7045902747103949579L;

        private final AtomicLong timestamp = new AtomicLong();

        private final AtomicLong relativeTime = new AtomicLong();

        @Override
        public long timestamp() {
            return timestamp.get();
        }

        public void incrementTimestamp() {
            timestamp.incrementAndGet();
        }

        public void incrementRelativeTime() {
            relativeTime.incrementAndGet();
        }

        public void incrementRelativeTime(int amount) {
            relativeTime.addAndGet(amount);
        }

        public void incrementTimestamp(int amount) {
            timestamp.addAndGet(amount);
        }

        @Override
        public long relativeTime() {
            return relativeTime.get();
        }

        public void setTimestamp(long amount) {
            timestamp.set(amount);
        }

        public void setRelativeTime(long amount) {
            relativeTime.set(amount);
        }
    }

    /**
     * Returns the current time.
     */
    public abstract long timestamp();

    public abstract long relativeTime();

    public boolean isPassed(long timestamp) {
        return timestamp() >= timestamp;
    }

    public long getDeadlineFromNow(long timeout, TimeUnit unit) {
        return getDeadlineFromNow(unit.toMillis(timeout));
    }

    public long getDeadlineFromNow(long timeoutMS) {
        return timestamp() + timeoutMS;
    }
}
