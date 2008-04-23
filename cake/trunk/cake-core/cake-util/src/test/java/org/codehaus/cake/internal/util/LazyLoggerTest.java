/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.LogManager;

import org.codehaus.cake.test.util.SystemErrCatcher;
import org.codehaus.cake.test.util.throwables.RuntimeException1;
import org.codehaus.cake.util.Logger.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LazyLoggerTest {
    LazyLogger ll;

    SystemErrCatcher str;

    @After
    public void after() {
        if (str != null) {
            try {
                // System.out.println(str.toString());
                assertTrue(str.toString().contains("getLogger"));
                assertTrue(str.toString().contains(LazyLogger.class.getName()));

            } finally {
                str.terminate();
            }
        }
    }

    @Before
    public void before() throws IOException {
        str = SystemErrCatcher.get();
        LogManager.getLogManager().readConfiguration();
    }

    @Test
    public void isEnabled() {
        ll = new LazyLogger("lazylogger-isEnabled", "foo321");
        assertEquals("lazylogger-isEnabled", ll.getName());
        assertFalse(ll.isInfoEnabled());
        assertTrue(ll.isWarnEnabled());
    }

    @Test
    public void jdkLogger() {
        java.util.logging.Logger.getLogger("myLogger");
        ll = new LazyLogger("myLogger", "foo321");
        ll.log(Level.Warn, "abc");
        assertTrue(str.toString().contains("abc"));
        assertFalse(str.toString().contains("getLogger"));
        str = null;
    }

    @Test
    public void log() {
        ll = new LazyLogger("lazylogger-log", "foo321");
        assertEquals("lazylogger-log", ll.getName());
        ll.log(Level.Info, "abc");
        assertFalse(str.toString().contains("abc"));
        ll.log(Level.Warn, "abc");
        assertTrue(str.toString().contains("abc"));
    }

    @Test
    public void logException() {
        ll = new LazyLogger("lazylogger-logException", "foo321");
        assertEquals("lazylogger-logException", ll.getName());
        ll.log(Level.Info, "abc", new RuntimeException1());
        assertFalse(str.toString().contains("RuntimeException1"));
        ll.log(Level.Warn, "abc", new RuntimeException1());
        assertTrue(str.toString().contains("abc"));
    }
}
