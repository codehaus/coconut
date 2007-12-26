/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.PrintStream;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.coconut.internal.util.LogHelper;
import org.coconut.internal.util.LogHelper.AbstractLogger;

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

    /** A logger that ignores all input. */
    public static final Logger NULL_LOGGER = new NullLogger();

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
        return logger instanceof AbstractLogger ? ((AbstractLogger) logger).getName() : null;
    }

    /**
     * Creates a new Logger that ignores any input below the specified level. Any logging
     * messages on this level or above it, will be logged to the specified printstream.
     *
     * @param level
     *            the maximum log level to log
     * @param ps
     *            the printstream to output to
     * @return the newly created logger
     * @throws NullPointerException
     *             if the specified PrintStream is <code>null</code>
     */
    public static Logger printStreamLogger(Logger.Level level, PrintStream ps) {
        return new PrintStreamLogger(level.getLevel(), ps);
    }

    /**
     * Creates a new Logger that ignores any input below the specified level. Any logging
     * messages on this level or above it, will be logged to {@link System#err}.
     *
     * @param level
     *            the maximum log level to log
     * @return the newly created logger
     */
    public static Logger systemErrLogger(Logger.Level level) {
        return new SystemErrLogger(level);
    }

    /**
     * Creates a new Logger that ignores any input below the specified level. Any logging
     * messages on this level or above it, will be logged to {@link System#out}.
     *
     * @param level
     *            the maximum log level to log
     * @return the newly created logger
     */
    public static Logger systemOutLogger(Logger.Level level) {
        return new SystemOutLogger(level);
    }

    /**
     * Used for Commons Logging conversion.
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

        /**
         * Wraps a Commons logging log.
         *
         * @param log
         *            the Commons logging log to wrap
         * @return a wrapped Commons logging logger
         */
        public static Logger from(org.apache.commons.logging.Log log) {
            return new CommonsLogger(log);
        }

        /**
         * Creates a decorated commons logger from the specified name. The commons log is
         * created by calling:
         *
         * <pre>
         * org.apache.commons.logging.LogFactory.getLog(name)
         * </pre>
         *
         * @param name
         *            the name of the logger
         * @return the decorated commons logger
         */
        public static Logger from(String name) {
            return from(org.apache.commons.logging.LogFactory.getLog(name));
        }

        /**
         * Unwraps a wrapped Commons {@link Log} that is wrapped in a {@link Logger}.
         *
         * @param logger
         *            the logger to unwrap
         * @return the unwrapped commons log
         * @throws IllegalArgumentException
         *             if the specified logger is not wrapping a commons log
         */
        public static org.apache.commons.logging.Log getAsCommonsLogger(Logger logger) {
            if (!isCommonsLogger(logger)) {
                throw new IllegalArgumentException("Not a Commons Logger");
            }
            return ((CommonsLogger) logger).log;
        }

        /**
         * Returns whether or not the specified logger encapsulates a Commons Logging log.
         *
         * @param logger
         *            the logger to test
         * @return true is the encapsulated logger is Commons Logging log
         */
        public static boolean isCommonsLogger(Logger logger) {
            return logger instanceof CommonsLogger;
        }
    }

    /**
     * Used for java.util.logger conversion.
     */
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

        /**
         * Wraps a JDK logger.
         *
         * @param logger
         *            the JDK logger to wrap
         * @return a wrapped JDK logger
         */
        public static Logger from(java.util.logging.Logger logger) {
            return new JDKLogger(logger);
        }

        /**
         * Creates a decorated Jdk logger from the specified name. The Jdk logger is
         * created by calling:
         *
         * <pre>
         * java.util.logging.Logger.getLogger(name)
         * </pre>
         *
         * @param name
         *            the name of the logger
         * @return the decorated Jdk logger
         */
        public static Logger from(String name) {
            return from(java.util.logging.Logger.getLogger(name));
        }

        /**
         * Unwraps a wrapped JDK {@link java.util.logging.Logger} that is wrapped in a
         * {@link Logger}.
         *
         * @param logger
         *            the logger to unwrap
         * @return the unwrapped jdk logger
         * @throws IllegalArgumentException
         *             if the specified logger is not wrapping a Jdk logger
         */
        public static java.util.logging.Logger getAsJDKLogger(Logger logger) {
            if (!isJDKLogger(logger)) {
                throw new IllegalArgumentException("Not a JDK Logger");
            }
            return ((JDKLogger) logger).logger;
        }

        /**
         * Returns whether or not the specified logger encapsulates a JDK logger.
         *
         * @param logger
         *            the logger to test
         * @return true is the encapsulated logger is a JDK logger
         */
        public static boolean isJDKLogger(Logger logger) {
            return logger instanceof JDKLogger;
        }
    }

    /**
     * Used for Log4J logging conversion.
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
         *            the Log4j logger to wrap
         * @return a wrapped Log4j logger
         */
        public static Logger from(org.apache.log4j.Logger logger) {
            return new Log4JLogger(logger);
        }

        /**
         * Creates a decorated Log4J logger from the specified name. The Log4J logger is
         * created by calling:
         *
         * <pre>
         * org.apache.log4j.Logger.getLogger(name)
         * </pre>
         *
         * @param name
         *            the name of the logger
         * @return the decorated Log4J logger
         */
        public static Logger from(String name) {
            return from(org.apache.log4j.Logger.getLogger(name));
        }

        /**
         * Unwraps a wrapped Log4J {@link org.apache.log4j.Logger} that is wrapped in a
         * {@link Logger}.
         *
         * @param logger
         *            the logger to unwrap
         * @return the unwrapped Log4J log
         * @throws IllegalArgumentException
         *             if the specified logger is not wrapping a Log4J log
         */
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
         *            the logger to test
         * @return true is the encapsulated logger is a Log4J logger
         */
        public static boolean isLog4jLogger(Logger logger) {
            return logger instanceof Log4JLogger;
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
        @Override
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
        @Override
        public String getName() {
            return logger.getName();
        }

        /** {@inheritDoc} */
        public boolean isEnabled(Level level) {
            return logger.isLoggable(LogHelper.toJdkLevel(level));
        }

        /** {@inheritDoc} */
        @Override
        public void log(Logger.Level level, String message) {
            logger.log(LogHelper.toJdkLevel(level), message);
        }

        /** {@inheritDoc} */
        public void log(Logger.Level level, String message, Throwable cause) {
            logger.log(LogHelper.toJdkLevel(level), message, cause);
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
        @Override
        public String getName() {
            return logger.getName();
        }

        /** {@inheritDoc} */
        public boolean isEnabled(Level level) {
            return logger.isEnabledFor(from(level));
        }

        /** {@inheritDoc} */
        @Override
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
     * A Logger that ignores all input.
     */
    final static class NullLogger extends AbstractLogger implements Serializable {
        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "null-logger";
        }

        /** {@inheritDoc} */
        public boolean isEnabled(Level level) {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public void log(Level level, String message) {}

        /** {@inheritDoc} */
        public void log(Level level, String message, Throwable cause) {}

        /** @return Preserves singleton property */
        private Object readResolve() {
            return NULL_LOGGER;
        }

    }

    /**
     * A simple logger that prints logging information to a PrintStream.
     */
    final static class PrintStreamLogger extends AbstractLogger {
        /** The level to log at. */
        private final int level;

        /** The PrintStream to write to. */
        private final PrintStream stream;

        /**
         * Creates a new SimpleLogger that logs to the specified print stream at the
         * specified level.
         *
         * @param level
         *            the level to log at
         * @param stream
         *            the stream to log to
         */
        PrintStreamLogger(int level, PrintStream stream) {
            if (stream == null) {
                throw new NullPointerException("stream is null");
            }
            this.level = level;
            this.stream = stream;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "simple-logger";
        }

        /** {@inheritDoc} */
        public boolean isEnabled(Level level) {
            return this.level <= level.getLevel();
        }

        /** {@inheritDoc} */
        public void log(Logger.Level l, String message, Throwable cause) {
            if (level <= l.getLevel()) {
                stream.println(message);
                if (cause != null) {
                    cause.printStackTrace(stream);
                }
            }
        }
    }

    /**
     * A simple logger that prints logging information to {@link System#err}.
     */
    final static class SystemErrLogger extends AbstractLogger implements Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 3128919839751961759L;

        /** The level to log at. */
        private final int level;

        /**
         * Creates a new SystemErrLogger.
         *
         * @param level
         *            the level to log at
         */
        SystemErrLogger(Logger.Level level) {
            this.level = level.getLevel();
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "system.err-logger";
        }

        /** {@inheritDoc} */
        public boolean isEnabled(Level level) {
            return this.level <= level.getLevel();
        }

        /** {@inheritDoc} */
        public void log(Level l, String message, Throwable cause) {
            if (level <= l.getLevel()) {
                System.err.println(message);
                if (cause != null) {
                    cause.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * A simple logger that prints logging information to {@link System#out}.
     */
    final static class SystemOutLogger extends AbstractLogger implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = -4803581500251076928L;
        /** The level to log at. */
        private final int level;

        /**
         * Creates a new SystemErrLogger.
         *
         * @param level
         *            the level to log at
         */
        SystemOutLogger(Logger.Level level) {
            this.level = level.getLevel();
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return "system.out-logger";
        }

        /** {@inheritDoc} */
        public boolean isEnabled(Level level) {
            return this.level <= level.getLevel();
        }

        /** {@inheritDoc} */
        public void log(Level l, String message, Throwable cause) {
            if (level <= l.getLevel()) {
                System.out.println(message);
                if (cause != null) {
                    cause.printStackTrace(System.out);
                }
            }
        }
    }

}
