package org.coconut.cache.policy;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.coconut.cache.policy.*;

public class Policies2<T> {

    static class SynchronizedReplacementPolicy<E> implements ReplacementPolicy<E>,
            Serializable {

        /* Backing ReplacementPolicy. */
        final ReplacementPolicy<E> policy;

        /* Object on which to synchronize. */
        final Object mutex;

        SynchronizedReplacementPolicy(ReplacementPolicy<E> policy) {
            if (policy == null) {
                throw new NullPointerException("policy is null");
            }
            this.policy = policy;
            mutex = this;
        }

        SynchronizedReplacementPolicy(ReplacementPolicy<E> policy, Object mutex) {
            this.policy = policy;
            this.mutex = mutex;
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#add(java.lang.Object)
         */
        public int add(E element) {
            synchronized (mutex) {
                return policy.add(element);
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#clear()
         */
        public void clear() {
            synchronized (mutex) {
                policy.clear();
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#evictNext()
         */
        public E evictNext() {
            synchronized (mutex) {
                return policy.evictNext();
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#getSize()
         */
        public int getSize() {
            synchronized (mutex) {
                return policy.getSize();
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#peek()
         */
        public E peek() {
            synchronized (mutex) {
                return policy.peek();
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#peekAll()
         */
        public List<E> peekAll() {
            synchronized (mutex) {
                return policy.peekAll();
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#remove(int)
         */
        public E remove(int index) {
            synchronized (mutex) {
                return policy.remove(index);
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#touch(int)
         */
        public void touch(int index) {
            synchronized (mutex) {
                policy.touch(index);
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#update(int,
         *      java.lang.Object)
         */
        public boolean update(int index, E newElement) {
            synchronized (mutex) {
                return policy.update(index, newElement);
            }
        }

        public boolean equals(Object o) {
            synchronized (mutex) {
                return policy.equals(o);
            }
        }

        public int hashCode() {
            synchronized (mutex) {
                return policy.hashCode();
            }
        }

        public String toString() {
            synchronized (mutex) {
                return policy.toString();
            }
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (mutex) {
                s.defaultWriteObject();
            }
        }
    }

    public static <E> ReplacementPolicy<E> synchronizedReplacementPolicy(
            ReplacementPolicy<E> policy) {
        return synchronizedReplacementPolicy(policy, policy);
    }

    public static <E> ReplacementPolicy<E> synchronizedReplacementPolicy(
            ReplacementPolicy<E> policy, Object mutex) {
        return new SynchronizedReplacementPolicy<E>(policy, mutex);
    }
}