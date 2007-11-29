/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.keyset;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { KeySet.class, KeySetAdd.class, KeySetClear.class, KeySetContains.class,
        KeySetHashCodeEquals.class, KeySetIsEmpty.class, KeySetIterator.class, KeySetRemove.class,
        KeySetRetain.class, KeySetSize.class, KeySetToArray.class })
public class KeySetSuite {

}
