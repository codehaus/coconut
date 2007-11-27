/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults.stubs;

import org.coconut.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributedStub2.java 415 2007-11-09 08:25:23Z kasper $
 */
public class VariousOperations {
	public int invokeCount;

     
	@ManagedOperation(defaultValue = "m2")
	public void method2() {
		invokeCount++;
	}

	@ManagedOperation()
	public String method3() {
		return "m3";
	}

	@ManagedOperation()
	public String method4(String arg) {
		return arg.toUpperCase();
	}

	@ManagedOperation(description = "desca")
	public void method5() {}

	@ManagedOperation(defaultValue = "foo", description = "desc")
	public void method6() {}
}
