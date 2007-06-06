/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.other;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import org.coconut.cache.tck.CommonCacheTestBundle;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class NoSerialization extends CommonCacheTestBundle {
    @Test(expected = NotSerializableException.class)
    public void testNotSerializable() throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new ByteArrayOutputStream(10)));
        out.writeObject(c0);
    }
}
