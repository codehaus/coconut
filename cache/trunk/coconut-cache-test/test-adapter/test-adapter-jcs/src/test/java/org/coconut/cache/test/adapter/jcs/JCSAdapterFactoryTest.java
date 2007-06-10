/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.jcs;

import org.coconut.cache.test.adapter.util.AbstractSimpleTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class JCSAdapterFactoryTest {

	@Test
	public void testFactory() throws Exception {
		AbstractSimpleTest.testPutGet(new JCSCacheAdapterFactory());
	}
}
