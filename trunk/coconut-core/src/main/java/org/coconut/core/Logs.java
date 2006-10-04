/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.PrintStream;

/**
 * This class is used for creating {@link Log} wrappers from popular logging
 * frameworks such as <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/logging/package-summary.html">
 * Standard JDK logging </a>, <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/logging/package-summary.html">
 * Log4j </a> or <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/logging/package-summary.html">
 * commons logging </a>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public final class Logs {

    /**
     * Creates a new Log that ignores any input.
     * 
     * @return a logger that ignores any input.
     */
    public static Log nullLog() {
        return new SimpelLogger(Log.Level.Fatal.getLevel() + 1);
    }

    /**
     * Creates a new Log that ignores any input below the specified level. Any
     * logging messages on this level or above it, will be logged to system.out
     * 
     * @return a system.out logger
     */
    public static Log systemOutLog(Log.Level level) {
        return new SimpelLogger(level.getLevel(), System.out);
    }

    public static Log systemErrLog(Log.Level level) {
        return new SimpelLogger(level.getLevel(), System.err);
    }

    public static Log printStreamLog(Log.Level level, PrintStream ps) {
        return new SimpelLogger(level.getLevel(), ps);
    }

    /**
     * This class is used for creating a Log4j wrapper Log.
     * 
     * <pre>
     * Log log = Logs.Log4j.from(myLog4jLogger);
     * </pre>
     */
    public static class Log4j {

        /**
         * Wraps a Log4j logger.
         * 
         * @param log
         *            the Log4j logger
         * @return a wrapped Log4j logger
         */
        public static Log from(org.apache.log4j.Logger logger) {
            return new Log4JLogger(logger);
        }

        public static Log from(Class clazz) {
            return from(org.apache.log4j.Logger.getLogger(clazz));
        }
    }

    static abstract class AbstractLogger implements Log {
        public boolean isDebugEnabled() {
            return isEnabled(Level.Debug);
        }

        public boolean isErrorEnabled() {
            return isEnabled(Level.Error);
        }

        public boolean isFatalEnabled() {
            return isEnabled(Level.Fatal);
        }

        public boolean isInfoEnabled() {
            return isEnabled(Level.Info);
        }

        public boolean isTraceEnabled() {
            return isEnabled(Level.Trace);
        }

        public boolean isWarnEnabled() {
            return isEnabled(Level.Warn);
        }

        public void trace(String message) {
            log(Level.Trace, message);
        }

        public void trace(String message, Throwable cause) {
            log(Level.Trace, message, cause);
        }

        public void debug(String message) {
            log(Level.Debug, message);
        }

        public void debug(String message, Throwable cause) {
            log(Level.Debug, message, cause);
        }

        public void info(String message) {
            log(Level.Info, message);
        }

        public void info(String message, Throwable cause) {
            log(Level.Info, message, cause);
        }

        public void warn(String message) {
            log(Level.Warn, message);
        }

        public void warn(String message, Throwable cause) {
            log(Level.Warn, message, cause);
        }

        public void error(String message) {
            log(Level.Error, message);
        }

        public void error(String message, Throwable cause) {
            log(Level.Error, message, cause);
        }

        public void fatal(String message) {
            log(Level.Fatal, message);
        }

        public void fatal(String message, Throwable cause) {
            log(Level.Fatal, message, cause);
        }
    }

    static class SimpelLogger extends AbstractLogger {
        private final int level;

        private final PrintStream stream;

        SimpelLogger(int level) {
            this.level = level;
            this.stream = null;
        }

        SimpelLogger(int level, PrintStream stream) {
            if (stream == null) {
                throw new NullPointerException("stream is null");
            }
            this.level = level;
            this.stream = stream;
        }

        public boolean isEnabled(Level level) {
            return this.level <= level.getLevel();
        }

        public void log(Log.Level l, String message) {
            log(l, message, null);
        }

        public void log(Log.Level l, String message, Throwable cause) {
            if (stream != null && level <= l.getLevel()) {
                stream.println(message);
                if (cause != null)
                    cause.printStackTrace(stream);
            }
        }
    }

    static class JDKLogger extends AbstractLogger {
        private final java.util.logging.Logger log;

        JDKLogger(java.util.logging.Logger log) {
            this.log = log;
        }

        public boolean isEnabled(Level level) {
            return log.isLoggable(from(level));
        }

        public void log(Log.Level level, String message) {
            log.log(from(level), message);
        }

        public void log(Log.Level level, String message, Throwable cause) {
            log.log(from(level), message, cause);
        }

        static java.util.logging.Level from(Level level) {
            switch (level) {
            case Debug:
                return java.util.logging.Level.FINE;
            case Error:
                return java.util.logging.Level.SEVERE;
            case Fatal:
                return java.util.logging.Level.SEVERE;
            case Info:
                return java.util.logging.Level.INFO;
            default /* Warn */:
                return java.util.logging.Level.WARNING;
            }
        }
    }

    static class Log4JLogger extends AbstractLogger {
        private final org.apache.log4j.Logger log;

        Log4JLogger(org.apache.log4j.Logger log) {
            this.log = log;
        }

        public boolean isEnabled(Level level) {
            return log.isEnabledFor(from(level));
        }

        public void log(Log.Level level, String message) {
            log(level, message, null);
        }

        public void log(Log.Level level, String message, Throwable cause) {
            log.log(from(level), message, cause);
        }

        static org.apache.log4j.Level from(Level level) {
            switch (level) {
            case Debug:
                return org.apache.log4j.Level.DEBUG;
            case Error:
                return org.apache.log4j.Level.ERROR;
            case Fatal:
                return org.apache.log4j.Level.FATAL;
            case Info:
                return org.apache.log4j.Level.INFO;
            default /* Warn */:
                return org.apache.log4j.Level.WARN;
            }
        }
    }

    public static class Commons {
        public static Log from(org.apache.commons.logging.Log log) {
            return new CommonsLogger(log);
        }

        public static Log from(Class clazz) {
            return from(org.apache.commons.logging.LogFactory.getLog(clazz));
        }

        // /CLOVER:OFF
        /** Cannot instantiate. */
        private Commons() {/* Cannot instantiate. */
        }
        // /CLOVER:ON
    }

    abstract static class AbstractLoggerForRetardedImplementations implements Log {
        public boolean isEnabled(Level level) {
            switch (level) {
            case Debug:
                return isDebugEnabled();
            case Error:
                return isErrorEnabled();
            case Fatal:
                return isFatalEnabled();
            case Info:
                return isInfoEnabled();
            default /* Warn */:
                return isWarnEnabled();
            }
        }

        /**
         * @see org.coconut.core.Log#log(org.coconut.core.Log.Level,
         *      java.lang.String)
         */
        public void log(Level level, String message) {
            switch (level) {
            case Debug:
                debug(message);
                break;
            case Error:
                error(message);
                break;
            case Fatal:
                fatal(message);
                break;
            case Info:
                info(message);
                break;
            default /* Warn */:
                warn(message);
            }
        }

        /**
         * @see org.coconut.core.Log#log(org.coconut.core.Log.Level,
         *      java.lang.String, java.lang.Throwable)
         */
        public void log(Level level, String message, Throwable cause) {
            switch (level) {
            case Debug:
                debug(message, cause);
                break;
            case Error:
                error(message, cause);
                break;
            case Fatal:
                fatal(message, cause);
                break;
            case Info:
                info(message, cause);
                break;
            default /* Warn */:
                warn(message, cause);
            }
        }
    }

    static class CommonsLogger extends AbstractLoggerForRetardedImplementations {
        private final org.apache.commons.logging.Log log;

        private CommonsLogger(org.apache.commons.logging.Log log) {
            this.log = log;
        }

        public void debug(String message) {
            log.debug(message);
        }

        public void debug(String message, Throwable cause) {
            log.debug(message, cause);
        }

        public void error(String message) {
            log.error(message);
        }

        public void error(String message, Throwable cause) {
            log.error(message, cause);
        }

        public void fatal(String message) {
            log.fatal(message);
        }

        public void fatal(String message, Throwable cause) {
            log.fatal(message, cause);
        }

        public void info(String message) {
            log.info(message);
        }

        public void info(String message, Throwable cause) {
            log.info(message, cause);
        }

        public boolean isDebugEnabled() {
            return log.isDebugEnabled();
        }

        public boolean isErrorEnabled() {
            return log.isErrorEnabled();
        }

        public boolean isFatalEnabled() {
            return log.isFatalEnabled();
        }

        public boolean isInfoEnabled() {
            return log.isInfoEnabled();
        }

        public boolean isTraceEnabled() {
            return log.isTraceEnabled();
        }

        public boolean isWarnEnabled() {
            return log.isWarnEnabled();
        }

        public void trace(String message) {
            log.trace(message);
        }

        public void trace(String message, Throwable cause) {
            log.trace(message, cause);
        }

        public void warn(String message) {
            log.warn(message);
        }

        public void warn(String message, Throwable cause) {
            log.warn(message, cause);
        }
    }

    public static class JDK {
        public static Log from(java.util.logging.Logger log) {
            return new JDKLogger(log);
        }

        // /CLOVER:OFF
        /** Cannot instantiate. */
        private JDK() {/* Cannot instantiate. */
        }
        // /CLOVER:ON
    }

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Logs() {/* Cannot instantiate. */
    }
    // /CLOVER:ON
}
