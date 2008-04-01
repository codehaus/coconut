/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimeInstanceAttributeTest {
    static final TimeInstanceAttribute DA = new TimeInstanceAttribute("foo") {};

    @Test
    public void isValid() {
        assertFalse(DA.isValid(Long.MIN_VALUE));
        assertTrue(DA.isValid(0));
        assertTrue(DA.isValid(1));
        assertTrue(DA.isValid(Long.MAX_VALUE));
    }

    @Test
    public void checkValid() {
        DA.checkValid(0);
        assertTrue(DA.isValid(1));
        assertTrue(DA.isValid(Long.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE() {
        DA.checkValid(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE1() {
        DA.checkValid(Long.MIN_VALUE);
    }

   
     protected AttributeMap newMap() {
        return new DefaultAttributeMap();
    }
}
