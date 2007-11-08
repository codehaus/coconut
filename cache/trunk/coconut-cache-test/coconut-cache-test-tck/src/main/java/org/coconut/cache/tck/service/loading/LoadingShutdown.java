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
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadingShutdown extends AbstractLoadingTestBundle {

    static Map<Integer, AttributeMap> M = new HashMap<Integer, AttributeMap>();

    static {
        M.put(1, AttributeMaps.EMPTY_MAP);
        M.put(2, AttributeMaps.EMPTY_MAP);
    }

    @Test
    public void load() {
        loading().load(1);
        awaitAllLoads();
        shutdownAndAwait();
        loading().load(1);
        awaitAllLoads();
    }

    @Test
    public void loadAtr() {
        loading().load(1, AttributeMaps.EMPTY_MAP);
        awaitAllLoads();
        shutdownAndAwait();
        loading().load(1, AttributeMaps.EMPTY_MAP);
        awaitAllLoads();
    }

    @Test
    public void loadAllCol() {
        loading().loadAll(Arrays.asList(1, 2));
        awaitAllLoads();
        shutdownAndAwait();
        loading().loadAll(Arrays.asList(1, 2));
        awaitAllLoads();
    }

    @Test
    public void loadAllMapAtr() {
        loading().loadAll(M);
        awaitAllLoads();
        shutdownAndAwait();
        loading().loadAll(M);
        awaitAllLoads();
    }

    @Test
    public void forceLoad() {
        loading().forceLoad(1);
        awaitAllLoads();
        shutdownAndAwait();
        loading().forceLoad(1);
        awaitAllLoads();
    }

    @Test
    public void forceLoadAtr() {
        loading().forceLoad(1, AttributeMaps.EMPTY_MAP);
        awaitAllLoads();
        shutdownAndAwait();
        loading().forceLoad(1, AttributeMaps.EMPTY_MAP);
        awaitAllLoads();
    }

    @Test
    public void forceLoadAllCol() {
        loading().forceLoadAll(Arrays.asList(1, 2));
        awaitAllLoads();
        shutdownAndAwait();
        loading().forceLoadAll(Arrays.asList(1, 2));
        awaitAllLoads();
    }
    @Test
    public void loadAllAtr() {
        loading().loadAll(AttributeMaps.EMPTY_MAP);
        awaitAllLoads();
        shutdownAndAwait();
        loading().loadAll(AttributeMaps.EMPTY_MAP);
        awaitAllLoads();
    }
    @Test
    public void forceLoadAllAtr() {
        loading().forceLoadAll(AttributeMaps.EMPTY_MAP);
        awaitAllLoads();
        shutdownAndAwait();
        loading().forceLoadAll(AttributeMaps.EMPTY_MAP);
        awaitAllLoads();
    }

    @Test
    public void forceLoadAllMapAtr() {
        loading().forceLoadAll(M);
        awaitAllLoads();
        shutdownAndAwait();
        loading().forceLoadAll(M);
        awaitAllLoads();
    }

    @Test
    public void forceLoadAll() {
        loading().forceLoadAll();
        awaitAllLoads();
        shutdownAndAwait();
        loading().forceLoadAll();
        awaitAllLoads();
    }

}
