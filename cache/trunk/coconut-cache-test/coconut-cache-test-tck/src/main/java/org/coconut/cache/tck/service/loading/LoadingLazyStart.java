/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.coconut.attribute.AttributeMap;
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
public class LoadingLazyStart extends AbstractLoadingTestBundle {

    static Map<Integer, AttributeMap> M = new HashMap<Integer, AttributeMap>();

    static {
        M.put(1, Attributes.EMPTY_MAP);
        M.put(2, Attributes.EMPTY_MAP);
    }

    @Test
    public void load() {
        assertFalse(c.isStarted());
        loading().load(1);
        assertTrue(c.isStarted());
    }

    @Test
    public void loadAtr() {
        assertFalse(c.isStarted());
        loading().load(1, Attributes.EMPTY_MAP);
        assertTrue(c.isStarted());
    }

    @Test
    public void loadAllCol() {
        assertFalse(c.isStarted());
        loading().loadAll(Arrays.asList(1, 2));
        assertTrue(c.isStarted());
    }

    @Test
    public void loadAllMapAtr() {
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
        loading().forceLoad(1, Attributes.EMPTY_MAP);
        assertTrue(c.isStarted());
    }

    @Test
    public void forceLoadAllCol() {
        assertFalse(c.isStarted());
        loading().forceLoadAll(Arrays.asList(1, 2));
        assertTrue(c.isStarted());
    }

    @Test
    public void loadAllAtr() {
        assertFalse(c.isStarted());
        loading().loadAll(Attributes.EMPTY_MAP);
        assertTrue(c.isStarted());
    }

    @Test
    public void forceLoadAllAtr() {
        assertFalse(c.isStarted());
        loading().forceLoadAll(Attributes.EMPTY_MAP);
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
