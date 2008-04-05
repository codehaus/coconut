/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.internal.service.debug;


public interface InternalDebugService {
    boolean isTraceEnabled();

    boolean isDebugEnabled();

    void debug(String str);

    void trace(String str);
    
    void info(String str);
}
