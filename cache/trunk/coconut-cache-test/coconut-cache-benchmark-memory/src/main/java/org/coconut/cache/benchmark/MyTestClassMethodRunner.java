/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark;

import java.lang.reflect.Method;

import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.notification.RunNotifier;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MyTestClassMethodRunner extends TestClassMethodsRunner {

	/**
     * @param klass
     */
	public MyTestClassMethodRunner(Class<?> klass) {
		super(klass);
	}

	/**
     * @see org.junit.internal.runners.TestClassMethodsRunner#createMethodRunner(java.lang.Object,
     *      java.lang.reflect.Method, org.junit.runner.notification.RunNotifier)
     */
	@Override
	protected TestMethodRunner createMethodRunner(Object test, Method method,
			RunNotifier notifier) {
		return new MyTestMethodRunner(test, method, notifier,
				methodDescription(method));
	}

}
