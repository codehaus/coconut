/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ThreadUtils {
    public final static Executor SAME_THREAD_EXECUTOR = new SameThreadExecutor();

    /**
     * A {@link java.util.concurrent.Callable} that returns <code>null</code>
     * on every invocation of {@link #call}.
     */
    public static Callable NULL_CALLABLE = new NullCallable();
    
    /**
     * A {@link java.util.concurrent.Callable} that returns <code>null</code>
     * on every invocation of {@link java.util.concurrent.Callable#call}.
     */
    @SuppressWarnings("unchecked")
    public static <V> Callable<V> nullCallable() {
        return NULL_CALLABLE;
    }
    

    /**
     * A {@link java.util.concurrent.Callable} that returns <code>null</code>
     * on every invocation of {@link #call}.
     */
    final static class NullCallable<V> implements Callable<V>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 4869209484084557763L;

        /** {@inheritDoc} */
        public V call() {
            return null;
        }
    }
    

    static class SameThreadExecutor implements Executor, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -6365439666830575122L;

        /**
         * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
         */
        public void execute(Runnable command) {
            command.run();
        }
    }
}
