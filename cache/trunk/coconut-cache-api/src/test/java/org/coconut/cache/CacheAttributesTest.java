/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

import java.util.concurrent.TimeUnit;

import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.core.Clock;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class CacheAttributesTest {

    Mockery context = new JUnit4Mockery();

    @Test
    public void costGet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getDouble(CacheAttributes.COST,
                        CacheAttributes.DEFAULT_COST);
                will(returnValue(0.5));
                one(attributes).getDouble(CacheAttributes.COST,
                        CacheAttributes.DEFAULT_COST);
                will(returnValue(-45.3));
            }
        });
        assertEquals(0.5, CacheAttributes.getCost(attributes));
        assertEquals(-45.3, CacheAttributes.getCost(attributes));
        assertEquals(CacheAttributes.DEFAULT_COST, CacheAttributes
                .getCost(AttributeMaps.EMPTY_MAP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void costGetIAE1() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getDouble(CacheAttributes.COST,
                        CacheAttributes.DEFAULT_COST);
                will(returnValue(Double.NaN));
            }
        });
        CacheAttributes.getCost(attributes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void costGetIAE2() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getDouble(CacheAttributes.COST,
                        CacheAttributes.DEFAULT_COST);
                will(returnValue(Double.POSITIVE_INFINITY));
            }
        });
        CacheAttributes.getCost(attributes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void costGetIAE3() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getDouble(CacheAttributes.COST,
                        CacheAttributes.DEFAULT_COST);
                will(returnValue(Double.NEGATIVE_INFINITY));
            }
        });
        CacheAttributes.getCost(attributes);
    }

    @Test(expected = NullPointerException.class)
    public void costGetNPE1() {
        CacheAttributes.getCost(null);
    }

    @Test
    public void costSet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).putDouble(CacheAttributes.COST, -123.22);
                one(attributes).putDouble(CacheAttributes.COST, Double.MAX_VALUE);
            }
        });
        assertSame(attributes, CacheAttributes.setCost(attributes, -123.22));
        assertSame(attributes, CacheAttributes.setCost(attributes, Double.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void costSetIAE1() {
        CacheAttributes.setCost(AttributeMaps.EMPTY_MAP, Double.NaN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void costSetIAE2() {
        CacheAttributes.setCost(AttributeMaps.EMPTY_MAP, Double.POSITIVE_INFINITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void costSetIAE3() {
        CacheAttributes.setCost(AttributeMaps.EMPTY_MAP, Double.NEGATIVE_INFINITY);
    }

    @Test(expected = NullPointerException.class)
    public void costSetNPE() {
        CacheAttributes.setCost(null, 1);
    }

    @Test
    public void creationTimeGet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        Clock.DeterministicClock c = new Clock.DeterministicClock();
        c.setTimestamp(555);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.CREATION_TIME);
                will(returnValue(23l));
                one(attributes).getLong(CacheAttributes.CREATION_TIME);
                will(returnValue(0l));
            }
        });
        assertEquals(23, CacheAttributes.getCreationTime(attributes, c));
        assertEquals(555, CacheAttributes.getCreationTime(attributes, c));
    }

    @Test(expected = IllegalArgumentException.class)
    public void creationTimeGetIAE1() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.CREATION_TIME);
                will(returnValue(-1l));
            }
        });
        CacheAttributes.getCreationTime(attributes, new Clock.DeterministicClock());
    }

    /**
     * Tests the the specified clock does not return negative values.
     */
    @Test(expected = IllegalArgumentException.class)
    public void creationTimeGetIAE2() {
        Clock.DeterministicClock c = new Clock.DeterministicClock();
        c.setTimestamp(-1);
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.CREATION_TIME);
                will(returnValue(0l));
            }
        });
        CacheAttributes.getCreationTime(attributes, c);
    }

    @Test(expected = NullPointerException.class)
    public void creationTimeGetNPE1() {
        CacheAttributes.getCreationTime(null, new Clock.DeterministicClock());
    }

    @Test(expected = NullPointerException.class)
    public void creationTimeGetNPE2() {
        CacheAttributes.getCreationTime(AttributeMaps.EMPTY_MAP, null);
    }

    @Test
    public void creationTimeSet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).putLong(CacheAttributes.CREATION_TIME, 0);
                one(attributes).putLong(CacheAttributes.CREATION_TIME, Long.MAX_VALUE);
            }
        });
        assertSame(attributes, CacheAttributes.setCreationTime(attributes, 0));
        assertSame(attributes, CacheAttributes.setCreationTime(attributes, Long.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void creationTimeSetIAE() {
        CacheAttributes.setCreationTime(AttributeMaps.EMPTY_MAP, -1);
    }

    @Test(expected = NullPointerException.class)
    public void creationTimeSetNPE() {
        CacheAttributes.setCreationTime(null, 1);
    }
    @Test
    public void lastModifiedTimeGet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        Clock.DeterministicClock c = new Clock.DeterministicClock();
        c.setTimestamp(555);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.LAST_MODIFIED_TIME);
                will(returnValue(23l));
                one(attributes).getLong(CacheAttributes.LAST_MODIFIED_TIME);
                will(returnValue(0l));
            }
        });
        assertEquals(23, CacheAttributes.getLastModified(attributes, c));
        assertEquals(555, CacheAttributes.getLastModified(attributes, c));
    }

    @Test(expected = IllegalArgumentException.class)
    public void lastModifiedTimeGetIAE1() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.LAST_MODIFIED_TIME);
                will(returnValue(-1l));
            }
        });
        CacheAttributes.getLastModified(attributes, new Clock.DeterministicClock());
    }

    /**
     * Tests the the specified clock does not return negative values.
     */
    @Test(expected = IllegalArgumentException.class)
    public void lastModifiedTimeGetIAE2() {
        Clock.DeterministicClock c = new Clock.DeterministicClock();
        c.setTimestamp(-1);
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.LAST_MODIFIED_TIME);
                will(returnValue(0l));
            }
        });
        CacheAttributes.getLastModified(attributes, c);
    }

    @Test(expected = NullPointerException.class)
    public void lastModifiedTimeGetNPE1() {
        CacheAttributes.getLastModified(null, new Clock.DeterministicClock());
    }

    @Test(expected = NullPointerException.class)
    public void lastModifiedTimeGetNPE2() {
        CacheAttributes.getLastModified(AttributeMaps.EMPTY_MAP, null);
    }

    @Test
    public void lastModifiedTimeSet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).putLong(CacheAttributes.LAST_MODIFIED_TIME, 0);
                one(attributes).putLong(CacheAttributes.LAST_MODIFIED_TIME, Long.MAX_VALUE);
            }
        });
        assertSame(attributes, CacheAttributes.setLastModifiedTime(attributes, 0));
        assertSame(attributes, CacheAttributes.setLastModifiedTime(attributes, Long.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void lastModifiedSetIAE() {
        CacheAttributes.setLastModifiedTime(AttributeMaps.EMPTY_MAP, -1);
    }

    @Test(expected = NullPointerException.class)
    public void lastModifiedSetNPE() {
        CacheAttributes.setLastModifiedTime(null, 1);
    }
    @Test
    public void hitsGet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.HITS);
                will(returnValue(0l));
                one(attributes).getLong(CacheAttributes.HITS);
                will(returnValue(1000l));
            }
        });
        assertEquals(0l, CacheAttributes.getHits(attributes));
        assertEquals(1000l, CacheAttributes.getHits(attributes));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hitsGetIAE() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.HITS);
                will(returnValue(-1l));
            }
        });
        CacheAttributes.getHits(attributes);
    }

    @Test(expected = NullPointerException.class)
    public void hitsGetNPE1() {
        CacheAttributes.getHits(null);
    }

    @Test
    public void hitsSet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).putLong(CacheAttributes.HITS, 0);
                one(attributes).putLong(CacheAttributes.HITS, Long.MAX_VALUE);
            }
        });
        assertSame(attributes, CacheAttributes.setHits(attributes, 0));
        assertSame(attributes, CacheAttributes.setHits(attributes, Long.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hitsSetIAE() {
        CacheAttributes.setHits(AttributeMaps.EMPTY_MAP, -1);
    }

    @Test(expected = NullPointerException.class)
    public void hitsSetNPE() {
        CacheAttributes.setHits(null, 1);
    }

    @Test
    public void sizeGet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.SIZE,
                        CacheAttributes.DEFAULT_SIZE);
                will(returnValue(0l));
                one(attributes).getLong(CacheAttributes.SIZE,
                        CacheAttributes.DEFAULT_SIZE);
                will(returnValue(1000l));
            }
        });
        assertEquals(0l, CacheAttributes.getSize(attributes));
        assertEquals(1000l, CacheAttributes.getSize(attributes));
        assertEquals(CacheAttributes.DEFAULT_SIZE, CacheAttributes
                .getSize(AttributeMaps.EMPTY_MAP));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void sizeGetIAE() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.SIZE,
                        CacheAttributes.DEFAULT_SIZE);
                will(returnValue(-1l));
            }
        });
        CacheAttributes.getSize(attributes);
    }

    @Test(expected = NullPointerException.class)
    public void sizeGetNPE1() {
        CacheAttributes.getSize(null);
    }

    @Test
    public void sizeSet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).putLong(CacheAttributes.SIZE, 0);
                one(attributes).putLong(CacheAttributes.SIZE, Long.MAX_VALUE);
            }
        });
        assertSame(attributes, CacheAttributes.setSize(attributes, 0));
        assertSame(attributes, CacheAttributes.setSize(attributes, Long.MAX_VALUE));
    }
    @Test(expected = IllegalArgumentException.class)
    public void sizeSetIAE() {
        CacheAttributes.setSize(AttributeMaps.EMPTY_MAP, -1);
    }

    @Test(expected = NullPointerException.class)
    public void sizeSetNPE() {
        CacheAttributes.setSize(null, 1);
    }
    @Test
    public void timeToLiveGet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.TIME_TO_LIVE_NS);
                will(returnValue(0l));
                one(attributes).getLong(CacheAttributes.TIME_TO_LIVE_NS);
                will(returnValue(10 * 1000 * 1000 * 1000l));
                one(attributes).getLong(CacheAttributes.TIME_TO_LIVE_NS);
                will(returnValue(Long.MAX_VALUE));
            }
        });
        assertEquals(5l, CacheAttributes.getTimeToLive(attributes, TimeUnit.SECONDS, 5l));
        assertEquals(10l, CacheAttributes.getTimeToLive(attributes, TimeUnit.SECONDS, 5l));
        assertEquals(Long.MAX_VALUE, CacheAttributes.getTimeToLive(attributes,
                TimeUnit.SECONDS, 5l));
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeToLiveGetIAE1() {
        CacheAttributes.getTimeToLive(AttributeMaps.EMPTY_MAP, TimeUnit.NANOSECONDS, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeToLiveGetIAE2() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.TIME_TO_LIVE_NS);
                will(returnValue(-1l));
            }
        });
        CacheAttributes.getTimeToLive(attributes, TimeUnit.NANOSECONDS, 1);
    }

    @Test(expected = NullPointerException.class)
    public void timeToLiveGetNPE1() {
        CacheAttributes.getTimeToLive(null, TimeUnit.NANOSECONDS, 1);
    }

    @Test(expected = NullPointerException.class)
    public void timeToLiveGetNPE2() {
        CacheAttributes.getTimeToLive(AttributeMaps.EMPTY_MAP, null, 1);
    }

    @Test
    public void timeToLiveSet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).putLong(CacheAttributes.TIME_TO_LIVE_NS,
                        5 * 1000 * 1000 * 1000l);
                one(attributes).putLong(CacheAttributes.TIME_TO_LIVE_NS, Long.MAX_VALUE);
            }
        });
        assertSame(attributes, CacheAttributes.setTimeToLive(attributes, 0,
                TimeUnit.SECONDS)); // ignored
        assertSame(attributes, CacheAttributes.setTimeToLive(attributes, 5l,
                TimeUnit.SECONDS));
        assertSame(attributes, CacheAttributes.setTimeToLive(attributes, Long.MAX_VALUE,
                TimeUnit.SECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeToLiveSetIAE() {
        CacheAttributes.setTimeToLive(AttributeMaps.EMPTY_MAP, -1, TimeUnit.NANOSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void timeToLiveSetNPE1() {
        CacheAttributes.setTimeToLive(null, 1, TimeUnit.NANOSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void timeToLiveSetNPE2() {
        CacheAttributes.setTimeToLive(AttributeMaps.EMPTY_MAP, 1, null);
    }

    @Test
    public void timeToRefreshGet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.TIME_TO_REFRESH_NS);
                will(returnValue(0l));
                one(attributes).getLong(CacheAttributes.TIME_TO_REFRESH_NS);
                will(returnValue(10 * 1000 * 1000 * 1000l));
                one(attributes).getLong(CacheAttributes.TIME_TO_REFRESH_NS);
                will(returnValue(Long.MAX_VALUE));
            }
        });
        assertEquals(5l, CacheAttributes.getTimeToRefresh(attributes, TimeUnit.SECONDS,
                5l));
        assertEquals(10l, CacheAttributes.getTimeToRefresh(attributes, TimeUnit.SECONDS,
                5l));
        assertEquals(Long.MAX_VALUE, CacheAttributes.getTimeToRefresh(attributes,
                TimeUnit.SECONDS, 5l));
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeToRefreshGetIAE1() {
        CacheAttributes
                .getTimeToRefresh(AttributeMaps.EMPTY_MAP, TimeUnit.NANOSECONDS, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeToRefreshGetIAE2() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).getLong(CacheAttributes.TIME_TO_REFRESH_NS);
                will(returnValue(-1l));
            }
        });
        CacheAttributes.getTimeToRefresh(attributes, TimeUnit.NANOSECONDS, 1);
    }

    @Test(expected = NullPointerException.class)
    public void timeToRefreshGetNPE1() {
        CacheAttributes.getTimeToRefresh(null, TimeUnit.NANOSECONDS, 1);
    }

    @Test(expected = NullPointerException.class)
    public void timeToRefreshGetNPE2() {
        CacheAttributes.getTimeToRefresh(AttributeMaps.EMPTY_MAP, null, 1);
    }

    @Test
    public void timeToRefreshSet() {
        final AttributeMap attributes = context.mock(AttributeMap.class);
        context.checking(new Expectations() {
            {
                one(attributes).putLong(CacheAttributes.TIME_TO_REFRESH_NS,
                        5 * 1000 * 1000 * 1000l);
                one(attributes).putLong(CacheAttributes.TIME_TO_REFRESH_NS,
                        Long.MAX_VALUE);
            }
        });
        assertSame(attributes, CacheAttributes.setTimeToRefresh(attributes, 0,
                TimeUnit.SECONDS)); // ignored
        assertSame(attributes, CacheAttributes.setTimeToRefresh(attributes, 5l,
                TimeUnit.SECONDS));
        assertSame(attributes, CacheAttributes.setTimeToRefresh(attributes,
                Long.MAX_VALUE, TimeUnit.SECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeToRefreshSetIAE() {
        CacheAttributes.setTimeToRefresh(AttributeMaps.EMPTY_MAP, -1,
                TimeUnit.NANOSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void timeToRefreshSetNPE1() {
        CacheAttributes.setTimeToRefresh(null, 1, TimeUnit.NANOSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void timeToRefreshSetNPE2() {
        CacheAttributes.setTimeToRefresh(AttributeMaps.EMPTY_MAP, 1, null);
    }
}
