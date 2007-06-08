package org.coconut.cache.tck.service.eviction;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.M6;
import static org.coconut.test.CollectionUtils.M7;
import static org.coconut.test.CollectionUtils.M8;
import static org.coconut.test.CollectionUtils.M9;

import org.coconut.cache.policy.Policies;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Before;
import org.junit.Test;
@SuppressWarnings("unchecked")
public class EvictionLRU extends AbstractCacheTCKTestBundle {

    //TODO check some loading
    @Before
    public void setup() {
        setCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(5));
    }

    @Test
    public void testPut() {
        put(5);
        assertSize(5);
        assertPeek(M1);
        put(6, 6);
        assertSize(5);
        assertNullPeek(M1);
        put(7, 9);
        assertNullPeek(M2);
        assertNullPeek(M3);
        assertNullPeek(M4);
        assertPeek(M5);
        assertPeek(M6);
        assertPeek(M7);
        assertPeek(M8);
        assertPeek(M9);
    }

    @Test
    public void testTouch() {
        put(5);
        assertSize(5);
        assertPeek(M1);
        assertPeek(M2);
        touch(M1);
        put(6, 6);
        assertSize(5);
        assertPeek(M1);
        assertNullPeek(M2);
        touch(M3, M4, M3, M1, M3);

        assertPeek(M5);
        put(17, 17);
        assertNullPeek(M5);

        assertPeek(M6);
        put(18, 18);
        assertNullPeek(M6);

        assertPeek(M4);
        put(18, 18);
        assertPeek(M4);

        assertPeek(M4);
        put(19, 19);
        assertNullPeek(M4);

        assertPeek(M1);
        put(20, 20);
        assertNullPeek(M1);

        assertPeek(M3);
        put(21, 21);
        assertNullPeek(M3);
    }
}
