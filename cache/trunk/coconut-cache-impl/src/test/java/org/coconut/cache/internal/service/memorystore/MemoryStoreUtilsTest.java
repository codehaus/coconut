/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.memorystore;

import org.coconut.cache.CacheConfiguration;
import org.junit.Test;

/**
 * Tests the {@link CacheConfiguration} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheConfigurationTest.java 434 2007-11-13 15:36:17Z kasper $
 */
@SuppressWarnings("unchecked")
public class MemoryStoreUtilsTest {

    /**
     * Tests that
     * {@link EvictionUtils#wrapMXBean(org.coconut.cache.service.eviction.CacheEvictionService)}
     * throws a {@link NullPointerException} when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void wrapMXBeanNPE() {
        MemoryStoreUtils.wrapMXBean(null);
    }
    
    /**
     * Tests that
     * {@link EvictionUtils#wrapService(org.coconut.cache.service.eviction.CacheEvictionService)}
     * throws a {@link NullPointerException} when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void wrapServiceNPE() {
        MemoryStoreUtils.wrapService(null);
    }
}
