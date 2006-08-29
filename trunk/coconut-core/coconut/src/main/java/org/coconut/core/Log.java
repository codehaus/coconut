/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * A simple logging interface abstracting logging APIs. The primary reason for
 * using this for coconut-cache is to avoid dependencies on any external logging
 * libraries.
 * <p>
 * Use {@link coconut.cache.util.Logs}to create wrappers from popular logging
 * frameworks such as <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/logging/package-summary.html">
 * Standard JDK logging </a>, <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/logging/package-summary.html">
 * Log4j </a> or <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/logging/package-summary.html">
 * commons logging </a>. Perhaps i'll just use jdk logging per default ...
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface Log {

    /**
     * Check to see if a message of <tt>debug</tt> level would actually be
     * logged by this logger.
     */
    boolean isDebugEnabled();

    /**
     * Check to see if a message of <tt>error</tt> level would actually be
     * logged by this logger.
     */
    boolean isErrorEnabled();

    /**
     * Check to see if a message of <tt>fatal</tt> level would actually be
     * logged by this logger.
     */
    boolean isFatalEnabled();

    /**
     * Check to see if a message of <tt>info</tt> level would actually be
     * logged by this logger.
     */
    boolean isInfoEnabled();

    /**
     * Check to see if a message of <tt>trace</tt> level would actually be
     * logged by this logger.
     */
    boolean isTraceEnabled();

    /**
     * Check to see if a message of <tt>warn</tt> level would actually be
     * logged by this logger.
     */
    boolean isWarnEnabled();

    /**
     * Logs the message if the logger is currently enabled for the trace log
     * level.
     * 
     * @param message
     *            the message to log
     */
    void trace(String message);

    /**
     * Logs the message if the logger is currently enabled for the trace log
     * level.
     * 
     * @param message
     *            the message to log
     * @param cause
     *            the cause to log
     */
    void trace(String message, Throwable cause);

    /**
     * Logs the message if the logger is currently enabled for the debug log
     * level.
     * 
     * @param message
     *            the message to log
     */
    void debug(String message);

    /**
     * Logs the message if the logger is currently enabled for the debug log
     * level.
     * 
     * @param message
     *            the message to log
     * @param cause
     *            the cause to log
     */
    void debug(String message, Throwable cause);

    /**
     * Logs the message if the logger is currently enabled for the info log
     * level.
     * 
     * @param message
     *            the message to log
     */
    void info(String message);

    /**
     * Logs the message if the logger is currently enabled for the info log
     * level.
     * 
     * @param message
     *            the message to log
     * @param cause
     *            the cause to log
     */
    void info(String message, Throwable cause);

    /**
     * Logs the message if the logger is currently enabled for the warn log
     * level.
     * 
     * @param message
     *            the message to log
     */
    void warn(String message);

    /**
     * Logs the message if the logger is currently enabled for the warn log
     * level.
     * 
     * @param message
     *            the message to log
     * @param cause
     *            the cause to log
     */
    void warn(String message, Throwable cause);

    /**
     * Logs the message if the logger is currently enabled for the error log
     * level.
     * 
     * @param message
     *            the message to log
     */
    void error(String message);

    /**
     * Logs the message if the logger is currently enabled for the error log
     * level.
     * 
     * @param message
     *            the message to log
     * @param cause
     *            the cause to log
     */
    void error(String message, Throwable cause);

    /**
     * Logs the message if the logger is currently enabled for the fatal log
     * level.
     * 
     * @param message
     *            the message to log
     */
    void fatal(String message);

    /**
     * Logs the message if the logger is currently enabled for the fatal log
     * level.
     * 
     * @param message
     *            the message to log
     * @param cause
     *            the cause to log
     */
    void fatal(String message, Throwable cause);

    boolean isEnabled(Level level);

    void log(Level level, String message);

    void log(Level level, String message, Throwable cause);

    /*
     * void log(Level level, String message, Throwable cause, Object[] info);
     */
    public enum Level {
        Trace(0), Debug(1), Info(2), Warn(3), Error(4), Fatal(5);

        private final int level;

        private Level(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }
}
