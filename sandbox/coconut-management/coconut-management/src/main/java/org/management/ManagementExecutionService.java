/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.execution;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ManagementExecutionService {

	List<ManagedRepeatableObject> getObjects();
    /**
     * (optional)
     * 
     * @param <T>
     * @param r
     * @param time
     * @param unit
     * @return
     */
    <T extends Runnable> ManagedRepeatableObject add(T r, long time, TimeUnit unit);

    <T extends Runnable> ManagedRepeatableObject reSchedule(T r, long time, TimeUnit unit);

    void start();

    void stop();
}
