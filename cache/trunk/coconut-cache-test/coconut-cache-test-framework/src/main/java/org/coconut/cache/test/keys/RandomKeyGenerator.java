/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.keys;

import org.coconut.operations.Ops.Generator;
import org.coconut.test.LoopHelpers;

public class RandomKeyGenerator implements Generator<Integer> {

    final LoopHelpers.SimpleRandom rng = new LoopHelpers.SimpleRandom();

    public Integer generate() {
        return rng.next() % 1000;
    }
}
