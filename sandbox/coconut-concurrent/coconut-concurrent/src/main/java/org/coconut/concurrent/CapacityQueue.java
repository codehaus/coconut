/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.concurrent;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
//TODO rename to bulkCapacityQueue?
public interface CapacityQueue<E> extends BulkQueue<E> {

    /**
     * Closes the queue. No new items can be added.
     */
    void closeQueue();

    int getCapacity();

    void setCapacity(int newCapacity);

    void setCapacity(int newCapacity, boolean downSize);


}
