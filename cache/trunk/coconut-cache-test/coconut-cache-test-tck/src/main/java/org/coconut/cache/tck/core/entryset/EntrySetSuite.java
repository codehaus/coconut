package org.coconut.cache.tck.core.entryset;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { EntrySet.class, EntrySetAdd.class, EntrySetAdd.class, EntrySetClear.class,
    EntrySetContains.class, EntrySetIsEmpty.class, EntrySetIterator.class,
    EntrySetRemove.class, EntrySetRetain.class, EntrySetSize.class, EntrySetHashCodeEquals.class })
public class EntrySetSuite {

}
