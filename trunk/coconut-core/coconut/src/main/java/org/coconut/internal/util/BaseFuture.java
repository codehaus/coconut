/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class BaseFuture<V> extends FutureTask<V> {

    private final static Callable<?> NULL_CALLABLE = new Callable() {
        public Object call() throws Exception {
            return null;
        }
    };

    public BaseFuture() {
        super((Callable<V>) NULL_CALLABLE);
    }

    public V getNoExecuteException() throws Exception {
        try {
            return get();
        } catch (ExecutionException ee) {
            throw (Exception) ee.getCause();
        }
    }

    public void completed(V v) {
        set(v);
    }

    public void failed(Throwable t) {
        setException(t);
    }
}
