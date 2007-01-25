/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.other;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * Tests serialization of a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class Serialization extends CacheTestBundle {

    public static Cache<Integer,String> serializeAndUnserialize(Cache<Integer,String> c) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(20000);
        ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(bout));
        out.writeObject(c);
        out.close();
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
                bin));
        Cache r = (Cache) in.readObject();
        return r;
    }
    
    @Test
    public void testSerialization() throws Exception {
        Cache r = serializeAndUnserialize(c5);
        assertEquals(c5.size(), r.size());
        assertTrue(c5.equals(r));
        assertTrue(r.equals(c5));
    }
    
    @Test(expected = NotSerializableException.class)
    public void testNotSerializable() throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new ByteArrayOutputStream(10)));
        try {
            Cache c = c0;
            c.put(1, new Object());
        } catch (UnsupportedOperationException e) {
            // put not supported by this type of cache.
            // we will just ignore the test then...
            throw new NotSerializableException();
        }

        out.writeObject(c0);
    }

}
