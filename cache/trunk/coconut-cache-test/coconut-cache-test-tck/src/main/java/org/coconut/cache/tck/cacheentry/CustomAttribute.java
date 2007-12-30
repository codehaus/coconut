package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.attributes.StringAttribute1;
import org.coconut.cache.test.attributes.StringAttribute2;
import org.junit.Test;

public class CustomAttribute extends AbstractCacheTCKTest {

    /**
     * Tests that put overrides the cost of an existing item.
     */
    @Test
    public void customAttribute() {
        tl.add(M1, StringAttribute1.INSTANCE, "foo");
        assertEquals("foo", getAttributes(M1).get(StringAttribute1.INSTANCE));
        assertEquals("foo", peekAttributes(M1).get(StringAttribute1.INSTANCE));
        assertNull(peekAttributes(M1).get(StringAttribute2.INSTANCE));
        assertEquals("fooa", peekAttributes(M1).get(StringAttribute2.INSTANCE, "fooa"));
    }

    /**
     * Tests that put overrides the cost of an existing item.
     */
    @Test
    public void customAttribute2() {
        tl.add(M1, StringAttribute1.INSTANCE, "foa", StringAttribute2.INSTANCE, "fob");
        assertEquals("foa", getAttributes(M1).get(StringAttribute1.INSTANCE));
        assertEquals("fob", getAttributes(M1).get(StringAttribute2.INSTANCE));
        assertEquals("foa", peekAttributes(M1).get(StringAttribute1.INSTANCE));
        assertEquals("fob", peekAttributes(M1).get(StringAttribute2.INSTANCE));
    }
}
