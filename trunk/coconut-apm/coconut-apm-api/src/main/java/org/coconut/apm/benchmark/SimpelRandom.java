/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.benchmark;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SimpelRandom {
    private final static long multiplier = 0x5DEECE66DL;

    private final static long addend = 0xBL;

    private final static long mask = (1L << 48) - 1;

    private long seed;

    public SimpelRandom(long s) {
        seed = s;
    }

    public int next() {
        long nextseed = (seed * multiplier + addend) & mask;
        seed = nextseed;
        return ((int) (nextseed >>> 17)) & 0x7FFFFFFF;
    }

    public int next(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be positive");
        int bits, val;
        do {
            bits = next();
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }
}
