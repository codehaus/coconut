/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.internal.util;

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.cake.util.Loggers;
import org.codehaus.cake.util.Logger.Level;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LogHelperTest {
    static final ResourceBundle bundle = ResourceBundle
            .getBundle("org.codehaus.cake.internal.util.loghelper");

    Document doc;

    Element e;

    @Before
    public void setup() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        doc = builder.newDocument();
        e = doc.createElement("element");
        doc.appendChild(e);
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
