/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.eviction;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * This class tests that even if a policy is not defined in the configuration the cache
 * will still be able to evict elements.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EvictionNoPolicy extends AbstractCacheTCKTest {

    @Test
    public void testMaximumSize() {
        init(newConf().eviction().setMaximumSize(3));
        put(5);
        assertSize(3);
        putAll(10, 15);
        assertSize(3);
    }

    @Test
    public void testMaximumSizeChange() {
        init(newConf().eviction().setMaximumSize(3));
        put(5);
        assertSize(3);
        eviction().setMaximumSize(6);
        putAll(10, 15);
        assertSize(6);
    }

    // TODO test elements with individual sizes.
}
