/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_KEY_SET;
import static org.coconut.test.CollectionUtils.M1_TO_M5_MAP;
import static org.coconut.test.CollectionUtils.M1_TO_M5_SET;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Before;
import org.junit.Test;

/**
 * This bundle tests various tweaks that could exist with an expiration
 * implementation.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExpirationCommon extends CacheTestBundle {

    @Before
    public void setUpCaches() {
        ExpirationFilter f = new ExpirationFilter();
        f.isExpired = true;
        c = newCache(newConf().setClock(clock).expiration().setFilter(f).c());
        fillItUp();
    }

    private void fillItUp() {
        put(M1, 2);
        put(M2, 3);
        put(M3, 4);
        put(M4, 4);
        put(M5, 6);
    }

    @Test
    public void mapOperationsDoNotTimeout() {
        incTime(100);
        assertSize(5);

        assertTrue(containsKey(M1));
        assertTrue(containsValue(M1));
        assertEquals(5, c.entrySet().size());
        assertEquals(M1_TO_M5_SET, c.entrySet());
        assertEquals(5, c.keySet().size());
        assertTrue(c.keySet().containsAll(M1_TO_M5_KEY_SET)
                && M1_TO_M5_KEY_SET.containsAll(c.keySet()));
        assertEquals(M1.getValue(), c.peek(M1.getKey()));

        assertEquals(M1.getValue(), c.put(M1.getKey(), "AB"));

        // TODO
        // putAll
        // putIfAbsent
        // remove
        // remove2
        // replace1
        // replace2
    }
}
