/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.pool;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Bla bla bla
 * 
 * @version $Id$
 */
public interface Pool<E> {

    /**
     * Acquires a ressource from this pool, blocking until one is available, or
     * the thread is {@linkplain Thread#interrupt interrupted}.
     * <p>
     * Acquires a ressource, if one is available and returns immediately,
     * reducing the number of available ressources by one.
     * <p>
     * If no ressource is currently available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until one of two
     * things happens:
     * <ul>
     * <li>Some other thread releases a ressource to this pool and the current
     * thread is next to be assigned a ressource; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread.
     * </ul>
     * <p>
     * If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting for a
     * permit,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * 
     * @throws InterruptedException
     *             if the current thread is interrupted
     */

    E borrow() throws InterruptedException;

    /**
     * Acquires the given number of ressources from this pool, blocking until
     * all are available, or the thread is
     * {@linkplain Thread#interrupt interrupted}.
     * <p>
     * Acquires the given number of ressources, if they are available, and
     * returns immediately, reducing the number of available ressources by the
     * given amount.
     * <p>
     * If insufficient ressources are available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until one of two
     * things happens:
     * <ul>
     * <li>Some other thread releases a ressource(s) to this pool, the current
     * thread is next to be assigned permits and the number of available ressources
     * satisfies this request; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread.
     * </ul>
     * <p>
     * If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting for a
     * permit,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared. Any ressources that were to be assigned to
     * this thread are instead assigned to other threads trying to acquire
     * ressources, as if ressources had been made available by a call to
     * {@link #release()}.
     * 
     * @param ressources
     *            the number of ressources to acquire
     * @throws InterruptedException
     *             if the current thread is interrupted
     * @throws IllegalArgumentException
     *             if {@code ressources} is negative
     */
    Collection<E> borrow(int ressources) throws InterruptedException;

    /**
     * Releases a ressource, returning it to the pool.
     * <p>
     * Releases a ressource, increasing the number of available ressources by
     * one. If any threads are trying to acquire a ressource, then one is
     * selected and given the ressource that was just released. That thread is
     * (re)enabled for thread scheduling purposes.
     */
    void returnToPool(E buffer);

    void returnToPool(Collection<? super E> buffers);

    /**
     * Acquires a ressource from this pool, only if one is available at the time
     * of invocation.
     * <p>
     * Acquires a ressource, if one is available and returns immediately, with
     * the ressource, reducing the number of available ressources by one.
     * <p>
     * If no ressource is available then this method will return immediately
     * with the value <tt>null</tt>.
     * 
     * @return a ressource if one was acquired was acquired and <tt>null</tt>
     *         otherwise
     */
    E tryBorrow();

    /**
     * Acquires a ressource from this pool, if one becomes available within the
     * given waiting time and the current thread has not been
     * {@linkplain Thread#interrupt interrupted}.
     * <p>
     * Acquires a ressource, if one is available and returns immediately, with
     * the ressource, reducing the number of available ressources by one.
     * <p>
     * If no ressource is available then the current thread becomes disabled for
     * thread scheduling purposes and lies dormant until one of three things
     * happens:
     * <ul>
     * <li>Some other thread releases a ressource to this pool and the current
     * thread is next to be assigned a ressource; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread; or
     * <li>The specified waiting time elapses.
     * </ul>
     * <p>
     * If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting to
     * acquire a permit,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * <p>
     * If the specified waiting time elapses then the value <tt>null</tt> is
     * returned. If the time is less than or equal to zero, the method will not
     * wait at all.
     * 
     * @param timeout
     *            the maximum time to wait for a permit
     * @param unit
     *            the time unit of the {@code timeout} argument
     * @return a ressource if one was acquired and <tt>null</tt> if the
     *         waiting time elapsed before a ressource was acquired
     * @throws InterruptedException
     *             if the current thread is interrupted
     */
    E tryBorrow(long timeout, TimeUnit unit) throws InterruptedException;

    Collection<E> tryBorrow(int amount);

    Collection<E> tryBorrow(int amount, long timeout, TimeUnit unit)
            throws InterruptedException;

    int getAvailable();

    int getSize();

}