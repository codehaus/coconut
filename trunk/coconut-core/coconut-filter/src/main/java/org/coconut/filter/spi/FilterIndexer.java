package org.coconut.filter.spi;

import java.util.Collection;

import org.coconut.filter.Filter;

public interface FilterIndexer<K,E> {

    boolean add(K key, Filter<E> filter);
    
    Collection<? extends K> match(E event);
    
    boolean remove(K key);
}
