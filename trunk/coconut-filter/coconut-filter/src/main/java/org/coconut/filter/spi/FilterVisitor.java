/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.spi;

import org.coconut.filter.Filter;

// breathfirst or depthfirst
public interface FilterVisitor<E> {
    void visitXorFilter(Filter<E> left, Filter<E> right);

    void visitClassBasedFilter(Class<?> clazz);

    // void filterEnter(Filter filter ) {
    //        
    // }
    //
    // void filterExit(Filter filter ) {
    //        
    // }

}
