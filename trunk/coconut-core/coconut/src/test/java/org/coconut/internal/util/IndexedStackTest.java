/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Random;

import junit.framework.JUnit4TestAdapter;

import org.coconut.test.MavenDummyTest;
import org.junit.Test;

/**
 * Test of PolicyStack.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class IndexedStackTest extends MavenDummyTest {

    private final Random rnd = new Random();

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(IndexedStackTest.class);
    }

    @Test
    public void testAdd() {
        IndexedStack<Integer> stack = new IndexedStack<Integer>(5);
        stack.add(1);
        assertEquals(1, stack.getSize());
        stack.add(2);
        assertEquals(2, stack.getSize());
        stack.add(3);
        assertEquals(3, stack.getSize());
    }

    @Test
    public void testResize() {
        IndexedStack<Integer> stack = new IndexedStack<Integer>(1);
        stack.add(1);
        stack.add(2);
        stack.add(3);
        stack.add(4);
        stack.add(5);
        stack.add(6);
        stack.add(7);
        assertEquals(7, stack.getSize());
    }

    @Test
    public void testRemove() {
        IndexedStack<Integer> list = new IndexedStack<Integer>(1);
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(3, list.remove().intValue());
        assertEquals(2, list.getSize());

        assertEquals(2, list.remove().intValue());
        assertEquals(1, list.getSize());

        assertEquals(1, list.remove().intValue());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testRemoveEmpty() {
        IndexedStack<Integer> list = new IndexedStack<Integer>(1);
        assertNull(list.remove());
    }

    @Test
    public void testRefresh() {
        IndexedStack<Integer> list = new IndexedStack<Integer>(5);
        int i = list.add(1);
        int i2 = list.add(2);
        int i3 = list.add(3);
        int i4 = list.add(4);

        list.touch(i3);
        list.touch(i2);
        list.touch(i);
        list.touch(i);
        list.touch(i4);
        list.touch(i3);
        list.touch(i);
        assertEquals(4, list.getSize());

        assertEquals(1, list.remove().intValue());
        assertEquals(3, list.remove().intValue());
        assertEquals(4, list.remove().intValue());
        assertEquals(2, list.remove().intValue());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testRefreshNonExistingIndex() {
        IndexedStack<Integer> list = new IndexedStack<Integer>(1);
        list.add(1);
        list.touch(300);
        assertEquals(1, list.remove().intValue());
    }

    @Test
    public void testRefreshOneElement() {
        IndexedStack<Integer> list = new IndexedStack<Integer>(1);
        int i1 = list.add(1);
        list.touch(i1);
        assertEquals(1, list.remove().intValue());
    }

    @Test
    public void testRemoveIndex() {
        IndexedStack<Integer> list = new IndexedStack<Integer>(15);
        int i1 = list.add(1);
        list.add(2);
        int i3 = list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        int i7 = list.add(7);
        list.add(8);
        int i9 = list.add(9);

        assertEquals(3, list.remove(i3).intValue());
        assertEquals(7, list.remove(i7).intValue());
        assertEquals(1, list.remove(i1).intValue());
        assertEquals(9, list.remove(i9).intValue());

        assertEquals(8, list.remove().intValue());
        assertEquals(6, list.remove().intValue());
        assertEquals(5, list.remove().intValue());
        assertEquals(4, list.remove().intValue());
        assertEquals(2, list.remove().intValue());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testRemoveNonExistingIndex() {
        IndexedStack<Integer> list = new IndexedStack<Integer>(1);
        list.add(1);
        assertNull(list.remove(3));
    }

    @Test
    public void testPeek() {
        IndexedStack<Integer> list = new IndexedStack<Integer>(15);
        assertNull(list.peek());
        list.add(1);
        list.add(2);
        assertEquals(2, list.peek().intValue());
        assertEquals(2, list.remove().intValue());
        assertEquals(1, list.peek().intValue());
        assertEquals(1, list.remove().intValue());
    }

    @Test
    public void testIntens() {
        IndexedStack<Integer> list = getDirtyList();

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        assertEquals(4, list.getSize());

        assertEquals(4, list.remove().intValue());
        assertEquals(3, list.getSize());
        assertEquals(3, list.remove().intValue());
        assertEquals(2, list.getSize());
        assertEquals(2, list.remove().intValue());
        assertEquals(1, list.getSize());
        assertEquals(1, list.remove().intValue());
        assertEquals(0, list.getSize());
    }

    private IndexedStack<Integer> getDirtyList() {
        IndexedStack<Integer> list = new IndexedStack<Integer>(1);
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        permuteRefresh(list);

        // list.print();
        for (int i = 0; i < 400; i++) {
            Integer data = list.remove();
            list.add(data);
            if (i % 20 == 0)
                list.add(i / 20 + 20);
        }
        while (list.remove() != null) {/* don't do anything */
        }
        // list.print();
        return list;
    }

    private void permuteRefresh(IndexedStack<Integer> list) {
        int iterations = rnd.nextInt(1000);
        for (int i = 0; i < iterations; i++) {
            int e = rnd.nextInt(list.getSize() + 5);
            list.touch(e);
        }
    }
}
