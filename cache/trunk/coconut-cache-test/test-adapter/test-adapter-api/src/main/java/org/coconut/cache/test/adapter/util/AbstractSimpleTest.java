/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.util;

import org.coconut.cache.test.adapter.CacheAdapterFactory;
import org.coconut.cache.test.adapter.CacheTestAdapter;
import static org.junit.Assert.*;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstractSimpleTest {

	public static void testPutGet(CacheAdapterFactory factory) throws Exception {
		CacheTestAdapter a = factory.createAdapter();
		a.put("fooboo", 123123);
		assertEquals(123123, a.get("fooboo"));
	}
}
