package org.coconut.filter.spi;

import org.coconut.filter.Filter;

//breathfirst or depthfirst
public interface FilterVisitor<E> {
    void visitXorFilter(Filter<E> left, Filter<E> right);
    void visitClassBasedFilter(Class<?> clazz);
    
//    void filterEnter(Filter filter ) {
//        
//    }
//
//    void filterExit(Filter filter ) {
//        
//    }

    
    interface FF {
        
    }
}
