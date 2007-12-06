/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;
import java.util.logging.LogManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.impl.Jdk14Logger;
import org.coconut.core.Logger;
import org.coconut.core.Loggers;
import org.coconut.core.Logger.Level;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LogHelperTest {
    static final ResourceBundle bundle = ResourceBundle
            .getBundle("org.coconut.internal.util.xmlutil");

    Document doc;

    Element e;

    @Before
    public void setup() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        doc = builder.newDocument();
        e = doc.createElement("element");
        doc.appendChild(e);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readLogIAE() {
        LogHelper.readLog(e);
    }

    @Test
    public void writeLoggerComment() {
        Element ee = LogHelper.saveLogger(doc, bundle, e, "mylogger", Loggers.printStreamLogger(
                Level.Debug, System.out));
        assertEquals(2, ee.getChildNodes().getLength());
        assertTrue(ee.getChildNodes().item(1) instanceof Comment);
    }

    @Test
    public void noLogger() {
        assertNull(LogHelper.saveLogger(doc, bundle, e, "logger", null));
        assertNull(LogHelper.readLog(null));
    }

    @Test
    public void nullLogger() {
        Element eh = LogHelper.saveLogger(doc, bundle, e, "myLogger", Loggers.NULL_LOGGER);
        assertSame(Loggers.NULL_LOGGER, LogHelper.readLog(eh));
    }

    @Test
    public void JdkLogging() {
        Element eh = LogHelper.saveLogger(doc, bundle, e, "myLogger", Loggers.JDK.from("foo"));
        Logger l = LogHelper.readLog(eh);
        assertSame(Loggers.JDK.from("boo").getClass(), l.getClass());
        assertSame(LogManager.getLogManager().getLogger("foo"), Loggers.JDK.getAsJDKLogger(l));
    }

    @Test
    public void CommonsLogging() {
        Logger logger = Loggers.Commons.from(new Jdk14Logger("foo"));
        Element eh = LogHelper.saveLogger(doc, bundle, e, "myLogger", logger);
        Logger l = LogHelper.readLog(eh);
        assertSame(Loggers.Commons.from("foo").getClass(), l.getClass());
        assertSame(org.apache.commons.logging.LogFactory.getLog("foo"), Loggers.Commons
                .getAsCommonsLogger(l));
    }

    @Test
    public void Log4JLogging() {
        Element eh = LogHelper.saveLogger(doc, bundle, e, "myLogger", Loggers.Log4j.from("foo"));
        Logger l = LogHelper.readLog(eh);
        assertSame(Loggers.Log4j.from("foo").getClass(), l.getClass());
        assertSame(org.apache.log4j.Logger.getLogger("foo"), Loggers.Log4j.getAsLog4jLogger(l));
    }

    @Test
    public void systemErrLogger() {
        Element eh = LogHelper.saveLogger(doc, bundle, e, "myLogger", Loggers
                .systemErrLogger(Level.Warn));
        assertSame(Loggers.systemErrLogger(Level.Error).getClass(), LogHelper.readLog(eh)
                .getClass());
        assertEquals(Level.Warn, LogHelper.toLevel(eh));
    }

    @Test
    public void systemOutLogger() {
        Element eh = LogHelper.saveLogger(doc, bundle, e, "myLogger", Loggers
                .systemOutLogger(Level.Off));
        assertSame(Loggers.systemOutLogger(Level.Error).getClass(), LogHelper.readLog(eh)
                .getClass());
        assertEquals(Level.Off, LogHelper.toLevel(eh));
    }

    @Test
    public void toLevel() {
        e.setAttribute("level", Level.Debug.toString());
        assertEquals(Level.Debug, LogHelper.toLevel(e));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toLevelIAE() {
        e.setAttribute("level", "unknownlevel");
        assertEquals(Level.Debug, LogHelper.toLevel(e));
    }

    @Test
    public void getLogLevel() {
        assertEquals(Level.Trace, LogHelper.getLogLevel(Loggers.systemErrLogger(Level.Trace)));
        assertEquals(Level.Debug, LogHelper.getLogLevel(Loggers.systemErrLogger(Level.Debug)));
        assertEquals(Level.Info, LogHelper.getLogLevel(Loggers.systemErrLogger(Level.Info)));
        assertEquals(Level.Warn, LogHelper.getLogLevel(Loggers.systemErrLogger(Level.Warn)));
        assertEquals(Level.Error, LogHelper.getLogLevel(Loggers.systemErrLogger(Level.Error)));
        assertEquals(Level.Fatal, LogHelper.getLogLevel(Loggers.systemErrLogger(Level.Fatal)));
        assertEquals(Level.Off, LogHelper.getLogLevel(Loggers.systemErrLogger(Level.Off)));
    }
}
