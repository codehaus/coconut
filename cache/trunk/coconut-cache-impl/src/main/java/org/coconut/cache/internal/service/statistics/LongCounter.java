/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.statistics;

import java.util.concurrent.atomic.AtomicLong;

import org.coconut.management.annotation.ManagedOperation;

/**
 * noget federe end det åndsvage event handling.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class LongCounter {

    public LongCounter(String name, String description) {
    // super(name, description);
    }


    /**
     * Atomically adds the given value to the current value.
     * 
     * @param delta
     *            the value to add
     * @return the updated value
     */
    public abstract long addAndGet(long delta);

    /**
     * Gets the current value.
     * 
     * @return the current value
     */
    public abstract long get();

    /**
     * Atomically increments by one the current value.
     * 
     * @return the updated value
     */
    public abstract long incrementAndGet();

    /**
     * Sets to the given value.
     * 
     * @param newValue
     *            the new value
     */
    public abstract void set(long newValue);

    /**
     * Returns the String representation of the current value.
     * 
     * @return the String representation of the current value.
     */
    public String toString() {
        return Long.toString(get());
    }


    @ManagedOperation(defaultValue = "reset $name", description = "Sets the value of $name to 0")
    public void reset() {
        set(0);
    }
   
    final static class ConcurrentLongCounter extends LongCounter {

        private final AtomicLong l = new AtomicLong();

        /**
         * @param name
         * @param description
         */
        ConcurrentLongCounter(String name, String description) {
            super(name, description);
        }

        @ManagedOperation(defaultValue = "reset $name", description = "Sets the value of $name to 0")
        public synchronized void reset() {
            l.set(0);
        }

        /**
         * Atomically adds the given value to the current value.
         * 
         * @param delta
         *            the value to add
         * @return the updated value
         */
        public long addAndGet(long delta) {
            long result = l.addAndGet(delta);
            return result;
        }

        /**
         * Gets the current value.
         * 
         * @return the current value
         */
        public long get() {
            return l.get();
        }


        /**
         * Atomically increments by one the current value.
         * 
         * @return the updated value
         */
        public long incrementAndGet() {
            return l.incrementAndGet();
        }

        /**
         * Sets to the given value.
         * 
         * @param newValue
         *            the new value
         */
        public void set(long newValue) {
            l.set(newValue);
        }


    }

    public static LongCounter newConcurrent(String string, String string2) {
        return new ConcurrentLongCounter(string, string2);
    }
}
