package org.coconut.internal.util;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.coconut.internal.util.LogHelper.AbstractLogger;

/**
 * Returns the exception logger configured for this cache. Or initializes the default
 * logger if no logger has been defined and the default logger has not already been
 * initialized
 * 
 * @return the exception logger for the cache
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
    public void log(Level level, String message) {
        getLogger().log(LogHelper.toJdkLevel(level), message);
    }

    /** {@inheritDoc} */
    public void log(Level level, String message, Throwable cause) {
        getLogger().log(LogHelper.toJdkLevel(level), message, cause);
    }

    private Logger getLogger() {
        Logger l = logger;
        if (l != null) {
            return l;
        }
        synchronized (this) {
            if (logger == null) {
                java.util.logging.Logger jucLogger = LogManager.getLogManager().getLogger(
                        jdkLoggerName);
                if (jucLogger == null) {
                    jucLogger = java.util.logging.Logger.getLogger(jdkLoggerName);
                    jucLogger.setLevel(java.util.logging.Level.ALL);
                    jucLogger.info(msg);
                    jucLogger.setLevel(java.util.logging.Level.WARNING);
                }
                logger = jucLogger;
            }
        }
        return logger;
    }
}
