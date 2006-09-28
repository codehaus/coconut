/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;

import org.coconut.core.EventHandler;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Events {

    static class EventHandlerAsBatch<E> implements BatchedEventHandler<E> {

        private final EventHandler<E> eh;

        public EventHandlerAsBatch(EventHandler<E> eh) {
            this.eh = eh;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(E)
         */
        public void handle(E event) {
            eh.handle(event);
        }

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
    }

    static class ProcessEventFromFactory<E> implements Runnable, Serializable {

        private final EventHandler<? super E> eh;

        private final Callable<E> factory;

        /**
         * @param event
         * @param eh
         */
        public ProcessEventFromFactory(final Callable<E> factory,
                final EventHandler<? super E> eh) {
            if (eh == null) {
                throw new NullPointerException("eh is null");
            } else if (factory == null) {
                throw new NullPointerException("factory is null");
            }
            this.factory = factory;
            this.eh = eh;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                E event = factory.call();
                eh.handle(event);
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                throw new IllegalStateException("Could not create new value", e);
            }
        }
    }

    static class ProcessEvent<E> implements Runnable, Serializable {

        private final EventHandler<? super E> eh;

        private final E event;

        /**
         * @param event
         * @param eh
         */
        public ProcessEvent(final E event, final EventHandler<? super E> eh) {
            if (eh == null) {
                throw new NullPointerException("eh is null");
            } else if (event == null) {
                throw new NullPointerException("event is null");
            }
            this.event = event;
            this.eh = eh;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            eh.handle(event);
        }
    }

    public static <E> Runnable processEvent(E event, EventHandler<E> handler) {
        return new ProcessEvent<E>(event, handler);
    }

    public static <E> Runnable processEvent(Callable<E> factory, EventHandler<E> handler) {
        return new ProcessEventFromFactory<E>(factory, handler);
    }

    public static StageManager newPipeline(Object... stages) {
        return null;
    }
}
