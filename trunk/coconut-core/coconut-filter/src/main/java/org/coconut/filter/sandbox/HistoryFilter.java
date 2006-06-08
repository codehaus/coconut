package org.coconut.filter.sandbox;

public interface HistoryFilter<T, E> {

    boolean accept(E indexedElement, T object);
    // public void equals(Transformer elem, Transformer other)
}
