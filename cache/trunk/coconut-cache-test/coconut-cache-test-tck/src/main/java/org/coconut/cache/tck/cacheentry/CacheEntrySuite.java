package org.coconut.cache.tck.cacheentry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { Cost.class, CreationTime.class, Hits.class, KeyValue.class,
        LastAccessedTime.class, LastModifiedTime.class, Size.class })
public class CacheEntrySuite {

}
