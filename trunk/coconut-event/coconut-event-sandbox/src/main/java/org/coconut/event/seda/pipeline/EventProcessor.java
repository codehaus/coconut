/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.pipeline;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface EventProcessor {

    /**
     * If the returned object is null the it should be dropped.
     * 
     * @param e
     * @return
     */
    Object processEvent(Object e);

    /**
     * @param e
     * @return <tt>true</tt> if the event was succesfully processed and
     *         enqueued on the next stage
     */
    boolean processAndEnqueueEvent(Object e) throws InterruptedException;

    boolean processAndEnqueueEvents(Object[] e) throws InterruptedException;

    int getQueueSize();

    void setQueueSize(int newSize);
}
