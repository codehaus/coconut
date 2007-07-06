package org.coconut.cache.service.loading;

import static org.junit.Assert.*;

import org.coconut.core.AttributeMaps;
import org.junit.Test;

public class CacheLoadersTest {

    @Test
    public void testNullLoader() throws Exception {
        CacheLoader<Integer, String> cl = CacheLoaders.nullLoader();
        assertNull(cl.load(1, AttributeMaps.EMPTY_MAP));
        assertNull(cl.load(3, null));
    }

    @Test
    public void testCacheAsCacheLoader() throws Exception {
        CacheLoader<Integer, String> cl = CacheLoaders.nullLoader();
        assertNull(cl.load(1, AttributeMaps.EMPTY_MAP));
        assertNull(cl.load(3, null));
    }
}
