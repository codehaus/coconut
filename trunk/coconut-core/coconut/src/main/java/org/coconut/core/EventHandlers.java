/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.core;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

/**
 * Factory and utility methods for {@link Callback}, {@link EventHandler},
 * {@link Offerable}, and {@link Transformer} classes defined in this package.
 * This class supports the following kinds of methods:
 * <ul>
 * <li>Methods that create and return an {@link Offerable} that performs common
 * tasks.
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class EventHandlers {

    static final class ImmutableMapEntry<K, V> implements Entry<K, V> {

        /** The value for this entry. */
        private final V value;

        /** The key for this entry. */
        private final K key;

        /**
         * Creates a new MapEntry.
         * 
         * @param key
         *            the key
         * @param value
         *            the value
         */
        public ImmutableMapEntry(final K key, final V value) {
            this.value = value;
            this.key = key;
        }

        public ImmutableMapEntry(Entry<K, V> entry) {
            this.value = entry.getValue();
            this.key = entry.getKey();
        }

        public static <K, V> Entry<K, V> from(final K key, final V value) {
            return new ImmutableMapEntry<K, V>(key, value);
        }

        /**
         * @see java.util.Map$Entry#getKey()
         */
        public K getKey() {
            return key;
        }

        /**
         * @see java.util.Map$Entry#getDouble()
         */
        public V getValue() {
            return value;
        }

        /**
         * @see java.util.Map$Entry#setValue(V)
         */
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o != null && o instanceof Entry) {
                Entry e = (Entry) o;
                Object k = e.getKey();
                if (key == k || (key != null && key.equals(k))) {
                    Object v = e.getValue();
                    return (value == v || (value != null && value.equals(v)));
                }
            }
            return false;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode())
                    ^ (value == null ? 0 : value.hashCode());
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }
    }

    public static <K, V> Map.Entry<K, V> newMapEntry(K key, V value) {
        return new ImmutableMapEntry<K, V>(key, value);
    }

    public static <K, V> Map.Entry<K, V> newMapEntry(Map.Entry<K, V> entry) {
        return new ImmutableMapEntry<K, V>(entry);
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
        if (offerable == null) {
            throw new NullPointerException("offerable is null");
        }
        return new EventHandler<E>() {
            public void handle(E element) {
                offerable.offer(element);
            }
        };
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
        if (queue == null) {
            throw new NullPointerException("queue is null");
        }
        return new EventHandler<E>() {
            public void handle(E element) {
                queue.offer(element); // ignore return value
            }
        };
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
            public boolean offer(E element) {
                handler.handle(element);
                return true;
            }

            @Override
            public Iterator<E> iterator() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return 0;
            }

            public E poll() {
                throw new UnsupportedOperationException();
            }

            public E peek() {
                throw new UnsupportedOperationException();
            }
        };
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

    static class IgnoreTrueOfferable<E> implements Offerable<E>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -8883512217513983631L;

        public boolean offer(E element) {
            return true;
        }
    }

    /**
     * Returns an {@link Offerable}that returns <tt>false</tt> for any
     * element that is offered to it.
     * 
     * @return an {@link Offerable}that returns <tt>false</tt> for any
     *         element that is offered to it.
     */
    public static <E> Offerable<E> ignoreFalse() {
        return new Offerable<E>() {
            public boolean offer(E element) {
                return false;
            }
        };
    }

    public static <F, T> EventHandler<T> wrapEventHandler(
            final EventHandler<F> from, final Transformer<T, F> t) {
        return new EventHandler<T>() {
            public void handle(T element) {
                from.handle(t.transform(element));
            }
        };
    }

    public static <E> EventHandler<E> ignoreEventHandler() {
        return new EventHandler<E>() {
            public void handle(E element) {
                /* ignore */
            }
        };
    }

    public static <E> Offerable<E> toOfferableSafe(
            final EventHandler<E> eventHandler) {
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

    public static <E> EventHandler<E> toSystemOut() {
        return new EventHandler<E>() {
            public void handle(E element) {
                System.out.println(element.toString());
            }
        };
    }

    public static <E> EventHandler<E> toSystemOutSafe() {
        return new EventHandler<E>() {
            public void handle(E element) {
                try {
                    System.out.println(element.toString());
                } catch (RuntimeException re) {
                    // ignore
                }
            }
        };
    }

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private EventHandlers() {
    }
    // /CLOVER:ON
}
