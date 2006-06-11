/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface StageManager {

    /**
     * Blocks until all events have been processed after a shutdown request, or
     * the timeout occurs, or the current thread is interrupted, whichever
     * happens first.
     * 
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return <tt>true</tt> if this stage manager terminated and
     *         <tt>false</tt> if the timeout elapsed before termination
     * @throws InterruptedException
     *             if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * This method returns all the stages that has been registered in this stage
     * manager. If the specific manager implementation poses any ordering among
     * the stages, for example, for example in a pipeline. This method will
     * return the stages in the specific ordering. If no specific ordering
     * exists among the stages the manager is free to return the stages in any
     * order.
     * 
     * @return a <code>List</code> with all registered stages
     */
    List<? extends Stage> getStages();

    /**
     * Returns the stage with the specified name.
     * 
     * @param name
     *            the unique name of the stage
     * @return the stage with the specified name or <tt>null</tt> if no such
     *         stage exist
     * @throws NullPointerException
     *             if the specified name is <tt>null</tt>.
     */
    Stage getStage(String name);

    /**
     * Returns <tt>true</tt> if this stage manager has been shut down.
     * 
     * @return <tt>true</tt> if this stage manager has been shut down
     */
    boolean isShutdown();

    /**
     * Returns <tt>true</tt> if all events have been processed following shut
     * down. Note that <tt>isTerminated</tt> is never <tt>true</tt> unless
     * <tt>shutdown</tt> was called first. Note, that some implementations
     * might define additional shutdown methods in which case a call to these
     * will also result in this method returning <tt>true</tt>.
     * 
     * @return <tt>true</tt> if all tasks have completed following shut down
     */
    boolean isTerminated();

    /**
     * Returns a <tt>Reentrant</tt> lock for the <code>StageManager</code>.
     * By holding this lock no internal threads will perform any execution
     * inside the container. However, any external thread is free to register or
     * unregister stages. The lock can be used, for example, for pausing all
     * event-handling temporarily are resume it later.
     * 
     * <pre>
     * Lock lock = stageManager.lock();
     * lock.lock(); //pause all internal threads
     * try {
     *     //  do some processing requiring that no threads are executing within any stage....
     * } finally {
     *     lock.unlock(); //internal threads will automatically start running
     * }
     * 
     * </pre>
     * 
     * A successful lock operation acts as in action in a thread happens-before
     * every action This can be more effective then acquiring the lock on each
     * stage.
     * <ul>
     * <li>Each action in any thread executing <tt>within</tt> a stage
     * manager happens-before a <tt>succesfull</tt> acquisition of the stage
     * lock.
     * <li>Likewise, the release of the stage lock happens-before every
     * subsequent action of any. And any modifications made by the thread
     * holding will be visible to any thread executing
     * <li>Any modification made by the Thread owning the lock will be read by
     * any thread working inside container happens before relationship with
     * subsequent actions of any threads executing within the stage manager.
     * <li>After having released the lock the container will resume with work
     * <li>Obtaining the lock does not force the runtime to empty any queues
     * </ul>
     * 
     * @return the lock for the <code>StageManager</code>
     */
    Lock lock();

    /**
     * Initiates an orderly shutdown in which all outstanding events are
     * processed, but no new events will be accepted. Invocation has no
     * additional effect if already shut down.
     * <p>
     * all queues will be empty,
     * 
     * @throws SecurityException
     *             if a security manager exists and shutting down this
     *             StageManager may manipulate threads that the caller is not
     *             permitted to modify because it does not hold
     *             {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>,
     *             or the security manager's <tt>checkAccess</tt> method
     *             denies access.
     */
    void shutdown();

    /**
     * <ul>
     * <li>Some stage managers might allow (protected) access to outstanding
     * events of a stage.
     * <li>Registration of stages
     * <li>StageGraph (In util??)
     * </ul>
     */
}
