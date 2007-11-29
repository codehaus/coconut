/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * An object with a function accepting objects of type T and returning those of type U.
 * For example the following mapper transforms a String into a Long.
 * 
 * <pre>
 * class StringToLongMapper implements Mapper&lt;String, Long&gt; {
 *     public Long map(String str) {
 *         return Long.parseLong(str);
 *     }
 * }
 * </pre>
 * 
 * <p>
 * Mappers are often applied together with {@link org.coconut.predicate.Predicate filters}
 * to extract information on objects.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <T>
 *            the type we are mapping from
 * @param <U>
 *            the type we are mapping to
 */
public interface Mapper<T, U> {

    /**
     * Transforms an element from one type to another.
     * 
     * @param from
     *            the element to transform from
     * @return the transformed element
     * @throws ClassCastException
     *             class of the specified element prevents it from being transformed by
     *             this mapper.
     * @throws NullPointerException
     *             if the specified element is null and this mapper does not support
     *             transformations of null elements.
     * @throws IllegalArgumentException
     *             some aspect of this element prevents it from being transformed by this
     *             mapper.
     */
    U map(T from);
}
