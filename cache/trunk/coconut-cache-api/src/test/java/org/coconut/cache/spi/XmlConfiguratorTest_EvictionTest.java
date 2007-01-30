/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static org.coconut.cache.spi.XmlConfiguratorTest.rw;

import org.coconut.cache.CacheConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfiguratorTest_EvictionTest {

    XmlConfigurator c;

    CacheConfiguration conf;

    @Before
    public void setup() {
        conf = CacheConfiguration.create();
        c = XmlConfigurator.getInstance();
    }

    protected CacheConfiguration.Eviction e() {
        return conf.eviction();
    }

    @Test
    public void testNoop() throws Exception {
        conf = rw(conf);
        assertEquals(Integer.MAX_VALUE, e().getMaximumSize());
        assertEquals(Integer.MAX_VALUE, e().getPreferableSize());
        assertEquals(Long.MAX_VALUE, e().getMaximumCapacity());
        assertEquals(Long.MAX_VALUE, e().getPreferableCapacity());
    }

    @Test
    public void testEviction() throws Exception {
        e().setMaximumSize(1);
        e().setPreferableSize(2);
        e().setMaximumCapacity(3);
        e().setPreferableCapacity(4);
        conf = rw(conf);
        assertEquals(1, e().getMaximumSize());
        assertEquals(2, e().getPreferableSize());
        assertEquals(3, e().getMaximumCapacity());
        assertEquals(4, e().getPreferableCapacity());
    }

    @Test
    public void testCornerCase() throws Exception {
        // coverage mostly
        e().setMaximumSize(2);
        conf = rw(conf);
        assertEquals(2, e().getMaximumSize());
        assertEquals(Integer.MAX_VALUE, e().getPreferableSize());
        assertEquals(Long.MAX_VALUE, e().getMaximumCapacity());
        assertEquals(Long.MAX_VALUE, e().getPreferableCapacity());
        
        conf = CacheConfiguration.create();
        e().setPreferableSize(3);
        conf = rw(conf);
        assertEquals(3, e().getPreferableSize());
    }

   
}
