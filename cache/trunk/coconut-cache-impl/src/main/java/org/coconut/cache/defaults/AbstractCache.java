/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.Iterator;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;

/**
 * An abstract implementation of {@link Cache}. Currently not general usable, hence some
 * methods and constructors have package private access.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    public CacheServices<K, V> services() {
        return new CacheServices<K, V>(this);
    }

    /**
     * Returns a string representation of this cache. The string representation consists
     * of a list of key-value mappings in the order returned by the caches
     * <tt>entrySet</tt> view's iterator, enclosed in braces (<tt>"{}"</tt>).
     * Adjacent mappings are separated by the characters <tt>", "</tt> (comma and
     * space). Each key-value mapping is rendered as the key followed by an equals sign (<tt>"="</tt>)
     * followed by the associated value. Keys and values are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this cache
     */
    @Override
    public String toString() {
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this Cache)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Cache)" : value);
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(", ");
        }
    }
}
