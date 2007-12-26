/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.coconut.internal.util.LogHelper.AbstractLogger;

/**
 * Returns the exception logger configured for this cache. Or initializes the default
 * logger if no logger has been defined and the default logger has not already been
 * initialized
 */
public class LazyLogger extends AbstractLogger {

    private final String jdkLoggerName;

    private volatile Logger logger;

    private final String msg;

    public LazyLogger(String jdkLoggerName, String msg) {
        this.msg = msg;
        this.jdkLoggerName = jdkLoggerName;
    }

    @Override
    public String getName() {
        return jdkLoggerName;
    }

    /** {@inheritDoc} */
    public boolean isEnabled(Level level) {
        return getLogger().isLoggable(LogHelper.toJdkLevel(level));
    }

    /** {@inheritDoc} */
    @Override
    public void log(Level level, String message) {
        getLogger().log(LogHelper.toJdkLevel(level), message);
    }

    /** {@inheritDoc} */
    public void log(Level level, String message, Throwable cause) {
        getLogger().log(LogHelper.toJdkLevel(level), message, cause);
    }

    private Logger getLogger() {
        Logger logger = this.logger;
        if (logger != null) {
            return logger;
        }
        synchronized (this) {
            if (this.logger == null) {
                logger = LogManager.getLogManager().getLogger(jdkLoggerName);
                if (logger == null) {
                    logger = java.util.logging.Logger.getLogger(jdkLoggerName);
                    logger.setLevel(java.util.logging.Level.ALL);
                    logger.info(msg);
                    logger.setLevel(java.util.logging.Level.WARNING);
                }
                this.logger = logger;
            }
            return logger;
        }
    }
}
