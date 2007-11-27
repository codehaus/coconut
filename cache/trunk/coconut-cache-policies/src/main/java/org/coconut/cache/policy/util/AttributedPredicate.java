/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.util;

import org.coconut.attribute.AttributeMap;

/**
 * @param <T>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface AttributedPredicate<T> {
    /**
     * Tests the given element and attributemap for acceptance.
     * 
     * @param t
     *            The element to check
     * @param attributes
     *            a map of attributes
     * @return <code>true</code> if the filter accepts the element; <code>false</code>
     *         otherwise.
     * @throws ClassCastException
     *             class of the specified element prevents it from being evaluated by this
     *             filter.
     * @throws NullPointerException
     *             if the specified element is null and this filter does not support null
     *             elements
     * @throws IllegalArgumentException
     *             some aspect of this element prevents it from being evaluated by this
     *             filter.
     */
    boolean evaluate(T t, AttributeMap attributes);
}
