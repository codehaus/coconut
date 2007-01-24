/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.query;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheQueryBundle extends CacheTestBundle {

    @Test()
    public void testNothing() {
        
    }
//    
//    @Test(expected = NullPointerException.class)
//    public void testPutAllNull() {
//        c0.query(null);
//    }
//
//    @Test
//    public void testQuery() {
//        c = c5;
//        CacheQuery<Integer, String> cq = CacheFilters.queryByKey(c5, ComparisonFilters.anyEquals(2, 4));
//        assertNotNull(cq);
//        assertEquals(0, cq.getCurrentIndex());
//        assertEquals(2, cq.getTotalCount());
//        assertNotNull(cq.iterator());
//    }
//
//    @Test
//    public void testIterator() {
//        c = c5;
//        CacheQuery<Integer, String> cq = CacheFilters.queryByKey(c5, ComparisonFilters.anyEquals(2, 4));
//        CacheEntry<Integer, String> c2 = null;
//        CacheEntry<Integer, String> c4 = null;
//        int count = 0;
//        for (Iterator<CacheEntry<Integer, String>> iter = cq.iterator(); iter.hasNext();) {
//            count++;
//            CacheEntry<Integer, String> tmp = iter.next();
//            if (tmp.getKey().equals(2)) {
//                c2 = tmp;
//            } else {
//                c4 = tmp;
//            }
//
//        }
//        assertEquals(2, count);
//        assertEquals(2, c2.getKey().intValue());
//        assertEquals(4, c4.getKey().intValue());
//        assertEquals("B", c2.getValue());
//        assertEquals("D", c4.getValue());
//    }
//    
//    @Test(expected = NoSuchElementException.class)
//    public void testIteratorFail() {
//        c = c5;
//        CacheQuery<Integer, String> cq = CacheFilters.queryByKey(c5, ComparisonFilters.anyEquals(2, 4));
//        Iterator<CacheEntry<Integer, String>> iter = cq.iterator();
//        iter.next();
//        iter.next();
//        assertFalse(iter.hasNext());
//        iter.next();
//    }
}
