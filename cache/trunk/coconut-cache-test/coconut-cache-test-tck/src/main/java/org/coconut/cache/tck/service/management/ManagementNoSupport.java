/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.management;

import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.junit.Test;

@RequireService(value = { CacheManagementService.class }, isAvailable = false)
public class ManagementNoSupport extends AbstractCacheTCKTest {
    @Test(expected = IllegalArgumentException.class)
    public void noManagementSupport() {
        newCache(newConf().management().setEnabled(true));
    }
}
