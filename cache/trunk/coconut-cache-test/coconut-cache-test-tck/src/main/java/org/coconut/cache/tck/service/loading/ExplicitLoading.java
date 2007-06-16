/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionUtils.M1;

import java.util.Map;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExplicitLoading extends AbstractLoadingTestBundle {

    @Test(expected = NullPointerException.class)
    public void testLoadNullPointer() {
        loading().load(null);
    }

    @Test
    public void keepLoadAndPeek() {
        loadAndAwait(M1);
        assertEquals(1, loader.getNumberOfLoads());
        assertPeek(M1);
    }

    @Test
    public void keepLoadedValue() {
        loadAndAwait(M1);
        assertEquals(1, loader.getNumberOfLoads());
        assertGet(M1);
        assertEquals(1, loader.getNumberOfLoads());
    }

    @Test(expected = NullPointerException.class)
    public void testLoadAllNullPointer() {
        loading().loadAll((Map) null);
    }

}
