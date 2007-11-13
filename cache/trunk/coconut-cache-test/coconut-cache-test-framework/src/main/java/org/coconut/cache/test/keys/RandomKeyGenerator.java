package org.coconut.cache.test.keys;

import java.util.Collection;

import org.coconut.test.LoopHelpers;

public class RandomKeyGenerator implements KeyGenerator<Integer> {

    final LoopHelpers.SimpleRandom rng = new LoopHelpers.SimpleRandom();

    public Integer nextKey() {
        return rng.next() % 1000;
    }

    public Collection<? extends Integer> nextKeys() {
        throw new UnsupportedOperationException();
    }

}
