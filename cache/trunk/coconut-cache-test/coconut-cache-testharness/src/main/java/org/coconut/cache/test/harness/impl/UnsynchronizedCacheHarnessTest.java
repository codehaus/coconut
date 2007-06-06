/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.harness.impl;

import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.tck.CacheTCKClassSpecifier;
import org.coconut.cache.test.harness.CacheHarnessRunner;
import org.junit.runner.RunWith;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@RunWith(CacheHarnessRunner.class)
@CacheTCKClassSpecifier(UnsynchronizedCache.class)
public class UnsynchronizedCacheHarnessTest {

}
