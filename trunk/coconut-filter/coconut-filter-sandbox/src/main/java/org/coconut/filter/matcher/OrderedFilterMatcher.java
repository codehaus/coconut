/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter.matcher;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class OrderedFilterMatcher<K, E> extends AbstractFilterMatcher<K, E> {

    private final ReentrantReadWriteLock rrw = new ReentrantReadWriteLock();

    /**
     * @param map
     */
    public OrderedFilterMatcher() {
        super(new TreeMap<K, Filter<? super E>>());
    }

    /**
     * @param map
     */
    public OrderedFilterMatcher(Comparator<K> c) {
        super(new TreeMap<K, Filter<? super E>>(c));
    }

    /**
     * @see org.coconut.filter.matcher.FilterMatcher#match(java.lang.Object)
     */
    public List<K> match(E object) {
        // TODO Auto-generated method stub
        return null;
    }

}
