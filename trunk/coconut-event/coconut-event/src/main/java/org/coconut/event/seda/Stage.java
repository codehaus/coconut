/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda;

import java.util.concurrent.locks.Lock;

/**
 * A stage is self-contained component that handle events, optionally
 * dispatching events to other stages while processing these. Most
 * implementations will use an incoming event-queue to store events that have
 * not been processed. A StageManager takes care of thread scheduling
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Stage /* extends ReadWriteLock */{

    /**
     * Returns the name of the Stage. Each stage is registered with a unique
     * name within a stage manager.
     */
    String getName();

    /**
     * copied here to provide a description at some later stage.
     * 
     * @see java.util.concurrent.locks.ReadWriteLock#readLock()
     */
    Lock readLock();

    /**
     * copied here to provide a description at some later stage.
     * 
     * @see java.util.concurrent.locks.ReadWriteLock#writeLock()
     */
    Lock writeLock();
}
