/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event;

import java.util.List;

import org.coconut.core.EventHandler;
import org.coconut.event.seda.StageManager;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Events {

    static class EventHandlerAsBatch<E> implements BatchedEventHandler<E> {

        private final EventHandler<E> eh;

        /**
         * @see org.coconut.event.BatchedEventHandler#handleAll(java.util.List)
         */
        public void handleAll(List<? extends E> list) {
            for (E e : list) {
                try {
                    handle(e);
                } catch (RuntimeException re) {
                    if (!handleRuntimeException(e, re)) {
                        return;
                    }
                }
            }
        }

        protected boolean handleRuntimeException(E event, RuntimeException re) {
            // ignore
            return true;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(E)
         */
        public void handle(E event) {
            eh.handle(event);
        }

        public EventHandlerAsBatch(EventHandler<E> eh) {
            this.eh = eh;
        }
    }
    
    public static StageManager newPipeline(Object... stages) {
        return null;
    }
}
