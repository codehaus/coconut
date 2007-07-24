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
    
    static <K, V> CacheEvent<K, V> evicted(Cache<K, V> cache, int currentSize,
            int previousSize, long currentCapacity, long previousCapacity) {
        return new Evicted<K, V>(cache, currentSize, previousSize);
    }

    static <K, V> CacheEvent<K, V> cleared(Cache<K, V> cache, int previousSize,
            long previousCapacity) {
        return new Cleared<K, V>(cache, previousSize, previousCapacity);
    }

    static class Evicted<K, V> implements CacheEvent.CacheEvicted<K, V> {
        private final Cache<K, V> cache;

        private final int previousSize;

        private final int currentSize;

        public Evicted(final Cache<K, V> cache, final int previousSize,
                final int currentSize) {
            this.cache = cache;
            this.previousSize = previousSize;
            this.currentSize = currentSize;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent.CacheEvicted#getCurrentSize()
         */
        public int getCurrentSize() {
            return currentSize;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent.CacheEvicted#getPreviousSize()
         */
        public int getPreviousSize() {
            return previousSize;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getCache()
         */
        public Cache<K, V> getCache() {
            return cache;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getName()
         */
        public String getName() {
            return CacheEvent.CacheEvicted.NAME;
        }
    }

    static class Cleared<K, V> implements CacheEvent.CacheCleared<K, V> {
        private final int previousSize;

        private final long previousCapacity;

        private final Cache<K, V> cache;

        /**
         * @param previousSize
         * @param previousCapacity
         * @param cache
         */
        public Cleared(final Cache<K, V> cache, final int previousSize,
                final long previousCapacity) {
            this.previousSize = previousSize;
            this.previousCapacity = previousCapacity;
            this.cache = cache;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent.CacheCleared#getPreviousSize()
         */
        public int getPreviousSize() {
            return previousSize;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getCache()
         */
        public Cache<K, V> getCache() {
            return cache;
        }

        /**
         * @see org.coconut.cache.service.event.CacheEvent#getName()
         */
        public String getName() {
            return CacheEvent.CacheCleared.NAME;
        }

        public long getPreviousCapacity() {
            return previousCapacity;
        }

    }
}
