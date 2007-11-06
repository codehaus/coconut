/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter.matcher;

import java.util.List;
import java.util.Map;

import org.coconut.filter.Predicate;

/**
 * A FilterMatcher can be used to query a large number of filters to obtain all
 * those that will match a particular object.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface PredicateMatcher<K, E> extends Map<K, Predicate<? super E>> {

    /**
     * This method will return the registered keys for all those filters that
     * accepts the specified object. The list returned should be processed in
     * the order returned by the lists iterator.
     * 
     * @param object
     * @return a Collection of keys whose filter match the particular event.
     */
    List<K> match(E object);
}
