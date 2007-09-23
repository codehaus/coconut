package org.coconut.cache.tck.service.loading;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.junit.Test;

/**
 * Tests that the cache will lazy start when calling any of the methods in
 * <p>
 * 
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadingLazyStart extends AbstractLoadingTestBundle {

    static Map<Integer, AttributeMap> M = new HashMap<Integer, AttributeMap>();

    static {
        M.put(1, AttributeMaps.EMPTY_MAP);
        M.put(2, AttributeMaps.EMPTY_MAP);
    }

    // TODO test, filtered load
    @Test
    public void load() {
        assertFalse(c.isStarted());
        loading().load(1);
        assertTrue(c.isStarted());
    }

    @Test
    public void loadAtr() {
        assertFalse(c.isStarted());
        loading().load(1, AttributeMaps.EMPTY_MAP);
        assertTrue(c.isStarted());
    }

    @Test
    public void loadAllCol() {
        assertFalse(c.isStarted());
        loading().loadAll(Arrays.asList(1, 2));
        assertTrue(c.isStarted());
    }

    @Test
    public void loadAllAtr() {
        assertFalse(c.isStarted());
        loading().loadAll(M);
        assertTrue(c.isStarted());
    }

    @Test
    public void forceLoad() {
        assertFalse(c.isStarted());
        loading().forceLoad(1);
        assertTrue(c.isStarted());
    }

    @Test
    public void forceLoadAtr() {
        assertFalse(c.isStarted());
        loading().forceLoad(1, AttributeMaps.EMPTY_MAP);
        assertTrue(c.isStarted());
    }

    @Test
    public void forceLoadAllCol() {
        assertFalse(c.isStarted());
        loading().forceLoadAll(Arrays.asList(1, 2));
        assertTrue(c.isStarted());
    }

    @Test
    public void forceLoadAllAtr() {
        assertFalse(c.isStarted());
        loading().forceLoadAll(AttributeMaps.EMPTY_MAP);
        assertTrue(c.isStarted());
    }

    @Test
    public void forceLoadAllMapAtr() {
        assertFalse(c.isStarted());
        loading().forceLoadAll(M);
        assertTrue(c.isStarted());
    }

    @Test
    public void forceLoadAll() {
        assertFalse(c.isStarted());
        loading().forceLoadAll();
        assertTrue(c.isStarted());
    }

}
