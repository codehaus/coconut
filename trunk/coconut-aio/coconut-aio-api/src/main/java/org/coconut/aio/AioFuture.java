package org.coconut.aio;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.coconut.core.Callback;
import org.coconut.core.CallbackFuture;
import org.coconut.core.Offerable;


/**
 * A IO Future, this class contains methods that does not wrap an asynchronous
 * exception in ExecutitionException.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface AioFuture<V, T> extends CallbackFuture<V> {

    /**
     * Waits if necessary for the computation to complete, and then retrieves
     * its result. The main difference from get() method is that the only
     * checked exception this method throws is an IOException
     * 
     * @return the computed result
     * @throws CancellationException
     *             if the computation was cancelled
     * @throws IOException
     *             if the computation threw an exception or the current thread
     *             was interrupted while waiting.
     */
    V getIO() throws IOException;

    /**
     * Waits if necessary for at most the given time for the computation to
     * complete, and then retrieves its result, if available. The main
     * difference from get() method is that the only checked exception this
     * method throws is an IOException
     * 
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException
     *             if the computation was cancelled
     * @throws IOException
     *             if the computation threw an exception or the current thread
     *             was interrupted while waiting.
     * @throws TimeoutException
     *             if the wait timed out
     */
    V getIO(long timeout, TimeUnit unit) throws IOException, TimeoutException;

    void setCallback(Callback<V> callback);

    void setDestination(Offerable< ? super T> dest);
}