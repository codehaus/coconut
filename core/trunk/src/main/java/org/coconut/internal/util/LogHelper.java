/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.util.ResourceBundle;

import org.coconut.core.Logger;
import org.coconut.core.Loggers;
import org.coconut.core.Logger.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Various {@link Logger} utilities.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class LogHelper {

    public final static String LOG_LEVEL_ATRB = "level";

    public final static String LOG_TYPE_ATRB = "type";

    private final static String COMMONS_LOGGING = "commons";

    private final static String JDK_LOGGING = "jul";

    private final static String LOG4J = "log4j";

    private final static String NULL_LOGGER = "null-logger";

    private final static String SYSTEM_ERR_LOGGER = "system.err-logger";

    private final static String SYSTEM_OUT_LOGGER = "system.out-logger";

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private LogHelper() {}

    // /CLOVER:ON

    public static Logger readLog(Element log) {
        if (log != null) {
            String type = log.getAttribute(LOG_TYPE_ATRB);
            if (type.equals(JDK_LOGGING)) {
                return Loggers.JDK.from(log.getTextContent());
            } else if (type.equals(LOG4J)) {
                return Loggers.Log4j.from(log.getTextContent());
            } else if (type.equals(COMMONS_LOGGING)) {
                return Loggers.Commons.from(log.getTextContent());
            } else if (type.equals(NULL_LOGGER)) {
                return Loggers.NULL_LOGGER;
            } else if (type.equals(SYSTEM_ERR_LOGGER)) {
                return Loggers.systemErrLogger(toLevel(log));
            } else if (type.equals(SYSTEM_OUT_LOGGER)) {
                return Loggers.systemOutLogger(toLevel(log));
            } else {
                throw new IllegalArgumentException("Unknown logger " + type);
            }
        }
        return null;
    }

    public static Element saveLogger(Document doc, ResourceBundle rb, Element e,
            String elementName, Logger logger) {
        if (logger != null) {
            String name = Loggers.getName(logger);
            Element eh = doc.createElement(name);
            e.appendChild(eh);
            eh.setTextContent(name);
            if (Loggers.Log4j.isLog4jLogger(logger)) {
                eh.setAttribute(LOG_TYPE_ATRB, LOG4J);
            } else if (Loggers.Commons.isCommonsLogger(logger)) {
                eh.setAttribute(LOG_TYPE_ATRB, COMMONS_LOGGING);
            } else if (Loggers.JDK.isJDKLogger(logger)) {
                eh.setAttribute(LOG_TYPE_ATRB, JDK_LOGGING);
            } else if (Loggers.getName(logger).equals(NULL_LOGGER)) {
                eh.setAttribute(LOG_TYPE_ATRB, NULL_LOGGER);
            } else if (Loggers.getName(logger).equals(SYSTEM_ERR_LOGGER)) {
                eh.setAttribute(LOG_TYPE_ATRB, SYSTEM_ERR_LOGGER);
                eh.setAttribute(LOG_LEVEL_ATRB, getLogLevel(logger).toString());
            } else if (Loggers.getName(logger).equals(SYSTEM_OUT_LOGGER)) {
                eh.setAttribute(LOG_TYPE_ATRB, SYSTEM_OUT_LOGGER);
                eh.setAttribute(LOG_LEVEL_ATRB, getLogLevel(logger).toString());
            } else {
                XmlUtil.addComment(doc, rb, LogHelper.class, "unknownLoggerType", eh, logger
                        .getClass());
            }
            return eh;
        }
        return null;
    }

    static Level getLogLevel(Logger logger) {
        if (!logger.isFatalEnabled()) {
            return Level.Off;
        } else if (!logger.isErrorEnabled()) {
            return Level.Fatal;
        } else if (!logger.isWarnEnabled()) {
            return Level.Error;
        } else if (!logger.isInfoEnabled()) {
            return Level.Warn;
        } else if (!logger.isDebugEnabled()) {
            return Level.Info;
        } else if (!logger.isTraceEnabled()) {
            return Level.Debug;
        } else {
            return Level.Trace;
        }
    }

    static Level toLevel(Element e) {
        String level = e.getAttribute(LOG_LEVEL_ATRB);
        Level l = Level.valueOf(level);
        return l;
    }

    /**
     * Converts from a {@link Level} to a {@link java.util.logging.Level}.
     * 
     * @param level
     *            the level to convert
     * @return the converted level
     */
    public static java.util.logging.Level toJdkLevel(Level level) {
        switch (level) {
        case Trace:
            return java.util.logging.Level.FINEST;
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

    /**
     * An AbstractLogger that all logger wrappers extend.
     */
    public static abstract class AbstractLogger implements Logger {
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
        public void log(Logger.Level l, String message) {
            log(l, message, null);
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
}
