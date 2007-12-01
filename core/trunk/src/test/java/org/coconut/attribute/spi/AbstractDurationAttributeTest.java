package org.coconut.attribute.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.junit.Ignore;
import org.junit.Test;

public abstract class AbstractDurationAttributeTest extends AbstractAttributeTest {
    public AbstractDurationAttributeTest(Attribute a) {
        super(a, POSITIV_LONGS, NON_POSITIVE_LONGS);
    }

    @Override
    @Test
    public void set() throws Exception {
        Method setMethod = a().getClass().getMethod("set", AttributeMap.class, Long.TYPE,
                TimeUnit.class);
        assertTrue(setMethod.getReturnType().equals(AttributeMap.class));
        AttributeMap am = newMap();

        setMethod.invoke(null, am, 10, TimeUnit.NANOSECONDS);
        assertEquals(10l, am.getLong(a(), 0l));

        setMethod.invoke(null, am, 10, TimeUnit.MICROSECONDS);
        assertEquals(10000l, am.getLong(a(), 0l));

        setMethod.invoke(null, am, Long.MAX_VALUE, TimeUnit.MICROSECONDS);
        assertEquals(Long.MAX_VALUE, am.getLong(a(), 0l));

        try {
            setMethod.invoke(null, am, -1, TimeUnit.NANOSECONDS);
            throw new AssertionError("Should Throw");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Override
    @Test
    public void singleton() throws Exception {
        Method singleton = a().getClass().getMethod("singleton", Long.TYPE, TimeUnit.class);
        assertTrue(singleton.getReturnType().equals(AttributeMap.class));

        AttributeMap map = (AttributeMap) singleton.invoke(null, 10, TimeUnit.NANOSECONDS);
        assertEquals(1, map.size());
        assertTrue(map.containsKey(a()));
        assertEquals(10L, map.get(a()));

        map = (AttributeMap) singleton.invoke(null, 10, TimeUnit.MICROSECONDS);
        assertEquals(1, map.size());
        assertTrue(map.containsKey(a()));
        assertEquals(10000L, map.get(a()));

        map = (AttributeMap) singleton.invoke(null, Long.MAX_VALUE, TimeUnit.MICROSECONDS);
        assertEquals(1, map.size());
        assertTrue(map.containsKey(a()));
        assertEquals(Long.MAX_VALUE, map.get(a()));

        try {
            singleton.invoke(null, -1, TimeUnit.NANOSECONDS);
            throw new AssertionError("Should Throw");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }
}
