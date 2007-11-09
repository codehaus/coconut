/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.util;

import org.coconut.test.MockTestCase;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class PolicyDecoratorTest extends MockTestCase {

//    public void testDelegate() throws Exception {
//        Mock m = mock(ReplacementPolicy.class);
//        PolicyDecorator cm = new PolicyDecorator((ReplacementPolicy) m.proxy());
//        delegateTest(cm, m, "add", "clear", "evictNext", "getSize", "peek",
//                "peekAll", "remove", "toString", "touch", "update");
//        assertEquals(m.proxy(), cm.getPolicy());
//
//    }

    public void testNPE() {
        try {
            new PolicyDecorator(null);
            fail("should throw NullPointerException");
        } catch (NullPointerException ok) {
            /* ignore */
        }
    }
}
