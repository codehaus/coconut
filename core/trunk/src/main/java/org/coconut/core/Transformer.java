/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * Used for transforming objects from one type to another. For example the
 * following transformer transforms a String into a Long.
 * 
 * <pre>
 * class StringToLongTransformer implements Transformer&lt;String, Long&gt; {
 *     public Long transform(String str) {
 *         return Long.parseLong(str);
 *     }
 * }
 * </pre>
 * 
 * <p>
 * Transformers are often applied together with
 * {@link org.coconut.filter.Filter filters} to extract information on objects.
 * <p>
 * The {@link Transformers} class supports easy construction of dynamic
 * transformers either via on-the-fly bytecode generation or reflection.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Transformer<F, T> {

    /**
     * Transforms an element from one type to another.
     * 
     * @param from
     *            the element to transform from
     * @return the transformed element
     * @throws ClassCastException
     *             class of the specified element prevents it from being
     *             transformed by this transformer.
     * @throws NullPointerException
     *             if the specified element is null and this transformer does
     *             not support transformations of null elements.
     * @throws IllegalArgumentException
     *             some aspect of this element prevents it from being
     *             transformed by this transformer.
     */
    T transform(F from);
}
