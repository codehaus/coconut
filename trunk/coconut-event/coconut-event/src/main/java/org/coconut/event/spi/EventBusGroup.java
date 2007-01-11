/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.spi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coconut.core.EventProcessor;
import org.coconut.event.EventSubscription;

/**
 * Consider this scenarioe we have 10 administration groups. people can be
 * member of one or more of these groups... we only want to notify people one
 * time... Hmm, think we need buildin support in the eventbus... Hieracal event
 * bus...
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface EventBusGroup<E> {
    
    Map<?, EventProcessor<? super E>> match(E event);
   
    class OnlyOne<E> implements EventCycle<E> {

        private final HashSet set = new HashSet();

        /**
         * @see org.coconut.event.bus.spi.EventBusGroup.EventCycle#afterDelivery(org.coconut.event.EventSubscription,
         *      java.lang.Throwable)
         */
        public void afterDelivery(EventSubscription<E> es, Throwable t) {
            set.add(es.getEventHandler());
        }

        /**
         * @see org.coconut.event.bus.spi.EventBusGroup.EventCycle#beforeDelivery(org.coconut.event.EventSubscription)
         */
        public synchronized boolean beforeDelivery(EventSubscription<E> es) {
            // TODO Auto-generated method stub
            return true;
        }

    }

    // could also have a boolean match(EventSubscription<E> es); // ??
    interface EventCycle<E> {
        // hmm ultimate control

        boolean beforeDelivery(EventSubscription<E> es);

        void afterDelivery(EventSubscription<E> es, Throwable t);
    }
}
