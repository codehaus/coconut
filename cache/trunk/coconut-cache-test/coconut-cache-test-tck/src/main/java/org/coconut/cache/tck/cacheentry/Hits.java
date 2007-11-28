/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.HitsAttribute;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Test;

public class Hits extends AbstractCacheTCKTest {
    static class MyLoader extends AbstractCacheLoader<Integer, String> {
        public String load(Integer key, AttributeMap attributes) throws Exception {
            HitsAttribute.INSTANCE.setAtttribute(attributes, key + 1);
            return "" + (char) (key + 64);
        }
    }

    @Test
    public void get() {
        c = newCache(2);
        assertEquals(0l, peekEntry(M1).getHits());
        assertEquals(0l, peekEntry(M2).getHits());

        get(M1);
        assertEquals(1l, peekEntry(M1).getHits());
        assertEquals(0l, peekEntry(M2).getHits());

        get(M1);
        getAll(M1, M2);
        assertEquals(3l, peekEntry(M1).getHits());
        assertEquals(1l, peekEntry(M2).getHits());
    }

    @Test
    public void getEntry() {
        c = newCache(2);

        getEntry(M1);
        assertEquals(1l, peekEntry(M1).getHits());
        assertEquals(0l, peekEntry(M2).getHits());

        getEntry(M1);
        getEntry(M2);
        getEntry(M1);
        assertEquals(3l, peekEntry(M1).getHits());
        assertEquals(1l, peekEntry(M2).getHits());
    }

    @Test
    public void loadedNoAttributes() {
        c = newCache(newConf().loading().setLoader(new IntegerToStringLoader()));

        loadAndAwait(M1);
        assertEquals(0l, peekEntry(M1).getHits());
        get(M1);
        assertEquals(1l, peekEntry(M1).getHits());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadedAttribute() {
        c = newCache(newConf().loading().setLoader(new MyLoader()));
        loadAndAwait(M1);
        assertEquals(2l, peekEntry(M1).getHits());
        get(M2);
        assertEquals(3l, peekEntry(M2).getHits());

        get(M1);
        getAll(M1, M2);
        assertEquals(4l, peekEntry(M1).getHits());
        assertEquals(4l, peekEntry(M2).getHits());
    }
}
