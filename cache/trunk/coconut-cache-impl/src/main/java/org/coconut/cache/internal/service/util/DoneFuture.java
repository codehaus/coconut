/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DoneFuture<V> implements Future<V> {

    public static Future DONE = new DoneFuture();

    /**
     * @see java.util.concurrent.Future#cancel(boolean)
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    /**
     * @see java.util.concurrent.Future#get()
     */
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }

    /**
     * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return null;
    }

    /**
     * @see java.util.concurrent.Future#isCancelled()
     */
    public boolean isCancelled() {
        return false;
    }

    /**
     * @see java.util.concurrent.Future#isDone()
     */
    public boolean isDone() {
        return true;
    }

}
