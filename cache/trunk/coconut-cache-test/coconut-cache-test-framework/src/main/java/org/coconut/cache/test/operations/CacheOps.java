/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.operations;

import org.coconut.cache.Cache;

public class CacheOps {

    static final String PREFIX = "test.opr.cache.";

    public static class Get<K, V> extends AbstractOperation<K, V> {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "get";

        /** {@inheritDoc} */
        public void run() {
            cache.get(keyGenerator.generate());
        }
    }

    /**
     * Invokes the {@link Cache#clear()} method.
     */
    public static class EntrySetIterateAll<K, V> extends AbstractOperation<K, V> {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "entrySetIterateAll";

        /** {@inheritDoc} */
        public void run() {
            synchronized (cache) {
                int currentSize = cache.size();
                int size = 0;
                for (Object o : cache.entrySet()) {
                    size++;
                }
                if (size != currentSize) {
                    throw new AssertionError("Size difference should be 0, expected " + currentSize
                            + ", was " + size);
                }
            }
        }
    }

    /**
     * Invokes the {@link Cache#clear()} method.
     */
    public static class Clear<K, V> extends AbstractOperation<K, V> {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "clear";

        /** {@inheritDoc} */
        public void run() {
            cache.clear();
        }
    }

    /**
     * Invokes the {@link Cache#shutdown()} method.
     */
    public static class Shutdown<K, V> extends AbstractOperation<K, V> {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "shutdown";

        /** {@inheritDoc} */
        public void run() {
            cache.shutdown();
        }
    }

    /**
     * Invokes the {@link Cache#shutdownNow()} method.
     */
    public static class ShutdownNow<K, V> extends AbstractOperation<K, V> {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "shutdownNow";

        /** {@inheritDoc} */
        public void run() {
            cache.shutdown();
        }
    }

    public static class Peek<K, V> extends AbstractOperation<K, V> {
        public final static String NAME = PREFIX + "peek";

        /** {@inheritDoc} */
        public void run() {
            cache.peek(keyGenerator.generate());
        }
    }
}
