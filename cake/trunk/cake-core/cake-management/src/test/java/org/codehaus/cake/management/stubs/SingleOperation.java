/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management.stubs;

import org.codehaus.cake.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributedStub1.java 415 2007-11-09 08:25:23Z kasper $
 */
public class SingleOperation {
    public int invokeCount;

    @ManagedOperation
    public void method1() {
        invokeCount++;
    }

}
