/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.DefaultAttributeMap;
import org.coconut.attribute.common.TimeToRefreshAttribute;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.test.service.loading.TestLoader;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.operations.Ops.Predicate;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class LoadingRefresh extends AbstractLoadingTestBundle {

    static class MyLoader extends IntegerToStringLoader {
        @Override
        public String load(Integer key, AttributeMap attributes) throws Exception {
            TimeToRefreshAttribute.INSTANCE.setAttribute(attributes, key, TimeUnit.MILLISECONDS);
            return super.load(key, attributes);
        }
    }

    protected void loadThoseNeedsRefresh() {
        loading().loadAll();
    }

    /**
     * Test refresh window
     */
    @SuppressWarnings("unchecked")
    public void defaultRefreshTime() throws Exception {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        init(conf.setClock(clock).loading().setDefaultTimeToRefresh(2, TimeUnit.MILLISECONDS)
                .setLoader(loader).c());
        assertGet(M1);
        assertGet(M2);
        incTime(); // 1
        assertGet(M3);
        loadThoseNeedsRefresh();
        assertEquals(3, loader.getNumberOfLoads());

        incTime(); // 2
        assertGet(M4);
        assertGet(M5);
        assertEquals(5, loader.getNumberOfLoads());

        loadThoseNeedsRefresh();// refresh, M1,M2
        assertEquals(7, loader.getNumberOfLoads());

        incTime(); // 3
        loadThoseNeedsRefresh();// refresh M3
        assertEquals(8, loader.getNumberOfLoads());

        incTime(); // 4
        loadThoseNeedsRefresh();// refresh, M1,M2,M4,M5
        assertEquals(12, loader.getNumberOfLoads());
    }

    /**
     * Checks setting refresh value while loading.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void explicitRefreshTimeCacheLoader() throws Exception {
        MyLoader loader = new MyLoader();
        c = newCache(newConf().setClock(clock).loading().setLoader(loader).c());
        assertGet(M1); // load at time=1
        assertGet(M2);// load at time=2
        incTime(); // 1
        assertGet(M3); // load at time=4
        loadThoseNeedsRefresh();
        assertEquals(4, loader.getNumberOfLoads());

        incTime(); // 2
        assertGet(M4);
        assertGet(M5);
        assertEquals(6, loader.getNumberOfLoads());

        loadThoseNeedsRefresh();// refresh, M1,M2
        assertEquals(8, loader.getNumberOfLoads());

        incTime(); // 3
        loadThoseNeedsRefresh();// refresh M1
        assertEquals(9, loader.getNumberOfLoads());

        incTime(); // 4
        loadThoseNeedsRefresh();// refresh, M1,M2,M4
        assertEquals(12, loader.getNumberOfLoads());
        incTime(10);
        loadThoseNeedsRefresh();// All
        assertEquals(17, loader.getNumberOfLoads());

    }

    /**
     * Test refresh window
     */
    @SuppressWarnings("unchecked")
    @Test
    public void refreshWindowSingleElementEvict() throws Exception {
        TestLoader tl = new TestLoader();
        // tl.add(M1.getKey(), value, attributes)
        init(conf.setClock(clock).loading().setDefaultTimeToRefresh(2, TimeUnit.MILLISECONDS)
                .setLoader(tl));
        tl.add(M1.getKey(), "AB1", TimeToRefreshAttribute.singleton(2, TimeUnit.MILLISECONDS));
        tl.add(M2.getKey(), "AB2", TimeToRefreshAttribute.singleton(3, TimeUnit.MILLISECONDS));
        tl.add(M3.getKey(), "AB3", TimeToRefreshAttribute.singleton(4, TimeUnit.MILLISECONDS));
        tl.add(M4.getKey(), "AB4", TimeToRefreshAttribute.singleton(7, TimeUnit.MILLISECONDS));

        getAll(M1, M2, M3, M4);
        assertSize(4);
        assertEquals(4, tl.totalLoads());

        loading().loadAll();
        awaitAllLoads();
        assertEquals(4, tl.totalLoads());
        tl.clearAndFromBase(4, 0);

        incTime(); // time = 1
        loading().loadAll();
        awaitAllLoads();
        assertEquals(0, tl.totalLoads());

        incTime(); // time = 2
        loading().loadAll();
        awaitAllLoads();
        assertEquals(1, tl.totalLoads());
        assertPeek(M1);

        incTime(); // time = 3
        loading().loadAll();
        awaitAllLoads();
        assertEquals(2, tl.totalLoads());
        assertPeek(M1, M2);

        incTime(); // time = 4
        loading().loadAll();
        awaitAllLoads();
        assertEquals(4, tl.totalLoads());
        assertPeek(M1, M2, M3);

        incTime(); // time = 5
        loading().loadAll();
        awaitAllLoads();
        assertEquals(5, tl.totalLoads());
        assertPeek(M1, M2, M3);

        incTime(); // time = 6
        loading().loadAll();
        awaitAllLoads();
        assertEquals(7, tl.totalLoads());
        assertPeek(M1, M2, M3);

        incTime(); // time = 6
        loading().loadAll();
        awaitAllLoads();
        assertEquals(9, tl.totalLoads());
        assertPeek(M1, M2, M3, M4);
    }

    @SuppressWarnings("unchecked")
    public void refreshWindowSingleElementGet() throws Exception {
        AsyncIntegerToStringLoader loader = new AsyncIntegerToStringLoader();
        c = newCache(newConf().setClock(clock).loading().setDefaultTimeToRefresh(2,
                TimeUnit.NANOSECONDS).setLoader(loader).c());
        expiration().put(M1.getKey(), "AB1", 2, TimeUnit.NANOSECONDS);
        expiration().put(M2.getKey(), "AB2", 3, TimeUnit.NANOSECONDS);
        expiration().put(M3.getKey(), "AB3", 4, TimeUnit.NANOSECONDS);
        expiration().put(M4.getKey(), "AB4", 7, TimeUnit.NANOSECONDS);

        incTime(); // time is one
        // test no refresh on get
        getAll(M1, M2, M3, M4);
        assertEquals(2, loader.getLoadedKeys().size());
        awaitAllLoads();
        assertGetAll(M1, M2);

        assertEquals("AB3", get(M3));
        assertEquals("AB4", get(M4));
    }

    /**
     * Tests load all.
     */
    @Test
    public void testLoadAll() {
        TestLoader loader = TestLoader.create(2);
        c = newCache(newConf().loading().setRefreshFilter(new RefreshFilter()).setLoader(loader));
        loading().loadAll();
        awaitAllLoads();
        assertEquals("A", c.get(1));
        assertEquals("B", c.get(2));
        assertEquals(2, loader.totalLoads());
        loader.clearAndFromBase(2, 2);
        loading().loadAll();
        awaitAllLoads();
        assertEquals("C", c.peek(1));
        assertEquals("B", c.peek(2));
        assertEquals(1, loader.totalLoads());
    }

    @Test
    public void loadAllWithAttributes() {
        AttributeMap am1 = new DefaultAttributeMap();
        am1.put(IntegerToStringLoader.RESULT_ATTRIBUTE_KEY, "a1");
        AttributeMap am2 = new DefaultAttributeMap();
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

    static class RefreshFilter implements Predicate<CacheEntry<Integer, String>> {
        public boolean evaluate(CacheEntry<Integer, String> element) {
            return element.getKey().equals(1);
        }
    }

    public static class AsyncIntegerToStringLoader extends IntegerToStringLoader implements
            CacheLoader<Integer, String> {

        private final List<Integer> keysLoader = Collections
                .synchronizedList(new LinkedList<Integer>());

        public List<Integer> getLoadedKeys() {
            return new ArrayList<Integer>(keysLoader);
        }
    }
}
