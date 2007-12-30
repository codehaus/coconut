/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.spi.AbstractValueTest;
import org.coconut.operations.LongPredicates;
import org.coconut.operations.Ops.Predicate;
import org.junit.Test;

/**
 * Tests the {@link SizeAttribute}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SizeAttributeTest extends AbstractValueTest {

    public SizeAttributeTest() {
        super(SizeAttribute.INSTANCE, NON_NEGATIV_LONGS, NEGATIV_LONGS);
    }

    @Test
    public void filterOnSize() {
        Predicate<AttributeMap> filter = SizeAttribute.filterOnSize(LongPredicates.greaterThen(5));
        assertTrue(filter.evaluate(SizeAttribute.singleton(6)));
        assertFalse(filter.evaluate(SizeAttribute.singleton(5)));
        assertFalse(filter.evaluate(SizeAttribute.singleton(4)));
    }
}
