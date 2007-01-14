/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.memory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheQuery;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
class CopyQuery<K, V> implements CacheQuery<K, V> {

    class Iter implements Iterator<CacheEntry<K, V>> {

        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return index < entries.length;
        }

        /**
         * @see java.util.Iterator#next()
         */
        public CacheEntry<K, V> next() {
            if (index >= entries.length) {
                throw new NoSuchElementException();
            }
            return entries[index++];
        }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private Iterator<CacheEntry<K, V>> iterator;

    final CacheEntry<K, V>[] entries;

    int index;

    CopyQuery(CacheEntry<K, V>[] entries) {
        this.entries = entries;
    }

    /**
     * @see org.coconut.cache.CacheQuery#get(int, int)
     */
    public List<CacheEntry<K, V>> get(int from, int to) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.coconut.cache.CacheQuery#getAll()
     */
    public List<CacheEntry<K, V>> getAll() {
        throw new UnsupportedOperationException();
    }

    /**
     * integer.max==end??
     * 
     * @see org.coconut.cache.CacheQuery#getCurrentIndex()
     */
    public int getCurrentIndex() {
        return index;
    }

    /**
     * @see org.coconut.cache.CacheQuery#getNext(int)
     */
    public List<CacheEntry<K, V>> getNext(int count) {
        int realCount = count + index > entries.length ? entries.length - index : count;
        CacheEntry<K, V>[] e = new CacheEntry[realCount];
        System.arraycopy(entries, index, e, 0, realCount);
        index += realCount;
        return Arrays.asList(e);
    }

    /**
     * @see org.coconut.cache.CacheQuery#getTotalCount()
     */
    public int getTotalCount() {
        return entries.length;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return index < entries.length;
    }

    /**
     * @see org.coconut.cache.CacheQuery#isIndexable()
     */
    public boolean isIndexable() {
        return false;
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<CacheEntry<K, V>> iterator() {
        if (iterator == null) {
            iterator = new Iter();
        }
        return iterator;
    }

    /**
     * @see java.util.Iterator#next()
     */
    public CacheEntry<K, V> next() {
        if (index >= entries.length) {
            throw new NoSuchElementException();
        }
        return entries[index++];
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
