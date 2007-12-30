/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.coconut.cache.tck.service.event.EventServiceEviction.RejectEntriesPolicy;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.operations.Predicates;
import org.junit.Test;

public class IgnoreEvents extends AbstractEventTestBundle {

    @Test
    public void add() {
        init(anythingBut(ItemAdded.class));
        subscribe(Predicates.truePredicate());
        put(M1);
        putAll(M2, M3);
    }

    @Test
    public void addLoad() {
        CacheConfiguration<Integer, String> conf = anythingBut(ItemAdded.class);
        init(conf.loading().setLoader(new IntegerToStringLoader()));
        subscribe(Predicates.truePredicate());
        loading().forceLoad(1);
        awaitAllLoads();
    }

    @Test
    public void update() {
        init(anythingBut(ItemUpdated.class));
        put(M1);
        subscribe(Predicates.truePredicate());
        c.put(M1.getKey(), "C");
    }

    @Test
    public void remove() {
        init(anythingBut(ItemRemoved.class));
        put(M1);
        subscribe(Predicates.truePredicate());
        remove(M1);
    }

    @Test
    public void removeCleared() {
        init(anythingBut(ItemRemoved.class));
        put(M1);
        subscribe(Predicates.truePredicate());
        c.clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c, CacheEvent.CacheCleared.class);
        assertEquals(1, cleared.getPreviousSize());

    }

    @Test
    public void clear() {
        init(anythingBut(CacheEvent.CacheCleared.class));
        put(M1);
        subscribe(Predicates.truePredicate());
        c.clear();
        consumeItem(ItemRemoved.class, M1);
    }

    @Test
    public void purgeExpired() {
        init(anythingBut(ItemRemoved.class));
        expiration().put(M1.getKey(), M1.getValue(), 1, TimeUnit.NANOSECONDS);
        assertSize(1);
        expiration().purgeExpired();
    }

    @Test
    public void putLoad() {
        CacheConfiguration<Integer, String> conf = anythingBut(ItemAdded.class);
        init(conf.loading().setLoader(new IntegerToStringLoader()));
        subscribe(Predicates.truePredicate());
        loading().forceLoad(1);
        awaitAllLoads();
    }

    @Test
    public void testRejectReplaceEntry() {
        RejectEntriesPolicy rep = new RejectEntriesPolicy();
        anythingBut(ItemRemoved.class);
        c = newCache(conf.eviction().setPolicy(rep).c());

        c.put(1, "A");
        c.put(2, "B");
        assertSize(2);
        subscribe(Predicates.truePredicate());
        rep.rejectUpdate = true;
        c.put(2, "C");
        assertEquals(1, c.size());
        assertFalse(c.containsKey(2));
    }
}
