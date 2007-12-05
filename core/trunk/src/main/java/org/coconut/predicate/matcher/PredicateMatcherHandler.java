/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate.matcher;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the types of keys matched
 * @param <E>
 *            the object that is being matched
 */
public interface PredicateMatcherHandler<K, E> {
    /**
     * @param key
     *            the key that was matched
     * @param object
     *            the object that was matched against
     */
    void handle(K key, E object);
}
