/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.test.TestUtil;
import org.junit.Test;

/**
 * Tests all load* methods of {@link CacheLoadingService}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExplicitLoading extends AbstractLoadingTestBundle {

    private final static AttributeMap ATR_FOO;

    private final static AttributeMap ATR_FO1;

    static {
        ATR_FOO = new Attributes.DefaultAttributeMap();
        ATR_FOO.put(TestUtil.dummy(Attribute.class), "boo");
        ATR_FO1 = new Attributes.DefaultAttributeMap();
        ATR_FO1.put(TestUtil.dummy(Attribute.class), "oob");
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE() {
        loading().load(null);
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE1() {
        loading().load(null, Attributes.EMPTY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE2() {
        loading().load(1, null);
    }

    @Test
    public void load() {
        loading().load(M1.getKey());
        awaitAllLoads();
        assertNotNull(loader.getLatestAttributeMap());
        assertEquals(1, loader.getNumberOfLoads());
        assertPeek(M1);
    }

    @Test
    public void loadWithAttributes() {
        loading().load(M1.getKey(), ATR_FOO);
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        assertNotNull(loader.getLatestAttributeMap());
        assertNotSame(loader.getLatestAttributeMap(), ATR_FOO);
        assertEquals(loader.getLatestAttributeMap(), ATR_FOO);
        assertPeek(M1);
    }

    @Test
    public void loadAndGet() {
        loading().load(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        assertGet(M1);// value is kept
        assertEquals(1, loader.getNumberOfLoads());
    }

    @Test
    public void loadTwice() {
        loading().load(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        loading().load(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
    }

    @Test
    public void loadWithAttributesTwice() {
        loading().load(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        loading().load(M1.getKey(), ATR_FO1);
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
    }

    @Test
    public void loadAll() {
        loading().loadAll(Arrays.asList(M1.getKey(), M2.getKey()));
        awaitAllLoads();
        assertEquals(2, loader.getNumberOfLoads());
        assertGet(M1);// value is kept
        assertGet(M2);// value is kept
        assertEquals(2, loader.getNumberOfLoads());
    }

    @Test
    public void loadAllTwice() {
        loading().load(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());
        loading().loadAll(Arrays.asList(M1.getKey(), M2.getKey()));
        awaitAllLoads();
        assertEquals(2, loader.getNumberOfLoads());
        loading().loadAll(Arrays.asList(M1.getKey(), M2.getKey()));
        awaitAllLoads();
        assertEquals(2, loader.getNumberOfLoads());
    }

    @Test
    public void loadAllWithAttributes() {
        AttributeMap am1 = new Attributes.DefaultAttributeMap();
        am1.put(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a1");
        AttributeMap am2 = new Attributes.DefaultAttributeMap();
        am2.put(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a2");

        Map<Integer, AttributeMap> req = new HashMap<Integer, AttributeMap>();
        req.put(1, am1);
        req.put(2, am2);
        loading().loadAll(req);
        awaitAllLoads();
        assertEquals(2, loader.getNumberOfLoads());
        assertEquals("a1", get(1));
        assertEquals("a2", get(2));
        assertEquals(2, loader.getNumberOfLoads());
    }

    @Test
    public void loadAllWithAttributesTwice() {
        loading().load(M1.getKey());
        awaitAllLoads();
        assertEquals(1, loader.getNumberOfLoads());

        AttributeMap am1 = new Attributes.DefaultAttributeMap();
        am1.put(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a1");
        AttributeMap am2 = new Attributes.DefaultAttributeMap();
        am2.put(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a2");

        Map<Integer, AttributeMap> req = new HashMap<Integer, AttributeMap>();
        req.put(M1.getKey(), am1);
        req.put(2, am2);
        loading().loadAll(req);
        awaitAllLoads();
        assertEquals(2, loader.getNumberOfLoads());
        assertGet(M1);
        assertEquals("a2", get(2));
        assertEquals(2, loader.getNumberOfLoads());
        loading().loadAll(req);
        assertEquals(2, loader.getNumberOfLoads());
    }

    @Test(expected = NullPointerException.class)
    public void testLoadAllNPE() {
        loading().loadAll((Map) null);
    }

    @Test(expected = NullPointerException.class)
    public void testLoadAllNPE1() {
        loading().loadAll((Collection) null);
    }
    
    @Test(expected = NullPointerException.class)
    public void loadAllWithAttributesNPE() {
        loading().loadAll((AttributeMap) null);
    }

    @Test(expected = NullPointerException.class)
    public void loadAllCollectionNPE1() {
        loading().loadAll(Arrays.asList(1, null));
    }
}
