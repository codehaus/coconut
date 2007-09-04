package org.coconut.cache.service.servicemanager;

import java.util.concurrent.TimeUnit;

public interface AsynchronousShutdownService {

    /**
     * Blocks until all tasks have completed execution after a shutdown request, or the
     * timeout occurs, or the current thread is interrupted, whichever happens first.
     * 
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return <tt>true</tt> if this service terminated and <tt>false</tt> if the
     *         timeout elapsed before termination
     * @throws InterruptedException
     *             if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Attempts to stop all actively executing tasks within the cache and halts the
     * processing of waiting tasks. Invocation has no additional effect if already shut
     * down.
     * <p>
     * There are no guarantees beyond best-effort attempts to stop processing actively
     * executing tasks in the cache. For example, typical implementations will cancel via
     * {@link Thread#interrupt}, so any task that fails to respond to interrupts may
     * never terminate.
     * 
     * @throws SecurityException
     *             if a security manager exists and shutting down this Cache may
     *             manipulate threads that the caller is not permitted to modify because
     *             it does not hold {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>,
     *             or the security manager's <tt>checkAccess</tt> method denies access.
     */
    void shutdownNow();

    /**
     * Returns <tt>true</tt> if all tasks have completed following shut down. Note that
     * <tt>isTerminated</tt> is never <tt>true</tt> unless either <tt>shutdown</tt>
     * or <tt>shutdownNow</tt> was called first.
     * 
     * @return <tt>true</tt> if all tasks have completed following shut down
     */
    boolean isTerminated();
}
