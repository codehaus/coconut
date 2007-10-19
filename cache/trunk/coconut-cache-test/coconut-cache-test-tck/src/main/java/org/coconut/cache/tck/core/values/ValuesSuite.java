package org.coconut.cache.tck.core.values;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { Values.class, ValuesAdd.class, ValuesAdd.class, ValuesClear.class,
        ValuesContains.class, ValuesIsEmpty.class, ValuesIterator.class,
        ValuesRemove.class, ValuesRetain.class, ValuesSize.class, ValuesHashCodeEquals.class })

public class ValuesSuite {

}
