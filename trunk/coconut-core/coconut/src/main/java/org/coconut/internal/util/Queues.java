/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.internal.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Queues {

    public static <T> BlockingQueue<T> noOutputSide(BlockingQueue bq) {
        return null; //should peek work??
    }
    
    static class NoOutputSide<E> implements BlockingQueue<E> {
        private final BlockingQueue<E> q;

        public NoOutputSide(BlockingQueue<E> q) {
            if (q == null) {
                throw new NullPointerException("q is null");
            }
            this.q = q;
        }

        public boolean add(E o) {
            return q.add(o);
        }

        public boolean addAll(Collection<? extends E> c) {
            return q.addAll(c);
        }

        public void clear() {
            throw new UnsupportedOperationException("clear not supported");
        }

        public boolean contains(Object o) {
            return q.contains(o);
        }

        public boolean containsAll(Collection<?> c) {
            return q.containsAll(c);
        }

        public int drainTo(Collection<? super E> c, int maxElements) {
            throw new UnsupportedOperationException("drainTo not supported");
        }

        public int drainTo(Collection<? super E> c) {
            throw new UnsupportedOperationException("drainTo not supported");
        }

        public E element() {
            return q.element();
        }

        public boolean equals(Object o) {
            return q.equals(o);
        }

        public int hashCode() {
            return q.hashCode();
        }

        public boolean isEmpty() {
            return q.isEmpty();
        }

        public Iterator<E> iterator() {
            return q.iterator();
        }

        public boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException {
            return q.offer(o, timeout, unit);
        }

        public boolean offer(E o) {
            return q.offer(o);
        }

        public E peek() {
            return q.peek();
        }

        public E poll() {
            throw new UnsupportedOperationException("poll not supported");
        }

        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException("poll not supported");

        }

        public void put(E o) throws InterruptedException {
            throw new UnsupportedOperationException("put not supported");
        }

        public int remainingCapacity() {
            return q.remainingCapacity();
        }

        public E remove() {
            throw new UnsupportedOperationException("drainTo not supported");
        }

        public boolean remove(Object o) {
            return q.remove(o);
        }

        public boolean removeAll(Collection<?> c) {
            return q.removeAll(c);
        }

        public boolean retainAll(Collection<?> c) {
            return q.retainAll(c);
        }

        public int size() {
            return q.size();
        }

        public E take() throws InterruptedException {
            return q.take();
        }

        public Object[] toArray() {
            return q.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return q.toArray(a);
        }
    }
}