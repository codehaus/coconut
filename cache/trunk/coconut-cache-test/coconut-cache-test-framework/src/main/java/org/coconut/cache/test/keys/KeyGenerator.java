package org.coconut.cache.test.keys;

import java.util.Collection;

public interface KeyGenerator<K> {
    K nextKey();
}
