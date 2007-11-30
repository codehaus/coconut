/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

import java.util.Random;

import org.junit.Test;

/**
 * Various String utils.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class IndexedListTest {

    private final Random rnd = new Random(123456432);

    @Test
    public void testAdd() {
        IndexedList<Integer> list = new IndexedList<Integer>(5);
        list.add(1);
        assertEquals(1, list.getSize());
        list.add(2);
        assertEquals(2, list.getSize());
        list.add(3);
        assertEquals(3, list.getSize());
    }

    @Test
    public void testResize() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        assertEquals(7, list.getSize());
    }

    @Test
    public void testRemove() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(1, list.removeFirst().intValue());
        assertEquals(2, list.getSize());

        assertEquals(2, list.removeFirst().intValue());
        assertEquals(1, list.getSize());

        assertEquals(3, list.removeFirst().intValue());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testRemoveEmpty() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        assertNull(list.removeFirst());
    }

    @Test
    public void testRefresh() {
        IndexedList<Integer> list = new IndexedList<Integer>(5);
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

        assertEquals(2, list.removeFirst().intValue());
        assertEquals(4, list.removeFirst().intValue());
        assertEquals(3, list.removeFirst().intValue());
        assertEquals(1, list.removeFirst().intValue());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testRefreshNonExistingIndex() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        list.add(1);
        list.touch(300);
        assertEquals(1, list.removeFirst().intValue());
    }

    @Test
    public void testRefreshOneElement() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        int i1 = list.add(1);
        list.touch(i1);
        assertEquals(1, list.removeFirst().intValue());
    }

    @Test
    public void testRemoveIndex() {
        IndexedList<Integer> list = new IndexedList<Integer>(15);
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

        assertEquals(2, list.removeFirst().intValue());
        assertEquals(4, list.removeFirst().intValue());
        assertEquals(5, list.removeFirst().intValue());
        assertEquals(6, list.removeFirst().intValue());
        assertEquals(8, list.removeFirst().intValue());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testRemoveNonExistingIndex() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);

        list.add(1);
        assertNull(list.remove(3));
    }

    @Test
    public void testIntens() {
        IndexedList<Integer> list = getDirtyList();

        list.add(1);
        assertEquals(1, list.getSize());
        list.add(2);
        assertEquals(2, list.getSize());
        list.add(3);
        assertEquals(3, list.getSize());
        list.add(4);
        assertEquals(4, list.getSize());

        assertEquals(1, list.removeFirst().intValue());
        assertEquals(3, list.getSize());
        assertEquals(2, list.removeFirst().intValue());
        assertEquals(2, list.getSize());
        assertEquals(3, list.removeFirst().intValue());
        assertEquals(1, list.getSize());
        assertEquals(4, list.removeFirst().intValue());
        assertEquals(0, list.getSize());
    }

    @Test
    public void replace() {
        IndexedList<Integer> list = new IndexedList<Integer>(15);
        int i = list.add(1);
        list.replace(i, 2);
        assertEquals(2, list.peek());
    }

    @Test(expected = IllegalArgumentException.class)
    public void indexedList() {
        new IndexedList<Integer>(-1);
    }

    @Test
    public void copyConstructor() {
        IndexedList<Integer> list = new IndexedList<Integer>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
            permuteRefresh(list);
        }
        IndexedList<Integer> copy = new IndexedList<Integer>(list);
        assertEquals(copy.peekAll(), list.peekAll());
        assertEquals(copy.peekAll(), list.clone().peekAll());
    }

    @Test
    public void testPeek() {
        IndexedList<Integer> list = new IndexedList<Integer>(15);
        assertNull(list.peek());
        list.add(1);
        list.add(2);
        assertEquals(1, list.peek().intValue());
        assertEquals(1, list.removeFirst().intValue());
        assertEquals(2, list.peek().intValue());
        assertEquals(2, list.removeFirst().intValue());
    }

    @Test
    public void clear() {
        IndexedList<Integer> l2 = new IndexedList<Integer>(20);
        for (int i = 0; i < 100; i++) {
            l2.add(i);
        }
        l2.clear();
        assertEquals(0, l2.getSize());
    }

    @Test
    public void equalsHashcodeToString() {

        IndexedList<Integer> l1 = new IndexedList<Integer>(10);
        IndexedList<Integer> l2 = new IndexedList<Integer>(20);
        assertFalse(l1.equals(new Object()));
        for (int i = 0; i < 100; i++) {
            l1.add(i);
            l2.add(i);
        }
        l1.touch(55);
        l2.touch(55);
        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
        assertEquals(l1.toString(), l2.toString());
    }

    @Test
    public void testPeekAll() {
        IndexedList<Integer> list = new IndexedList<Integer>(15);
        assertEquals(0, list.peekAll().size());
        list.add(1);
        list.add(2);
        assertEquals(1, list.peekAll().get(0));
        assertEquals(2, list.peekAll().get(1));
        list.touch(1);
        assertEquals(2, list.peekAll().get(0));
        assertEquals(1, list.peekAll().get(1));
    }

    private IndexedList<Integer> getDirtyList() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        permuteRefresh(list);

        // list.print();
        for (int i = 0; i < 400; i++) {
            Integer data = list.removeFirst();
            list.add(data);
            if (i % 20 == 0)
                list.add((i / 20 + 20));
        }
        while (list.removeFirst() != null) {/* don't do anything */
        }
        // list.print();
        return list;
    }

    private void permuteRefresh(IndexedList<Integer> list) {
        int iterations = rnd.nextInt(1000);
        for (int i = 0; i < iterations; i++) {
            int e = rnd.nextInt(list.getSize() + 5);
            list.touch(e);
        }
    }
}
