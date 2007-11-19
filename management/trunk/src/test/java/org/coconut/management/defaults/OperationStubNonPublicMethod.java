/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import org.coconut.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributedStub1.java 415 2007-11-09 08:25:23Z kasper $
 */
public class OperationStubNonPublicMethod {
	public int invokeCount;

	@ManagedOperation
	void method1() {
		invokeCount++;
	}

}
