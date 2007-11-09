/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class MyRunner extends TestClassRunner{

	/**
	 * @param klass
	 * @throws InitializationError
	 */
	public MyRunner(Class<?> klass) throws InitializationError {
		super(klass, new MyTestClassMethodRunner(klass));
	}


}
