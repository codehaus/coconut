package org.coconut.cache.tck.service.eviction;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { EvictionMXBean.class, EvictionService.class,
        EvictionNoPolicy.class, EvictionLRU.class, EvictionLandlord.class,
        EvictionClock.class

})
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
