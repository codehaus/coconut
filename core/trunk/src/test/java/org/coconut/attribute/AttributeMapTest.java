/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Map;

import org.coconut.test.MockTestCase;
import org.junit.Test;

public class AttributeMapTest {

    @Test
    public void singleton() {
        Attribute a = MockTestCase.mockDummy(Attribute.class);
        AttributeMap am = AttributeMaps.singleton(a, 1);
        assertEquals(1, am.size());
        assertEquals(1, am.get(a));
    }

    @Test
    public void toMap() {
        AttributeMap am = MockTestCase.mockDummy(AttributeMap.class);
        Map<Integer, AttributeMap> m = AttributeMaps.toMap(Arrays.asList(1, 2, 3, 4), am);
        assertEquals(4, m.size());
        assertEquals(am, m.get(1));
        assertEquals(am, m.get(2));
        assertEquals(am, m.get(3));
        assertEquals(am, m.get(4));
        assertNull(m.get(5));
    }

}
