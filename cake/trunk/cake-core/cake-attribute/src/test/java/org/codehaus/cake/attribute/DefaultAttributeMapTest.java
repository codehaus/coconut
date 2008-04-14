/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class DefaultAttributeMapTest extends AbstractAttributeMapTest {

    @Override
    protected AttributeMap create() {
        return new DefaultAttributeMap();
    }

    @Test
    @Ignore
    public void copyConstructor() {
        AttributeMap am = create();
        am.put(a1, 12.23);
        am.put(a2, 12);
        assertEquals(am, new DefaultAttributeMap(am));
    }
}
