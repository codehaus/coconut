package org.coconut.cache.internal.service.entry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.coconut.cache.internal.service.eviction.InternalCacheEvictionService;
import org.coconut.cache.internal.service.servicemanager.ServiceComposer;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;

public class SynchronizedEntryMap<K, V> extends EntryMap<K, V> {

    public SynchronizedEntryMap(ServiceComposer sc,
            AbstractCacheEntryFactoryService<K, V> entryService,
            InternalCacheEvictionService<K, V, AbstractCacheEntry<K, V>> evictionService,
            InternalCacheSupport<K, V> ics) {
        super(sc, entryService, evictionService, ics);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet(ConcurrentMap<K, V> cache) {
        synchronized (cache) {
            return entrySet != null ? entrySet : (entrySet = (Set) new EntrySetSynchronized<K, V>(
                    cache, this));
        }
    }

    @Override
    public Set<K> keySet(ConcurrentMap<K, V> cache) {
        synchronized (cache) {
            return keySet != null ? keySet : (keySet = new KeySetSynchronized<K, V>(cache, this));
        }
    }

    /**
     * @see java.util.AbstractMap#values()
     */
    @Override
    public Collection<V> values(Map<K, V> cache) {
        synchronized (cache) {
            return values != null ? values : (values = new ValuesSynchronized<K, V>(cache, this));
        }
    }

    static class ValuesSynchronized<K, V> extends Values<K, V> {
        final Object mutex;

        ValuesSynchronized(Map<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
            this.mutex = cache;
        }

        /** {@inheritDoc} */
        @Override
        public boolean remove(Object o) {
            synchronized (mutex) {
                return super.remove(o);
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean containsAll(Collection<?> c) {
            synchronized (mutex) {
                return super.containsAll(c);
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean retainAll(Collection<?> c) {
            synchronized (mutex) {
                return super.retainAll(c);
            }
        }

        /** {@inheritDoc} */
        @Override
        public Object[] toArray() {
            synchronized (mutex) {
                return super.toArray();
            }
        }

        /** {@inheritDoc} */
        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return super.toArray(a);
            }
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            synchronized (mutex) {
                return super.toString();
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean removeAll(Collection<?> c) {
            synchronized (mutex) {
                return super.removeAll(c);
            }
        }
    }

    static class KeySetSynchronized<K, V> extends KeySet<K, V> {
        private final Object mutex;

        KeySetSynchronized(ConcurrentMap<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
            this.mutex = cache;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            synchronized (mutex) {
                return super.equals(o);
            }
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            synchronized (mutex) {
                return super.hashCode();
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean removeAll(Collection<?> c) {
            synchronized (mutex) {
                return super.removeAll(c);
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean containsAll(Collection<?> c) {
            synchronized (mutex) {
                return super.containsAll(c);
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean retainAll(Collection<?> c) {
            synchronized (mutex) {
                return super.retainAll(c);
            }
        }

        /** {@inheritDoc} */
        @Override
        public Object[] toArray() {
            synchronized (mutex) {
                return super.toArray();
            }
        }

        /** {@inheritDoc} */
        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return super.toArray(a);
            }
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            synchronized (mutex) {
                return super.toString();
            }
        }
    }

    static class EntrySetSynchronized<K, V> extends EntrySet<K, V> {
        private final Object mutex;

        EntrySetSynchronized(ConcurrentMap<K, V> cache, EntryMap<K, V> map) {
            super(cache, map);
            this.mutex = cache;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            synchronized (mutex) {
                return super.equals(o);
            }
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            synchronized (mutex) {
                return super.hashCode();
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean removeAll(Collection<?> c) {
            synchronized (mutex) {
                return super.removeAll(c);
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean containsAll(Collection<?> c) {
            synchronized (mutex) {
                return super.containsAll(c);
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean retainAll(Collection<?> c) {
            synchronized (mutex) {
                return super.retainAll(c);
            }
        }

        /** {@inheritDoc} */
        @Override
        public Object[] toArray() {
            synchronized (mutex) {
                return super.toArray();
            }
        }

        /** {@inheritDoc} */
        @Override
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {
                return super.toArray(a);
            }
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            synchronized (mutex) {
                return super.toString();
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean contains(Object o) {
            synchronized (mutex) {
                return super.contains(o);
            }
        }

        /** {@inheritDoc} */
        @Override
        public boolean remove(Object o) {
            synchronized (mutex) {
                return super.remove(o);
            }
        }
    }
}
