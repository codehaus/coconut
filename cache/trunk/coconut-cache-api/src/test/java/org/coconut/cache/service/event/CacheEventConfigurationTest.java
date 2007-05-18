/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Set;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.IllegalCacheConfigurationException;
import org.coconut.cache.spi.XmlConfigurator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEventConfigurationTest {

    static CacheEventConfiguration DEFAULT = new CacheEventConfiguration();

    CacheEventConfiguration conf;

    @Before
    public void setUp() {
        conf = new CacheEventConfiguration();
    }

    @Test
    public void testInitialIncludeExclude() throws Exception {
        assertTrue(conf.isIncluded(CacheEntryEvent.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemAdded.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemRemoved.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertTrue(conf.isIncluded(CacheEvent.class));
        assertTrue(conf.isIncluded(CacheEvent.CacheCleared.class));
        assertTrue(conf.isIncluded(CacheEvent.CacheEvicted.class));
      //  assertTrue(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
        conf = rw(conf);
        assertTrue(conf.isIncluded(CacheEntryEvent.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemAdded.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemRemoved.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertTrue(conf.isIncluded(CacheEvent.class));
        assertTrue(conf.isIncluded(CacheEvent.CacheCleared.class));
        assertTrue(conf.isIncluded(CacheEvent.CacheEvicted.class));
    //    assertTrue(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
    }

    @Test
    public void testIncludeExclude1() throws Exception {
        conf.exclude(CacheEvent.class);
        assertFalse(conf.isIncluded(CacheEntryEvent.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertFalse(conf.isIncluded(CacheEvent.class));
    //    assertFalse(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
        conf = rw(conf);
        assertFalse(conf.isIncluded(CacheEntryEvent.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertFalse(conf.isIncluded(CacheEvent.class));
  //      assertFalse(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
    }

    @Test
    public void testIncludeExclude2() throws Exception {
        conf.exclude(CacheEntryEvent.class);
        assertFalse(conf.isIncluded(CacheEntryEvent.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertTrue(conf.isIncluded(CacheEvent.class));
    //    assertTrue(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
        conf = rw(conf);
        assertFalse(conf.isIncluded(CacheEntryEvent.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertTrue(conf.isIncluded(CacheEvent.class));
  //      assertTrue(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
    }

    @Test
    public void testIncludeExclude3() throws Exception {
        conf.exclude(CacheEvent.class);
        conf.include(CacheEntryEvent.class);
        assertTrue(conf.isIncluded(CacheEntryEvent.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertFalse(conf.isIncluded(CacheEvent.class));
   //     assertFalse(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
        conf = rw(conf);
        assertTrue(conf.isIncluded(CacheEntryEvent.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertFalse(conf.isIncluded(CacheEvent.class));
   //     assertFalse(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
    }

    @Test
    public void testIncludeExclude4() throws Exception {
        conf.exclude(CacheEntryEvent.class);
        conf.include(CacheEntryEvent.ItemAccessed.class);

        assertFalse(conf.isIncluded(CacheEntryEvent.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertTrue(conf.isIncluded(CacheEvent.class));
   //     assertTrue(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
        conf = rw(conf);
        assertFalse(conf.isIncluded(CacheEntryEvent.class));
        assertTrue(conf.isIncluded(CacheEntryEvent.ItemAccessed.class));
        assertFalse(conf.isIncluded(CacheEntryEvent.ItemUpdated.class));

        assertTrue(conf.isIncluded(CacheEvent.class));
 //       assertTrue(conf.isIncluded(CacheEvent.CacheStatisticsReset.class));
    }

    @Test(expected = IllegalCacheConfigurationException.class)
    public void testInvalidXML() throws Exception {
        conf = rw(conf);
        Field f = CacheEventConfiguration.class.getDeclaredField("includes");
        f.setAccessible(true);
        ((Set) f.get(conf)).add(Object.class);
        f.setAccessible(false);

        conf = rw(conf);
    }

    @Test(expected = IllegalCacheConfigurationException.class)
    public void testInvalidXML1() throws Exception {
        conf = rw(conf);
        Field f = CacheEventConfiguration.class.getDeclaredField("excludes");
        f.setAccessible(true);
        ((Set) f.get(conf)).add(Object.class);
        f.setAccessible(false);

        conf = rw(conf);
    }

    @Test(expected = NullPointerException.class)
    public void isIncludedNPE() {
        conf.isIncluded(null);
    }

    @Test(expected = NullPointerException.class)
    public void includeNPE() {
        conf.include(null);
    }

    @Test(expected = NullPointerException.class)
    public void includeNPE1() {
        conf.include(CacheEvent.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void includeIAE() {
        conf.include(CacheEvent.class, Object.class);
    }

    @Test(expected = NullPointerException.class)
    public void excludeNPE() {
        conf.exclude(CacheEvent.class, null);
    }

    @Test(expected = NullPointerException.class)
    public void excludeNPE1() {
        conf.exclude(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void excludeIAE() {
        conf.exclude(CacheEvent.class, Object.class);
    }

    static CacheEventConfiguration rw(CacheEventConfiguration conf) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CacheConfiguration cc = CacheConfiguration.create();
        cc.addConfiguration(conf);
        XmlConfigurator.getInstance().to(cc, os);
        cc = XmlConfigurator.getInstance().from(
                new ByteArrayInputStream(os.toByteArray()));
        return (CacheEventConfiguration) cc
                .getConfiguration(CacheEventConfiguration.class);
    }
}
