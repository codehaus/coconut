/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.map;

import java.util.HashMap;

import org.coconut.cache.test.adapter.util.AbstractSimpleTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class HashMapAdapterFactoryTest {

	@Test
	public void testFactory() throws Exception {
		AbstractSimpleTest.testPutGet(new MapAdapterFactory(HashMap.class));
	}
}
