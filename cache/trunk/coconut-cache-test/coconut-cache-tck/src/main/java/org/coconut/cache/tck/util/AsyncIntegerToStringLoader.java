/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.coconut.cache.spi.AsyncCacheLoader;
import org.coconut.core.AttributeMap;
import org.coconut.core.Callback;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AsyncIntegerToStringLoader extends IntegerToStringLoader implements
        AsyncCacheLoader<Integer, String> {

    private List<Integer> keysLoader = Collections
            .synchronizedList(new LinkedList<Integer>());

    final static Runnable DUMMY_RUNNABLE = new Runnable() {

        public void run() {
            // do nothing
        }
    };

    public List<Integer> getLoadedKeys() {
        return new ArrayList<Integer>(keysLoader);
    }

    /**
     * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoad(java.lang.Object,
     *      org.coconut.core.Callback)
     */
    public Future<?> asyncLoad(Integer key, AttributeMap attributes, Callback<String> c) {
        c.completed(load(key, attributes));
        keysLoader.add(key);
        return new FutureTask(DUMMY_RUNNABLE, null);
    }

    /**
     * @see org.coconut.cache.spi.AsyncCacheLoader#asyncLoadAll(java.util.Collection,
     *      org.coconut.core.Callback)
     */
    public Future<?> asyncLoadAll(Map<? extends Integer, AttributeMap> keysWithAttributes,
            Callback<Map<Integer, String>> c) {
        HashMap<Integer, String> h = new HashMap<Integer, String>();
        for (Map.Entry<? extends Integer, AttributeMap> entry : keysWithAttributes.entrySet()) {
            h.put(entry.getKey(), load(entry.getKey(), entry.getValue())); // we
                                                                            // keep
                                                                            // nulls
        }
        c.completed(h);
        keysLoader.addAll(keysWithAttributes.keySet());
        return new FutureTask(DUMMY_RUNNABLE, null);
    }
}
