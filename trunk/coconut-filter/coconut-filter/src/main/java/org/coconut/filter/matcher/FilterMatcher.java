/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter.matcher;

import java.util.Collection;
import java.util.Map;

import org.coconut.core.EventProcessor;
import org.coconut.filter.Filter;

/**
 * A FilterMatcher can be used to query a large number of filters to obtain all
 * those that will match a particular object
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface FilterMatcher<K, E> extends Map<K, Filter<? super E>> {

    /**
     * This method will return the registered keys for all those filters that
     * accepts the specified object.
     * 
     * @param object
     * @return a Collection of keys whose filter match the particular event.
     */
    Collection<K> match(E object);
    
    void match(E object, EventProcessor<E> eh);
}
