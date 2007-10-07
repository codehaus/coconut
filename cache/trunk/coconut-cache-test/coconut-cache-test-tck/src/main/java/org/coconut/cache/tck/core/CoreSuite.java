/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { Clear.class, Constructors.class, ContainsKey.class,
        ContainsValue.class, EntrySet.class, EntrySetModifying.class,
        EqualsHashcode.class, Get.class, GetAll.class, IsEmpty.class, KeySet.class,
        KeySetModifying.class, Peek.class, Put.class, PutAll.class, PutIfAbsent.class,
        Remove.class, RemoveAll.class, Replace.class, Size.class, ToString.class,
        Values.class, ValuesModifying.class })
public class CoreSuite {

}
