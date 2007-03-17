/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultFilterMatcher<K, E> extends AbstractFilterMatcher<K, E> {
    public DefaultFilterMatcher() {
        this(new ConcurrentHashMap<K, Filter<? super E>>());
    }

    public DefaultFilterMatcher(Map<K, Filter<? super E>> map) {
        super(map);
    }

    /**
     * @see org.coconut.filter.spi.FilterIndexer#match(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public List<K> match(E event) {
        List<K> al = null;
        for (Map.Entry<K, Filter<? super E>> f : getMap().entrySet()) {
            if (f.getValue().accept(event)) {
                if (al == null) {
                    al = new ArrayList<K>();
                }
                al.add(f.getKey());
            }
        }
        return al == null ? Collections.EMPTY_LIST : al;
    }
}
