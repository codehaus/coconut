/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.coconut.core.EventProcessor;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class OrderedEventBus<E> extends DefaultEventBus<E> {

    private final Semaphore s = new Semaphore(1);

    private final ThreadLocal<LinkedBlockingQueue<E>> next = new ThreadLocal<LinkedBlockingQueue<E>>();

    protected boolean doInform(final E element) {
        List<DefaultEventSubscription<E>> l = indexer.match(element);
        Thread t = Thread.currentThread();
        s.acquireUninterruptibly();
        try {
            for (DefaultEventSubscription<E> d : l) {
                OrderedEventSubscription<E> oes = (OrderedEventSubscription<E>) d;
                oes.intend(t);
            }
        } finally {
            s.release();
        }
        ArrayList remaining=null;
        for (DefaultEventSubscription<E> d : l) {
            OrderedEventSubscription<E> oes = (OrderedEventSubscription<E>) d;
            Thread next = null;
            if (next == null) {
                if (remaining == null) {
                    remaining = new ArrayList();
                }
            } else {
                //oes.
            }
            oes.intend(t);
        }

        // ReadLock rl = s.readLock();
        // rl.lock();
        // try {
        // deliver(element, s);
        // } finally {
        // rl.unlock();
        // }
        // }
        return true;
    }

    DefaultEventSubscription newSubscription(EventProcessor<? super E> eventHandler,
            Filter<? super E> filter, String name) {
        return new OrderedEventSubscription<E>(this, name, eventHandler, filter);
    }
}
