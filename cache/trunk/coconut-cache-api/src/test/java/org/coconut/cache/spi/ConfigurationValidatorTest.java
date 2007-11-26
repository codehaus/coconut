/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.DummyCache;
import org.coconut.cache.service.management.CacheManagementService;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ConfigurationValidator} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheServicesTest.java 472 2007-11-19 09:34:26Z kasper $
 */
public class ConfigurationValidatorTest {
    ConfigurationValidator cv = ConfigurationValidator.getInstance();

    CacheConfiguration conf;

    @Before
    public void setup() {
        assertNotNull(cv);
        conf = CacheConfiguration.create();
    }

    @Test
    public void genericSignature() {
        assertTrue(cv.tryVerify(conf, OK.class));
        cv.verify(conf, OK.class);// does not fail
    }

    @Test
    public void ok() {
        verifyOK(OK.class);
    }

    void verifyOK(Class<? extends Cache> cacheType) {
        assertTrue(cv.tryVerify(conf, cacheType));
        cv.verify(conf, cacheType);// does not fail

    }

    void verifyFail(Class<? extends Cache> cacheType) {
        assertFalse(cv.tryVerify(conf, cacheType));
        try {
            cv.verify(conf, cacheType);
            throw new AssertionError("Should fail with noCacheServiceSupport");
        } catch (IllegalCacheConfigurationException ok) {/* ok */}
    }

    @Test
    public void noCacheServiceSupport() {
        verifyFail(NoCacheServiceSupport.class);
    }

    @Test
    public void noManagementSupportSupport() {
        verifyOK(NoManagementServiceSupport.class);
        conf.management().setEnabled(true);
        verifyFail(NoManagementServiceSupport.class);
        verifyOK(ManagementServiceSupport.class);
    }

    static class NoCacheServiceSupport extends DummyCache {}

    @CacheServiceSupport( {})
    static class NoManagementServiceSupport extends DummyCache {}

    @CacheServiceSupport( { CacheManagementService.class })
    static class ManagementServiceSupport extends DummyCache {}

    @CacheServiceSupport( { CacheManagementService.class })
    static class OK extends DummyCache {}
}
