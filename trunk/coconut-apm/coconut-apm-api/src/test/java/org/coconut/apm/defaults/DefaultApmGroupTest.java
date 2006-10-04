/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.defaults;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultApmGroupTest extends TestCase {
    public void testConstructor() {
        new DefaultGroup("foo");
        new DefaultGroup("foo.asd");
        new DefaultGroup("foo.asd.sad.asd.asd.qe24.24wer");
        //test illegal
        //DefaultGroup dg3 = new DefaultGroup("foo.asd.");
    }
}
