package org.coconut.cache.internal.service.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.test.MockTestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class InternalEntryEventTest {
    Mockery context = new JUnit4Mockery();

    CacheEntry<Integer, String> ce;

    Cache<Integer, String> cache = MockTestCase.mockDummy(Cache.class);

    @Before
    public void setup() {
        ce = context.mock(CacheEntry.class);
        context.checking(new Expectations() {
            {
                allowing(ce).getKey();
                will(returnValue(1));
                allowing(ce).getValue();
                will(returnValue("A"));
            }
        });
    }

    @Test
    public void removed() {
        CacheEntryEvent.ItemRemoved<Integer, String> c = InternalEntryEvent.removed(cache, ce);
        assertSame(cache, c.getCache());
        assertEquals(1, c.getKey());
        assertEquals("A", c.getValue());
        assertFalse(c.hasExpired());
        assertEquals(CacheEntryEvent.ItemRemoved.NAME, c.getName());
        c.toString();
    }

    @Test(expected = NullPointerException.class)
    public void removedNPE() {
        InternalEntryEvent.removed(null, ce);
    }

    @Test(expected = NullPointerException.class)
    public void removedNPE1() {
        InternalEntryEvent.removed(cache, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removedSetValue() {
        InternalEntryEvent.removed(cache, ce).setValue("foo");
    }

    @Test
    public void evicted() {
        CacheEntryEvent.ItemRemoved<Integer, String> c = InternalEntryEvent.evicted(cache, ce);
        assertSame(cache, c.getCache());
        assertEquals(1, c.getKey());
        assertEquals("A", c.getValue());
        assertFalse(c.hasExpired());
        assertEquals(CacheEntryEvent.ItemRemoved.NAME, c.getName());
        c.toString();
    }

    @Test(expected = NullPointerException.class)
    public void evictedNPE() {
        InternalEntryEvent.evicted(null, ce);
    }

    @Test(expected = NullPointerException.class)
    public void evictedNPE1() {
        InternalEntryEvent.evicted(cache, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void evictedSetValue() {
        InternalEntryEvent.evicted(cache, ce).setValue("foo");
    }

    @Test
    public void expired() {
        CacheEntryEvent.ItemRemoved<Integer, String> c = InternalEntryEvent.expired(cache, ce);
        assertSame(cache, c.getCache());
        assertEquals(1, c.getKey());
        assertEquals("A", c.getValue());
        assertTrue(c.hasExpired());
        assertEquals(CacheEntryEvent.ItemRemoved.NAME, c.getName());
        c.toString();
    }

    @Test(expected = NullPointerException.class)
    public void expiredNPE() {
        InternalEntryEvent.expired(null, ce);
    }

    @Test(expected = NullPointerException.class)
    public void expiredNPE1() {
        InternalEntryEvent.expired(cache, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void expiredSetValue() {
        InternalEntryEvent.expired(cache, ce).setValue("foo");
    }

    @Test
    public void added() {
        CacheEntryEvent.ItemAdded<Integer, String> c = InternalEntryEvent.added(cache, ce);
        assertSame(cache, c.getCache());
        assertEquals(1, c.getKey());
        assertEquals("A", c.getValue());
        assertEquals(CacheEntryEvent.ItemAdded.NAME, c.getName());
        c.toString();
    }

    @Test(expected = NullPointerException.class)
    public void addedNPE() {
        InternalEntryEvent.added(null, ce);
    }

    @Test(expected = NullPointerException.class)
    public void addedNPE1() {
        InternalEntryEvent.added(cache, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addedSetValue() {
        InternalEntryEvent.added(cache, ce).setValue("foo");
    }

    @Test
    public void updated() {
        CacheEntryEvent.ItemUpdated<Integer, String> c = InternalEntryEvent.updated(cache, ce, "B",
                false);
        assertSame(cache, c.getCache());
        assertEquals(1, c.getKey());
        assertEquals("A", c.getValue());
        assertEquals("B", c.getPreviousValue());
        assertFalse(c.hasExpired());
        assertEquals(CacheEntryEvent.ItemUpdated.NAME, c.getName());
        c.toString();
    }

    @Test(expected = NullPointerException.class)
    public void updatedNPE() {
        InternalEntryEvent.updated(null, ce, "B", false);
    }

    @Test(expected = NullPointerException.class)
    public void updatedNPE1() {
        InternalEntryEvent.updated(cache, null, "B", false);
    }

    @Test(expected = NullPointerException.class)
    public void updatedNPE2() {
        InternalEntryEvent.updated(cache, ce, null, false);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void updatedSetValue() {
        InternalEntryEvent.updated(cache, ce, "B", false).setValue("foo");
    }
}
