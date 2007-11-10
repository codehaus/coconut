/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { Attributes.class, Cost.class, CreationTime.class,
        ExpirationTime.class, GetEntry.class, Hits.class, KeyValue.class,
        LastAccessedTime.class, LastUpdatedTime.class, PeekEntry.class,
        RefreshTime.class, Size.class })
public class CacheEntrySuite {

}
