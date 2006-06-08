/**
 * 
 */
package org.coconut.cache.policy.util;

import java.util.List;

import org.coconut.cache.policy.ReplacementPolicy;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ReplacementPolicyDecorator<T> implements ReplacementPolicy<T> {

    protected final ReplacementPolicy<T> policy;

    public ReplacementPolicyDecorator(ReplacementPolicy<T> policy) {
        if (policy == null) {
            throw new NullPointerException("policy is null");
        }
        this.policy = policy;
    }

    /**
     * {@inheritDoc}
     */
    public int add(T data) {
        return policy.add(data);
    }

    /**
     * {@inheritDoc}
     */
    public T evictNext() {
        return policy.evictNext();
    }

    /**
     * {@inheritDoc}
     */
    public T peek() {
        return policy.peek();
    }

    /**
     * {@inheritDoc}
     */
    public List<T> peekAll() {
        return policy.peekAll();
    }

    /**
     * {@inheritDoc}
     */
    public T remove(int index) {
        return policy.remove(index);
    }

    /**
     * {@inheritDoc}
     */
    public void touch(int index) {
        policy.touch(index);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        policy.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean update(int index, T newElement) {
        return policy.update(index, newElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return policy.toString();
    }

    /**
     * {@inheritDoc}
     */
    public int getSize() {
        return policy.getSize();
    }
}
