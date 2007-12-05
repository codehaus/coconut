/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.AssertionFailedError;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class TestUtil {

    public static Object serializeAndUnserialize(Object o) {
        try {
            return readWrite(o);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionFailedError("not serialiable");
        }
    }

    public static void assertNotSerializable(Object o) {
        try {
            readWrite(o);
        } catch (NotSerializableException nse) {
            // ignore
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionFailedError("Unknown exception");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new AssertionFailedError("Unknown exception");
        }
    }

    public static void assertIsSerializable(Object o) {
        try {
            readWrite(o);
            // TODO test has serializableID
        } catch (IOException e) {
            throw new AssertionFailedError("class " + o.getClass() + " not serializable");
        } catch (ClassNotFoundException e) {
            // should not happen
            e.printStackTrace();
            throw new AssertionFailedError("class " + o.getClass()
                    + " not serializable, this is strange");
        }
    }

    static Object readWrite(Object o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(20000);
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
        out.writeObject(o);
        out.close();
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
        return in.readObject();
    }
}
