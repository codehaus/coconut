/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter.matcher;

import java.util.List;
import java.util.Map;

import org.coconut.filter.Filter;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstraktFilterMatcherTest extends MockTestCase {

    public void testMock() throws Exception {
        Mock m = mock(Map.class);
        DummyMatcher cm = new DummyMatcher((Map) m.proxy());
        delegateTest(cm, m, "size", "isEmpty", "containsKey", "containsValue", "putAll",
                "clear", "keySet", "values", "entrySet", "equals", "hashCode", "toString");
        Filter f = mockDummy(Filter.class);
        Filter f2 = mockDummy(Filter.class);

        m.expects(once()).method("get").with(eq(1)).will(returnValue(f));
        m.expects(once()).method("put").with(eq(2), eq(f2)).will(returnValue(f));
        m.expects(once()).method("remove").with(eq(3)).will(returnValue(f));
        assertEquals(f, cm.get(1));
        assertEquals(f, cm.put(2, f2));
        assertEquals(f, cm.remove(3));
        assertEquals(m.proxy(), cm.getMap());
    }

    static class DummyMatcher<K, V> extends AbstractFilterMatcher<K, V> {
        DummyMatcher(Map m) {
            super(m);
        }

        /**
         * @see org.coconut.filter.matcher.FilterMatcher#match(java.lang.Object)
         */
        public List<K> match(V object) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.filter.matcher.AbstractFilterMatcher#getMap()
         */
        @Override
        protected Map<K, Filter<? super V>> getMap() {
            return super.getMap();
        }
        
        
    }

}
