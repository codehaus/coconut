package org.coconut.attribute.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.junit.Test;

public class AbstractValueTest extends AbstractAttributeTest {

    public AbstractValueTest(Attribute a, Collection valid, Collection invalid) {
        super(a, valid, invalid);
    }
    @Test
    public void singleton() throws Exception {
        Method singleton = a.getClass().getMethod("singleton", c);
        assertTrue(singleton.getReturnType().equals(AttributeMap.class));
        for (Object l : valid) {
            AttributeMap map = (AttributeMap) singleton.invoke(null, l);
            assertEquals(1, map.size());
            assertTrue(map.containsKey(a));
            assertEquals(l, map.get(a));
            if (l instanceof Long) {
                assertEquals(l, map.getLong(a));
                assertEquals(l, map.getLong(a, 0l));
            }
        }
        for (Object l : invalid) {
            try {
                singleton.invoke(null, l);
                throw new AssertionError("Should Throw");
            } catch (InvocationTargetException e) {
                assertTrue(e.getCause() instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void set() throws Exception {
        Method setMethod = a.getClass().getMethod("set", AttributeMap.class, c);
        assertTrue(setMethod.getReturnType().equals(AttributeMap.class));

        for (Object l : valid) {
            AttributeMap am = newMap();
            if (l instanceof Long) {
                assertEquals(0l, am.getLong(a, 0));
            }
            assertSame(am, setMethod.invoke(null, am, l));
            if (l instanceof Long) {
                assertEquals(l, am.getLong(a, 0));
            }
        }
        for (Object l : invalid) {
            AttributeMap am = newMap();
            try {
                setMethod.invoke(null, am, l);
                throw new AssertionError("Should Throw");
            } catch (InvocationTargetException e) {
                assertTrue(e.getCause() instanceof IllegalArgumentException);
            }
        }
    }
}
