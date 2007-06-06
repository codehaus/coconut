package org.coconut.cache.tck.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { BasicCache.class, BasicMap.class, ClearRemove.class,
        ConcurrentMap.class, Constructors.class, Put.class, EntrySet.class,
        EntrySetModifying.class, KeySet.class, KeySetModifying.class, Values.class,
        ValuesModifying.class, })
public class CoreSuite {

}
