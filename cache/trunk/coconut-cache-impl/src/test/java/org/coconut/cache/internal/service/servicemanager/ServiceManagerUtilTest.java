/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import org.junit.Test;

public class ServiceManagerUtilTest {

    @Test(expected = NullPointerException.class)
    public void wrapServiceNPE() {
        ServiceManagerUtil.wrapService(null);
    }
}
