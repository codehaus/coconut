/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.memory;

import org.coconut.internal.forkjoin.ParallelArray;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Procedure;

public interface MemoryStoreWithMapping<T> {
    void clear();
    ParallelArray<T> all();
    ParallelArray<T> all(Class<? super T> elementType);
    T any();
    void apply(Procedure<? super T> procedure);
    int size();
    <U> MemoryStoreWithMapping<U> withMapping(Mapper<? super T, ? extends U> mapper);
}