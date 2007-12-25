/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.bus.defaults;

import java.util.Collection;

import org.coconut.event.bus.EventBus;
import org.coconut.event.bus.EventSubscription;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Procedure;

/**
 * @param <E>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AbstractEventBus.java 415 2007-11-09 08:25:23Z kasper $
 */
public abstract class AbstractEventBus<E> implements EventBus<E> {

    /** {@inheritDoc} */
    public boolean offer(final E element) {
        return inform(element, true);
    }

    /** {@inheritDoc} */
    public boolean offerAll(Collection<? extends E> c) {
        return informAll(c, false);
    }

    /** {@inheritDoc} */
    public void apply(E event) {
        inform(event, false);
    }

    /** {@inheritDoc} */
    public EventSubscription<E> subscribe(Procedure<? super E> eventHandler) {
        return subscribe(eventHandler, Predicates.truePredicate());
    }

    private boolean inform(E element, boolean doThrow) {
        if (element == null) {
            throw new NullPointerException("element is null");
        }
        return doInform(element, doThrow);
    }

    private boolean informAll(Collection<? extends E> c, boolean doThrow) {
        if (c == null) {
            throw new NullPointerException("c is null");
        }
        for (E element : c) {
            if (element == null) {
                throw new NullPointerException("collection contained a null");
            }
        }
        return doInformAll(c, doThrow);
    }

    abstract boolean doInform(E element, boolean doThrow);

    boolean doInformAll(Collection<? extends E> col, boolean doThrow) {
        boolean ok = true;
        for (E element : col) {
            ok &= inform(element, doThrow);
        }
        return ok;
    }

    void subscribed(EventSubscription<E> s) {}

    void unsubscribed(EventSubscription<E> s) {}
}
