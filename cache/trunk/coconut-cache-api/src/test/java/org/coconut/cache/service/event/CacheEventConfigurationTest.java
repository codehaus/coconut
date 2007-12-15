/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import java.lang.reflect.Field;
import java.util.Set;

import org.coconut.cache.spi.IllegalCacheConfigurationException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheEventConfigurationTest {

    static CacheEventConfiguration DEFAULT = new CacheEventConfiguration();

    CacheEventConfiguration conf;

    @Before
    public void setUp() {
        conf = new CacheEventConfiguration();
    }

    @Test
    public void testEnabled() throws Exception {
        assertFalse(conf.isEnabled());
        conf = reloadService(conf);
        assertFalse(conf.isEnabled());
        assertSame(conf, conf.setEnabled(true));
        assertTrue(conf.isEnabled());
        conf = reloadService(conf);
        assertTrue(conf.isEnabled());
    }
}
