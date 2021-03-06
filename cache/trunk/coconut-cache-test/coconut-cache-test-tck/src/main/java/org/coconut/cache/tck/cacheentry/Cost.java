/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M6;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.CostAttribute;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Test;

/**
 * Tests {@link CacheEntry#getCost()}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Cost extends AbstractCacheTCKTest {

    /**
     * A loader that sets the cost attribute
     */
    static class MyLoader extends AbstractCacheLoader<Integer, String> {
        private int totalCount;

        public String load(Integer key, AttributeMap attributes) throws Exception {
            CostAttribute.setCost(attributes, key + 0.5 + totalCount);
            totalCount++;
            return "" + (char) (key + 64);
        }
    }

    /**
     * Tests default cost of 1 for new entries.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void put() {
        put(M1);
        assertCostEquals(M1, CostAttribute.DEFAULT_VALUE);
        putAll(M1, M2);
        assertCostEquals(M1, CostAttribute.DEFAULT_VALUE);
        assertCostEquals(M2, CostAttribute.DEFAULT_VALUE);
    }

    /**
     * Tests that loaded valus has a default cost of 1.
     */
    @Test
    public void loaded() {
        init(conf.loading().setLoader(new IntegerToStringLoader()));
        assertGet(M1);
        assertCostEquals(M1, CostAttribute.DEFAULT_VALUE);
    }

    /**
     * Test a cache loader where entries has a specific cost.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void loadedCost() {
        init(conf.loading().setLoader(new MyLoader()));
        assertGet(M1);
        assertCostEquals(M1, 1.5);

        assertGet(M3);
        assertCostEquals(M3, 4.5);

        assertGet(M6);
        assertCostEquals(M6, 8.5);

        c.clear();
        assertGet(M1);
        assertCostEquals(M1, 4.5);

        // tests that a loaded value will override the cost
        loading().forceLoad(M1.getKey());
        awaitAllLoads();
        assertCostEquals(M1, 5.5);
    }

    /**
     * Tests that put overrides the cost of an existing item.
     */
    @Test
    public void putOverride() {
        init(conf.loading().setLoader(new MyLoader()));
        assertGet(M1);
        assertCostEquals(M1, 1.5);
        put(M1);
        assertCostEquals(M1, CostAttribute.DEFAULT_VALUE);
    }

    /**
     * Asserts that the entry has the specified cost.
     */
    private void assertCostEquals(Map.Entry<Integer, String> entry, double cost) {
        assertEquals(cost, peekEntry(entry).getCost());
        assertEquals(cost, getEntry(entry).getCost());

        assertEquals(cost, CostAttribute.getCost(peekAttributes(entry)));
        assertEquals(cost, peekAttributes(entry).getDouble(CostAttribute.INSTANCE));
        assertEquals(cost, peekAttributes(entry).getDouble(CostAttribute.INSTANCE, cost - 1));
        assertEquals(cost, peekAttributes(entry).get(CostAttribute.INSTANCE));
        assertEquals(cost, peekAttributes(entry).get(CostAttribute.INSTANCE, cost - 1));

        assertEquals(cost, CostAttribute.getCost(getAttributes(entry)));
        assertEquals(cost, getAttributes(entry).getDouble(CostAttribute.INSTANCE));
        assertEquals(cost, getAttributes(entry).getDouble(CostAttribute.INSTANCE, cost - 1));
        assertEquals(cost, getAttributes(entry).get(CostAttribute.INSTANCE));
        assertEquals(cost, getAttributes(entry).get(CostAttribute.INSTANCE, cost - 1));

    }
}
