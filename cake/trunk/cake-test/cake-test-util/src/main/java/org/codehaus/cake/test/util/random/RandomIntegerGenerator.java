/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.codehaus.cake.test.util.random;

import org.codehaus.cake.test.util.LoopHelpers;

public class RandomIntegerGenerator {

    private final int start;

    private final int diff;

    private final LoopHelpers.SimpleRandom rng = new LoopHelpers.SimpleRandom();

    RandomIntegerGenerator(int startInclusive, int endExclusive) {
        this.start = startInclusive;
        this.diff = endExclusive - startInclusive;
    }

    public Integer nextKey() {
        return start + rng.nextInt(diff);
    }

    public static void main(String[] args) {
        RandomIntegerGenerator rig = new RandomIntegerGenerator(1, 4);
        for (int i = 0; i < 10; i++) {
            System.out.println(rig.nextKey());
        }
    }

}
