/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.container;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.codehaus.cake.service.ServiceManager;

public interface Container extends ServiceManager {

    /**
     * Returns the name of this container. If no name has been specified while configuring the
     * container. The implementation must choose a valid name. A valid name contains no other
     * characters then alphanumeric characters and '_' or '-'.
     * 
     * @return the name of the container
     * @see ContainerConfiguration#setName(String)
     */
    String getName();

    /**
     * Blocks until all tasks within this container have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is interrupted, whichever happens
     * first.
     * 
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return <tt>true</tt> if this container terminated and <tt>false</tt> if the timeout
     *         elapsed before termination
     * @throws InterruptedException
     *             if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Returns <tt>true</tt> if this container has been shut down.
     * 
     * @return <tt>true</tt> if this container has been shut down
     */
    boolean isShutdown();

    /**
     * Returns <tt>true</tt> if this container has been started.
     * 
     * @return <tt>true</tt> if this container has been started
     */
    boolean isStarted();

    /**
     * Returns <tt>true</tt> if all service tasks have completed following shut down. Note that
     * <tt>isTerminated</tt> is never <tt>true</tt> unless either <tt>shutdown</tt> or
     * <tt>shutdownNow</tt> was called first.
     * 
     * @return <tt>true</tt> if all tasks have completed following shut down
     */
    boolean isTerminated();

    /**
     * Initiates an orderly shutdown of the container. In which currently running tasks will be
     * executed, but no new tasks will be started. Invocation has no additional effect if already
     * shut down.
     * 
     * @throws SecurityException
     *             if a security manager exists and shutting down this container may manipulate
     *             threads that the caller is not permitted to modify because it does not hold
     *             {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>, or the security
     *             manager's <tt>checkAccess</tt> method denies access.
     */
    void shutdown();

    /**
     * Attempts to stop all actively executing tasks within the container and halts the processing
     * of waiting tasks. Invocation has no additional effect if already shut down.
     * <p>
     * There are no guarantees beyond best-effort attempts to stop processing actively executing
     * tasks in the container. For example, typical implementations will cancel via
     * {@link Thread#interrupt}, so any task that fails to respond to interrupts may never
     * terminate.
     * 
     * @throws SecurityException
     *             if a security manager exists and shutting down this container may manipulate
     *             threads that the caller is not permitted to modify because it does not hold
     *             {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>, or the security
     *             manager's <tt>checkAccess</tt> method denies access.
     */
    void shutdownNow();

    /**
     * Used on a Container implementation to document what type of services the container supports.
     */
    @Target( { ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    /* @Documented */
    public static @interface SupportedServices {
        /**
         * Returns the type of services the container implementation supports.
         */
        Class[] value();
    }
}
