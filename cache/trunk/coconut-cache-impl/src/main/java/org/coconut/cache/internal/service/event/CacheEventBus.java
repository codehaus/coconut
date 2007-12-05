/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import java.util.concurrent.Semaphore;

import org.coconut.core.EventProcessor;
import org.coconut.event.bus.EventSubscription;
import org.coconut.event.bus.defaults.DefaultEventBus;
import org.coconut.operations.Ops.Predicate;

public class CacheEventBus<E> extends DefaultEventBus<E> {

    private boolean isShutdown;

    private final Semaphore s = new Semaphore(Integer.MAX_VALUE);

    void setShutdown() {
        s.acquireUninterruptibly(Integer.MAX_VALUE);
        isShutdown = true;
        unsubscribeAll();
        s.release(Integer.MAX_VALUE);
    }

    @Override
    protected boolean doInform(E element, boolean doThrow) {
        s.acquireUninterruptibly();
        try {
            checkShutdown();
            return super.doInform(element, doThrow);
        } finally {
            s.release();
        }
    }

    @Override
    public EventSubscription<E> subscribe(EventProcessor<? super E> eventHandler,
            Predicate<? super E> filter, String name) {
        s.acquireUninterruptibly();
        try {
            checkShutdown();
            return super.subscribe(eventHandler, filter, name);
        } finally {
            s.release();
        }
    }

    @Override
    public EventSubscription<E> subscribe(EventProcessor<? super E> eventHandler,
            Predicate<? super E> filter) {
        s.acquireUninterruptibly();
        try {
            checkShutdown();
            return super.subscribe(eventHandler, filter);
        } finally {
            s.release();
        }
    }

    private void checkShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("Cache has been shutdown");
        }
    }

}
