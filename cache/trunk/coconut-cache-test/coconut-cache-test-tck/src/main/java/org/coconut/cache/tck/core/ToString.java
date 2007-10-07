/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class ToString extends AbstractCacheTCKTest {

    /**
     * Just test that the toString() method works.
     */
    @Test
    public void toStringX() {
        c = newCache(5);
        String s = c.toString();
        try {
            for (int i = 1; i < 6; i++) {
                assertTrue(s.indexOf(String.valueOf(i)) >= 0);
                assertTrue(s.indexOf("" + (char) (i + 64)) >= 0);
            }
        } catch (AssertionError ar) {
            System.out.println(s);
            throw ar;
        }
    }
}
