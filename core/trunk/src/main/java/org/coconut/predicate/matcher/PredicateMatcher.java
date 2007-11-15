/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate.matcher;

import java.util.List;
import java.util.Map;

import org.coconut.predicate.Predicate;

/**
 * A PredicateMatcher can be used to query a large number of predicates to obtain all those
 * that will match a particular object.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface PredicateMatcher<K, E> extends Map<K, Predicate<? super E>> {

    /**
     * This method will return the registered keys for all those filters that accepts the
     * specified object. The list returned should be processed in the order returned by
     * the lists iterator.
     * 
     * @param object
     *            the object that should be matched against
     * @return a Collection of keys whose filter match the particular event.
     */
    List<K> match(E object);
}
