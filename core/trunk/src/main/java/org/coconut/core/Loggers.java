/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.PrintStream;

/**
 * This class is used for creating {@link Logger} wrappers from popular logging frameworks
 * such as <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/logging/package-summary.html">
 * Standard JDK logging </a>, <a href="http://logging.apache.org/log4j/"> Log4j </a> or <a
 * href="http://commons.apache.org/logging/"> commons logging </a>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public final class Loggers {
    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Loggers() {}

    // /CLOVER:ON

    /**
     * Returns the name of the specified logger or <code>null</code> if the name could
     * not be determinded.
     * 
     * @param logger
     *            the logger for which the name should be returned
     * @return the name of the specified logger or <code>null</code> if the name could
     *         not be determinded
     */
    public static String getName(Logger logger) {
        return logger instanceof Loggers.AbstractLogger ? ((Loggers.AbstractLogger) logger)
                .getName() : null;
    }

    /**
     * Creates a new Logger that ignores any input.
     * 
     * @return a logger that ignores any input.
     */
    public static Logger nullLog() {
        return new SimpleLogger(Logger.Level.Off.getLevel());
    }

    /**
     * Creates a new Logger that ignores any input below the specified level. Any logging
     * messages on this level or above it, will be logged to the specified printstream.
     * 
     * @param level
     *            the maximum log level to log
     * @param ps
     *            the printstream to output to
     * @return a printstream logger
     * @throws NullPointerException
     *             if the specified PrintStream is <code>null</code>
     */
    public static Logger printStreamLogger(Logger.Level level, PrintStream ps) {
        return new SimpleLogger(level.getLevel(), ps);
    }

    /**
     * Creates a new Logger that ignores any input below the specified level. Any logging
     * messages on this level or above it, will be logged to {@link System#err}.
     * 
     * @param level
     *            the maximum log level to log
     * @return a system.err logger
     */
    public static Logger systemErrLogger(Logger.Level level) {
        return new SimpleLogger(level.getLevel(), System.err);
    }

    /**
     * Creates a new Logger that ignores any input below the specified level. Any logging
     * messages on this level or above it, will be logged to {@link System#out}.
     * 
     * @param level
     *            the maximum log level to log
     * @return a system.out logger
     */
    public static Logger systemOutLogger(Logger.Level level) {
        return new SimpleLogger(level.getLevel(), System.out);
    }

    /**
     * Used to access commons logging.
     */
    public final static class Commons {

        // /CLOVER:OFF
        /** Cannot instantiate. */
        private Commons() {}

        // /CLOVER:ON
        /**
         * Shorthand for {@link #from(String)}.
         * 
         * @param clazz
         *            name of clazz will be used as the name of the logger to retrieve.
         *            See getLogger(String) for more detailed information.
         * @return returns the logger for the specified class
         */
        public static Logger from(Class<?> clazz) {
            return from(org.apache.commons.logging.LogFactory.getLog(clazz));
        }

        public static Logger from(org.apache.commons.logging.Log log) {
            return new CommonsLogger(log);
        }

        public static Logger from(String name) {
            return from(org.apache.commons.logging.LogFactory.getLog(name));
        }

        public static org.apache.commons.logging.Log getAsCommonsLogger(Logger log) {
            if (!isCommonsLogger(log)) {
                throw new IllegalArgumentException("Not a Commons Logger");
            }
            return ((CommonsLogger) log).log;
        }

        public static boolean isCommonsLogger(Logger log) {
            return log instanceof CommonsLogger;
        }
    }

    public final static class JDK {
        // /CLOVER:OFF
        /** Cannot instantiate. */
        private JDK() {}

        // /CLOVER:ON
        /**
         * Shorthand for {@link #from(String)}.
         * 
         * @param clazz
         *            name of clazz will be used as the name of the logger to retrieve.
         *            See getLogger(String) for more detailed information.
         * @return returns the logger for the specified class
         */
        public static Logger from(Class<?> clazz) {
            return from(clazz.getName());
        }

        public static Logger from(java.util.logging.Logger log) {
            return new JDKLogger(log);
        }

        public static Logger from(String name) {
            return from(java.util.logging.Logger.getLogger(name));
        }

        public static java.util.logging.Logger getAsJDKLogger(Logger log) {
            if (!isJDKLogger(log)) {
                throw new IllegalArgumentException("Not a JDK Logger");
            }
            return ((JDKLogger) log).logger;
        }

        public static boolean isJDKLogger(Logger log) {
            return log instanceof JDKLogger;
        }
    }

    /**
     * This class is used for creating Log4j wrapper Loggers.
     * 
     * <pre>
     * Logger logger = Loggers.Log4j.from(myLog4jLogger);
     * </pre>
     */
    public final static class Log4j {
        // /CLOVER:OFF
        /** Cannot instantiate. */
        private Log4j() {}

        // /CLOVER:ON
        /**
         * Shorthand for {@link #from(String)}.
         * 
         * @param clazz
         *            name of clazz will be used as the name of the logger to retrieve.
         *            See getLogger(String) for more detailed information.
         * @return returns the logger for the specified class
         */
        public static Logger from(Class<?> clazz) {
            return from(org.apache.log4j.Logger.getLogger(clazz));
        }

        /**
         * Wraps a Log4j logger.
         * 
         * @param logger
         *            the Log4j logger
         * @return a wrapped Log4j logger
         */
        public static Logger from(org.apache.log4j.Logger logger) {
            return new Log4JLogger(logger);
        }

        public static Logger from(String name) {
            return from(org.apache.log4j.Logger.getLogger(name));
        }

        public static org.apache.log4j.Logger getAsLog4jLogger(Logger logger) {
            if (!isLog4jLogger(logger)) {
                throw new IllegalArgumentException("Not a JDK Logger");
            }
            return ((Log4JLogger) logger).logger;
        }

        /**
         * Returns whether or not the specified logger encapsulates a Log4J logger.
         * 
         * @param logger
         * @return
         */
        public static boolean isLog4jLogger(Logger logger) {
            return logger instanceof Log4JLogger;
        }
    }

    /**
     * An AbstractLogger that all logger wrappers extend.
     */
    static abstract class AbstractLogger implements Logger {
        /** {@inheritDoc} */
        public void debug(String message) {
            log(Level.Debug, message);
        }

        /** {@inheritDoc} */
        public void debug(String message, Throwable cause) {
            log(Level.Debug, message, cause);
        }

        /** {@inheritDoc} */
        public void error(String message) {
            log(Level.Error, message);
        }

        /** {@inheritDoc} */
        public void error(String message, Throwable cause) {
            log(Level.Error, message, cause);
        }

        /** {@inheritDoc} */
        public void fatal(String message) {
            log(Level.Fatal, message);
        }

        /** {@inheritDoc} */
        public void fatal(String message, Throwable cause) {
            log(Level.Fatal, message, cause);
        }

        /**
         * Returns the name of the logger.
         * 
         * @return the name of the logger
         */
        public abstract String getName();

        /** {@inheritDoc} */
        public void info(String message) {
            log(Level.Info, message);
        }

        /** {@inheritDoc} */
        public void info(String message, Throwable cause) {
            log(Level.Info, message, cause);
        }

        /** {@inheritDoc} */
        public boolean isDebugEnabled() {
            return isEnabled(Level.Debug);
        }

        /** {@inheritDoc} */
        public boolean isErrorEnabled() {
            return isEnabled(Level.Error);
        }

        /** {@inheritDoc} */
        public boolean isFatalEnabled() {
            return isEnabled(Level.Fatal);
        }

        /** {@inheritDoc} */
        public boolean isInfoEnabled() {
            return isEnabled(Level.Info);
        }

        /** {@inheritDoc} */
        public boolean isTraceEnabled() {
            return isEnabled(Level.Trace);
        }

        /** {@inheritDoc} */
        public boolean isWarnEnabled() {
            return isEnabled(Level.Warn);
        }

        /** {@inheritDoc} */
        public void trace(String message) {
            log(Level.Trace, message);
        }

        /** {@inheritDoc} */
        public void trace(String message, Throwable cause) {
            log(Level.Trace, message, cause);
        }

        /** {@inheritDoc} */
        public void warn(String message) {
            log(Level.Warn, message);
        }

        /** {@inheritDoc} */
        public void warn(String message, Throwable cause) {
            log(Level.Warn, message, cause);
        }
    }

    /**
     * The wrapper class commons logging.
     */
    final static class CommonsLogger extends AbstractLogger {
        /** The commons Log class we are wrapping. */
        private final org.apache.commons.logging.Log log;

        /**
         * Creates a new Logger by wrapping a commons Log class.
         * 
         * @param log
         *            the log to wrap
         */
        private CommonsLogger(org.apache.commons.logging.Log log) {
            this.log = log;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            if (log instanceof org.apache.commons.logging.impl.Jdk14Logger) {
                return ((org.apache.commons.logging.impl.Jdk14Logger) log).getLogger().getName();
            } else if (log instanceof org.apache.commons.logging.impl.Log4JLogger) {
                return ((org.apache.commons.logging.impl.Log4JLogger) log).getLogger().getName();
            } else {
                return null;// or should we throw an exception?
            }
        }

        /** {@inheritDoc} */
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

        /** {@inheritDoc} */
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

        /** {@inheritDoc} */
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
    }

    /**
     * The wrapper class for a JDK logger.
     */
    final static class JDKLogger extends AbstractLogger {

        /** The logger we are wrapping. */
        private final java.util.logging.Logger logger;

        /**
         * Creates a new JDKLogger from the specified logger.
         * 
         * @param logger
         *            the logger to wrap
         */
        JDKLogger(java.util.logging.Logger logger) {
            this.logger = logger;
        }

        /** {@inheritDoc} */
        public String getName() {
            return logger.getName();
        }

        /** {@inheritDoc} */
        public boolean isEnabled(Level level) {
            return logger.isLoggable(from(level));
        }

        /** {@inheritDoc} */
        public void log(Logger.Level level, String message) {
            logger.log(from(level), message);
        }

        /** {@inheritDoc} */
        public void log(Logger.Level level, String message, Throwable cause) {
            logger.log(from(level), message, cause);
        }

        /**
         * Converts from a {@link Level} to a {@link java.util.logging.Level}.
         * 
         * @param level
         *            the level to convert
         * @return the converted level
         */
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

    /**
     * The wrapper class for a Log4j logger.
     */
    final static class Log4JLogger extends AbstractLogger {

        /** The logger we are wrapping. */
        private final org.apache.log4j.Logger logger;

        /**
         * Creates a new Log4JLogger from the specified logger.
         * 
         * @param logger
         *            the logger to wrap
         */
        Log4JLogger(org.apache.log4j.Logger logger) {
            this.logger = logger;
        }

        /** {@inheritDoc} */
        public String getName() {
            return logger.getName();
        }

        /** {@inheritDoc} */
        public boolean isEnabled(Level level) {
            return logger.isEnabledFor(from(level));
        }

        /** {@inheritDoc} */
        public void log(Logger.Level level, String message) {
            log(level, message, null);
        }

        /** {@inheritDoc} */
        public void log(Logger.Level level, String message, Throwable cause) {
            logger.log(from(level), message, cause);
        }

        /**
         * Converts from a {@link Level} to a {@link org.apache.log4j.Level}.
         * 
         * @param level
         *            the level to convert
         * @return the converted level
         */
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

    /**
     * A simple logger that prints logging information to a PrintStream.
     */
    final static class SimpleLogger extends AbstractLogger {
        /** The level to log at. */
        private final int level;

        /** The PrintStream to write to. */
        private final PrintStream stream;

        /**
         * Creates a new SimpleLogger, only used for the {@link Loggers#nullLog()} method.
         * 
         * @param level
         *            the level to log at
         */
        SimpleLogger(int level) {
            this.level = level;
            this.stream = null;
        }

        /**
         * Creates a new SimpleLogger that logs to the specified print stream at the
         * specified level.
         * 
         * @param level
         *            the level to log at
         * @param stream
         *            the stream to log to
         */
        SimpleLogger(int level, PrintStream stream) {
            if (stream == null) {
                throw new NullPointerException("stream is null");
            }
            this.level = level;
            this.stream = stream;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "simple";
        }

        /** {@inheritDoc} */
        public boolean isEnabled(Level level) {
            return this.level <= level.getLevel();
        }

        /** {@inheritDoc} */
        public void log(Logger.Level l, String message) {
            log(l, message, null);
        }

        /** {@inheritDoc} */
        public void log(Logger.Level l, String message, Throwable cause) {
            if (stream != null && level <= l.getLevel()) {
                stream.println(message);
                if (cause != null)
                    cause.printStackTrace(stream);
            }
        }
    }

}
