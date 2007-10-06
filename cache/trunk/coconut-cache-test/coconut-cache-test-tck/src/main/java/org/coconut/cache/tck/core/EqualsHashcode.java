package org.coconut.cache.tck.core;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class EqualsHashcode extends AbstractCacheTCKTest {

    @Test
    public void equals() {
        Cache<Integer, String> c3 = newCache(3);
        Cache<Integer, String> c4 = newCache(4);
        assertTrue(c4.equals(c4));
        assertTrue(c3.equals(c3));
        assertFalse(c3.equals(c4));
        assertFalse(c4.equals(c3));
    }

}
