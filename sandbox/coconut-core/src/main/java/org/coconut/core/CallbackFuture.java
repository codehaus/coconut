/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * A <tt>CallbackFuture</tt> extends the basic <tt>Future</tt> interface
 * with a method that takes a <tt>Callback</tt> that should be executed once
 * the computation finishes.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CallbackFuture.java 200 2007-01-25 17:04:12Z kasper $
 */
public interface CallbackFuture<T> extends Future<T> {

    /**
     * Specifies how the system should react once the computation that this
     * Future represents completes.
     * 
     * @param executor
     *            the Executor that should invoke the Callback
     * @param callback
     *            the Callback that should be run
     */
    void setCallback(Executor executor, Callback<T> callback);
}
