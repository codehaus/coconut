package org.coconut.cache.tck.service.eviction;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.core.AttributeMap;
import org.junit.Test;

public class EvictionTrimming extends AbstractCacheTCKTest {

    /**
     * Tests trimToSize.
     */
    @Test
    public void trimToSize() {
        setCache(newConf().eviction().setPolicy(Policies.newLRU()));
        put(5);
        assertSize(5);
        eviction().trimToSize(6);
        assertSize(5);

        eviction().trimToSize(5);
        assertSize(5);
        eviction().trimToSize(3);
        assertSize(3);
        assertFalse(c.containsKey(1));
        assertFalse(c.containsKey(2));
        assertTrue(c.containsKey(3));
        c.get(3);
        eviction().trimToSize(1);
        assertSize(1);
        assertTrue(c.containsKey(3));
    }

    /**
     * Tests trimToSize.
     */
    @Test
    public void trimToSize2() {
        c = newCache();
        put(5);
        assertSize(5);
        eviction().trimToSize(3);
        assertSize(3);
        put(10, 15);
        assertSize(9);
        eviction().trimToSize(1);
        assertSize(1);
    }

    /**
     * Tests trimToSize IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void trimToSizeIAE() {
        c = newCache();
        eviction().trimToSize(-1);
    }

    @Test
    public void trimToVolume() {
        MyLoader m = new MyLoader();
        setCache(newConf().eviction().setPolicy(Policies.newLRU()).c().loading().setLoader(m));
        c.get(1);
        assertEquals(2, c.getVolume());
        c.get(2);
        assertEquals(6, c.getVolume());
        c.get(4);
        assertEquals(13, c.getVolume());
        c.get(5);
        c.get(6);
        assertEquals(33, c.getVolume());

        eviction().trimToVolume(34);
        assertEquals(33, c.getVolume());

        eviction().trimToVolume(33);
        assertEquals(33, c.getVolume());

        eviction().trimToVolume(32);
        assertFalse(c.containsKey(1));
        assertEquals(31, c.getVolume());

        eviction().trimToVolume(22);
        assertEquals(20, c.getVolume());

        eviction().trimToVolume(1);
        assertEquals(0, c.getVolume());
    }

    /**
     * Tests trimToVolume IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void trimToVolumeIAE() {
        c = newCache();
        eviction().trimToVolume(-1);
    }

    static class MyLoader implements CacheLoader<Integer, String> {
        private int totalCount;

        public String load(Integer key, AttributeMap attributes) throws Exception {
            CacheAttributes.setSize(attributes, key + 1 + totalCount);
            totalCount++;
            return "" + (char) (key + 64);
        }
    }
}
