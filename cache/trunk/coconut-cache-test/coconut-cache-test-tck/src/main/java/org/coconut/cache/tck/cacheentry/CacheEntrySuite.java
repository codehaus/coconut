package org.coconut.cache.tck.cacheentry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { Attributes.class, Cost.class, CreationTime.class,
        ExpirationTime.class, Hits.class, KeyValue.class, LastAccessedTime.class,
        LastModifiedTime.class, RefreshTime.class, Size.class })
public class CacheEntrySuite {

}
