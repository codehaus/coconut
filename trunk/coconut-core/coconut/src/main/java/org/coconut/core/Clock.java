package org.coconut.core;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class is used by most components in Coconut that relies on some kind og
 * time concept. The primary purpose is control whether we are using real world
 * calendars/clocks time (including daylight saving) or we used purely elapsed
 * time from some fixed but arbitrary time.
 * <p>
 * The following describes how this is used in Coconut Cache.
 * <p>
 * This class can also useful for making sure that tests run deterministically
 * each time.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public abstract class Clock implements Serializable {
    public static final Clock NANO_CLOCK = new Clock() {
        /** serialVersionUID */
        private static final long serialVersionUID = 6877144010765511690L;

        public long relativeTime() {
            return System.nanoTime();
        }

        public long absolutTime() {
            return relativeTime() + nanoEpochDelta;
        }
    };

    public static final Clock MILLI_CLOCK = new Clock() {
        /** serialVersionUID */
        private static final long serialVersionUID = 7306913846095922533L;

        public long relativeTime() {
            return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis());
        }

        public long absolutTime() {
            return relativeTime();
        }
    };

    public static class DeterministicClock extends Clock {

        /** serialVersionUID */
        private static final long serialVersionUID = -7600850757932509321L;

        private final AtomicLong relativeTime = new AtomicLong();

        private final AtomicLong absolutTime = new AtomicLong();

        @Override
        public long relativeTime() {
            return relativeTime.get();
        }

        @Override
        public long absolutTime() {
            return absolutTime.get();
        }

        public void setAbsolutTime(long amount) {
            absolutTime.set(amount);
        }
        public void setRelativeTime(long amount) {
            relativeTime.set(amount);
        }
        public void incrementRelativeTime() {
            relativeTime.incrementAndGet();
        }

        public void incrementRelativeTime(int amount) {
            relativeTime.addAndGet(amount);
        }

        public void incrementAbsolutTime() {
            absolutTime.incrementAndGet();
        }

    }

    private final static long nanoTimeInitial;

    private final static long milliTimeInitial;

    private final static long nanoEpochDelta;
    static {
        boolean finished = false;
        long m1 = 0;
        long n1 = 0;
        while (!finished) {
            m1 = System.currentTimeMillis();
            n1 = System.nanoTime();
            finished = m1 == System.currentTimeMillis();
        }
        nanoTimeInitial = n1;
        milliTimeInitial = m1;
        nanoEpochDelta = TimeUnit.NANOSECONDS.convert(milliTimeInitial,
                TimeUnit.MILLISECONDS)
                - System.nanoTime();
    }

    /**
     * This method can only be used to measure elapsed time and is not related
     * to any other notion of system or wall-clock time.
     */
    public abstract long relativeTime();

    public final long relativeTime(TimeUnit unit) {
        return TimeUnit.NANOSECONDS.convert(relativeTime(), unit);
    }

    /**
     * Returns the difference, measured in milliseconds, between the current
     * time and midnight, January 1, 1970 UTC.
     */
    public final long timestamp() {
        return absolutTime(TimeUnit.MILLISECONDS);
    }

    public abstract long absolutTime();

    public final long absolutTime(TimeUnit unit) {
        return TimeUnit.NANOSECONDS.convert(absolutTime(), unit);
    }

    /**
     * deadline is exclusive. current time=5, deadline =7, newTime=6->ok,
     * newTime=7->expired
     * 
     * @param deadline
     * @return
     */
    public boolean hasExpired(long deadline) {
        return relativeTime() >= deadline;
    }

    /**
     * Used to determine when a certain deadline occurs. For example, it is used
     * in coconut cache for calculating when an element should expire. The
     * default implementation uses elapsed time calculations.
     * 
     * @param timeout
     * @param unit
     * @return
     */
    public long getDeadlineFromNow(long timeout, TimeUnit unit) {
        return relativeTime() + unit.toNanos(timeout);
    }
}
