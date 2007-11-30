package org.coconut.cache.test.generators.base;

import org.coconut.test.LoopHelpers;

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
        for (int i = 0; i < 100; i++) {
            System.out.println(rig.nextKey());
        }
    }

}
