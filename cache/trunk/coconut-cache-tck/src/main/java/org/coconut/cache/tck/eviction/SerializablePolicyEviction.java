/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.eviction;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.other.Serialization;
import org.junit.Test;

/**
 * This test 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SerializablePolicyEviction extends CacheTestBundle {

    @Test
    public void testSerializable() throws IOException, ClassNotFoundException {
        Cache<Integer, String> c = newCache(newConf().eviction().setPolicy(
                Policies.newLRU()).setMaximumSize(10).c());
        for (int i = 0; i < 10; i++) {
            c.put(i, Integer.toString(i), 1, TimeUnit.NANOSECONDS);
        }
        c.get(4);
        c.get(4);
        c.get(0);
        c.get(3);
        c.get(2);
        c.get(9);

        //serialize and unserialize and make sure that policy order is kept
        Cache<Integer, String> cc = Serialization.serializeAndUnserialize(c);
        
        replaceAndCheck(cc, 10, 1);
        replaceAndCheck(cc, 11, 5);
        replaceAndCheck(cc, 12, 6);
        replaceAndCheck(cc, 13, 7);
        replaceAndCheck(cc, 14, 8);
        replaceAndCheck(cc, 15, 4);
        replaceAndCheck(cc, 16, 0);
        replaceAndCheck(cc, 17, 3);
        replaceAndCheck(cc, 18, 2);
        replaceAndCheck(cc, 19, 9);
    }

    void replaceAndCheck(Cache<Integer, String> c, Integer newKey,
            Integer oldKey) {
        assertNull(c.put(newKey, "foo"));
        assertFalse(c.containsKey(oldKey));
    }

}
