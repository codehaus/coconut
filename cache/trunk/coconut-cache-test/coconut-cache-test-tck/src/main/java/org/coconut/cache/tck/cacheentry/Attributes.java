/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.TestUtil.dummy;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class Attributes extends AbstractCacheTCKTest {
// most attributes will be tested in their individual test case - CreationTime.java, ...
    @Test
    public void attributeMap() {
        init(2);
        AttributeMap am = peekEntry(M1).getAttributes();
        assertNotNull(am);
        // Test the map returned from CacheEntry.getAttributes();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void noClear() {
        init(2);
        AttributeMap am = peekEntry(M1).getAttributes();
        am.clear();
    }

    public void noPut() {
        init(2);
        AttributeMap map = peekEntry(M1).getAttributes();
        Attribute key = dummy(Attribute.class);
        try {
            map.putAll(new HashMap());
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            map.put(key, 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            map.putBoolean(key, true);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            map.putByte(key, (byte) 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            map.putChar(key, 'd');
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            map.putDouble(key, 3.4d);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            map.putFloat(key, 123.3f);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            map.putInt(key, 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            map.putLong(key, 34l);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            map.putShort(key, (short) 123);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
    }
}
