/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.spi;

import java.util.List;

import org.coconut.filter.Filter;

/**
 * This interface should be implemented by filters that wrap other filters.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CompositeFilter.java 36 2006-08-22 09:59:45Z kasper $
 */
public interface CompositeFilter<E> {

    /**
     * Returns all the filters that are used for the accept method.
     */
    List<Filter<E>> getFilters();
}
