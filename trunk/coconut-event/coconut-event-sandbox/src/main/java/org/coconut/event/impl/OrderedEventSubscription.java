/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.core.EventProcessor;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class OrderedEventSubscription<E> extends DefaultEventSubscription<E> {

    private final LinkedBlockingQueue<Thread> intend = new LinkedBlockingQueue<Thread>();

    /**
     * @param bus
     * @param name
     * @param destination
     * @param filter
     */
    OrderedEventSubscription(AbstractEventBus<E> bus, String name,
            EventProcessor<? super E> destination, Filter<? super E> filter) {
        super(bus, name, destination, filter);
    }

    void intend(Thread t) {
        intend.add(t);
    }

    boolean canAcquire(Thread t) {
        return intend.peek() == t;
    }

    Thread acquired(Thread t) {
        intend.poll();
        return intend.peek();
    }
}
