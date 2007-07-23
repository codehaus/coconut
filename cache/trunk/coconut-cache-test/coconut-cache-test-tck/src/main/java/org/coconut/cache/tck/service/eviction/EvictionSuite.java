/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.eviction;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { EvictionMXBean.class, EvictionService.class,
        EvictionNoPolicy.class, EvictionLRU.class, EvictionLandlord.class,
        EvictionClock.class, CacheEntryToPolicy.class, SimplePolicyEviction.class })
public class EvictionSuite {

    // special support
    // 1. Buildin replacement policy, don't use EvictionLRU
    // 2. No loading support,
    // 3. Idling Support
    // 3.a immutable once set in configuration
    // 4. maximumSize
    // 4.a immutable once set in configuration
    // 5. maximumCapacity
    // 5.a immutable once set in configuration
}
