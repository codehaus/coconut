/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.codehaus.cake.internal.util;

/**
 * A <tt>Pair</tt> consists of two references to two other objects. The two objects that are being
 * wrapped are
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public final class Pair<T1, T2> implements java.io.Serializable {

    /** <code>serialVersionUID</code> */
    private static final long serialVersionUID = 3256720688860444465L;

    /** The first instance. */
    private final T1 instance1;

    /** The second instance. */
    private final T2 instance2;

    /**
     * Constructs a new Pair. <tt>null</tt> values are permitted.
     * 
     * @param item1
     *            the first item in the Pair
     * @param item2
     *            the second item in the Pair
     */
    public Pair(Pair<T1, T2> other) {
        this.instance1 = other.instance1;
        this.instance2 = other.instance2;
    }

    /**
     * Constructs a new Pair. <tt>null</tt> values are permitted.
     * 
     * @param item1
     *            the first item in the Pair
     * @param item2
     *            the second item in the Pair
     */
    public Pair(final T1 item1, final T2 item2) {
        this.instance1 = item1;
        this.instance2 = item2;
    }

    /**
     * Returns the first item in the Pair.
     * 
     * @return the first item in the Pair
     */
    public T1 getFirst() {
        return instance1;
    }

    /**
     * Returns the second item in the Pair.
     * 
     * @return the second item in the Pair
     */
    public T2 getSecond() {
        return instance2;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        // we need to calculate the hashCode every time.
        // if we keept the hashCode the hashCode/equals contract might fail.
        return (instance1 == null ? 0 : instance1.hashCode())
                ^ (instance2 == null ? 0 : instance2.hashCode());
    }

    /**
     * Compare two pairs.
     * 
     * @param other
     *            the other Pair to compare with
     * @return true if the pairs are equal
     */
    public boolean equals(Pair other) {
        return other != null && CollectionUtils.eq(instance1, other.instance1)
                && CollectionUtils.eq(instance2, other.instance2);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Pair && equals((Pair) other);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "{" + instance1 + "," + instance2 + "}";
    }

}
