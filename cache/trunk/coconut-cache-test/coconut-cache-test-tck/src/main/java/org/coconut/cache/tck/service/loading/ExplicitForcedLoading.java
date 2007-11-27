/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import static org.coconut.attribute.AttributeMaps.from;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.coconut.attribute.spi.AbstractAttribute;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.test.MockTestCase;
import org.junit.Test;

/**
 * Tests all forcedLoad* methods of {@link CacheLoadingService}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ExplicitForcedLoading extends AbstractLoadingTestBundle {

    private final static AttributeMap ATR_FOO;

    private final static AttributeMap ATR_FO1;

    private final static Attribute FOO = MockTestCase.mockDummy(Attribute.class);

    private final static Attribute OOF = MockTestCase.mockDummy(Attribute.class);
    static {
        ATR_FOO = new AttributeMaps.DefaultAttributeMap();
        ATR_FOO.put(FOO, "boo");
        ATR_FO1 = new AttributeMaps.DefaultAttributeMap();
        ATR_FO1.put(OOF, "oob");
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE() {
        loading().forceLoad(null);
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE1() {
        loading().forceLoad(null, AttributeMaps.EMPTY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE2() {
        loading().forceLoad(1, null);
    }

    @Test
    public void load() {
        loading().forceLoad(M1.getKey());
        awaitAllLoads();
        assertNotNull(loader.getLatestAttributeMap());
        assertEquals(1, loader.getNumberOfLoads());
        assertPeek(M1);
    }

    @Test
    public void loadWithAttributes() {
        loading().forceLoad(M1.getKey(), ATR_FOO);
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        assertNotNull(loader.getLatestAttributeMap());
        assertNotSame(loader.getLatestAttributeMap(), ATR_FOO);
        assertEquals(loader.getLatestAttributeMap(), ATR_FOO);
        assertPeek(M1);
    }

    @Test
    public void loadAndGet() {
        loading().forceLoad(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        assertGet(M1);// value is kept
        assertEquals(1, loader.getNumberOfLoads());
    }

    @Test
    public void loadTwice() {
        loading().forceLoad(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        loading().forceLoad(M1.getKey());
        awaitAllLoads();
        assertEquals(2, loader.getNumberOfLoads());
    }

    @Test
    public void loadWithAttributesTwice() {
        loading().forceLoad(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        loading().forceLoad(M1.getKey(), ATR_FO1);
        awaitAllLoads();
        assertEquals(2, loader.getNumberOfLoads());
    }

    @Test
    public void loadAll() {
        loading().forceLoadAll(Arrays.asList(M1.getKey(), M2.getKey()));
        awaitAllLoads();
        assertEquals(2, loader.getNumberOfLoads());
        assertGet(M1);// value is kept
        assertGet(M2);// value is kept
        assertEquals(2, loader.getNumberOfLoads());
    }

    @Test
    public void loadAllTwice() {
        loading().forceLoad(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        loading().forceLoadAll(Arrays.asList(M1.getKey(), M2.getKey()));
        awaitAllLoads();
        assertEquals(3, loader.getNumberOfLoads());
        loading().forceLoadAll(Arrays.asList(M1.getKey(), M2.getKey()));
        awaitAllLoads();
        assertEquals(5, loader.getNumberOfLoads());
    }

    @Test
    public void loadAllWithAttributes() {
        AttributeMap am1 = new AttributeMaps.DefaultAttributeMap();
        am1.put(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a1");
        AttributeMap am2 = new AttributeMaps.DefaultAttributeMap();
        am2.put(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a2");

        Map<Integer, AttributeMap> req = new HashMap<Integer, AttributeMap>();
        req.put(1, am1);
        req.put(2, am2);
        loading().forceLoadAll(req);
        awaitAllLoads();
        assertEquals(2, loader.getNumberOfLoads());
        assertEquals("a1", get(1));
        assertEquals("a2", get(2));
        assertEquals(2, loader.getNumberOfLoads());
    }

    @Test
    public void loadAllWithAttributesTwice() {
        loading().forceLoad(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());

        Map<Integer, AttributeMap> req = new HashMap<Integer, AttributeMap>();
        req.put(M1.getKey(), from(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a1"));
        req.put(2, from(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a2"));
        loading().forceLoadAll(req);
        awaitAllLoads();
        assertEquals(3, loader.getNumberOfLoads());
        assertEquals("a1", get(1));
        assertEquals("a2", get(2));
        assertEquals(3, loader.getNumberOfLoads());
        loading().forceLoadAll(req);
        awaitAllLoads();
        assertEquals(5, loader.getNumberOfLoads());
    }

    @Test(expected = NullPointerException.class)
    public void testLoadAllNPE() {
        loading().forceLoadAll((Map) null);
    }

    @Test(expected = NullPointerException.class)
    public void testLoadAllNPE1() {
        loading().forceLoadAll((Collection) null);
    }

    @Test
    public void forceLoadAll() {
        Map<Integer, AttributeMap> req = new HashMap<Integer, AttributeMap>();
        req.put(M1.getKey(), from(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a1"));
        req.put(2, from(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a2"));
        req.put(3, from(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a3"));
        loading().loadAll(req);
        awaitAllLoads();
        assertEquals(3, loader.getNumberOfLoads());
        loading().forceLoadAll();
        awaitAllLoads();
        assertGet(M1, M2, M3);
        assertEquals(6, loader.getNumberOfLoads());
    }

    @Test
    public void forceLoadAllWithAttributes() {
        loading().forceLoadAll();
        awaitAllLoads();
        assertGet(M1, M2, M3);
        assertEquals(3, loader.getNumberOfLoads());
        loading().forceLoadAll(from(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a3"));
        awaitAllLoads();
        assertEquals("a3", get(M1.getKey()));
        assertEquals("a3", get(M2.getKey()));
        assertEquals("a3", get(M3.getKey()));
        assertEquals(6, loader.getNumberOfLoads());
    }

    @Test(expected = NullPointerException.class)
    public void forceLoadAllWithAttributesNPE() {
        loading().forceLoadAll((AttributeMap) null);
    }

    @Test(expected = NullPointerException.class)
    public void forceLoadAllCollectionNPE1() {
        loading().forceLoadAll(Arrays.asList(1, null));
    }

}
