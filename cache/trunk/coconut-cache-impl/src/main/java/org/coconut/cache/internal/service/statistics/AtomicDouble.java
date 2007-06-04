/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.statistics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@code double} value that may be updated atomically.  See the
 * {@link java.util.concurrent.atomic} package specification for
 * description of the properties of atomic variables. An
 * {@code AtomicDouble} is used in applications such as atomically
 * aggregating numbers, and cannot be used as a replacement
 * for a {@link java.lang.Double}. However, this class does extend
 * {@code Number} to allow uniform access by tools and utilities that
 * deal with numerically-based classes.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AtomicDouble extends Number {

    /** serialVersionUID */
    private static final long serialVersionUID = 3159267328827547102L;

    /* The AtomicLong we are wrapping */
    private final AtomicLong al;

    //Replace AtomicLong with AtomicReference, much faster..
    
    /**
     * Create a new AtomicLong with initial value <tt>0</tt>.
     */
    public AtomicDouble() {
        this(0);
    }
    
    /**
     * Create a new AtomicDouble with the given initial value.
     * 
     * @param initialValue
     *            the initial value
     */
    public AtomicDouble(double initialValue) {
        al = new AtomicLong(c(initialValue));
    }


    static long c(double i) {
        return Double.doubleToRawLongBits(i);
    }

    /**
     * Get the current value.
     * 
     * @return the current value
     */
    public final double get() {
        return Double.longBitsToDouble(al.get());
    }

    /**
     * Set to the given value.
     * 
     * @param newValue
     *            the new value
     */
    public final void set(double newValue) {
        al.set(c(newValue));
    }

    /**
     * Set to the give value and return the old value.
     * 
     * @param newValue
     *            the new value
     * @return the previous value
     */
    public final double getAndSet(double newValue) {
        return Double.longBitsToDouble(al.getAndSet(c(newValue)));
    }

    /**
     * Atomically set the value to the given updated value if the current value
     * <tt>==</tt> the expected value.
     * 
     * @param expect
     *            the expected value
     * @param update
     *            the new value
     * @return true if successful. False return indicates that the actual value
     *         was not equal to the expected value.
     */
    public final boolean compareAndSet(double expect, double update) {
        return al.compareAndSet(c(expect), c(update));
    }

    /**
     * Atomically set the value to the given updated value if the current value
     * <tt>==</tt> the expected value. May fail spuriously.
     * 
     * @param expect
     *            the expected value
     * @param update
     *            the new value
     * @return true if successful.
     */
    public final boolean weakCompareAndSet(double expect, double update) {
        return al.weakCompareAndSet(c(expect), c(update));
    }

    /**
     * Atomically increment by one the current value.
     * 
     * @return the previous value
     */
    public final double getAndIncrement() {
        while (true) {
            double current = get();
            double next = current + 1;
            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically decrement by one the current value.
     * 
     * @return the previous value
     */
    public final double getAndDecrement() {
        while (true) {
            double current = get();
            double next = current - 1;
            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically add the given value to current value.
     * 
     * @param delta
     *            the value to add
     * @return the previous value
     */
    public final double getAndAdd(double delta) {
        while (true) {
            double current = get();
            double next = current + delta;
            if (compareAndSet(current, next))
                return current;
        }
    }

    /**
     * Atomically increment by one the current value.
     * 
     * @return the updated value
     */
    public final double incrementAndGet() {
        for (;;) {
            double current = get();
            double next = current + 1;
            if (compareAndSet(current, next))
                return next;
        }
    }

    /**
     * Atomically decrement by one the current value.
     * 
     * @return the updated value
     */
    public final double decrementAndGet() {
        for (;;) {
            double current = get();
            double next = current - 1;
            if (compareAndSet(current, next))
                return next;
        }

    }

    /**
     * Atomically add the given value to current value.
     * 
     * @param delta
     *            the value to add
     * @return the updated value
     */
    public final double addAndGet(double delta) {
        for (;;) {
            double current = get();
            double next = current + delta;
            if (compareAndSet(current, next))
                return next;
        }
    }

    /**
     * Returns the String representation of the current value.
     * 
     * @return the String representation of the current value.
     */
    public String toString() {
        return Double.toString(get());
    }

    /**
     * @see java.lang.Number#intValue()
     */
    public int intValue() {
        return (int) get();
    }

    /**
     * @see java.lang.Number#longValue()
     */
    public long longValue() {
        return (long) get();
    }

    /**
     * @see java.lang.Number#floatValue()
     */
    public float floatValue() {
        return (float) get();
    }

    /**
     * @see java.lang.Number#doubleValue()
     */
    public double doubleValue() {
        return get();
    }
}
