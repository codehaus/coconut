package org.coconut.cache;

import java.util.concurrent.TimeUnit;

import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.core.EventProcessor;
import org.coconut.core.EventUtils;
import org.coconut.core.Offerable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.Assert.*;

@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class CacheAttributesTest {

    Mockery context = new JUnit4Mockery();

    @Test
    public void testToHandler() {
        final Offerable subscriber = context.mock(Offerable.class);
        context.checking(new Expectations() {
            {
                one(subscriber).offer(0);
            }
        });
        EventProcessor eh = EventUtils.fromOfferable(subscriber);
        eh.process(0);
    }

    @Test
    public void getTimeToLive() {
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

    @Test(expected = NullPointerException.class)
    public void getTimeToLiveNPE1() {
        CacheAttributes.getTimeToLive(null, TimeUnit.NANOSECONDS, 1);
    }

    @Test(expected = NullPointerException.class)
    public void getTimeToLiveNPE2() {
        CacheAttributes.getTimeToLive(AttributeMaps.EMPTY_MAP, null, 1);
    }

    @Test
    public void setTimeToLive() {
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

    @Test(expected = NullPointerException.class)
    public void setTimeToLiveNPE1() {
        CacheAttributes.setTimeToLive(null, 1, TimeUnit.NANOSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void setTimeToLiveNPE2() {
        CacheAttributes.setTimeToLive(AttributeMaps.EMPTY_MAP, 1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setTimeToLiveIAE() {
        CacheAttributes.setTimeToLive(AttributeMaps.EMPTY_MAP, -1, TimeUnit.NANOSECONDS);
    }
}
