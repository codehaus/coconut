/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.codehaus.cake.ops.Ops.Predicate;
import org.junit.Test;

/**
 * Tests {@link StringPredicates}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: StringPredicatesTest.java 587 2008-02-06 08:21:44Z kasper $
 */
public class StringPredicatesTest {

    @Test
    public void contains() {
        Predicate<String> p = StringOps.contains("foo");
        assertNotNull(p);
        assertTrue(p.op("foo"));
        assertTrue(p.op("bfoo"));
        assertTrue(p.op("foofff"));
        assertFalse(p.op("fofofofo"));
        assertEquals(StringOps.contains("foo"), p);
        assertEquals(StringOps.contains("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test(expected = NullPointerException.class)
    public void containsNPE() {
        StringOps.contains(null);
    }

    @Test
    public void startsWith() {
        Predicate<String> p = StringOps.startsWith("foo");
        assertNotNull(p);
        assertTrue(p.op("foo"));
        assertFalse(p.op("bfoo"));
        assertTrue(p.op("foofff"));
        assertEquals(StringOps.startsWith("foo"), p);
        assertEquals(StringOps.startsWith("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test
    public void endsWith() {
        Predicate<String> p = StringOps.endsWith("foo");
        assertNotNull(p);
        assertTrue(p.op("foo"));
        assertFalse(p.op("foob"));
        assertTrue(p.op("ffffoo"));
        assertEquals(StringOps.endsWith("foo"), p);
        assertEquals(StringOps.endsWith("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test(expected = NullPointerException.class)
    public void startsWithNPE() {
        StringOps.startsWith(null);
    }

    @Test(expected = NullPointerException.class)
    public void endsWithNPE() {
        StringOps.endsWith(null);
    }

    @Test
    public void equalsIgnoreCase() {
        Predicate<String> p = StringOps.equalsToIgnoreCase("foo");
        assertNotNull(p);
        assertTrue(p.op("foo"));
        assertFalse(p.op("boo"));
        assertTrue(p.op("Foo"));
        assertTrue(p.op("FOO"));
        assertEquals(StringOps.equalsToIgnoreCase("foo"), p);
        assertEquals(StringOps.equalsToIgnoreCase("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test(expected = NullPointerException.class)
    public void equalsIgnoreCaseNPE() {
        StringOps.equalsToIgnoreCase(null);
    }
}
