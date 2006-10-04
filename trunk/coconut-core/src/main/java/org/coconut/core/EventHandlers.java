/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;

/**
 * Factory and utility methods for {@link Callback}, {@link EventHandler} and
 * {@link Offerable} classes defined in this package. This class supports the
 * following kinds of methods:
 * <ul>
 * <li>Methods that create and return an {@link Offerable} that performs common
 * tasks.
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class EventHandlers {

    static class IgnoreTrueOfferable<E> implements Offerable<E>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -8883512217513983631L;

        public boolean offer(E element) {
            return true;
        }
    }

    static class IgnoreFalseOfferable<E> implements Offerable<E>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -3497640264421275470L;

        public boolean offer(E element) {
            return false;
        }
    }

    static class Offerable2EventHandlerAdaptor<E> implements EventHandler<E>,
            Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -4293606104983956712L;

        private final Offerable<E> offerable;

        public Offerable2EventHandlerAdaptor(final Offerable<E> offerable) {
            if (offerable == null) {
                throw new NullPointerException("offerable is null");
            }
            this.offerable = offerable;
        }

        public void handle(E element) {
            offerable.offer(element);
        }
    }

    static class EventHandler2OfferableAdaptor<E> implements Offerable<E>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 5555001640212350081L;

        private final EventHandler<E> offerable;

        public EventHandler2OfferableAdaptor(final EventHandler<E> offerable) {
            if (offerable == null) {
                throw new NullPointerException("offerable is null");
            }
            this.offerable = offerable;
        }

        public boolean offer(E element) {
            offerable.handle(element);
            return true;
        }
    }

    static class QueueAdaptor<E> implements EventHandler<E>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -467485596894009881L;

        private final Queue<E> queue;

        public QueueAdaptor(final Queue<E> queue) {
            if (queue == null) {
                throw new NullPointerException("queue is null");
            }
            this.queue = queue;
        }

        public void handle(E element) {
            queue.offer(element); // ignore return value
        }
    }

    /**
     * Wraps an {@link Offerable} in an {@link EventHandler}.
     * 
     * @param offerable
     *            the offerable to wrap
     * @return an EventHandler wrapping an Offerable
     * @throws NullPointerException
     *             if the supplied offerable is <code>null</code>
     */
    public static <E> EventHandler<E> fromOfferable(final Offerable<E> offerable) {
        return new Offerable2EventHandlerAdaptor<E>(offerable);

    }

    /**
     * Wraps an {@link java.util.Queue} in an {@link EventHandler}.
     * 
     * @param Queue
     *            the queue to wrap
     * @return an EventHandler wrapping an Queue
     * @throws NullPointerException
     *             if the supplied offerable is <code>null</code>
     */
    public static <E> EventHandler<E> fromQueue(final Queue<E> queue) {
        return new QueueAdaptor<E>(queue);
    }

    @SuppressWarnings("unchecked")
    public static <E> EventHandler<E> ignoreEventHandler() {
        return (EventHandler<E>) fromOfferable(ignoreFalse());
    }

    /**
     * Returns an {@link Offerable} that returns <tt>false</tt> for any
     * element that is offered to it.
     * 
     * @return an {@link Offerable}that returns <tt>false</tt> for any
     *         element that is offered to it.
     */
    public static <E> Offerable<E> ignoreFalse() {
        return new IgnoreFalseOfferable<E>();
    }

    /**
     * Returns an {@link Offerable}that returns <tt>true</tt> for any element
     * that is offered to it.
     * 
     * @return an {@link Offerable}that returns <tt>true</tt> for any element
     *         that is offered to it.
     */
    public static <E> Offerable<E> ignoreTrue() {
        return new IgnoreTrueOfferable<E>();
    }

    /**
     * Wraps an {@link EventHandler} in an {@link Offerable}.
     * 
     * @param handler
     *            the EventHandler to wrap
     * @return an Offerable wrapping an EventHandler
     * @throws NullPointerException
     *             if the supplied eventHandler is <code>null</code>
     */
    public static <E> Offerable<E> toOfferable(final EventHandler<E> handler) {
        if (handler == null) {
            throw new NullPointerException("handler is null");
        }
        return new Offerable<E>() {
            public boolean offer(E element) {
                handler.handle(element);
                return true;
            }
        };
    }

    public static <E> Offerable<E> toOfferableSafe(final EventHandler<E> eventHandler) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler is null");
        }
        return new Offerable<E>() {
            public boolean offer(E element) {
                try {
                    eventHandler.handle(element);
                    return true;
                } catch (RuntimeException ex) {
                    return false;
                }
            }
        };
    }

    public static <E> EventHandler<E> toPrintStream(final PrintStream ps) {
        if (ps == null) {
            throw new NullPointerException("ps is null");
        }
        return new EventHandler<E>() {
            public void handle(E element) {
                ps.println(element.toString());
            }
        };
    }

    public static <E> EventHandler<E> toPrintStreamSafe(final PrintStream ps) {
        if (ps == null) {
            throw new NullPointerException("ps is null");
        }
        return new EventHandler<E>() {
            public void handle(E element) {
                try {
                    ps.println(element.toString());
                } catch (RuntimeException re) {
                    // ignore
                }
            }
        };
    }

    /**
     * Wraps an {@link EventHandler} in an {@link Queue}.
     * 
     * @param handler
     *            the EventHandler to wrap
     * @return a Queue wrapping an EventHandler
     * @throws NullPointerException
     *             if the supplied eventHandler is <code>null</code>
     */
    public static <E> Queue<E> toQueue(final EventHandler<E> handler) {
        if (handler == null) {
            throw new NullPointerException("handler is null");
        }
        return new AbstractQueue<E>() {
            @Override
            public Iterator<E> iterator() {
                throw new UnsupportedOperationException();
            }

            public boolean offer(E element) {
                handler.handle(element);
                return true;
            }

            public E peek() {
                throw new UnsupportedOperationException();
            }

            public E poll() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return 0;
            }
        };
    }

    public static <E> EventHandler<E> toSystemOut() {
        return toPrintStream(System.out);
    }

    public static <E> EventHandler<E> toSystemOutSafe() {
        return toPrintStreamSafe(System.out);
    }

    public static <F, T> EventHandler<T> wrapEventHandler(final EventHandler<F> from,
            final Transformer<T, F> t) {
        return new EventHandler<T>() {
            public void handle(T element) {
                from.handle(t.transform(element));
            }
        };
    }

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private EventHandlers() {
    }
    // /CLOVER:ON
}
