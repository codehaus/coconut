/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ExpirationEvict.class, ExpirationFilterBased.class,
        ExpirationMXBean.class, ExpirationPutWithTimeouts.class, ExpirationService.class,
        ExpirationWithDefaultTimeout.class, ExpirationWithExplicitTimeout.class,
        RemoveAllAndFiltered.class })
public class ExpirationSuite {

}
