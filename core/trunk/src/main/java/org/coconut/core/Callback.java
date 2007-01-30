/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * A <tt>Callback</tt> defines how the system should react to a completion of
 * an asynchronous computation.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Callback<E> {

    /**
     * This method is invoked if the asynchronous computation completed
     * succesfully.
     * 
     * @param result
     *            the result of the computation
     */
    void completed(E result);

    /**
     * This method is invoked if the asynchronous computation completed with
     * failures.
     * 
     * @param cause
     *            the cause of the failure
     */
    void failed(Throwable cause);
}
