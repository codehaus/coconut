/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event;

import java.util.concurrent.Callable;

import org.coconut.event.Events;
import org.coconut.test.MockTestCase;
import org.coconut.test.TestUtil;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class BusTest extends MockTestCase {

    public void testStaticCallable() throws Exception {
        Integer i = 1;
        Callable<Integer> c = Events.asCallable(i);
        assertSame(i, c.call());
        TestUtil.assertIsSerializable(c);
    }
}
