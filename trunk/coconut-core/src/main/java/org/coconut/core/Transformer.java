/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * A class used for transforming elements for one type to another.
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
 * {@link org.coconut.filter.Filter filters}.
 * <p>
 * the {@link Transformers} class supports dynamic construction of transformers
 * either via on-the-fly bytecode generation or reflection.
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
