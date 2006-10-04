/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.pool;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MostRecentlyReturnedPool<E> extends AbstractSemaphorePool<E> {
    private final Object[] array;

    private int pointer;

    public MostRecentlyReturnedPool(Collection<? extends E> col) {
        super(col.size());
        array = col.toArray();
    }

    /**
     * @see org.coconut.pool.AbstractSemaphorePool#get()
     */
    @Override
    protected synchronized E get() {
        return (E) array[pointer++];
    }

    /**
     * @see org.coconut.pool.AbstractSemaphorePool#get(int)
     */
    @Override
    protected synchronized Collection<E> get(int count) {
        ArrayList<E> al = new ArrayList<E>(count);
        int to = pointer + count;
        for (int i = pointer; to < i; i++) {
            al.add((E) array[i]);
        }
        pointer = to;
        return al;
    }

    /**
     * @see org.coconut.pool.Pool#returnToPool(java.lang.Object)
     */
    public synchronized void returnToPool(E buffer) {
        array[--pointer] = buffer;
    }

    /**
     * @see org.coconut.pool.Pool#returnToPool(java.util.Collection)
     */
    public synchronized void returnToPool(Collection<? super E> buffers) {
        for (Object buffer : buffers) {
            array[--pointer] = buffer;
        }
    }

}