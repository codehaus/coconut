package org.coconut.predicate.matcher;


public interface PredicateMatcherHandler<K, E> {
    /**
     * @param key
     * @param predicate
     *            the predicate that was matched
     * @param object
     */
    void handle(K key, E object);
}
