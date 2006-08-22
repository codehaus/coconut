/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.newstore;

import java.io.IOException;
import java.io.ObjectOutput;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface StoreWriter {
    void writeKey(Object key, ObjectOutput oo) throws IOException;
    void writeValue(Object key, ObjectOutput oo) throws IOException;
}
