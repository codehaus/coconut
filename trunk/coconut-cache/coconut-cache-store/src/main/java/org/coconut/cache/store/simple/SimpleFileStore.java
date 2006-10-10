/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.cache.CacheLoader;
import org.coconut.cache.CacheEntryEvent.ItemAdded;
import org.coconut.cache.spi.AsyncCacheLoader;
import org.coconut.cache.spi.AsyncLoadCallback;
import org.coconut.core.Callback;
import org.coconut.event.annotation.Handler;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class SimpleFileStore<K, V> implements 
        AsyncCacheLoader<K, V> {

    public static String ff = "c:/cache/" + System.currentTimeMillis() + "/";

    private final BlockingQueue q = new LinkedBlockingQueue();

    private final Lock lock = new ReentrantLock();

    @Handler
    public void handleEvicted(ItemAdded<K, V> event) {
        q.add(event);
        performWork();
    }

    private void performWork() {
        if (lock.tryLock()) {
            try {
                doWork();
            } finally {
                lock.unlock();
            }
        }
    }

    private void doWork() {
        Object o = q.poll();
        while (o != null) {
            // TODO should be item evicted
            if (o instanceof ItemAdded) {
                ItemAdded<K, V> evicted = (ItemAdded) o;
                try {
                    store(evicted.getKey(), evicted.getValue());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else if (o instanceof Runnable) {
                ((Runnable) o).run();
            } else {
                System.out.println(o.getClass());
            }
            o = q.poll();
        }
    }

    /**
     * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoad(java.lang.Object)
     */
    public Future<V> asyncLoad(K key) {
        Future<V> f = new MyFuture(new InnerCallable(key));
        q.add(f);
        return f;
    }

    public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws ClassNotFoundException
     * @throws IOException
     * @see org.coconut.cache.util.AbstractCacheLoader#asyncLoad(K)
     */
    public V load(K key) throws IOException, ClassNotFoundException {
        File f = getFolder(key);
        if (!f.exists())
            return null;
        File file = getFile(key);
        final FileInputStream str;
        try {
            str = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null; // no value found
        }

        ObjectInputStream oos = new ObjectInputStream(str);
        K oKey = (K) oos.readObject();
        if (key.equals(oKey)) {
            V oValue = (V) oos.readObject();
            return oValue;
        }

        oos.close();
        return null;
    }

    /**
     * @see org.coconut.cache.OldCacheStore#erase(K)
     */
    public void erase(K key) throws Exception {
        File f = getFile(key);
        f.delete();
    }

    /**
     * @see org.coconut.cache.OldCacheStore#store(K, V)
     */
    public void store(K key, V value) throws Exception {
        File f = getFolder(key);
        if (!f.exists())
            f.mkdir();
        File file = getFile(key);
        FileOutputStream str = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(str);
        oos.writeObject(key);
        oos.writeObject(value);
        oos.close();
    }

    private File getFolder(K key) {
        String filename = getFolderName(key.hashCode());
        File f = new File(ff + filename);
        return f;
    }

    private File getFile(K key) {
        File f = new File(ff + getFolderName(key.hashCode())
                + getFileName(key.hashCode()));
        return f;
    }



    private static String getFolderName(int i) {
        char[] chars = new char[3];
        chars[0] = DIGITS[(i >> 28) & 0xf];
        chars[1] = DIGITS[(i >> 24) & 0xf];
        chars[2] = '/';
        return new String(chars);
    }

    private static String getFileName(int i) {
        char[] chars = new char[6];
        chars[0] = DIGITS[(i >> 20) & 0xf];
        chars[1] = DIGITS[(i >> 16) & 0xf];
        chars[2] = DIGITS[(i >> 12) & 0xf];
        chars[3] = DIGITS[(i >> 8) & 0xf];
        chars[4] = DIGITS[(i >> 4) & 0xf];
        chars[5] = DIGITS[i & 0xf];
        return new String(chars);
    }

    private final static char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * @see org.coconut.cache.OldCacheStore#entries()
     */
    public Iterable<Entry<? extends K, ? extends V>> entries() throws Exception {
        return null;
    }

    class MyFuture<V> extends FutureTask<V> {

        public V get() throws InterruptedException, ExecutionException {
            performWork();
            return super.get();
        }

        public V get(long timeout, TimeUnit unit) throws InterruptedException,
                ExecutionException, TimeoutException {
            performWork();
            return super.get(timeout, unit);
        }

        /**
         * @param callable
         */
        public MyFuture(Callable<V> callable) {
            super(callable);
        }

    }

    private final class InnerCallable implements Callable<V> {
        private final K key;

        /**
         * @param key
         */
        public InnerCallable(final K key) {
            this.key = key;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() throws Exception {
            return load(key);
        }
    }

    /**
     * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoad(java.lang.Object, org.coconut.core.Callback)
     */
    public Future<?> asyncLoad(K key, Callback<V> c) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoadAll(java.util.Collection, org.coconut.core.Callback)
     */
    public Future<?> asyncLoadAll(Collection<? extends K> keys, Callback<Map<K, V>> c) {
        // TODO Auto-generated method stub
        return null;
    }


}
