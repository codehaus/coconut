/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import java.util.concurrent.TimeUnit;

import javax.management.JMException;

/**
 * ExecutableGroup is an active entity that regular polls certain data sources
 * for update values.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ManagedExecutableGroup extends ManagedGroup {

    /**
     * Adds an object to the group. The
     * 
     * @param o
     *            the object to add
     * @return the object that was added
     * @throws NullPointerException
     *             if the specified object is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the object has already been registered
     */
    ManagedExecutableGroup add(Object o);

    /**
     * (optional)
     * 
     * @param <T>
     * @param r
     * @param time
     * @param unit
     * @return
     */
    <T extends Runnable> ManagedExecutableGroup add(T r, long time, TimeUnit unit);

    <T extends Runnable> ManagedExecutableGroup reSchedule(T r, long time, TimeUnit unit);

    void start();

    void stop();

    void startAndRegister(String name) throws JMException;

    void stopAndUnregister() throws JMException;
}
