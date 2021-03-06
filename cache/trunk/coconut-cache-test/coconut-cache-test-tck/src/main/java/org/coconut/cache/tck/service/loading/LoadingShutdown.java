/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import java.util.Arrays;

import org.coconut.attribute.Attributes;
import org.junit.Test;

/**
 * Tests that the cache will lazy start when calling any of the methods in
 * <p>
 * <p>
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class LoadingShutdown extends AbstractLoadingTestBundle {

    @Test
    public void load() {
        loading().load(1);
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().load(1);
        awaitAllLoads();
    }

    @Test
    public void loadAtr() {
        loading().load(1, Attributes.EMPTY_ATTRIBUTE_MAP);
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().load(1, Attributes.EMPTY_ATTRIBUTE_MAP);
        awaitAllLoads();
    }

    @Test
    public void loadAllCol() {
        loading().loadAll(Arrays.asList(1, 2));
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().loadAll(Arrays.asList(1, 2));
        awaitAllLoads();
    }

    @Test
    public void loadAllMapAtr() {
        loading().loadAll(Attributes.toMap(Arrays.asList(1, 2)));
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().loadAll(Attributes.toMap(Arrays.asList(1, 2)));
        awaitAllLoads();
    }

    @Test
    public void forceLoad() {
        loading().forceLoad(1);
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().forceLoad(1);
        awaitAllLoads();
    }

    @Test
    public void forceLoadAtr() {
        loading().forceLoad(1, Attributes.EMPTY_ATTRIBUTE_MAP);
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().forceLoad(1, Attributes.EMPTY_ATTRIBUTE_MAP);
        awaitAllLoads();
    }

    @Test
    public void forceLoadAllCol() {
        loading().forceLoadAll(Arrays.asList(1, 2));
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().forceLoadAll(Arrays.asList(1, 2));
        awaitAllLoads();
    }

    @Test
    public void loadAllAtr() {
        loading().loadAll(Attributes.EMPTY_ATTRIBUTE_MAP);
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().loadAll(Attributes.EMPTY_ATTRIBUTE_MAP);
        awaitAllLoads();
    }

    @Test
    public void forceLoadAllAtr() {
        loading().forceLoadAll(Attributes.EMPTY_ATTRIBUTE_MAP);
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().forceLoadAll(Attributes.EMPTY_ATTRIBUTE_MAP);
        awaitAllLoads();
    }

    @Test
    public void forceLoadAllMapAtr() {
        loading().forceLoadAll(Attributes.toMap(Arrays.asList(1, 2)));
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().forceLoadAll(Attributes.toMap(Arrays.asList(1, 2)));
        awaitAllLoads();
    }

    @Test
    public void forceLoadAll() {
        loading().forceLoadAll();
        awaitAllLoads();
        shutdownAndAwaitTermination();
        loading().forceLoadAll();
        awaitAllLoads();
    }

}
