/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.util;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CapacityQueue<E> extends BlockingQueue<E> {

    /**
     * Closes the queue. No new items can be added.
     */
    void closeQueue();

    int getCapacity();

    void setCapacity(int newCapacity);

    void setCapacity(int newCapacity, boolean downSize);

    int drainTo(Collection<? super E> c, long timeout, TimeUnit unit)
            throws InterruptedException;

}
