/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.test;

import org.jmock.Mockery;

public class MockTestCase {

    @SuppressWarnings("unchecked")
    public static <V> V mockDummy(Class<V> arg) {
        return new Mockery().mock(arg);
    }

}
