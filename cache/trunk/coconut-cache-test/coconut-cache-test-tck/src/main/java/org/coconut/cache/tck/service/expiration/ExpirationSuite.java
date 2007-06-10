package org.coconut.cache.tck.service.expiration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ExpirationEvict.class, ExpirationFilterBased.class,
        ExpirationPutWithTimeouts.class, ExpirationService.class,
        ExpirationWithDefaultTimeout.class, ExpirationWithExplicitTimeout.class })
public class ExpirationSuite {

}
