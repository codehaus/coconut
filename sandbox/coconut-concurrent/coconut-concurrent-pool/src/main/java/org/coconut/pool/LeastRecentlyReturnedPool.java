/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.pool;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LeastRecentlyReturnedPool<E> extends AbstractSemaphorePool<E> {

    private final Object[] array;

    private int pointer;

    private int free;

    public LeastRecentlyReturnedPool(Collection<? extends E> col) {
        super(col.size());
        array = col.toArray();
    }

    /**
     * @see org.coconut.pool.AbstractSemaphorePool#get()
     */
    @Override
    protected synchronized E get() {
        if (++pointer == getSize()) {
            pointer = 0;
        }
        return (E) array[pointer];
    }

    /**
     * @see org.coconut.pool.AbstractSemaphorePool#get(int)
     */
    @Override
    protected synchronized Collection<E> get(int count) {
        ArrayList<E> al = new ArrayList<E>(count);
        for (int i = 0; i < count; i++) {
            if (++pointer == getSize()) {
                pointer = 0;
            }
            al.add((E) array[pointer]);
        }
        return al;
    }

    /**
     * @see org.coconut.pool.Pool#returnToPool(java.lang.Object)
     */
    public synchronized void returnToPool(E buffer) {
        if (free == pointer) {
            throw new IllegalStateException(
                    "Attempting to add new elements, pool already at max size");
        }
        if (++free == getSize()) {
            free = 0;
        }
        array[free] = buffer;
        sem.release();
    }

    /**
     * @see org.coconut.pool.Pool#returnToPool(java.util.Collection)
     */
    public synchronized void returnToPool(Collection<? super E> buffers) {
        for (Object buffer : buffers) {
            if (free == pointer) {
                throw new IllegalStateException(
                        "Attempting to add new elements, pool already at max size");
            }
            if (++free == getSize()) {
                free = 0;
            }
            array[free] = buffer;
        }
        sem.release(buffers.size());
    }

}
