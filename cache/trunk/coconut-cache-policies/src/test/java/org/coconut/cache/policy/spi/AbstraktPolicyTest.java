/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.spi;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstraktPolicyTest {

    @Test
    public void testAddAll() {
        Mock m = new Mock();
        int[] res = m.addAll(Arrays.asList("a", "b", "c"));
        assertTrue(Arrays.equals(new int[] { 0, 1, 2 }, res));
        assertEquals("a", m.list.poll());
        assertEquals("b", m.list.poll());
        assertEquals("c", m.list.poll());
        assertNull(m.list.poll());
    }

    @Test
    public void testEvict() {
        Mock m = new Mock();
        m.list = new LinkedList<String>(Arrays.asList("a", "b", "c", "d","e"));

        List<String> t = m.evict(2);
        assertEquals("a", t.get(0));
        assertEquals("b", t.get(1));
        assertEquals(2, t.size());
        t = m.evictAll();
        assertEquals("c", t.get(0));
        assertEquals("d", t.get(1));
        assertEquals("e", t.get(2));
        assertEquals(3, t.size());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIAE() {
        new Mock().evict(0);
    }
    static class Mock extends AbstractPolicy<String> {

        LinkedList<String> list = new LinkedList<String>();

        int addCount = 0;

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#add(java.lang.Object)
         */
        public int add(String element) {
            list.add(element);
            return addCount++;
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#clear()
         */
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#evictNext()
         */
        public String evictNext() {
            return list.poll();
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#getSize()
         */
        public int getSize() {
            return list.size();
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#peek()
         */
        public String peek() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#peekAll()
         */
        public List<String> peekAll() {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#remove(int)
         */
        public String remove(int index) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#touch(int)
         */
        public void touch(int index) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#update(int,
         *      java.lang.Object)
         */
        public boolean update(int index, String newElement) {
            throw new UnsupportedOperationException();
        }

    }
}
