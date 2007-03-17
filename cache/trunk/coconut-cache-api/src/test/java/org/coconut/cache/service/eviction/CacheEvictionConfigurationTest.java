/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.spi.XmlConfigurator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEvictionConfigurationTest {
    CacheEvictionConfiguration<Number, Collection> ee;


    @Before
    public void setUp() {
        ee = new CacheEvictionConfiguration();
    }

    static CacheEvictionConfiguration rw(CacheEvictionConfiguration conf) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CacheConfiguration cc = CacheConfiguration.create();
        cc.addService(conf);
        XmlConfigurator.getInstance().to(cc, os);
        cc = XmlConfigurator.getInstance().from(
                new ByteArrayInputStream(os.toByteArray()));
        return (CacheEvictionConfiguration) cc
                .getServiceConfiguration(CacheEvictionConfiguration.class);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMaximumCapacity() {
        assertEquals(Long.MAX_VALUE, ee.getMaximumCapacity());
        assertEquals(ee, ee.setMaximumCapacity(4));
        assertEquals(4, ee.getMaximumCapacity());
        ee.setMaximumCapacity(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumSize() {
        assertEquals(Integer.MAX_VALUE, ee.getMaximumSize());
        assertEquals(ee, ee.setMaximumSize(4));
        assertEquals(4, ee.getMaximumSize());
        ee.setMaximumSize(-1);
    }

    @Test
    public void testPolicy() {
        assertNull(ee.getPolicy());
        ReplacementPolicy p = Policies.newClock();
        assertEquals(ee, ee.setPolicy(p));
        assertEquals(p, ee.getPolicy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPreferableCapacity() {
        assertEquals(Long.MAX_VALUE, ee.getPreferableCapacity());
        assertEquals(ee, ee.setPreferableCapacity(4));
        assertEquals(4, ee.getPreferableCapacity());
        ee.setPreferableCapacity(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPreferableSize() {
        assertEquals(Integer.MAX_VALUE, ee.getPreferableSize());
        assertEquals(ee, ee.setPreferableSize(4));
        assertEquals(4, ee.getPreferableSize());
        ee.setPreferableSize(-1);
    }
    

    @Test
    public void testNoop() throws Exception {
        ee = rw(ee);
        assertEquals(Integer.MAX_VALUE, ee.getMaximumSize());
        assertEquals(Integer.MAX_VALUE, ee.getPreferableSize());
        assertEquals(Long.MAX_VALUE, ee.getMaximumCapacity());
        assertEquals(Long.MAX_VALUE, ee.getPreferableCapacity());
    }

    @Test
    public void testEviction() throws Exception {
        ee.setMaximumSize(1);
        ee.setPreferableSize(2);
        ee.setMaximumCapacity(3);
        ee.setPreferableCapacity(4);
        ee = rw(ee);
        assertEquals(1, ee.getMaximumSize());
        assertEquals(2, ee.getPreferableSize());
        assertEquals(3, ee.getMaximumCapacity());
        assertEquals(4, ee.getPreferableCapacity());
    }

    @Test
    public void testCornerCase() throws Exception {
        // coverage mostly
        ee.setMaximumSize(2);
        ee = rw(ee);
        assertEquals(2, ee.getMaximumSize());
        assertEquals(Integer.MAX_VALUE, ee.getPreferableSize());
        assertEquals(Long.MAX_VALUE, ee.getMaximumCapacity());
        assertEquals(Long.MAX_VALUE, ee.getPreferableCapacity());
//        
//        ee = CacheConfiguration.create();
//        ee.setPreferableSize(3);
//        ee = rw(ee);
//        assertEquals(3, ee.getPreferableSize());
    }
}
