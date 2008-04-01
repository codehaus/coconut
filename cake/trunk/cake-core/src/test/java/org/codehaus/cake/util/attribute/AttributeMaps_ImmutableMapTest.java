package org.codehaus.cake.util.attribute;

import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class AttributeMaps_ImmutableMapTest extends AbstractAttributeMapTest {

    private final static Attribute KEY = new Attribute("key", Integer.class, 5) {};

    private final static Object VALUE = "10";

    private AttributeMap immutable;

    AttributeMap map;

    @Override
    @Before
    public void setup() {
        super.setup();
        map = new DefaultAttributeMap();
        map.put(KEY, VALUE);
        immutable = Attributes.unmodifiableAttributeMap(map);
    }

    @Test(expected = NullPointerException.class)
    public void unmodifiableAttributeMapNPE() {
        Attributes.unmodifiableAttributeMap(null);
    }

    @Test
    public void unmodifiableAttributeMap() {
        for (Attribute a : map.keySet()) {
            assertTrue(immutable.containsKey(a));
        }
        assertFalse(immutable.containsKey(new Attribute("no",Integer.class, 6){}));
        for (Object a : map.values()) {
            assertTrue(immutable.containsValue(a));
        }
        assertFalse(immutable.containsValue(new Object()));

        assertEquals(immutable.entrySet(), map.entrySet());

        try {
            immutable.entrySet().clear();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        assertEquals(immutable, map);
        assertEquals(map, immutable);
        assertEquals(immutable, immutable);
        assertEquals(immutable.hashCode(), map.hashCode());
        assertEquals(immutable.isEmpty(), map.isEmpty());
        assertTrue(new Attributes.ImmutableAttributeMap(new DefaultAttributeMap()).isEmpty());
        assertEquals(immutable.keySet(), map.keySet());
        try {
            immutable.keySet().clear();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        assertEquals(immutable.size(), map.size());
        assertEquals(0, new Attributes.ImmutableAttributeMap(new DefaultAttributeMap()).size());
        assertEquals(new HashSet(immutable.values()), new HashSet(map.values()));
        assertEquals(immutable.toString(), map.toString());
        try {
            immutable.values().clear();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
    }

    @Test
    public void unsupportedOperations() {
        try {
            immutable.clear();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        try {
            immutable.remove(KEY);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */
        }
        AbstractAttributeMapTest.noPut(immutable, KEY);
    }

    @Override
    protected AttributeMap create() {
        return new DefaultAttributeMap();
    }

    @Override
    public void clear() {
    // ignore
    }

    @Override
    void mappedPutted() {
        m = new Attributes.ImmutableAttributeMap(m);
    }

    /**
     * Tests that ImmutableAttributeMap map is serializable
     * 
     * @throws Exception
     *             something went wrong
     */
    @Test
    public void serialization() throws Exception {
        assertIsSerializable(new Attributes.ImmutableAttributeMap(Attributes.singleton(KEY, 123)));
    }
}
