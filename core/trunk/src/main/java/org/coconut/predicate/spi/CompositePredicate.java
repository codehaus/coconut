/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.predicate.spi;

import java.util.List;

import org.coconut.predicate.Predicate;

/**
 * This interface should be implemented by filters that wrap other filters.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CompositePredicate<E> {

    /**
     * Returns all the predicates that are used for the accept method.
     */
    List<? extends Predicate<? super E>> getPredicates();
}
