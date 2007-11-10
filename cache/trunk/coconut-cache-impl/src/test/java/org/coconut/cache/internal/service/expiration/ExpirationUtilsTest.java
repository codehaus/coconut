/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import org.junit.Test;

public class ExpirationUtilsTest {

    @Test(expected = NullPointerException.class)
    public void wrapServiceNPE() {
        ExpirationUtils.wrapService(null);
    }

    @Test
    public void wrapService() {
    // TODO create
    }

    @Test(expected = NullPointerException.class)
    public void wrapAsMXBeanNPE() {
        ExpirationUtils.wrapAsMXBean(null);
    }
    
    @Test
    public void wrapAsMXBean() {
    // TODO create
    }
}
