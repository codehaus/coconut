/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.util;

import java.util.List;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.policy.ReplacementPolicy;

/**
 * A ReplacementPolicyDecorator is used to decorate an existing ReplacementPolicy with additional
 * behavior. See the description of the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">Decorator Pattern</a> for
 * further information.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <T>
 *            the type of data maintained by the policy
 */
public class ReplacementPolicyDecorator<T> implements ReplacementPolicy<T> {

    /** The replacement policy we are decorating. */
    private final ReplacementPolicy<T> policy;

    /**
     * Creates a new PolicyDecorator decorating the specified replacement policy.
     * 
     * @param policy
     *            the replacement policy to decorate
     * @throws NullPointerException
     *             if the specified replacement policy is null
     */
    public ReplacementPolicyDecorator(ReplacementPolicy<T> policy) {
        if (policy == null) {
            throw new NullPointerException("policy is null");
        }
        this.policy = policy;
    }

    /** {@inheritDoc} */
    public int add(T element, AttributeMap attributes) {
        return policy.add(element, attributes);
    }

    /** {@inheritDoc} */
    public void clear() {
        policy.clear();
    }

    /** {@inheritDoc} */
    public T evictNext() {
        return policy.evictNext();
    }

    /** {@inheritDoc} */
    public int getSize() {
        return policy.getSize();
    }

    /** {@inheritDoc} */
    public T peek() {
        return policy.peek();
    }

    /** {@inheritDoc} */
    public List<T> peekAll() {
        return policy.peekAll();
    }

    /** {@inheritDoc} */
    public T remove(int index) {
        return policy.remove(index);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return policy.toString();
    }

    /** {@inheritDoc} */
    public void touch(int index) {
        policy.touch(index);
    }

    /** {@inheritDoc} */
    public boolean update(int index, T newElement, AttributeMap attributes) {
        return policy.update(index, newElement, attributes);
    }

    /**
     * Returns the replacement policy that is being decorated.
     * 
     * @return the replacement policy that is being decorated
     */
    protected ReplacementPolicy<T> getPolicy() {
        return policy;
    }
}
