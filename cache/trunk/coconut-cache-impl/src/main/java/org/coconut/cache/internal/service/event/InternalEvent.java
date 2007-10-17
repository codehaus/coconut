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
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
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
        public long getPreviousVolume() {
            return previousCapacity;
        }

        /** {@inheritDoc} */
        public int getPreviousSize() {
            return previousSize;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof CacheEvent.CacheCleared
                    && equals((CacheEvent.CacheCleared) obj);
        }

        public boolean equals(CacheEvent.CacheCleared<K, V> event) {
            return cache.equals(event.getCache()) && getName() == event.getName()
                    && getPreviousSize() == event.getPreviousSize()
                    && getPreviousVolume() == event.getPreviousVolume();
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return cache.hashCode() ^ previousSize;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return getName() + " [previousSize = " + getPreviousSize()
                    + " , previousCapacity = " + getPreviousVolume() + "]";
        }
    }

//    /**
//     * The default implementation of the cache Evicted event.
//     * 
//     * @param <K>
//     *            the type of keys maintained by the cache
//     * @param <V>
//     *            the type of values maintained by the cache
//     */
//    static class Evicted<K, V> implements CacheEvent.CacheEvicted<K, V> {
//        private final Cache<K, V> cache;
//
//        private final int currentSize;
//
//        private final int previousSize;
//
//        public Evicted(final Cache<K, V> cache, final int previousSize,
//                final int currentSize) {
//            this.cache = cache;
//            this.previousSize = previousSize;
//            this.currentSize = currentSize;
//        }
//
//        /** {@inheritDoc} */
//        public Cache<K, V> getCache() {
//            return cache;
//        }
//
//        /** {@inheritDoc} */
//        public int getCurrentSize() {
//            return currentSize;
//        }
//
//        /** {@inheritDoc} */
//        public String getName() {
//            return CacheEvent.CacheEvicted.NAME;
//        }
//
//        /** {@inheritDoc} */
//        public int getPreviousSize() {
//            return previousSize;
//        }
//    }
//
//    /**
//     * An event indicating that {@link Cache#evict()} was called on a particular
//     * {@link Cache}.
//     */
//    interface CacheEvicted<K, V> extends CacheEvent<K, V> {
//        /** The unique name of the event. */
//        String NAME = "cache.evicted";
//
//        /**
//         * Returns the current number of elements contained in the cache after evict has
//         * been called.
//         * @return the current number of elements contained in the cache after evict has
//         * been called 
//         */
//        int getCurrentSize();
//
//        /**
//         * Return the previous number of elements contained in the cache before the call
//         * to evict.
//         * @return the previous number of elements contained in the cache before the call
//         * to evict
//         */
//        int getPreviousSize();
//    }
    /**
     * Creates a new {@link CacheCleared} event from the specified parameters.
     * 
     * @param previousSize
     *            the size of the cache before it was cleared
     * @param previousCapacity
     *            the capacity of the cache before it was cleared
     * @param cache
     *            the cache that was cleared
     * @return a Cleared event from the specified parameters
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of values maintained by the cache
     */
    static <K, V> CacheEvent.CacheCleared<K, V> cleared(Cache<K, V> cache,
            int previousSize, long previousCapacity) {
        return new Cleared<K, V>(cache, previousSize, previousCapacity);
    }
}
