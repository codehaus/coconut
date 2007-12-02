/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.predicate;

/**
 * A Predicate can determine a true or false value for any input of its parameterized
 * type. For example, a FileIsDirectoryPredicate might implement Predicate and return
 * <code>true</code> for any File that is a directory.
 * 
 * <pre>
 * class FileIsDirectoryPredicate implements Predicate&lt;File&gt; {
 *     public boolean evaluate(File file) {
 *         return file.isDirectory();
 *     }
 * }
 * </pre>
 * 
 * A number of files can then be tested against this predicate accepting only those files
 * that are directories. A Predicate should be stateless. Furthermore, invoking the
 * <tt>evaluate</tt> method should not have any side effects.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 * @param <E>
 *            the type of elements acccepted by the accept method
 */
public interface Predicate<E> {

    /**
     * Tests the given element for acceptance.
     * 
     * @param element
     *            The element to check
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
    boolean evaluate(E element);
}
