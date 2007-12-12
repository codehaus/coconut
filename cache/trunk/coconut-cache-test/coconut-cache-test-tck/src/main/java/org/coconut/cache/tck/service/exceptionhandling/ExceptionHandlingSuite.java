/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.exceptionhandling;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { Debugging.class, DebuggingWithManagement.class, ExceptionHandling.class,
        ExceptionHandlingLifecycle.class })
public class ExceptionHandlingSuite {

}