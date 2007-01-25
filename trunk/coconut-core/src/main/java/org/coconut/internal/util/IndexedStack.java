/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.coconut.annotation.ThreadSafe;

/**
 * A PolicyStack
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@ThreadSafe(false)
public class IndexedStack<T> implements Serializable{
    private int threshold;

    private int currentEntryIndex;

    private int[] next;

    private int[] prev; // double linked to allow in-the-middle removals

    private int[] freeEntries;

    private T[] data;

    /**
     * Creates a new PolicyStack by copying an existing.
     * 
     * @param stack
     *            the stack to copy
     */
    @SuppressWarnings("unchecked")
    public IndexedStack(IndexedStack stack) {
        this.threshold = stack.threshold;
        this.currentEntryIndex = stack.currentEntryIndex;

        next = new int[stack.next.length];
        System.arraycopy(stack.next, 0, next, 0, stack.next.length);

        prev = new int[stack.prev.length];
        System.arraycopy(stack.prev, 0, prev, 0, stack.prev.length);

        freeEntries = new int[stack.freeEntries.length];
        System.arraycopy(stack.freeEntries, 0, freeEntries, 0,
                stack.freeEntries.length);

        data = (T[]) new Object[stack.data.length];
        System.arraycopy(stack.data, 0, data, 0, stack.data.length);
    }

    @SuppressWarnings("unchecked")
    public IndexedStack(int initialCapacity) {
        this.threshold = initialCapacity;
        next = new int[initialCapacity + 1];
        prev = new int[initialCapacity + 1];
        data = (T[]) new Object[initialCapacity + 1];
        freeEntries = new int[initialCapacity + 1];
        for (int i = 1; i < freeEntries.length; i++) {
            freeEntries[i] = i;
        }
    }

    public int getSize() {
        return currentEntryIndex;
    }

    public int add(T element) {
        if (currentEntryIndex >= threshold - 1)
            resize(threshold * 2);
        int newIndex = freeEntries[++currentEntryIndex];
        this.data[newIndex] = element;
        prev[next[0]] = newIndex; // update previous tail to point at new
        prev[newIndex] = 0; // new index point at previous tail
        next[newIndex] = next[0]; // new index point at tail
        next[0] = newIndex; // head point at new index
        return newIndex;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newSize) {
        threshold = newSize + 1;
        int[] oldNext = next;
        next = new int[threshold];
        System.arraycopy(oldNext, 0, next, 0, Math.min(oldNext.length,
                next.length));

        int[] oldPrev = prev;
        prev = new int[threshold];
        System.arraycopy(oldPrev, 0, prev, 0, Math.min(oldPrev.length,
                prev.length));

        int[] oldFreeEntries = freeEntries;
        freeEntries = new int[threshold];
        System.arraycopy(oldFreeEntries, 0, freeEntries, 0, Math.min(
                oldFreeEntries.length, freeEntries.length));
        for (int i = oldFreeEntries.length - 1; i < freeEntries.length; i++) {
            freeEntries[i] = i;
        }
        Object[] oldData = data;
        data = (T[]) new Object[threshold];
        System.arraycopy(oldData, 0, data, 0, Math.min(oldData.length,
                data.length));
    }

    public void touch(int index) {
        if (index < data.length - 1 && data[index] != null) {
            // we do not keep count of access attempts so we only
            // need to modify datastructures if we have 2 or more entries
            if (currentEntryIndex > 1) {
                prev[next[index]] = prev[index]; // update next head pointer
                next[prev[index]] = next[index];
                prev[index] = 0;

                int n0 = next[0]; // list dif
                prev[n0] = index; // list dif
                next[index] = n0; // list dif

                next[0] = index;
            }
        }
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
        return oldData;
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

    public T peek() {
        if (currentEntryIndex == 0) {
            return null;
        } else {
            return peekAll().get(0); // TODO optimize
        }
    }

    public List<T> peekAll() {
        ArrayList<T> col = new ArrayList<T>(getSize());
        int head = next[0];
        while (head != 0) {
            col.add(data[head]);
            head = next[head];
        }
        return col;
    }

    public T replace(int index, T newElement) {
        T old = data[index];
        data[index] = newElement;
        return old;
    }
    
    ///CLOVER:OFF
    void print() {
        for (int i = 0; i < currentEntryIndex; i++) {
            System.out.println(i + " " + next[i] + " " + prev[i] + " "
                    + freeEntries[i]);
        }
        int head = next[0];
        StringBuilder sb=new StringBuilder();
        sb.append("head:");
        while (head != 0) {
            sb.append(head);
            sb.append(", ");
            head = next[head];
        }
        System.out.println(sb.toString());
    }
    ///CLOVER:ON
}
