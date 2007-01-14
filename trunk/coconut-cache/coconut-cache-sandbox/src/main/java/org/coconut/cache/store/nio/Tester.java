/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.nio;

import java.io.Serializable;

import sun.security.util.BitArray;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class Tester implements Serializable {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private final int value;

    public static void main(String[] args) {
        int v = -12573;
        System.out.println(v >>> 3);
        byte[] b = Integer.toString(32334349).getBytes();

        BitArray bb = new BitArray(32, b);

        System.out.println(bb);
        System.out.println(v);
    }
    public Tester(int value) {
        this.value = value;
    }
    public String toString() {
        return Integer.toString(value);
    }
    public boolean equals(Object other) {
        return other != null && other instanceof Tester && ((Tester) other).value == value;
    }
}
