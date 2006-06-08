package org.coconut.filter.matcher;

import junit.framework.JUnit4TestAdapter;

import org.coconut.filter.Filters;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
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
