/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheConfiguration_StatisticsTest {
    CacheConfiguration<Number, Collection> conf;
    CacheConfiguration.Statistics s;
    @Before
    public void setUp() {
        conf = CacheConfiguration.create();
        s=conf.statistics();
    }

    @Test
    public void testExpiration() {
        assertEquals(conf, conf.statistics().c());
    }
    
    @Test
    public void testEnabled() {
        assertTrue(s.getEnabled());
        assertEquals(s, s.setEnabled(false));
        assertFalse(s.getEnabled());
    }
}
