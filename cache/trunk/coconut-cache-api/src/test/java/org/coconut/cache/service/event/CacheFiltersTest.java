/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.event;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.coconut.cache.Cache;
import org.coconut.predicate.Predicate;
import org.coconut.predicate.Predicates;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class CacheFiltersTest {

    Mockery context = new JUnit4Mockery();

    @Test
    public void cacheFilter() {
        final CacheEvent event = context.mock(CacheEvent.class);
        final Cache c = context.mock(Cache.class);
        context.checking(new Expectations() {
            {
                one(event).getCache();
                will(returnValue(c));
            }
        });
        Predicate f = Predicates.equalsTo(c);
        Predicate<CacheEvent> filter = CacheEventFilters.mapperPredicate(f);
        assertTrue(filter.evaluate(event));
    }

    @Test(expected = NullPointerException.class)
    public void cacheFilterNPE() {
        CacheEventFilters.mapperPredicate(null);
    }

    @Test(expected = NullPointerException.class)
    public void cacheFilterNPE1() {
        CacheEventFilters.mapperPredicate(Predicates.TRUE).evaluate(null);
    }

    @Test(expected = NullPointerException.class)
    public void cacheNameNPE() {
        CacheEventFilters.eventName(null);
    }

    @Test
    public void cacheSameFilter() {
        final CacheEvent event1 = context.mock(CacheEvent.class);
        final CacheEvent event2 = context.mock(CacheEvent.class);
        final Cache c = context.mock(Cache.class);
        context.checking(new Expectations() {
            {
                one(event1).getCache();
                will(returnValue(c));
                one(event2).getCache();
                will(returnValue(context.mock(Cache.class)));
            }
        });
        Predicate<CacheEvent> f = CacheEventFilters.cacheSameFilter(c);
        assertTrue(f.evaluate(event1));
        assertFalse(f.evaluate(event2));
    }

    @Test
    public void cacheNameEqualsFilter() {
        final CacheEvent event1 = context.mock(CacheEvent.class);
        final CacheEvent event2 = context.mock(CacheEvent.class);
        context.checking(new Expectations() {
            {
                one(event1).getName();
                will(returnValue("T1"));
                one(event2).getName();
                will(returnValue("T2"));
            }
        });
        Predicate<CacheEvent<Integer, String>> f = CacheEventFilters.eventName(Predicates
                .equalsTo("T1"));
        assertTrue(f.evaluate(event1));
        assertFalse(f.evaluate(event2));
    }

}
