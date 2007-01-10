/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AtomicDouble extends Number {

    /** serialVersionUID */
    private static final long serialVersionUID = 3159267328827547102L;

    private final AtomicLong al;

    /**
     * Create a new AtomicLong with initial value <tt>0</tt>.
     */
    public AtomicDouble() {
        this(0);
    }
    
    /**
     * Create a new AtomicLong with the given initial value.
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
        return Double.longBitsToDouble(al.getAndIncrement());
    }

    /**
     * Atomically decrement by one the current value.
     * 
     * @return the previous value
     */
    public final double getAndDecrement() {
        return Double.longBitsToDouble(al.getAndDecrement());
    }

    /**
     * Atomically add the given value to current value.
     * 
     * @param delta
     *            the value to add
     * @return the previous value
     */
    public final double getAndAdd(double delta) {
        return Double.longBitsToDouble(al.getAndAdd(c(delta)));
    }

    /**
     * Atomically increment by one the current value.
     * 
     * @return the updated value
     */
    public final double incrementAndGet() {
        return Double.longBitsToDouble(al.incrementAndGet());
    }

    /**
     * Atomically decrement by one the current value.
     * 
     * @return the updated value
     */
    public final double decrementAndGet() {
        return Double.longBitsToDouble(al.decrementAndGet());

    }

    /**
     * Atomically add the given value to current value.
     * 
     * @param delta
     *            the value to add
     * @return the updated value
     */
    public final double addAndGet(double delta) {
        return Double.longBitsToDouble(al.addAndGet(c(delta)));
    }

    /**
     * Returns the String representation of the current value.
     * 
     * @return the String representation of the current value.
     */
    public String toString() {
        return Double.toString(get());
    }

    public int intValue() {
        return (int) get();
    }

    public long longValue() {
        return (long) get();
    }

    public float floatValue() {
        return (float) get();
    }

    public double doubleValue() {
        return get();
    }
}
