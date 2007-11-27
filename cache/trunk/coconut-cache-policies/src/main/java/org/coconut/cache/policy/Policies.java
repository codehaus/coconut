/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy;

import org.coconut.cache.policy.paging.ClockPolicy;
import org.coconut.cache.policy.paging.FIFOPolicy;
import org.coconut.cache.policy.paging.LFUPolicy;
import org.coconut.cache.policy.paging.LIFOPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.policy.paging.MRUPolicy;
import org.coconut.cache.policy.paging.RandomPolicy;
import org.coconut.cache.spi.ReplacementPolicy;

/**
 * Factory methods for different {@link org.coconut.cache.spi.ReplacementPolicy}
 * implementations. This class provides shortcuts for the specific implementations of
 * policies defined in <tt>coconut.cache.policy</tt>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Policies {

    ///CLOVER:OFF
    /** Cannot instantiate. */
    private Policies() {}
    ///CLOVER:ON

    /**
     * Returns a new
     * {@link org.coconut.cache.policy.paging.ClockPolicy Clock Replacement Policy}.
     * 
     * @return a new Clock policy
     * @param <E>
     *            the type of data maintained by the policy
     */
    public static <E> ReplacementPolicy<E> newClock() {
        return new ClockPolicy<E>();
    }

    /**
     * Returns a new
     * {@link org.coconut.cache.policy.paging.FIFOPolicy FIFO Replacement Policy}.
     * 
     * @return a new FIFO policy
     * @param <E>
     *            the type of data maintained by the policy
     */
    public static <E> ReplacementPolicy<E> newFIFO() {
        return new FIFOPolicy<E>();
    }

    /**
     * Returns a new
     * {@link org.coconut.cache.policy.paging.LIFOPolicy LIFO Replacement Policy}.
     * 
     * @return a new LIFO policy
     * @param <E>
     *            the type of data maintained by the policy
     */
    public static <E> ReplacementPolicy<E> newLIFO() {
        return new LIFOPolicy<E>();
    }

    /**
     * Returns a new
     * {@link org.coconut.cache.policy.paging.LFUPolicy LFU Replacement Policy}.
     * 
     * @return a new LFU policy
     * @param <E>
     *            the type of data maintained by the policy
     */
    public static <E> ReplacementPolicy<E> newLFU() {
        return new LFUPolicy<E>();
    }

    /**
     * Returns a new
     * {@link org.coconut.cache.policy.paging.LRUPolicy LRU Replacement Policy}.
     * 
     * @return a new LRU policy
     * @param <E>
     *            the type of data maintained by the policy
     */
    public static <E> ReplacementPolicy<E> newLRU() {
        return new LRUPolicy<E>();
    }

    /**
     * Returns a new
     * {@link org.coconut.cache.policy.paging.MRUPolicy MRU Replacement Policy}.
     * 
     * @return a new MRU policy
     * @param <E>
     *            the type of data maintained by the policy
     */
    public static <E> ReplacementPolicy<E> newMRU() {
        return new MRUPolicy<E>();
    }

    /**
     * Returns a new
     * {@link org.coconut.cache.policy.paging.RandomPolicy Random Replacement Policy}.
     * 
     * @return a new Random policy
     * @param <E>
     *            the type of data maintained by the policy
     */
    public static <E> ReplacementPolicy<E> newRandom() {
        return new RandomPolicy<E>();
    }
}
