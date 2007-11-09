/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.harness.impl;

import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.tck.CacheTCKImplementationSpecifier;
import org.coconut.cache.test.harness.CacheHarnessRunner;
import org.junit.runner.RunWith;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@RunWith(CacheHarnessRunner.class)
@CacheTCKImplementationSpecifier(UnsynchronizedCache.class)
public class UnsynchronizedCacheHarnessTest {

}
