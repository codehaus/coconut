/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.coconut.internal.util.Mappers.passThroughTransformer;
import static org.coconut.internal.util.Mappers.t;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.coconut.core.Mapper;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: TransformersTest.java 415 2007-11-09 08:25:23Z kasper $
 */
public class TransformersTest {

    private static final Mapper t1 = new Mapper<String, String>() {
        public String map(String from) {
            return from.substring(0, 3);
        }
    };

    private static final Mapper t2 = new Mapper<String, Integer>() {
        public Integer map(String from) {
            return Integer.parseInt(from);
        }
    };

    @Test(expected = NullPointerException.class)
    public void testArrayNPE() {
        t(null);
        //TODO t((Transformer) null) doesn't throw
    }

    @Test
    public void testArray1arg() {
        assertEquals(t1, t(t1));
    }

    @Test
    public void testArrayManyArg() {
        Mapper t = t(t1, t2);
        assertEquals(t(t1, t2).hashCode(), t.hashCode());
        assertEquals(t(t1, t2), t);
        assertFalse(t(t2, t1).equals(t));
        assertEquals(Arrays.toString(new Mapper[] { t1, t2 }), t.toString());

        assertEquals(123, t.map("123243234"));
    }

    @Test
    public void testPassThroughTransformer() {
        assertEquals("123", passThroughTransformer().map("123"));
    }
}
