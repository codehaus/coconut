/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.predicate.CollectionPredicates.IsTypePredicate;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class PredicatesIsAssignableFromTest  {

    @Test
    public void testFilter() {
        IsTypePredicate filter = Predicates.isType(Number.class);
        assertEquals(Number.class, filter.getFilteredClass());
        assertTrue(filter.evaluate(Integer.valueOf(0)));
        assertTrue(filter.evaluate(Long.valueOf(0)));
        assertFalse(filter.evaluate(new Object()));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        Predicates.isType(null);
    }

}
