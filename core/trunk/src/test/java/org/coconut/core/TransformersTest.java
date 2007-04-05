/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import java.util.Arrays;

import org.coconut.core.Transformer;
import org.coconut.test.MockTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.coconut.core.Transformers.*;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class TransformersTest {

    private static final Transformer t1 = new Transformer<String, String>() {
        public String transform(String from) {
            return from.substring(0, 3);
        }
    };

    private static final Transformer t2 = new Transformer<String, Integer>() {
        public Integer transform(String from) {
            return Integer.parseInt(from);
        }
    };

    @Test(expected = NullPointerException.class)
    public void testArrayNPE() {
        t(null);
    }

    @Test
    public void testArray1arg() {
        assertEquals(t1, t(t1));
    }

    @Test
    public void testArrayManyArg() {
        Transformer t = t(t1, t2);
        assertEquals(t(t1, t2).hashCode(), t.hashCode());
        assertEquals(t(t1, t2), t);
        assertFalse(t(t2, t1).equals(t));
        assertEquals(Arrays.toString(new Transformer[] { t1, t2 }), t.toString());

        assertEquals(123, t.transform("123243234"));
    }

    @Test
    public void testPassThroughTransformer() {
        assertEquals("123", passThroughTransformer().transform("123"));
    }
}
