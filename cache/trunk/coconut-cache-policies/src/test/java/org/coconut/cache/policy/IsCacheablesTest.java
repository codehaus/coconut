/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.coconut.attribute.common.CostAttribute;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.operations.Predicates;
import org.coconut.test.MockTestCase;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class IsCacheablesTest {

    @Test
    public void acceptAll() {
        assertSame(IsCacheables.ACCEPT_ALL, IsCacheables.acceptAll());
        assertTrue(IsCacheables.ACCEPT_ALL.isCacheable(1, 2, AttributeMaps.EMPTY_MAP));
        TestUtil.assertIsSerializable(IsCacheables.ACCEPT_ALL);
    }

    @Test
    public void fromAttributeMapPredicate() {
        AttributeMap nono = MockTestCase.mockDummy(AttributeMap.class);
        IsCacheable ic = IsCacheables.fromAttributeMapPredicate(Predicates
                .isSame(AttributeMaps.EMPTY_MAP));
        assertTrue(ic.isCacheable(0, 123, AttributeMaps.EMPTY_MAP));
        assertFalse(ic.isCacheable(0, 123, nono));
        TestUtil.assertIsSerializable(ic);
    }

    @Test(expected = NullPointerException.class)
    public void fromAttributeMapPredicateNPE() {
        IsCacheables.fromAttributeMapPredicate(null);
    }

    @Test
    public void fromKeyPredicate() {
        IsCacheable ic = IsCacheables.fromKeyPredicate(Predicates.anyEquals(1, 2));
        assertFalse(ic.isCacheable(0, 123, AttributeMaps.EMPTY_MAP));
        assertTrue(ic.isCacheable(1, "12", AttributeMaps.EMPTY_MAP));
        assertTrue(ic.isCacheable(2, 1, AttributeMaps.EMPTY_MAP));
        assertFalse(ic.isCacheable(3, 3, AttributeMaps.EMPTY_MAP));

        TestUtil.assertIsSerializable(ic);
    }

    @Test(expected = NullPointerException.class)
    public void fromKeyPredicateNPE() {
        IsCacheables.fromKeyPredicate(null);
    }

    @Test
    public void fromValuePredicate() {
        IsCacheable ic = IsCacheables.fromValuePredicate(Predicates.anyEquals(1, 2));
        assertFalse(ic.isCacheable(0, 0, AttributeMaps.EMPTY_MAP));
        assertTrue(ic.isCacheable("1", 1, AttributeMaps.EMPTY_MAP));
        assertTrue(ic.isCacheable(23, 2, AttributeMaps.EMPTY_MAP));
        assertFalse(ic.isCacheable(13, 3, AttributeMaps.EMPTY_MAP));

        TestUtil.assertIsSerializable(ic);
    }

    @Test(expected = NullPointerException.class)
    public void fromValuePredicateNPE() {
        IsCacheables.fromValuePredicate(null);
    }

    @Test
    public void maximumSize() {
        IsCacheable i = IsCacheables.maximumSize(5);
        assertTrue(i.isCacheable(1, 2, SizeAttribute.singleton(4)));
        assertTrue(i.isCacheable(1, 2, SizeAttribute.singleton(5)));
        assertFalse(i.isCacheable(1, 2, SizeAttribute.singleton(6)));

        TestUtil.assertIsSerializable(i);
    }

    @Test(expected = IllegalArgumentException.class)
    public void maximumSizeIAE() {
        IsCacheables.maximumSize(-1);
    }

    @Test
    public void minimumCost() {
        IsCacheable i = IsCacheables.minimumCost(5);
        assertFalse(i.isCacheable(1, 2, CostAttribute.singleton(4)));
        assertTrue(i.isCacheable(1, 2, CostAttribute.singleton(5)));
        assertTrue(i.isCacheable(1, 2, CostAttribute.singleton(6)));

        TestUtil.assertIsSerializable(i);
        IsCacheables.minimumCost(-120);// do not fail
    }

    @Test
    public void rejectAll() {
        assertSame(IsCacheables.REJECT_ALL, IsCacheables.rejectAll());
        assertFalse(IsCacheables.REJECT_ALL.isCacheable(1, 2, AttributeMaps.EMPTY_MAP));
        TestUtil.assertIsSerializable(IsCacheables.REJECT_ALL);
    }
}
