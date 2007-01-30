/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.util;

import java.util.concurrent.atomic.AtomicReference;

import org.coconut.cache.util.AbstractCacheLoader;

/**
 * A simple cache loader used for testing. Will return 1->A, 2->B, 3->C, 4->D,
 * 5->E and <code>null</code> for any other key.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class MutableLoader extends AbstractCacheLoader<Integer, String> {

    private AtomicReference<String> ref = new AtomicReference<String>();

    /**
     * @see org.coconut.cache.util.AbstractCacheLoader#load(java.lang.Object)
     */
    public String load(Integer key) {
        return ref.get();
    }

    public void setLoadNext(String loadNext) {
        ref.set(loadNext);
    }
}