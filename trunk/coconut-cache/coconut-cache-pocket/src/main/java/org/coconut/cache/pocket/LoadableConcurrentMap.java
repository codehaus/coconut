/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadableConcurrentMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return super.keySet();
    }

    @Override
    public V remove(Object key) {
        Object v = map.remove(key);
        if (v instanceof MyLock) {
            checkCancelLock((MyLock) v);
            return null;
        } else {
            return (V) v;
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection<V> values() {
        // TODO Auto-generated method stub
        return super.values();
    }

    private final ConcurrentMap map = new ConcurrentHashMap();

    private final ValueLoader<K, V> loader = null;

    // specify max size
    // specify loader
    public V peek(K key) {
        Object v = map.get(key);
        return v instanceof MyLock ? null : (V) v;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#get0(java.lang.Object, boolean)
     */
    @Override
    public V get(Object k) {
        K key = (K) k;
        Object v = map.get(key);

        if (v == null) {
            MyLock reserved = new MyLock(new LoadValueCallable<K, V>(this, key));
            v = map.putIfAbsent(key, reserved);
            if (v == null) {
                v = reserved;
            }
        }

        if (v instanceof MyLock) {
            MyLock lock = (MyLock) v;
            try {
                v = lock.get();
            } catch (InterruptedException e) {
                // do we want to load it myself?
            } catch (ExecutionException e) {
                if (e.getCause() instanceof Error) {
                    throw (Error) e.getCause();
                } else if (e instanceof Exception) {

                }
                e.printStackTrace();
            }
        } else {
            return (V) v;
        }
        return null;
    }

    public boolean isLoading(K key) {
        return map.get(key) instanceof MyLock;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCache#put0(java.lang.Object,
     *      java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public V put(K key, V value) {
        Object v = map.put(key, value);
        if (v instanceof MyLock) {
            MyLock lock = (MyLock) v;
            // if allready set return previous value
            lock.set(value);
            return null;
        } else {
            return (V) v;
        }
    }

    /**
     * @see java.util.AbstractMap#entrySet()
     */
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        // TODO Auto-generated method stub
        return null;
    }

    protected boolean interruptLoader() {
        return false;
    }

    static class MyLock extends FutureTask {

        /**
         * @param callable
         */
        public MyLock(LoadValueCallable callable) {
            super(callable);
        }

        @Override
        protected void set(Object v) {
            super.set(v);
        }

        @Override
        protected void setException(Throwable t) {
            super.setException(t);
        }
    }

    /**
     * This class can use in for asynchronously loading a value into the cache.
     */
    @SuppressWarnings("hiding")
    static class LoadValueCallable<K, V> implements Callable<V> {
        /* The cache to load the value from */
        private final LoadableConcurrentMap<K, V> c;

        /* The key for which a value should be loaded */
        private final K key;

        /**
         * Constructs a new LoadValueCallable.
         * 
         * @param c
         *            The cache to load the value from
         * @param key
         *            The key for which a value should be loaded
         */
        LoadValueCallable(LoadableConcurrentMap<K, V> c, K key) {
            this.c = c;
            this.key = key;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() throws Exception {
            return c.loader.load(key);
        }
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object,
     *      java.lang.Object)
     */
    public V putIfAbsent(K key, V value) {
        throw new UnsupportedOperationException("putIfAbsent not supported");
    }

    @Override
    public void clear() {
        if (interruptLoader()) {

        } else {
            super.clear();
        }
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object,
     *      java.lang.Object)
     */
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
     *      java.lang.Object)
     */
    public V replace(K key, V value) {
        Object v = map.replace(key, value);
        if (v instanceof MyLock) {
            checkCancelLock((MyLock) v);
            return null;
        } else {
            return (V) v;
        }
    }

    /**
     * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }
    
    private void checkCancelLock(MyLock lock) {
        
    }
}
