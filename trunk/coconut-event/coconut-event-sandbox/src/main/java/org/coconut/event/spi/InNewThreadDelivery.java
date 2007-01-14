/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.spi;

import java.util.Collection;

import org.coconut.core.EventProcessor;
import org.coconut.event.EventSubscription;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class InNewThreadDelivery<E> implements EventBusDispatcher<E> {

    /**
     * @see org.coconut.event.bus.spi.EventBusDispatcher#deliver(java.util.Collection,
     *      java.lang.Object)
     */
    public void deliver(Collection<EventSubscription<? super E>> s, E e) {
        for (EventSubscription<? super E> es : s) {
            EventProcessor<? super E> eh = es.getEventProcessor();
            eh.process(e);
        }
    }
}
