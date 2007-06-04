/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
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
public final class Loggers {
    ///CLOVER:OFF
    /** Cannot instantiate. */
    private Loggers() {}
    ///CLOVER:ON
    
    /**
     * Creates a new Log that ignores any input.
     * 
     * @return a logger that ignores any input.
     */
    public static Logger nullLog() {
        return new SimpleLogger(Logger.Level.Off.getLevel());
    }

    /**
     * Creates a new Log that ignores any input below the specified level. Any
     * logging messages on this level or above it, will be logged to system.out
     * 
     * @return a system.out logger
     */
    public static Logger systemOutLog(Logger.Level level) {
        return new SimpleLogger(level.getLevel(), System.out);
    }

    public static Logger systemErrLog(Logger.Level level) {
        return new SimpleLogger(level.getLevel(), System.err);
    }

    public static Logger printStreamLog(Logger.Level level, PrintStream ps) {
        return new SimpleLogger(level.getLevel(), ps);
    }

    public static String getName(Logger log) {
        return log instanceof Loggers.AbstractLogger ? ((Loggers.AbstractLogger) log).getName()
                : null;
    }

    /**
     * This class is used for creating a Log4j wrapper Log.
     * 
     * <pre>
     * Log log = Logs.Log4j.from(myLog4jLogger);
     * </pre>
     */
    public final static class Log4j {
        ///CLOVER:OFF
        /** Cannot instantiate. */
        private Log4j() {}
        ///CLOVER:ON
        /**
         * Wraps a Log4j logger.
         * 
         * @param log
         *            the Log4j logger
         * @return a wrapped Log4j logger
         */
        public static Logger from(org.apache.log4j.Logger logger) {
            return new Log4JLogger(logger);
        }

        public static Logger from(Class clazz) {
            return from(org.apache.log4j.Logger.getLogger(clazz));
        }

        public static Logger from(String name) {
            return from(org.apache.log4j.Logger.getLogger(name));
        }

        public static boolean isLog4jLogger(Logger log) {
            return log instanceof Log4JLogger;
        }

        public static org.apache.log4j.Logger getAsLog4jLogger(Logger log) {
            if (!isLog4jLogger(log)) {
                throw new IllegalArgumentException("Not a JDK Logger");
            }
            return ((Log4JLogger) log).log;
        }
    }

    static abstract class AbstractLogger implements Logger {
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

        public abstract String getName();
    }

    final static class SimpleLogger extends AbstractLogger {
        private final int level;

        private final PrintStream stream;

        SimpleLogger(int level) {
            this.level = level;
            this.stream = null;
        }

        SimpleLogger(int level, PrintStream stream) {
            if (stream == null) {
                throw new NullPointerException("stream is null");
            }
            this.level = level;
            this.stream = stream;
        }

        public boolean isEnabled(Level level) {
            return this.level <= level.getLevel();
        }

        public void log(Logger.Level l, String message) {
            log(l, message, null);
        }

        public void log(Logger.Level l, String message, Throwable cause) {
            if (stream != null && level <= l.getLevel()) {
                stream.println(message);
                if (cause != null)
                    cause.printStackTrace(stream);
            }
        }

        /**
         * @see org.coconut.core.util.Logs.AbstractLogger#getName()
         */
        @Override
        public String getName() {
            return "simple";
        }
    }

    final static class JDKLogger extends AbstractLogger {
        private final java.util.logging.Logger log;

        JDKLogger(java.util.logging.Logger log) {
            this.log = log;
        }

        public boolean isEnabled(Level level) {
            return log.isLoggable(from(level));
        }

        public void log(Logger.Level level, String message) {
            log.log(from(level), message);
        }

        public void log(Logger.Level level, String message, Throwable cause) {
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

        public String getName() {
            return log.getName();
        }
    }

    final static class Log4JLogger extends AbstractLogger {
        private final org.apache.log4j.Logger log;

        Log4JLogger(org.apache.log4j.Logger log) {
            this.log = log;
        }

        public boolean isEnabled(Level level) {
            return log.isEnabledFor(from(level));
        }

        public void log(Logger.Level level, String message) {
            log(level, message, null);
        }

        public void log(Logger.Level level, String message, Throwable cause) {
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

        public String getName() {
            return log.getName();
        }
    }

    public final static class Commons {

        ///CLOVER:OFF
        /** Cannot instantiate. */
        private Commons() {}
        ///CLOVER:ON

        public static Logger from(org.apache.commons.logging.Log log) {
            return new CommonsLogger(log);
        }

        public static Logger from(Class clazz) {
            return from(org.apache.commons.logging.LogFactory.getLog(clazz));
        }

        /**
         * @param string
         * @return
         */
        public static Logger from(String name) {
            return from(org.apache.commons.logging.LogFactory.getLog(name));
        }

        public static boolean isCommonsLogger(Logger log) {
            return log instanceof CommonsLogger;
        }

        public static org.apache.commons.logging.Log getAsCommonsLogger(Logger log) {
            if (!isCommonsLogger(log)) {
                throw new IllegalArgumentException("Not a Commons Logger");
            }
            return ((CommonsLogger) log).log;
        }
    }

    final static class CommonsLogger extends AbstractLogger {
        private final org.apache.commons.logging.Log log;

        private CommonsLogger(org.apache.commons.logging.Log log) {
            this.log = log;
        }

        public boolean isEnabled(Level level) {
            switch (level) {
            case Debug:
                return log.isDebugEnabled();
            case Error:
                return log.isErrorEnabled();
            case Fatal:
                return log.isFatalEnabled();
            case Info:
                return log.isInfoEnabled();
            case Trace:
                return log.isTraceEnabled();
            default /* Warn */:
                return log.isWarnEnabled();
            }
        }

        /**
         * @see org.coconut.core.Log#log(org.coconut.core.Log.Level,
         *      java.lang.String)
         */
        public void log(Level level, String message) {
            switch (level) {
            case Debug:
                log.debug(message);
                break;
            case Error:
                log.error(message);
                break;
            case Fatal:
                log.fatal(message);
                break;
            case Info:
                log.info(message);
                break;
            case Trace:
                log.trace(message);
                break;
            default /* Warn */:
                log.warn(message);
            }
        }

        /**
         * @see org.coconut.core.Log#log(org.coconut.core.Log.Level,
         *      java.lang.String, java.lang.Throwable)
         */
        public void log(Level level, String message, Throwable cause) {
            switch (level) {
            case Debug:
                log.debug(message, cause);
                break;
            case Error:
                log.error(message, cause);
                break;
            case Fatal:
                log.fatal(message, cause);
                break;
            case Info:
                log.info(message, cause);
                break;
            case Trace:
                log.trace(message, cause);
                break;
            default /* Warn */:
                log.warn(message, cause);
            }
        }

        /**
         * @see org.coconut.core.util.Logs.AbstractLogger#getName()
         */
        @Override
        public String getName() {
            if (log instanceof org.apache.commons.logging.impl.Jdk14Logger) {
                return ((org.apache.commons.logging.impl.Jdk14Logger) log).getLogger()
                        .getName();
            } else if (log instanceof org.apache.commons.logging.impl.Log4JLogger) {
                return ((org.apache.commons.logging.impl.Log4JLogger) log).getLogger()
                        .getName();
            } else {
                return null;// or should we throw an exception?
            }
        }
    }

    public final static class JDK {
        ///CLOVER:OFF
        /** Cannot instantiate. */
        private JDK() {}
        ///CLOVER:ON
        
        public static Logger from(java.util.logging.Logger log) {
            return new JDKLogger(log);
        }

        public static Logger from(Class clazz) {
            return from(clazz.getName());
        }

        public static Logger from(String name) {
            return from(java.util.logging.Logger.getLogger(name));
        }

        public static boolean isJDKLogger(Logger log) {
            return log instanceof JDKLogger;
        }

        public static java.util.logging.Logger getAsJDKLogger(Logger log) {
            if (!isJDKLogger(log)) {
                throw new IllegalArgumentException("Not a JDK Logger");
            }
            return ((JDKLogger) log).log;
        }
    }

}