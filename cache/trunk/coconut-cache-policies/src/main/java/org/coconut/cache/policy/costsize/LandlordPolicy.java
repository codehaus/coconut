/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.costsize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.spi.AbstractPolicy;
import org.coconut.core.AttributeMap;

@NotThreadSafe
public class LandlordPolicy<T> extends AbstractPolicy<T> {

    /**
     * @see org.coconut.cache.spi.AbstractPolicy#getSize()
     */
    public int getSize() {
        return size;
    }

    private double[] objectCost;

    private double[] objectCredit;

    private T[] objects;

    private long[] objectSize;

    private int size;

    public LandlordPolicy() {
        this(100);
    }

    @SuppressWarnings("unchecked")
    public LandlordPolicy(int initialCapacity) {
        objectSize = new long[initialCapacity];
        objectCredit = new double[initialCapacity];
        objectCost = new double[initialCapacity];
        objects = (T[]) new Object[initialCapacity];
    }

    @SuppressWarnings("unchecked")
    public LandlordPolicy(LandlordPolicy landlord) {
        this.size = landlord.size;
        objectCost = new double[landlord.objectCost.length];
        objectCredit = new double[landlord.objectCredit.length];
        objects = (T[]) new Object[landlord.objects.length];
        objectSize = new long[landlord.objectSize.length];
        System.arraycopy(landlord.objects, 0, objects, 0, objects.length);
        System.arraycopy(landlord.objectSize, 0, objectSize, 0, objectSize.length);
        System.arraycopy(landlord.objectCost, 0, objectCost, 0, objectCost.length);
        System.arraycopy(landlord.objectCredit, 0, objectCredit, 0, objectCredit.length);
    }

    public int add(T element, AttributeMap attributes) {
        return add(element, CacheAttributes.getSize(attributes), CacheAttributes.getCost(attributes));
    }


    private int add(T element, long size, double cost) {
        if (element == null) {
            throw new NullPointerException("element is null");
        } 
        int index = this.size++;
        if (this.size >= objects.length)
            grow(this.size);
        this.objects[index] = element;
        this.objectSize[index] = size;
        this.objectCost[index] = cost;
        this.objectCredit[index] = cost;
        return index;
    }
    /**
     * @see org.coconut.cache.ReplacementPolicy#clear()
     */
    public void clear() {
        while (evictNext() != null) {
            /* ignore */
        }
    }

    private int evict() {
        double delta = Double.MAX_VALUE;
        int foundZero = -1;
        for (int i = 0; i < size; i++) {
            delta = Math.min(delta, objectCredit[i] / objectSize[i]);
        }
        for (int i = 0; i < size; i++) {
            objectCredit[i] -= delta * objectSize[i];
            if (objectCredit[i] <= 0) {
                objectCredit[i] = 0;
                if (foundZero == -1) {
                    foundZero = i; // first element found
                }
            }
        }
        return foundZero;
    }

    /**
     * Classical description from paper.
     * 
     */
    public Collection<T> evictAllZeros() {
        if (size == 0) {
            return Collections.emptyList();
        } else {
            List<T> l = new ArrayList<T>();
            l.add(evictNext());
            for (int i = 0; i < size; i++) {
                if (objectCredit[i] == 0) {
                    l.add(remove(i));
                }
            }
            return l;
        }
    }

    public T evictNext() {
        if (size == 0) {
            return null;
        }
        // hmm evictNext is o(n) we probably need a queue for these
        for (int i = 0; i < size; i++) {
            if (objectCredit[i] == 0) {
                return remove(i);
            }
        }
        for (;;) {
            int e = evict();
            if (e >= 0) {
                return remove(e);
            }
        }
    }

    private void grow(int requestedSize) {
        int newlen = objects.length;
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

    public T peek() {
        if (size == 0) {
            return null;
        } else {
            LandlordPolicy<T> landlord = new LandlordPolicy<T>(this);
            return landlord.evictNext();
        }
    }

    public List<T> peekAll() {
        ArrayList<T> l = new ArrayList<T>(size);
        LandlordPolicy<T> landlord = new LandlordPolicy<T>(this);
        T element = landlord.evictNext();
        while (element != null) {
            l.add(element);
            element = landlord.evictNext();
        }
        return l;
    }

    public T remove(int i) {
        // todo check range
        T t = objects[i];
        objects[i] = null;
        return t;
    }

    protected double resetCredit(double currentCredit, double cost) {
        // todo find optimal formula
        return (cost - currentCredit) / 2;
    }

    @SuppressWarnings("unchecked")
    private void setSize(int newlen) {
        Object[] newData = new Object[newlen];
        long[] newSize = new long[newlen];
        double[] newCost = new double[newlen];
        double[] newCredit = new double[newlen];
        System.arraycopy(objects, 0, newData, 0, objects.length);
        System.arraycopy(objectSize, 0, newSize, 0, objectSize.length);
        System.arraycopy(objectCost, 0, newCost, 0, objectCost.length);
        System.arraycopy(objectCredit, 0, newCredit, 0, objectCredit.length);
        objects = (T[]) newData;
        objectSize = newSize;
        objectCredit = newCredit;
        objectCost = newCost;
    }

    public int size() {
        return size;
    }

    public void touch(int entry) {
        // TODO translate entry
        int i = entry;
        if (objectCredit[i] == 0) {
            // item has been marked for eviction, however has not been removed
            // treat it as a new insert.
            objectCredit[i] = objectCost[i];
        } else {
            objectCredit[i] += resetCredit(objectCredit[i], objectCost[i]);
        }
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#update(int,
     *      java.lang.Object)
     */
    public boolean update(int index, T newElement, AttributeMap attributes) {
        objects[index] = newElement;
        // TODO update cost/size
        // We should also check if cost size has changed...
        // If not there is no need to update placement
        return true;
    }
}
