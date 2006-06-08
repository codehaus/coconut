package org.coconut.filter.spi;

import java.util.List;

import org.coconut.filter.Filter;

/**
 * This interface should be implemented by filters that wrap other filters.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CompositeFilter<E> {

    /**
     * Returns all the filters that are used for the accept method.
     */
    List<Filter<E>> getFilters();
}
