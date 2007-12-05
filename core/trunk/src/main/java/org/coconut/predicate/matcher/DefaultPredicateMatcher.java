/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.operations.Ops.Predicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultPredicateMatcher<K, E> extends AbstractPredicateMatcher<K, E> {
    public DefaultPredicateMatcher() {
        this(new ConcurrentHashMap<K, Predicate<? super E>>());
    }

    public DefaultPredicateMatcher(Map<K, Predicate<? super E>> map) {
        super(map);
    }

    @SuppressWarnings("unchecked")
    public List<K> match(E event) {
        List<K> al = null;
        for (Map.Entry<K, Predicate<? super E>> f : getMap().entrySet()) {
            if (f.getValue().evaluate(event)) {
                if (al == null) {
                    al = new ArrayList<K>();
                }
                al.add(f.getKey());
            }
        }
        return al == null ? Collections.EMPTY_LIST : al;
    }

    public void matchAndHandle(PredicateMatcherHandler<K, E> handler, E event) {
        for (Map.Entry<K, Predicate<? super E>> f : getMap().entrySet()) {
            Predicate<? super E> predicate = f.getValue();
            if (predicate.evaluate(event)) {
                handler.handle(f.getKey(), event);
            }
        }
    }
}
