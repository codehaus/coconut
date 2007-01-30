/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.management;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: StageManagerMXBean.java 56 2006-09-13 10:31:22Z kasper $
 */
public interface StageManagerMXBean {

    // ThreadMXBean ss();

    /**
     * Returns all stages registered within a stage manager.
     * 
     * @return an array of <tt>String</tt>, each is a stage name
     * @throws java.lang.SecurityException
     *             if a security manager exists and the caller does not have
     *             ManagementPermission("monitor").
     */
    String[] getAllStageNames();

    /**
     * Returns the current number of stages registered within the stage manager.
     * 
     * @return the current number of stages.
     */
    int getStageCount();

    /**
     * Returns the stage info for a stage of the specified <tt>name</tt>.
     * <p>
     * This method returns a <tt>StageInfo</tt> object representing the stage
     * information for the stage of the specified name.. If a stage with the
     * given name does not exist, this method will return <tt>null</tt>.
     * <p>
     * <b>MBeanServer access</b>:<br>
     * The mapped type of <tt>StageInfo</tt> is <tt>CompositeData</tt> with
     * attributes as specified in {@link StageStatistics#from StageInfo}.
     * 
     * @param name
     *            the name of the stage.
     * @return a {@link StageStatistics} object for the stage with the given
     *         name; <tt>null</tt> if no stage with the name exist.
     * @throws NullPointerException
     *             if the specified stage name is <tt>null</tt>
     * @throws java.lang.SecurityException
     *             if a security manager exists and the caller does not have
     *             ManagementPermission("monitor").
     */
    StageStatistics getStageInfo(String name);

    /**
     * Returns the stage info for each stage whose name is in the input array
     * <tt>names</tt>.
     * <p>
     * This method returns an array of the <tt>StageInfo</tt> objects. If a
     * stage with the given name does not exist, the corresponding element in
     * the returned array will contain <tt>null</tt>.
     * <p>
     * <b>MBeanServer access</b>:<br>
     * The mapped type of <tt>StageInfo</tt> is <tt>CompositeData</tt> with
     * attributes as specified in {@link StageStatistics#from StageInfo}.
     * 
     * @param names
     *            an array of stage names
     * @return an array of the {@link StageStatistics} objects, each containing
     *         information about a stage whose name is in the corresponding
     *         element of the input array of names.
     * @throws NullPointerException
     *             if the specified array is <tt>null</tt> or any String
     *             within it
     * @throws java.lang.SecurityException
     *             if a security manager exists and the caller does not have
     *             ManagementPermission("monitor").
     */
    StageStatistics[] getStageInfo(String[] names);

    StageStatistics[] getStageInfo();

    /**
     * Returns the total CPU time used by all threads operating within the stage
     * manager. The returned value is of nanoseconds precision but not
     * necessarily nanoseconds accuracy. If the implementation distinguishes
     * between user mode time and system mode time, the returned CPU time is the
     * amount of time that the thread has executed in user mode or system mode.
     * <p>
     * If CPU time measurement is disabled, this method returns <tt>-1</tt>.
     * <p>
     * Most implementations will rely on thread so you want to make sure CPU
     * time measurement is enabled.
     * <p>
     * If the system allows (real) concurrent execution of multiple threads the
     * value returned is the sum of cpu times on all processors.
     * <p>
     * If CPU time measurement is enabled after the stage manager has started,
     * the implementation may choose any time up to and including the time that
     * the capability is enabled as the point where CPU time measurement starts.
     */
    long getTotalCpuTime();

    long getTotalUserTime();

    long getStageCpuTime(String stage);

    long getStageUserTime(String stage);

    String[] getIncomingStages(String stage);

    StageEdgeInfo[] getIncomingEdges(String stage);

    /**
     * Returns the largest number of threads that have ever simultaneously been
     * used in the stage manager.
     * 
     * @return the number of threads
     */
    int getLargestTotalCount();

    /**
     * Returns the current number of threads that are actively processing events
     * within any stage or waiting for work.
     * 
     * @return the number of threads
     */
    int getTotalCount();

    /**
     * Returns the approximate number of threads that are actively processing
     * events within any stage.
     * 
     * @return the number of threads
     */
    int getActiveCount();

    /**
     * Returns the approximate number of threads that are actively processing
     * events within a stage.
     * 
     * @param stage
     * @return the number of threads
     */
    int getActiveCount(String stage);

    /**
     * Returns the uptime of the stage manager in milliseconds.
     * 
     * @return uptime of the stage manager in milliseconds.
     */
    public long getUptime();

    /**
     * Returns the start time of the stage manager in milliseconds. This method
     * returns the approximate time when the stage manager was started.
     * 
     * @return start time of the stage manager in milliseconds.
     */
    long getStartTime();

    /**
     * For example for pipelines, the total number of events processed
     * 
     * @return
     */
    // hmm just take the first stage and see how many incoming events there are
    // and then see how many the last stage succesfully processed.
    // No need for these methods I think
    // long getTotalEventsProcessed();
    // long getTotalEventsAccepted();
}
