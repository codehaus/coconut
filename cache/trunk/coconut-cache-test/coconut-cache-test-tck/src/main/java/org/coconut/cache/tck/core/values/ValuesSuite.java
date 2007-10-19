package org.coconut.cache.tck.core.values;

import org.coconut.cache.tck.core.keyset.KeySet;
import org.coconut.cache.tck.core.keyset.KeySetAdd;
import org.coconut.cache.tck.core.keyset.KeySetClear;
import org.coconut.cache.tck.core.keyset.KeySetContains;
import org.coconut.cache.tck.core.keyset.KeySetIsEmpty;
import org.coconut.cache.tck.core.keyset.KeySetIterator;
import org.coconut.cache.tck.core.keyset.KeySetRemove;
import org.coconut.cache.tck.core.keyset.KeySetRetain;
import org.coconut.cache.tck.core.keyset.KeySetSize;
import org.coconut.cache.tck.core.keyset.KeySetHashCodeEquals;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { KeySet.class, KeySetAdd.class, KeySetAdd.class, KeySetClear.class,
        KeySetContains.class, KeySetIsEmpty.class, KeySetIterator.class,
        KeySetRemove.class, KeySetRetain.class, KeySetSize.class, KeySetHashCodeEquals.class })

public class ValuesSuite {

}
