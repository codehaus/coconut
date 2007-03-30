/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.loading;

import static org.coconut.test.CollectionUtils.asList;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.junit.Test;

/**
 * Test AbstractCacheLoader.
 * <p>
 * This test is Called AbstraktCacheLoaderTest instead of
 * AbstractCacheLoaderTest because Abstract*.java tests by default are not run
 * by maven.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class AbstraktCacheLoaderTest {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AbstraktCacheLoaderTest.class);
    }

    @Test
    public void testLoadAll() throws Exception {
        AbstractCacheLoader<Integer, Integer> loader = new LoaderStub();

        Map<Integer, Integer> map = loader.loadAll(AttributeMaps
                .createMap(asList(0, 1, 2)));
        assertEquals(5, map.get(0).intValue());
        assertEquals(6, map.get(1).intValue());
        assertEquals(7, map.get(2).intValue());
    }

    public static class LoaderStub extends AbstractCacheLoader<Integer, Integer> {
        public Integer load(Integer i, AttributeMap attributes) {
            assertEquals(AttributeMaps.EMPTY_MAP, attributes);
            return i + 5;
        }
    }
}
