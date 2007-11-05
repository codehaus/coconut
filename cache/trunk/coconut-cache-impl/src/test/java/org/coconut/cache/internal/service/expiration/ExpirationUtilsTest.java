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
