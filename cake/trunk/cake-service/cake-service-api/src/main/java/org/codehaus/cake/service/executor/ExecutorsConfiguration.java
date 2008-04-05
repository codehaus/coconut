/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.service.executor;


/**
 * This class is used to configure the executor manager service prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheWorkerConfiguration.java 537 2007-12-30 19:21:20Z kasper $
 * @see ExecutorsService
 */
public class ExecutorsConfiguration {

    /** The executor manager to use. */
    private ExecutorsManager executeManager;

    /**
     * Returns the {@link ExecutorsManager} or <code>null</code> if no executor manager
     * has been set.
     * 
     * @return the {@link ExecutorsManager} or <code>null</code> if no executor manager
     *         has been set
     * @see #setExecutorManager(ExecutorsManager)
     */
    public ExecutorsManager getExecutorManager() {
        return executeManager;
    }

    /**
     * Sets the ExecutorManager that should be used. If no ExecutorManager is set one will
     * be created automatically if needed.
     * 
     * @param executeManager
     *            the executor manager to use
     * @return this configuration
     * @see #getExecutorManager()
     */
    public ExecutorsConfiguration setExecutorManager(ExecutorsManager executeManager) {
        this.executeManager = executeManager;
        return this;
    }

}
