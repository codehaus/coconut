/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.matcher;

import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.Filters;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: TypeMatcherTest.java 36 2006-08-22 09:59:45Z kasper $
 */
public class TypeMatcherTest {

    @Test(expected = NullPointerException.class)
    public void addNullFilter() {
        TypeMatcher<Number,Number> tm = new TypeMatcher<Number,Number>();
        tm.add(null, Filters.IS_NUMBER);
    }

    @Test(expected = NullPointerException.class)
    public void addKeyNull() {
        TypeMatcher<Object, Object> tm = new TypeMatcher<Object, Object>();
        tm.add(new Object(), null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TypeMatcherTest.class);
    }
}
