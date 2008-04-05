package org.codehaus.cake.internal.service.listener;

public interface InternalContainerListener {
    void afterContainerStart();

    void afterContainerStop();
}
