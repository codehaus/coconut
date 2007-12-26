/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.predicatematcher;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.coconut.operations.Ops.Predicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractPredicateMatcher<K, E> implements PredicateMatcher<K, E> {

    private final Map<K, Predicate<? super E>> map;

    /**
     * Creates a new AbstractPredicateMatcher using the specified Map as backend.
     *
     * @param map
     *            the map to use as backend
     */
    public AbstractPredicateMatcher(Map<K, Predicate<? super E>> map) {
        this.map = map;
    }

    /** {@inheritDoc} */
    public void clear() {
        map.clear();
    }

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /** {@inheritDoc} */
    public Set<java.util.Map.Entry<K, Predicate<? super E>>> entrySet() {
        return map.entrySet();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    /** {@inheritDoc} */
    public Predicate<? super E> get(Object key) {
        return map.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return map.hashCode();
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /** {@inheritDoc} */
    public Set<K> keySet() {
        return map.keySet();
    }

    /** {@inheritDoc} */
    public Predicate<? super E> put(K key, Predicate<? super E> value) {
        return map.put(key, value);
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends K, ? extends Predicate<? super E>> t) {
        map.putAll(t);
    }

    /** {@inheritDoc} */
    public Predicate<? super E> remove(Object key) {
        return map.remove(key);
    }

    /** {@inheritDoc} */
    public int size() {
        return map.size();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return map.toString();
    }

    /** {@inheritDoc} */
    public Collection<Predicate<? super E>> values() {
        return map.values();
    }

    /**
     * @return the map we are wrapping
     */
    protected Map<K, Predicate<? super E>> getMap() {
        return map;
    }

}
