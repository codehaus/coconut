/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.LogManager;

import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.CacheException;
import org.coconut.cache.CacheLoader;
import org.coconut.core.Log;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheErrorHandlerTest {

    @Before
    public void setup() throws IOException {
        // We need to reset logging!
        // There are some problems when changing System.err
        // Because it is cached in the root logger.

        LogManager.getLogManager().readConfiguration();
    }

    @Test
    public void testContructor0() throws IOException {
        CacheErrorHandler ce = new CacheErrorHandler();
        assertFalse(ce.hasLogger());
        assertNull(ce.getCacheName());
        ce.setCacheName("testContructor0");
        assertEquals("testContructor0", ce.getCacheName());
    }

    @Test
    public void testContructor1() {
        Log l = MockTestCase.mockDummy(Log.class);
        CacheErrorHandler ce = new CacheErrorHandler(l);
        assertTrue(ce.hasLogger());
        assertEquals(l, ce.getLogger());
        assertNull(ce.getCacheName());
        ce.setCacheName("testContructor1");
        assertEquals("testContructor1", ce.getCacheName());
    }

    @Test
    public void testLazyInitializeLogger() throws Exception {
        PrintStream ps = System.err;
        try {
            CacheErrorHandler ce = new CacheErrorHandler();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ce.setCacheName("testLazyInitializeLogger");
            System.setErr(new PrintStream(os));
            ce.getLogger();
            assertTrue(os.toString().contains(
                    "org.coconut.cache.testLazyInitializeLogger"));
            os.flush();
            os.reset();

            // warning not logged
            ce.warning("foerer");
            assertEquals("", os.toString());
            ce.unhandledRuntimeException(new IllegalThreadStateException());
            assertTrue(os.toString().contains("IllegalThreadStateException"));
        } finally {
            System.setErr(ps);
        }
    }

    @Test
    public void testLazyInitializeLogger2() throws Exception {
        PrintStream ps = System.err;
        try {
            CacheErrorHandler ce = new CacheErrorHandler();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ce.setCacheName("testLazyInitializeLogger2");
            System.setErr(new PrintStream(os));
            ce.getLogger();
            assertTrue(os.toString().contains(
                    "org.coconut.cache.testLazyInitializeLogger2"));
            os.flush();
            os.reset();

            // warning not logged
            ce.warning("foerer");
            assertEquals("", os.toString());
            ce.unhandledRuntimeException(new IllegalThreadStateException());
            assertTrue(os.toString().contains("IllegalThreadStateException"));
        } finally {
            System.setErr(ps);
        }
    }

    @Test
    public void testLoad() throws Exception {
        PrintStream ps = System.err;
        try {
            CacheErrorHandler ce = new CacheErrorHandler();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ce.setCacheName("foobar");
            System.setErr(new PrintStream(os));
            ce.getLogger();
            ce.unhandledRuntimeException(new IllegalThreadStateException());
            try {
                ce.loadFailed(MockTestCase.mockDummy(CacheLoader.class), 7, true,
                        new IllegalThreadStateException());
                fail("shouldThrow");
            } catch (CacheException ok) {
                assertTrue(os.toString().contains("IllegalThreadStateException"));
                assertTrue(os.toString().contains("7"));
            }
        } finally {
            System.setErr(ps);
        }
    }

    @Test
    public void testLoadAll() throws Exception {
        PrintStream ps = System.err;
        try {
            CacheErrorHandler ce = new CacheErrorHandler();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            System.setErr(new PrintStream(os));
            try {
                ce.loadAllFailed(MockTestCase.mockDummy(CacheLoader.class), Arrays
                        .asList(6, 7), true, new IllegalThreadStateException());
                fail("shouldThrow");
            } catch (CacheException ok) {
                assertTrue(os.toString().contains("IllegalThreadStateException"));
                assertTrue(os.toString().contains("6"));
                assertTrue(os.toString().contains("7"));
            }
        } finally {
            System.setErr(ps);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testSetLoggerNPE() {
        new CacheErrorHandler().setLogger(null);
    }
}
