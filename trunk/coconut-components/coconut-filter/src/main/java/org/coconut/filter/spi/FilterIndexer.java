/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.spi;

import java.util.Collection;

import org.coconut.filter.Filter;

public interface FilterIndexer<K,E> {

    boolean add(K key, Filter<E> filter);
    
    Collection<? extends K> match(E event);
    
    boolean remove(K key);
}
