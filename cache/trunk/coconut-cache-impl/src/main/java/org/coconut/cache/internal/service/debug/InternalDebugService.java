package org.coconut.cache.internal.service.debug;



public interface InternalDebugService {
    boolean isTraceEnabled();
    boolean isDebugEnabled();
    
    void debug(String str);
    void trace(String str);
}
