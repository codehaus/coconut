/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.sandbox;

public interface HistoryFilter<T, E> {

    boolean accept(E indexedElement, T object);
    // public void equals(Transformer elem, Transformer other)
}
