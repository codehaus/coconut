package org.coconut.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Random;

import org.coconut.test.MavenDummyTest;
import org.junit.Test;

/**
 * Test of PolicyList.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class IndexedListTest extends MavenDummyTest {

    private final Random rnd = new Random();

//    public static junit.framework.Test suite() {
//        return new JUnit4TestAdapter(IndexedListTest.class);
//    }

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

        assertEquals(1, list.remove());
        assertEquals(2, list.getSize());

        assertEquals(2, list.remove());
        assertEquals(1, list.getSize());

        assertEquals(3, list.remove());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testRemoveEmpty() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        assertNull(list.remove());
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

        assertEquals(2, list.remove());
        assertEquals(4, list.remove());
        assertEquals(3, list.remove());
        assertEquals(1, list.remove());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testRefreshNonExistingIndex() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        list.add(1);
        list.touch(300);
        assertEquals(1, list.remove());
    }

    @Test
    public void testRefreshOneElement() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        int i1 = list.add(1);
        list.touch(i1);
        assertEquals(1, list.remove());
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

        assertEquals(3, list.remove(i3));
        assertEquals(7, list.remove(i7));
        assertEquals(1, list.remove(i1));
        assertEquals(9, list.remove(i9));

        assertEquals(2, list.remove());
        assertEquals(4, list.remove());
        assertEquals(5, list.remove());
        assertEquals(6, list.remove());
        assertEquals(8, list.remove());
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

        assertEquals(1, list.remove());
        assertEquals(3, list.getSize());
        assertEquals(2, list.remove());
        assertEquals(2, list.getSize());
        assertEquals(3, list.remove());
        assertEquals(1, list.getSize());
        assertEquals(4, list.remove());
        assertEquals(0, list.getSize());
    }

    @Test
    public void testPeek() {
        IndexedList<Integer> list = new IndexedList<Integer>(15);
        assertNull(list.peek());
        list.add(1);
        list.add(2);
        assertEquals(1, list.peek());
        assertEquals(1, list.remove());
        assertEquals(2, list.peek());
        assertEquals(2, list.remove());
    }
    
    private IndexedList<Integer> getDirtyList() {
        IndexedList<Integer> list = new IndexedList<Integer>(1);
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        permuteRefresh(list);

        // list.print();
        for (int i = 0; i < 400; i++) {
            Integer data = list.remove();
            list.add(data);
            if (i % 20 == 0)
                list.add((i / 20 + 20));
        }
        while (list.remove() != null) {/* don't do anything */
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
