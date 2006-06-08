package org.coconut.jobs;

import java.util.UUID;
import java.util.concurrent.Future;

import org.coconut.core.Priority;

public interface JobFuture<T> extends Future<T> {
    
    Priority getPriority();
    
    //void setPriority();
    long getId();
    
    String getDescription();

    UUID getUUID();

    /**
     * between 0 and 1, 0.5 half done. 0 started no work done 1 completly done,
     * Nagative number not started or not supported, or could not get update
     * status.
     */
    float getCompletion();
    // well think this should be removed, requires sync
    // with remote servers
    // think it is okay anyway, its on a best effort basis
    
    
    boolean isStarted();

    long submitted();
    
    long started();
    long stopped();
}
