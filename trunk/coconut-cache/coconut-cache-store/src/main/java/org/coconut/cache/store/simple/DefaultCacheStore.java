/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.coconut.cache.CacheLoader;
import org.coconut.cache.sandbox.store.Store;
import org.coconut.core.Callback;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultCacheStore<K, V> implements Store<K, V>, CacheLoader<K, V> {

    public static String ff = "c:/cache/" + System.currentTimeMillis() + "/";

    private final static char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private Entry getFile(final K key, boolean createNew, boolean LoadValue)
            throws IOException, ClassNotFoundException {
        StringBuilder sb = new StringBuilder(ff);
        final int value = key.hashCode();
        short j = (short) (value ^ (value >>> 16));
        sb.append(DIGITS[(j >> 12) & 0xf]);
        sb.append(DIGITS[(j >> 8) & 0xf]);
        sb.append("/");
        sb.append(DIGITS[(j >> 4) & 0xf]);
        sb.append(DIGITS[j & 0xf]);
        sb.append("/");
        sb.append(Integer.toHexString(value));
        File f = new File(sb.toString());
        File dir = f.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String prefix = Integer.toHexString(value);
        synchronized (this) {
            File[] files = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(prefix);
                }
            });
            for (int i = 0; i < files.length; i++) {
                Entry e = loadFile(files[i], key, LoadValue);
                if (e != null) {
                    return e;
                }
            }
            if (createNew) {
                sb.append("-");
                sb.append(files.length);
                f = new File(sb.toString());
                Entry e = new Entry();
                e.file = f;
                return e;
            }
        }
        return null;
    }

    private Entry loadFile(File file, K key, boolean loadValue)
            throws IOException, ClassNotFoundException {
        final FileInputStream str;
        try {
            str = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null; // no value found
        }

        ObjectInputStream oos = new ObjectInputStream(str);
        try {
            final K oKey = (K) oos.readObject();
            if (key == null || key.equals(oKey)) {
                Entry e = new Entry();
                e.file = file;
                e.key = oKey;

                if (loadValue) {
                    final V oValue = (V) oos.readObject();
                    e.value = oValue;
                }
                return e;
            }
        } finally {
            oos.close();
        }
        return null;

    }

    /**
     * @see org.coconut.cache.sandbox.store.Store#delete(K, boolean)
     */
    public Future<V> delete(K key, boolean retrivePreviousValue) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.sandbox.store.Store#deleteAll(java.util.Collection,
     *      boolean)
     */
    public Future<Map<K, V>> deleteAll(Collection<? extends K> colKeys,
            boolean retrivePreviousValues) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.sandbox.store.Store#deleteAll(org.coconut.filter.Filter,
     *      boolean)
     */
    public Future<Map<K, V>> deleteAll(Filter<? extends V> filter,
            boolean retrivePreviousValues) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.sandbox.store.Store#loadAll(org.coconut.filter.Filter)
     */
    public Future<Map<K, V>> loadAll(Filter<? extends V> filter) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.sandbox.store.Store#loadAllAsync(java.util.Collection)
     */
    public Future<Map<K, V>> loadAllAsync(Collection<? extends K> keys) {
        return loadAllA(keys);
    }

    /**
     * @see org.coconut.cache.sandbox.store.Store#loadAllAsync(org.coconut.filter.Filter)
     */
    public Future<Map<K, V>> loadAllAsync(Filter<? extends V> filter) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.sandbox.store.Store#loadAsync(K)
     */
    public Future<V> loadAsync(K key, Callback<V> callback) {
        return loadA(key, callback);
    }

    private BaseFuture<V> loadA(K key, Callback<V> callback) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        Load<K, V> sv = new Load<K, V>(key);
        schedule(sv);
        return sv;
    }

    private BaseFuture<Map<K, V>> loadAllA(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("key is null");
        }
        // check all keys for null
        LoadAll<K, V> sv = new LoadAll<K, V>(keys);
        schedule(sv);
        return sv;
    }

    void handleLoadValue(Load<K, V> sv) {
        V result = null;
        try {
            Entry f = getFile(sv.key, false, true);
            result = f.value;
            sv.completed(result);
        } catch (Exception ioe) {
            sv.failed(ioe);
        }
    }

    void handleStoreValue(StoreValue<K, V> sv) {
        V result = null;
        try {
            Entry f = getFile(sv.key, true, sv.returnPrevious);

            result = sv.value;
            if (f.key != null) {
                f.file.delete(); // delete old file
            }
            f.file.createNewFile();
            FileOutputStream str = new FileOutputStream(f.file);
            ObjectOutputStream oos = new ObjectOutputStream(str);
            oos.writeObject(sv.key);
            oos.writeObject(sv.value);
            oos.close();
            sv.set(result);
        } catch (Exception ioe) {
            sv.setException(ioe);
        }
    }

    /**
     * @see org.coconut.cache.sandbox.store.Store#store(K, V, boolean)
     */
    public Future<V> store(K key, V value, boolean retrivePreviousValues) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (value == null) {
            throw new NullPointerException("value is null");
        }
        StoreValue<K, V> sv = new StoreValue<K, V>(key, value,
                retrivePreviousValues);
        schedule(sv);
        return sv;
    }

    private void schedule(Object o) {

    }

    /**
     * @see org.coconut.cache.sandbox.store.Store#storeAll(java.util.Map,
     *      boolean)
     */
    public Future<Map<K, V>> storeAll(Map<K, V> entries,
            boolean retrivePreviousValues) {
        throw new UnsupportedOperationException();
    }

    class Entry {
        File file;

        K key;

        V value;
    }

    /**
     * @see org.coconut.cache.CacheLoader#load(K)
     */
    public V load(K key) throws Exception {
        return loadA(key, null).getNoExecuteException();
    }

    /**
     * @see org.coconut.cache.CacheLoader#loadAll(java.util.Collection)
     */
    public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    private final static Callable<?> NULL_CALLABLE = new Callable() {
        public Object call() throws Exception {
            return null;
        }
    };

    static class BaseFuture<V> extends FutureTask<V> {

        Callback<V> callback;

        BaseFuture(Callback<V> callback) {
            super((Callable<V>) NULL_CALLABLE);
            this.callback = callback;
        }

        BaseFuture() {
            this(null);
        }

        protected V getNoExecuteException() throws Exception {
            try {
                return get();
            } catch (ExecutionException ee) {
                throw (Exception) ee.getCause();
            }
        }

        protected void completed(V v) {
            if (callback != null) {
                callback.completed(v);
            }
            set(v);
        }

        protected void failed(Throwable t) {
            if (callback != null) {
                callback.failed(t);
            }
            setException(t);
        }
    }

    static class StoreValue<K, V> extends BaseFuture<V> {
        private final K key;

        private final V value;

        private final boolean returnPrevious;

        public StoreValue(K key, V value, boolean returnPrevious) {
            this.key = key;
            this.value = value;
            this.returnPrevious = returnPrevious;
        }

        @Override
        protected void set(V v) {
            super.set(v);
        }

        @Override
        protected void setException(Throwable t) {
            super.setException(t);
        }

    }

    static class Load<K, V> extends BaseFuture<V> {
        private final K key;

        public Load(K key) {
            this.key = key;
        }

        protected K getKey() {
            return key;
        }
    }

    static class LoadAll<K, V> extends BaseFuture<Map<K, V>> {
        private final Collection<? extends K> keys;

        public LoadAll(Collection<? extends K> keys) {
            this.keys = keys;
        }

        protected Collection<? extends K> getKeys() {
            return keys;
        }
    }
}
