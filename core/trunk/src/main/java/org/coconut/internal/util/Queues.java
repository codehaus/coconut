/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Queues {

    ///CLOVER:OFF
    /** Cannot instantiate. */
    private Queues() {}
    ///CLOVER:ON
    
    public static <T> BlockingQueue<T> noOutputSide(BlockingQueue<T> bq) {
        return new NoOutputSide<T>(bq); // should peek work??
    }

    static class LoosyQueue<E> implements BlockingQueue<E> {
        LoosyQueue() {

        }

        public boolean add(E o) {
            return true;
        }

        public boolean addAll(Collection<? extends E> c) {
            return true;
        }

        public void clear() {
            throw new UnsupportedOperationException("clear not supported");
        }

        public boolean contains(Object o) {
            return false;
        }

        public boolean containsAll(Collection<?> c) {
            return false;
        }

        public int drainTo(Collection<? super E> c, int maxElements) {
            throw new UnsupportedOperationException("drainTo not supported");
        }

        public int drainTo(Collection<? super E> c) {
            throw new UnsupportedOperationException("drainTo not supported");
        }

        public E element() {
            throw new UnsupportedOperationException("element not supported");
        }

        public boolean equals(Object o) {
            return o instanceof LoosyQueue;
        }

        public int hashCode() {
            return 0;
        }

        public boolean isEmpty() {
            return true;
        }

        public Iterator<E> iterator() {
            throw new UnsupportedOperationException();
        }

        public boolean offer(E o, long timeout, TimeUnit unit)
                throws InterruptedException {
            return true;
        }

        public boolean offer(E o) {
            return true;
        }

        public E peek() {
            throw new UnsupportedOperationException();
        }

        public E poll() {
            throw new UnsupportedOperationException("poll not supported");
        }

        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException("poll not supported");
        }

        public void put(E o) throws InterruptedException {
            
        }

        public int remainingCapacity() {
            return 0;
        }

        public E remove() {
            throw new UnsupportedOperationException("remove not supported");
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException("remove not supported");
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("removeAll not supported");
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("retainAll not supported");
        }

        public int size() {
            return 0;
        }

        public E take() throws InterruptedException {
            throw new UnsupportedOperationException("take not supported");
        }

        public Object[] toArray() {
            throw new UnsupportedOperationException("toArray not supported");
        }

        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException("toArray not supported");
        }
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
            throw new UnsupportedOperationException("element not supported");
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
            return new NoRemoveIterator<E>(q.iterator());
        }

        public boolean offer(E o, long timeout, TimeUnit unit)
                throws InterruptedException {
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
            q.put(o);
        }

        public int remainingCapacity() {
            return q.remainingCapacity();
        }

        public E remove() {
            throw new UnsupportedOperationException("remove not supported");
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException("remove not supported");
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("removeAll not supported");
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("retainAll not supported");
        }

        public int size() {
            return q.size();
        }

        public E take() throws InterruptedException {
            throw new UnsupportedOperationException("take not supported");
        }

        public Object[] toArray() {
            return q.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return q.toArray(a);
        }
    }

    static class NoRemoveIterator<E> implements Iterator<E> {
        private final Iterator<E> iter;

        /**
         * @param iter
         */
        public NoRemoveIterator(final Iterator<E> iter) {
            this.iter = iter;
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return iter.hasNext();
        }

        /**
         * @see java.util.Iterator#next()
         */
        public E next() {
            return iter.next();
        }

        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }

    }

    static class NoInputSide<E> implements BlockingQueue<E> {
        private final BlockingQueue<E> q;

        public NoInputSide(BlockingQueue<E> q) {
            if (q == null) {
                throw new NullPointerException("q is null");
            }
            this.q = q;
        }

        public boolean add(E o) {
            throw new UnsupportedOperationException("add not supported");
        }

        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException("addAll not supported");
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
            return q.drainTo(c, maxElements);
        }

        public int drainTo(Collection<? super E> c) {
            return q.drainTo(c);
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

        public boolean offer(E o, long timeout, TimeUnit unit)
                throws InterruptedException {
            throw new UnsupportedOperationException("offer not supported");
        }

        public boolean offer(E o) {
            throw new UnsupportedOperationException("offer not supported");
        }

        public E peek() {
            return q.peek();
        }

        public E poll() {
            return q.poll();
        }

        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
            return q.poll(timeout, unit);

        }

        public void put(E o) throws InterruptedException {
            throw new UnsupportedOperationException("put not supported");
        }

        public int remainingCapacity() {
            return q.remainingCapacity();
        }

        public E remove() {
            return q.remove();
        }

        public boolean remove(Object o) {
            return q.remove(o);
        }

        public boolean removeAll(Collection<?> c) {
            return c.removeAll(c);
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
