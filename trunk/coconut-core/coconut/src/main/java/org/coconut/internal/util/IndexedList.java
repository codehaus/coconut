/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.coconut.annotation.ThreadSafe;

/**
 * A PolicyList
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
// TODO implement equals, hashcode
@ThreadSafe(false)
public class IndexedList<T> implements Serializable {
    protected int threshold;

    protected int currentEntryIndex;

    protected int[] next;

    protected int[] prev;

    protected int[] freeEntries;

    protected T[] data;

    /**
     * @param initialCapacity
     *            the initial capacity for this list, must be bigger then 0.
     * @throws IllegalArgumentException
     *             if <tt>initialCapacity</tt> is less than 1
     */
    @SuppressWarnings("unchecked")
    public IndexedList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be 0 or greater, was "
                    + initialCapacity);
        }

        this.threshold = initialCapacity;
        next = new int[threshold + 1];
        prev = new int[threshold + 1];
        data = (T[]) new Object[threshold + 1];
        freeEntries = new int[threshold + 1];
        for (int i = 1; i < freeEntries.length; i++) {
            freeEntries[i] = i;
        }
    }

    @SuppressWarnings("unchecked")
    public IndexedList(IndexedList other) {
        threshold = other.threshold;
        currentEntryIndex = other.currentEntryIndex;

        next = new int[other.next.length];
        System.arraycopy(other.next, 0, next, 0, other.next.length);

        prev = new int[other.prev.length];
        System.arraycopy(other.prev, 0, prev, 0, other.prev.length);

        freeEntries = new int[other.freeEntries.length];
        System.arraycopy(other.freeEntries, 0, freeEntries, 0, other.freeEntries.length);

        data = (T[]) new Object[other.data.length];
        System.arraycopy(other.data, 0, data, 0, other.data.length);

    }

    public int getSize() {
        return currentEntryIndex;
    }

    public int add(T newData) {
        if (currentEntryIndex >= threshold - 1)
            resize(threshold * 2);
        int newIndex = freeEntries[++currentEntryIndex];
        this.data[newIndex] = newData;
        next[prev[0]] = newIndex; // update previous tail to point at new
        prev[newIndex] = prev[0]; // new index point at previous tail
        next[newIndex] = 0; // new index point at tail
        prev[0] = newIndex; // tail point at new index
        innerAdd(newIndex);
        return newIndex;
    }

    protected void innerAdd(int index) {
        /* Override to be informed */
    }

    @SuppressWarnings("unchecked")
    private void resize(int newSize) {
        threshold = newSize + 1;
        int[] oldNext = next;
        next = new int[threshold];
        System.arraycopy(oldNext, 0, next, 0, Math.min(oldNext.length, next.length));

        int[] oldPrev = prev;
        prev = new int[threshold];
        System.arraycopy(oldPrev, 0, prev, 0, Math.min(oldPrev.length, prev.length));

        int[] oldFreeEntries = freeEntries;
        freeEntries = new int[threshold];
        System.arraycopy(oldFreeEntries, 0, freeEntries, 0, Math.min(oldFreeEntries.length,
                freeEntries.length));
        for (int i = oldFreeEntries.length - 1; i < freeEntries.length; i++) {
            freeEntries[i] = i;
        }
        Object[] oldData = data;
        data = (T[]) new Object[threshold];
        System.arraycopy(oldData, 0, data, 0, Math.min(oldData.length, data.length));
        innerResize(threshold);
    }

    protected void innerResize(int newThreshold) {
        /* Override to be informed */
    }

    public void touch(int index) {
        if (index < data.length - 1 && data[index] != null) {
            // we do not keep count of access attempts so we only
            // need to modify datastructures if we have 2 or more entries
            if (currentEntryIndex > 1) {
                prev[next[index]] = prev[index]; // update next head pointer
                next[prev[index]] = next[index];
                next[index] = 0;
                next[prev[0]] = index;
                prev[index] = prev[0];
                prev[0] = index;
            }
            innerRefresh(index); // subclasses might want some special
            // treatment
        }
    }

    protected void innerRefresh(int index) {
        /* Override to be informed */
    }

    public T remove(int index) {

        if (index > data.length - 1 || data[index] == null)
            return null;
        int entry = index;
        if (index >= currentEntryIndex) {
            int removeMe = freeEntries[index];
            freeEntries[removeMe] = freeEntries[currentEntryIndex];
            freeEntries[currentEntryIndex--] = index;

        } else {
            freeEntries[index] = freeEntries[currentEntryIndex];
            freeEntries[currentEntryIndex--] = entry;
        }
        prev[next[entry]] = prev[entry]; // update next head pointer
        next[prev[entry]] = next[entry];
        T oldData = data[entry];
        data[entry] = null;
        innerRemove(entry);
        return oldData;
    }

    protected void innerRemove(int index) {
        /* Override to be informed */
    }

    public T remove() {
        if (currentEntryIndex == 0) {
            return null;
        } else {
            int remove = next[0]; // remove head
            freeEntries[currentEntryIndex--] = remove; // recycle entry
            if (currentEntryIndex == 0) { // clear
                next[0] = 0;
                prev[0] = 0;
            } else {
                int nextHead = next[remove];
                prev[nextHead] = 0; // update next head pointer
                next[0] = nextHead; // update head
            }
            T removeMe = data[remove];
            data[remove] = null;
            return removeMe;
        }
    }

    public List<T> peekAll() {
        ArrayList<T> col = new ArrayList<T>(currentEntryIndex);
        int head = next[0];
        while (head != 0) {
            col.add(data[head]);
            head = next[head];
        }
        return col;
    }

    // /CLOVER:OFF
    void print() {
        for (int i = 0; i < threshold; i++) {
            System.out.println(i + " " + next[i] + " " + prev[i] + " " + freeEntries[i] + " Data: "
                    + data[i]);
        }
        int head = next[0];
        String str = "head:";
        while (head != 0) {
            str += head + ", ";
            head = next[head];
        }
        System.out.println(str);
    }

    // /CLOVER:ON

    public T peek() {
        if (currentEntryIndex == 0) {
            return null;
        } else {
            return data[next[0]];
        }
    }

    public T replace(int index, T newElement) {
        T old = data[index];
        data[index] = newElement;
        return old;
    }


    /**
     * Clears the list.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        currentEntryIndex = 0;
        next = new int[threshold];
        prev = new int[threshold];
        freeEntries = new int[threshold];
        data = (T[]) new Object[threshold];
    }
}