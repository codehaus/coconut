/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.spi;

import java.util.concurrent.TimeUnit;

import org.coconut.management2.ActiveMonitor;
import org.coconut.management2.ActiveMonitorResult;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractActiveMonitor<E> implements ActiveMonitor<E> {

    private volatile ActiveMonitorResult<E> latest;

    /**
     * @see org.coconut.management2.ActiveMonitor#getLatest()
     */
    public ActiveMonitorResult<E> getLatest() {
        return latest;
    }

    /**
     * @see org.coconut.management2.ActiveMonitor#update()
     */
    public ActiveMonitorResult<E> update() throws InterruptedException {
   
            doUpdate();
   
        // TODO Auto-generated method stub
        return null;
    }

    abstract protected E doUpdate() throws InterruptedException;
    /**
     * @see org.coconut.management2.ActiveMonitor#update(long,
     *      java.util.concurrent.TimeUnit)
     */
    public ActiveMonitorResult<E> update(long timeout, TimeUnit unit)
            throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

}
