/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.coconut.cache.test.memory.other.TestHelper;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MyTestMethodRunner extends TestMethodRunner {

	private static final Runtime RUNTIME = Runtime.getRuntime();
	public volatile int foo;
	
	/**
     * @param test
     * @param method
     * @param notifier
     * @param description
     */
	public MyTestMethodRunner(Object test, Method method, RunNotifier notifier,
			Description description) {
		super(test, method, notifier, description);
	}
	/**
     * @see org.junit.internal.runners.TestMethodRunner#executeMethodBody()
     */
	@Override
	protected void executeMethodBody() throws IllegalAccessException,
			InvocationTargetException {
		super.executeMethodBody();
		foo=1;
		TestHelper.runGC();
		
		long after = 0;
		long before = TestHelper.usedMemory();
		foo=1;
		super.executeMethodBody();
		foo=1;
		TestHelper.runGC();
		foo=1;
		TestHelper.runGC();
		after = TestHelper.usedMemory();
		System.out.println("Used memory " + (after - before));
		
	}
	
//	System.out.println(RUNTIME.totalMemory() + ", " + RUNTIME.freeMemory());

}

