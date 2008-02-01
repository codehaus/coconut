/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.test.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.AssertionFailedError;

import org.jmock.Mockery;

public class TestUtil {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @SuppressWarnings("unchecked")
    public static <V> V dummy(Class<V> arg) {
        return new Mockery().mock(arg);
    }

    public static <T>  T serializeAndUnserialize(T o) {
        try {
            return (T) readWrite(o);
        } catch (Exception e) {
            throw new AssertionFailedError(o + " not serializable");
        }
    }

    public static void assertNotSerializable(Object o) {
        try {
            readWrite(o);
            throw new AssertionFailedError(o + " is serializable");
        } catch (NotSerializableException nse) {/* ok */}
    }

    public static void assertIsSerializable(Object o) {
        try {
            readWrite(o);
        } catch (NotSerializableException e) {
            throw new AssertionFailedError(o + " not serializable");
        }
    }

    static Object readWrite(Object o) throws NotSerializableException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(20000);
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
            out.writeObject(o);
            out.close();
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
            return in.readObject();
        } catch (NotSerializableException nse) {
            throw nse; //NotSerializableException extends IOException which we catch later
        } catch (ClassNotFoundException e) {
            throw new Error(e);// should'nt happen
        } catch (IOException e) {
            throw new Error(e);// should'nt happen
        }
    }
}
