package org.coconut.attribute;

import static org.junit.Assert.*;

import org.junit.Test;

public class AttributeMaps_DefaultMapTest extends AbstractAttributeMapTest {

    @Override
    protected AttributeMap create() {
        return new AttributeMaps.DefaultAttributeMap();
    }

    @Test
    public void copyConstructor() {
        AttributeMap am = create();
        am.put(a1, 12.23);
        am.put(a2, 12);
        assertEquals(am, new AttributeMaps.DefaultAttributeMap(am));
    }
}
