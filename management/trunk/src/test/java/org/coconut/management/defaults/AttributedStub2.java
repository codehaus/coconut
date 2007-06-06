/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import org.coconut.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AttributedStub2 {
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
