/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Map;

import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

public class AttributeMapTest {

    @Test
    public void singleton() {
        Attribute a = new Attribute("key",Integer.class, 5){};
        AttributeMap am = Attributes.singleton(a, 1);
        assertEquals(1, am.size());
        assertEquals(1, am.get(a));
    }
    @Test
    public void toMapEmpty() {
        Map<Integer, AttributeMap> m = Attributes.toMap(Arrays.asList(1, 2, 3, 4));
        assertEquals(4, m.size());
        assertEquals(Attributes.EMPTY_ATTRIBUTE_MAP, m.get(1));
        assertEquals(Attributes.EMPTY_ATTRIBUTE_MAP, m.get(2));
        assertEquals(Attributes.EMPTY_ATTRIBUTE_MAP, m.get(3));
        assertEquals(Attributes.EMPTY_ATTRIBUTE_MAP, m.get(4));
        assertNull(m.get(5));
    }
    @Test
    public void toMap() {
        AttributeMap am = TestUtil.dummy(AttributeMap.class);
        Map<Integer, AttributeMap> m = Attributes.toMap(Arrays.asList(1, 2, 3, 4), am);
        assertEquals(4, m.size());
        assertEquals(am, m.get(1));
        assertEquals(am, m.get(2));
        assertEquals(am, m.get(3));
        assertEquals(am, m.get(4));
        assertNull(m.get(5));
    }

}
