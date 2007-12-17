/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.debug;



public interface InternalDebugService {
    boolean isTraceEnabled();
    boolean isDebugEnabled();
    
    void debug(String str);
    void trace(String str);
}
