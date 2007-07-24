/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A future that is always done.
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <V>
 *            The result type returned by this Future's <tt>get</tt> method
 */
public class DoneFuture<V> implements Future<V> {

    /**
     * {@inheritDoc}
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCancelled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDone() {
        return true;
    }

}
