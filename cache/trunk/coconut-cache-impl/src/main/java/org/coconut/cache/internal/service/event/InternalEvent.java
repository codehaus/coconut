/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
final class InternalEvent {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private InternalEvent() {}

    // /CLOVER:ON
    static int cacheHashCode(Cache<?, ?> cache) {
        return cache.getName().hashCode();
    }

    /**
     * Creates a new {@link CacheCleared} event from the specified parameters.
     * 
     * @param previousSize
     *            the size of the cache before it was cleared
     * @param previousVolume
     *            the capacity of the cache before it was cleared
     * @param cache
     *            the cache that was cleared
     * @return a Cleared event from the specified parameters
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of values maintained by the cache
     */
    static <K, V> CacheEvent.CacheCleared<K, V> cleared(Cache<K, V> cache, int previousSize,
            long previousVolume) {
        return new Cleared<K, V>(cache, previousSize, previousVolume);
    }

    static <K, V> CacheEvent.CacheStarted<K, V> started(Cache<K, V> cache) {
        return new Started<K, V>(cache);
    }

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

        /** The size of the cache before it was cleared. */
        private final int previousSize;

        /** The capacity of the cache before it was cleared. */
        private final long previousVolume;

        /**
         * Creates a new Cleared event.
         * 
         * @param previousSize
         *            the size of the cache before it was cleared
         * @param previousVolume
         *            the capacity of the cache before it was cleared
         * @param cache
         *            the cache that was cleared
         */
        Cleared(final Cache<K, V> cache, final int previousSize, final long previousVolume) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            } else if (previousVolume < 0) {
                throw new IllegalArgumentException("previousVolume must a non negative number");
            } else if (previousSize < 0) {
                throw new IllegalArgumentException("previousSize must a non negative number");
            }
            this.previousSize = previousSize;
            this.previousVolume = previousVolume;
            this.cache = cache;
        }

        public boolean equals(CacheEvent.CacheCleared<K, V> event) {
            return cache.equals(event.getCache()) && getName() == event.getName()
                    && getPreviousSize() == event.getPreviousSize()
                    && getPreviousVolume() == event.getPreviousVolume();
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof CacheEvent.CacheCleared && equals((CacheEvent.CacheCleared) obj);
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
        public int getPreviousSize() {
            return previousSize;
        }

        /** {@inheritDoc} */
        public long getPreviousVolume() {
            return previousVolume;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return (int) (previousVolume ^ (previousVolume >>> 32)) ^ previousSize;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return getName() + " [previousSize = " + getPreviousSize() + " , previousCapacity = "
                    + getPreviousVolume() + "]";
        }
    }

    /**
     * The default implementation of the cache cleared event.
     * 
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of values maintained by the cache
     */
    static class Started<K, V> implements CacheEvent.CacheStarted<K, V> {
        /** The cache that was cleared. */
        private final Cache<K, V> cache;

        /**
         * Creates a new Started event.
         * 
         * @param cache
         *            the cache that was started
         */
        Started(final Cache<K, V> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        public boolean equals(CacheEvent.CacheStarted<K, V> event) {
            return cache.equals(event.getCache()) && getName() == event.getName();
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof CacheEvent.CacheStarted && equals((CacheEvent.CacheStarted) obj);
        }

        /** {@inheritDoc} */
        public Cache<K, V> getCache() {
            return cache;
        }

        /** {@inheritDoc} */
        public String getName() {
            return CacheEvent.CacheStarted.NAME;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return cacheHashCode(cache) ^ getName().hashCode();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return getName();
        }
    }

}
