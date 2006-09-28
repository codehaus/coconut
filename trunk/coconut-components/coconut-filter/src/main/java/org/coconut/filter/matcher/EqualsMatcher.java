/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.matcher;

import java.util.Collection;

import org.coconut.filter.Filter;
import org.coconut.filter.spi.FilterIndexer;

public class EqualsMatcher<K, V, E> implements FilterIndexer<K, E> {

    public boolean add(K key, Filter<E> filter) {
        // TODO Auto-generated method stub
        return false;
    }

    public Collection<? extends K> match(E event) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean remove(K key) {
        // TODO Auto-generated method stub
        return false;
    }

}
