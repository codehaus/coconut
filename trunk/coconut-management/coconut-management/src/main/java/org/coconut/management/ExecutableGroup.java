/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ExecutableGroup extends ManagedGroup {

    /**
     * (optional)
     * 
     * @param <T>
     * @param r
     * @param time
     * @param unit
     * @return
     */
    <T extends Runnable> T add(T r, long time, TimeUnit unit);

    <T extends Runnable> T reSchedule(T r, long time, TimeUnit unit);

    void start();

    void stop();

    void startAndRegister(String name) throws Exception;

    void stopAndUnregister() throws Exception;
}
