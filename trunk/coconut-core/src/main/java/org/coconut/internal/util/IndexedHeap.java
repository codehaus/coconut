/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

/**
 * For internal use in the various cache policies. This class can be optimized
 * somewhat when swapping elements around. However, be absolutely sure on what
 * you are doing before changing anything. This class is not synchronized in any
 * way.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@NotThreadSafe
public class IndexedHeap<T> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4996450401667126273L;

    /**
     * 
     */
    private int[] ref;

    private int[] refBack;

    /** The priority of each element. */
    private long[] priority;

    /** The objects attached to each element. */
    private T[] data;

    /** The actual number of elements contained in the heap. */
    private int numberOfElements;

    /**
     * Constructors a new heap with an initial size of 100.
     */
    public IndexedHeap() {
        this(100);
    }

    /**
     * Constructs a new heap with a specified initial size.
     * 
     * @param intialSize
     *            the initial sie
     */
    @SuppressWarnings("unchecked")
    public IndexedHeap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(
                    "initialSize must be a positive number or 0");
        }
        priority = new long[initialCapacity + 1];
        ref = new int[initialCapacity + 1];
        refBack = new int[initialCapacity + 1];
        data = (T[]) new Object[initialCapacity + 1];
    }

    /**
     * Constructs a new Heap by copying an existing.
     * 
     * @param h
     *            the heap to copy
     */
    @SuppressWarnings("unchecked")
    public IndexedHeap(IndexedHeap h) {
        numberOfElements = h.numberOfElements;
        priority = new long[h.priority.length];
        System.arraycopy(h.priority, 0, priority, 0, h.priority.length);
        ref = new int[h.ref.length];
        System.arraycopy(h.ref, 0, ref, 0, h.ref.length);
        refBack = new int[h.refBack.length];
        System.arraycopy(h.refBack, 0, refBack, 0, h.refBack.length);
        data = (T[]) new Object[h.data.length];
        System.arraycopy(h.data, 0, data, 0, h.data.length);
    }

    /**
     * Adds an element to heap with a priority of 0.
     * 
     * @param element
     *            the element to add
     * @return a reference that can be used when trying to remove or changing
     *         the priority of the element
     */
    public int add(T element) {
        return innerAdd(element, 0);
    }

    /**
     * Adds an element to heap with a specified priority.
     * 
     * @param element
     *            the element to add
     * @param prio
     *            the priority of the element
     * @return a reference that can be used when trying to remove or changing
     *         the priority of the element
     */
    public int add(T element, long prio) {
        return innerAdd(element, prio);
    }

    public T replace(int index, T newElement) {
        T old = data[ref[index]];
        data[ref[index]] = newElement;
        return old;
    }
    /**
     * Retrieves and removes the top of this heap, or <code>null</code> if
     * this heap is empty
     * 
     * @return the top of the heap, or <code>null</code> if this heap is empty
     */
    public T poll() {
        return heapExtractMin();
    }

    /**
     * Returns the number of elements in this collection.
     * 
     * @return the number of elements in this collection
     */
    public int size() {
        return numberOfElements;
    }

    @SuppressWarnings("unchecked")
    private void setSize(int newlen) {
        Object[] newData = new Object[newlen];
        long[] newA = new long[newlen];
        int[] newReferences = new int[newlen];
        int[] newReferencesBack = new int[newlen];
        System.arraycopy(data, 0, newData, 0, data.length);
        System.arraycopy(priority, 0, newA, 0, priority.length);
        System.arraycopy(ref, 0, newReferences, 0, ref.length);
        System.arraycopy(refBack, 0, newReferencesBack, 0, refBack.length);
        for (int i = priority.length; i < newA.length; i++) {
            newA[i] = i;
            newReferences[i] = -1;
        }
        data = (T[]) newData;
        priority = newA;
        ref = newReferences;
        refBack = newReferencesBack;
    }

    private void grow(int requestedSize) {
        int newlen = priority.length;
        if (requestedSize < newlen) // don't need to grow
            return;
        if (requestedSize == Integer.MAX_VALUE)
            throw new OutOfMemoryError();
        while (newlen <= requestedSize) {
            if (newlen >= Integer.MAX_VALUE / 2) // avoid overflow
                newlen = Integer.MAX_VALUE;
            else
                newlen <<= 1; // Double up baby
        }

        setSize(newlen);
    }

    public T remove(int index) {
        checkIndex(index);
        if (numberOfElements > 0) {
            int i = ref[index];
            long oldKey = priority[i];
            long key = priority[numberOfElements];
            T d = data[i];
            data[i] = null;
            swap(i, numberOfElements);
            // swap something
            numberOfElements--;
            if (key < oldKey) {
                fixUp(i);
            } else if (key > oldKey) {
                minHeapify(i);
            }
            return d;
        } else {
            return null;
        }
    }

    public T peek() {
        if (numberOfElements > 0) {
            return data[1];
        } else {
            return null;
        }
    }
    private T heapExtractMin() {
        if (numberOfElements > 0) {
            T d = data[1];
            data[1] = null;
            swap(1, numberOfElements);
            numberOfElements--;
            minHeapify(1);
            return d;
        } else
            return null;
    }

    // public T peek() {
    // return (T) data[1];
    // }
    // public long peekPrio() {
    // return a[1];
    // }
    private int innerAdd(T d, long prio) {
        numberOfElements++;
        if (numberOfElements >= priority.length)
            grow(numberOfElements);
        priority[numberOfElements] = prio;
        ref[numberOfElements] = numberOfElements;
        refBack[numberOfElements] = numberOfElements;
        data[numberOfElements] = d;
        fixUp(numberOfElements);

        return numberOfElements;
    }

    public long changePriorityDelta(int index, long delta) {
        checkIndex(index);
        int i = ref[index];
        return heapChangeKey(i, priority[i] + delta);
    }

    /**
     * Sets the priority of a single element and fix the heap.
     * 
     * @param index
     *            the index of the element
     * @param newPriority
     *            the new priority of the element
     * @return the old priority of the elements.
     */
    public long setPriority(int index, long newPriority) {
        checkIndex(index);
        int i = ref[index];
        return heapChangeKey(i, newPriority);
    }

    private long heapChangeKey(int i, long key) {
        long oldKey = priority[i];
        if (key == oldKey)
            return oldKey;
        priority[i] = key;
        if (key < oldKey) {
            fixUp(i);
        } else {
            minHeapify(i);
        }
        return oldKey;
    }

    private void fixUp(int i) {
        while (i > 1) {
            int j = i >>> 1;
            if (priority[j] <= priority[i])
                break;
            swap(j, i);
            i = j;
        }
    }

    private void minHeapify(int i) {
        // no recursive version of heapify
        int j;
        while ((j = i << 1) <= numberOfElements && (j > 0)) {
            if (j < numberOfElements && priority[j] > priority[j + 1])
                j++; // j indexes smallest child
            if (priority[i] <= priority[j])
                break;
            swap(i, j);
            i = j;
        }
    }

    /**
     * Swap two elements around.
     * 
     * @param i
     *            the first element
     * @param j
     *            the second element
     */
    private void swap(int i, int j) {
        long tmp = priority[i];
        priority[i] = priority[j];
        priority[j] = tmp;

        int oldRef = refBack[i];
        refBack[i] = refBack[j];
        refBack[j] = oldRef;
        int refbi = refBack[i];
        int refbj = refBack[j];
        oldRef = ref[refbi];
        ref[refbi] = ref[refbj];
        ref[refbj] = oldRef;

        T t = data[i];
        data[i] = data[j];
        data[j] = t;
    }

    /**
     * Returns an ordered list of elements contained in the heap. The first
     * element in the list is the top of the heap th as applied by all elements.
     * 
     * @return
     */
    public List<T> peekAll() {
        ArrayList<T> col = new ArrayList<T>(numberOfElements);
        IndexedHeap<T> h = new IndexedHeap<T>(this);
        T t = h.poll();
        while (t != null) {
            col.add(t);
            t = h.poll();
        }
        return col;
    }

    private void checkIndex(int index) {
        if (index >= ref.length)
            throw new IllegalArgumentException("index does not exist [index="
                    + index + "]");
    }

    // /CLOVER:OFF
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= numberOfElements; i++) {
            sb.append(priority[i]);
            sb.append(",");
        }
        sb.append("\r");
        for (int i = 1; i <= numberOfElements; i++) {
            sb.append(ref[i] + "(" + priority[ref[i]] + "),");
        }

        return sb.toString();
    }

    public void print() {
        System.out.println("--Size= " + numberOfElements + "----------");
        for (int i = 0; i < data.length; i++) {
            if (i <= numberOfElements) {
                System.out.println(i + " " + ref[i] + " " + refBack[i]
                        + " Data: " + data[i] + " Prio:" + priority[i]);
            } else {
                System.out.println(i + "*" + ref[i] + " " + refBack[i]
                        + " Data: " + data[i] + " Prio:" + priority[i]);

            }
        }
        System.out.println("------------");
    }

    // /CLOVER:ON
    //
    // public static <E> Heap<E> newMaxHeap() {
    // return new MaxHeap<E>();
    // }
    //
    // static class MaxHeap<E> extends Heap<E> {
    //
    // /** serialVersionUID */
    // private static final long serialVersionUID = -4756684940025994283L;
    //
    // @Override
    // public int add(E element, long prio) {
    // return super.add(element, -prio);
    // }
    //
    // @Override
    // public long changePriorityDelta(int index, long delta) {
    // return super.changePriorityDelta(index, -delta);
    // }
    //
    // @Override
    // public long setPriority(int index, long newPriority) {
    // return super.setPriority(index, -newPriority);
    // }
    //
    // }
}
