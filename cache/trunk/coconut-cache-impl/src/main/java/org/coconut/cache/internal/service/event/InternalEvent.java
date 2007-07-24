/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class InternalEvent {

    /** Cannot instantiate. */
    private InternalEvent() {}

    /**
     * The default implementation of the cache cleared event.
     * 
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of values maintained by the cache
     */
    static class Cleared<K, V> implements CacheEvent.CacheCleared<K, V> {
        /** The cache that was cleared. */
        private final Cache<K, V> cache;

        /** The capacity of the cache before it was cleared. */
        private final long previousCapacity;

        /** The size of the cache before it was cleared. */
        private final int previousSize;

        /**
         * Creates a new Cleared event.
         * 
         * @param previousSize
         *            the size of the cache before it was cleared
         * @param previousCapacity
         *            the capacity of the cache before it was cleared
         * @param cache
         *            the cache that was cleared
         */
        public Cleared(final Cache<K, V> cache, final int previousSize,
                final long previousCapacity) {
            this.previousSize = previousSize;
            this.previousCapacity = previousCapacity;
            this.cache = cache;
        }

        /** {@inheritDoc} */
        public Cache<K, V> getCache() {
            return cache;
        }

        /** {@inheritDoc} */
        public String getName() {
            return CacheEvent.CacheCleared.NAME;
        }

        /** {@inheritDoc} */
        public long getPreviousCapacity() {
            return previousCapacity;
        }

        /** {@inheritDoc} */
        public int getPreviousSize() {
            return previousSize;
        }

    }

    static class Evicted<K, V> implements CacheEvent.CacheEvicted<K, V> {
        private final Cache<K, V> cache;

        private final int currentSize;

        private final int previousSize;

        public Evicted(final Cache<K, V> cache, final int previousSize,
                final int currentSize) {
            this.cache = cache;
            this.previousSize = previousSize;
            this.currentSize = currentSize;
        }

        /** {@inheritDoc} */
        public Cache<K, V> getCache() {
            return cache;
        }

        /** {@inheritDoc} */
        public int getCurrentSize() {
            return currentSize;
        }

        /** {@inheritDoc} */
        public String getName() {
            return CacheEvent.CacheEvicted.NAME;
        }

        /** {@inheritDoc} */
        public int getPreviousSize() {
            return previousSize;
        }
    }

    /**
     * @param previousSize
     *            the size of the cache before it was cleared
     * @param previousCapacity
     *            the capacity of the cache before it was cleared
     * @param cache
     *            the cache that was cleared
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of values maintained by the cache
     * @return a Cleared event from the specified parameters
     */
    static <K, V> CacheEvent<K, V> cleared(Cache<K, V> cache, int previousSize,
            long previousCapacity) {
        return new Cleared<K, V>(cache, previousSize, previousCapacity);
    }

    static <K, V> CacheEvent<K, V> evicted(Cache<K, V> cache, int currentSize,
            int previousSize, long currentCapacity, long previousCapacity) {
        return new Evicted<K, V>(cache, currentSize, previousSize);
    }
}
