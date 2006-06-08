package org.coconut.cache.tck.locking;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * A cache that does not support locking should be tested against this
 * feature.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class NoLockSupport extends CacheTestBundle {

    /**
     * Asserts that a getLock() on a cache throws an
     * UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLockGlobal() {
        c1.getLock();
    }

    /**
     * Asserts that a getLock(single key) on a cache throws an
     * UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLockSingleElement() {
        c1.getLock(M1.getKey());
    }

    /**
     * Asserts that a getLock(multiple keys) on a cache throws an
     * UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLockMultipleElement() {
        c2.getLock(M1.getKey(), M2.getKey());
    }
}
