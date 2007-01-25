/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class Clock {

    public static final Clock DEFAULT_CLOCK = new Clock() {
        /** serialVersionUID */

        public long timestamp() {
            return System.currentTimeMillis();
        }

        public long relativeTime() {
            return System.nanoTime();
        }
    };
    public static class DeterministicClock extends Clock {

        /** serialVersionUID */
        private static final long serialVersionUID = -7600850757932509321L;

        private final AtomicLong absolutTime = new AtomicLong();

        private final AtomicLong relativeTime = new AtomicLong();

        @Override
        public long timestamp() {
            return absolutTime.get();
        }

        public void incrementTimestamp() {
            absolutTime.incrementAndGet();
        }

        public void incrementRelativeTime() {
            relativeTime.incrementAndGet();
        }

        public void incrementRelativeTime(int amount) {
            relativeTime.addAndGet(amount);
        }
        public void incrementAbsolutTime(int amount) {
            absolutTime.addAndGet(amount);
        }
        @Override
        public long relativeTime() {
            return relativeTime.get();
        }

        public void setTimestamp(long amount) {
            absolutTime.set(amount);
        }

        public void setRelativeTime(long amount) {
            relativeTime.set(amount);
        }
    }
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
