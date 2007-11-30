package org.coconut.cache.test.keys;

import java.util.Collection;

public interface KeyCollectionGenerator<K> {
    Collection<? extends K> nextKeys();

}
