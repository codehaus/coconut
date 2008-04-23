package org.codehaus.cake.attribute;

import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AttributeMaps_ImmutableMapTest extends AbstractAttributeMapTest {

    private final static ObjectAttribute KEY = new ObjectAttribute("key", Integer.class, 5) {};

    private final static Object VALUE = "10";

    private AttributeMap realMap;

    protected AttributeMap create() {
        return new DefaultAttributeMap();
    }

    @Test
    public void noPutClearRemove() {
        super.assertImmutable();
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

    @Before
    public void setup() {
        realMap = new DefaultAttributeMap();
        realMap.put(KEY, VALUE);
        map = Attributes.unmodifiableAttributeMap(realMap);
    }

    @Test
    public void unmodifiableAttributeMap() {
        for (Attribute a : realMap.attributeSet()) {
            assertTrue(map.contains(a));
        }
        assertFalse(map.contains(new ObjectAttribute("no", Integer.class, 6) {}));
        assertEquals(true, map.get(B_TRUE));
        assertEquals((byte) 2, map.get(B_2));
        assertEquals((char) 2, map.get(C_2));
        assertEquals(2.5d, map.get(D_2), 0);
        assertEquals((float) 2.5, map.get(F_2), 0);
        assertEquals(2, map.get(I_2));
        assertEquals(2L, map.get(L_2));
        assertEquals("15", map.get(O_2));
        assertEquals((short) 2, map.get(S_2));

        assertEquals(false, map.get(B_TRUE, false));
        assertEquals((byte) 3, map.get(B_2, (byte) 3));
        assertEquals((char) 3, map.get(C_2, (char) 3));
        assertEquals(3.5d, map.get(D_2, 3.5d), 0);
        assertEquals((float) 3.5, map.get(F_2, 3.5f), 0);
        assertEquals(3, map.get(I_2, 3));
        assertEquals(3L, map.get(L_2, 3));
        assertEquals("25", map.get(O_2, "25"));
        assertEquals((short) 3, map.get(S_2, (short) 3));

        // for (Object a : map.values()) {
        // assertTrue(immutable.containsValue(a));
        // }
        // assertFalse(immutable.containsValue(new Object()));
        //
        // assertEquals(immutable.entrySet(), map.entrySet());
        //
        // try {
        // immutable.entrySet().clear();
        // fail("should throw UnsupportedOperationException");
        // } catch (UnsupportedOperationException ok) {/* ok */
        // }
        assertEquals(map, realMap);
        assertEquals(realMap, map);
        assertEquals(map, map);
        assertEquals(map.hashCode(), realMap.hashCode());
        assertEquals(map.isEmpty(), realMap.isEmpty());
        assertTrue(new Attributes.ImmutableAttributeMap(new DefaultAttributeMap()).isEmpty());
        assertEquals(map.attributeSet(), realMap.attributeSet());
        assertEquals(map.size(), realMap.size());
        assertTrue(realMap.values().containsAll(map.values()));
        assertTrue(map.values().containsAll(realMap.values()));
        assertEquals(map.toString(), realMap.toString());
        assertEquals(0, new Attributes.ImmutableAttributeMap(new DefaultAttributeMap()).size());
        // assertEquals(new HashSet(immutable.values()), new HashSet(map.values()));
        // assertEquals(immutable.toString(), map.toString());
        // try {
        // immutable.values().clear();
        // fail("should throw UnsupportedOperationException");
        // } catch (UnsupportedOperationException ok) {/* ok */
        // }
    }

    @Test(expected = NullPointerException.class)
    public void unmodifiableAttributeMapNPE() {
        Attributes.unmodifiableAttributeMap(null);
    }
}
