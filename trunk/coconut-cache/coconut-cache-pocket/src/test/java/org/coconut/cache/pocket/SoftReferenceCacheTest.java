/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.pocket;

import java.lang.ref.SoftReference;

import org.coconut.internal.util.SimpleEntry;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SoftReferenceCacheTest extends TestCase {

    public void testSoftReferences() {
        SoftReferenceCache<Integer, String> src = new SoftReferenceCache<Integer, String>(
                new IntegerToStringValueLoader());
        src.evicted(new SimpleEntry<Integer, String>(10, "foo"));
        assertEquals("foo", src.get(10));
        assertEquals("E", src.get(5));
    }

    public void testSoftReferencesClear() {
        SoftReferenceCache<Integer, String> src = new SoftReferenceCache<Integer, String>(
                new IntegerToStringValueLoader());
        src.evicted(new SimpleEntry<Integer, String>(10, "foo"));
        src.clear();
        assertNull(src.get(10));
    }

    public void testSoftReferences2() {
        SoftReferenceCache<Integer, String> src = new SoftReferenceCache<Integer, String>(
                (ValueLoader) PocketCaches.nullLoader(), 5);
        for (int i = 0; i < 10; i++) {
            src.put(i, "" + i);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(src.get(i), "" + i);
        }
    }

    // public void testSoftReferencesMemoryLeak() {
    // SoftReferenceCache<Integer, String> src = new SoftReferenceCache<Integer,
    // String>(
    // new IntegerToStringValueLoader(), 50);
    // for (int i = 0; i < 1000000000; i++) {
    // src.put(i, "" + i);
    // }
    // }
}
