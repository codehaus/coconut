/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.newstore;

import java.io.IOException;
import java.io.ObjectOutput;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultStoreWriter implements StoreWriter {

    /**
     * @throws IOException
     * @see org.coconut.cache.store.newstore.StoreWriter#writeKey(java.lang.Object,
     *      java.io.ObjectOutput)
     */
    public void writeKey(Object key, ObjectOutput oo) throws IOException {
        oo.writeObject(key);
    }

    public int getHash(Object key) {
        return key.hashCode();
    }
    /**
     * @throws IOException
     * @see org.coconut.cache.store.newstore.StoreWriter#writeValue(java.lang.Object,
     *      java.io.ObjectOutput)
     */
    public void writeValue(Object key, ObjectOutput oo) throws IOException {
        oo.writeObject(key);
    }

}
