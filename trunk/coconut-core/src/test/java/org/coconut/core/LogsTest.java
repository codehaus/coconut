/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import org.apache.commons.logging.impl.SimpleLog;
import org.coconut.test.MockTestCase;

import org.jmock.Mock;

/**
 * 
 * Test of different loggers.
 * 
 * TODO: log4j, commons logging test of real output
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class LogsTest extends MockTestCase {

    public void testCommonsLogging() {
        Mock mock = mock(org.apache.commons.logging.Log.class);
        Throwable t = new Throwable();
        mock.expects(once()).method("debug").with(eq("a"));
        mock.expects(once()).method("debug").with(eq("b"), eq(t));
        mock.expects(once()).method("error").with(eq("c"));
        mock.expects(once()).method("error").with(eq("d"), eq(t));
        mock.expects(once()).method("fatal").with(eq("e"));
        mock.expects(once()).method("fatal").with(eq("f"), eq(t));
        mock.expects(once()).method("info").with(eq("g"));
        mock.expects(once()).method("info").with(eq("h"), eq(t));
        mock.expects(once()).method("trace").with(eq("i"));
        mock.expects(once()).method("trace").with(eq("j"), eq(t));
        mock.expects(once()).method("warn").with(eq("k"));
        mock.expects(once()).method("warn").with(eq("l"), eq(t));
        mock.expects(once()).method("isDebugEnabled").will(returnValue(true));
        mock.expects(once()).method("isErrorEnabled").will(returnValue(true));
        mock.expects(once()).method("isFatalEnabled").will(returnValue(true));
        mock.expects(once()).method("isInfoEnabled").will(returnValue(true));
        mock.expects(once()).method("isTraceEnabled").will(returnValue(true));
        mock.expects(once()).method("isWarnEnabled").will(returnValue(true));

        Log l = Logs.Commons.from((org.apache.commons.logging.Log) mock.proxy());
        runMock(l, t);
    }
    public void testCommonsCacheLogging() {
        InnerPrintStream str = InnerPrintStream.getErr();

        System.setProperty("org.apache.commons.logging.Log", SimpleLog.class.getName());
        System.setProperty("org.apache.commons.logging.simplelog.showlogname", "true");
        System.setProperty("org.apache.commons.logging.simplelog.showShortLogname",
                "false");
        Log l = Logs.Commons.from(LogsTest.class);
        l.error("test error");
        assertTrue(str.last.getLast().indexOf(LogsTest.class.getName()) >= 0);
        assertTrue(str.last.getLast().indexOf("test error") >= 0);
        str.terminate();
    }

    public void testCommonsCacheFromClassLogging() {
//        InnerPrintStream str = InnerPrintStream.getErr();
//
//        System.setProperty("org.apache.commons.logging.Log", SimpleLog.class.getName());
//        System.setProperty("org.apache.commons.logging.simplelog.showlogname", "true");
//        System.setProperty("org.apache.commons.logging.simplelog.showShortLogname",
//                "false");
//        Log l = Logs.Commons.fromCacheClass();
//        l.error("test error");
//        assertTrue(str.last.getLast().indexOf(Cache.class.getName()) >= 0);
//        assertTrue(str.last.getLast().indexOf("test error") >= 0);
//        str.terminate();
    }

    public void testJDKLogging() {
        java.util.logging.Handler[] handlers = java.util.logging.Logger.getLogger("").getHandlers();
        for (int index = 0; index < handlers.length; index++) {
            handlers[index].setLevel(java.util.logging.Level.FINEST);
        }
        java.util.logging.Logger l = java.util.logging.Logger.getLogger("FooLogger");
        l.setLevel(java.util.logging.Level.FINEST);
        Log log = Logs.JDK.from(l);

        testLevelOn(log, Log.Level.Trace.getLevel());

        l.setLevel(java.util.logging.Level.OFF);
        testLevelOn(log, Log.Level.Fatal.getLevel() + 1);
    }
    public void testJDKNoLogging() {
        java.util.logging.Handler[] handlers = java.util.logging.Logger.getLogger("").getHandlers();
        for (int index = 0; index < handlers.length; index++) {
            handlers[index].setLevel(java.util.logging.Level.FINEST);
        }
        java.util.logging.Logger l = java.util.logging.Logger.getLogger("FooLogger");
        l.setLevel(java.util.logging.Level.OFF);
        Log log = Logs.JDK.from(l);
        testIgnoreLog(log);
    }

    public void testLog4J() {
        org.apache.log4j.Logger logger=org.apache.log4j.Logger.getRootLogger();
        
        logger.setLevel(org.apache.log4j.Level.ALL);
        Log log = Logs.Log4j.from(logger);

        testLevelOn(log, Log.Level.Trace.getLevel());

        logger.setLevel(org.apache.log4j.Level.OFF);
        testLevelOn(log, Log.Level.Fatal.getLevel() + 1);


    }
    
    public void testLog4jNoLogging() {
        org.apache.log4j.Logger logger=org.apache.log4j.Logger.getRootLogger();

        logger.setLevel(org.apache.log4j.Level.OFF);
        Log log = Logs.Log4j.from(logger);
        testIgnoreLog(log);
    }
    
    
    public void testIgnoreLog(Log log) {
        InnerPrintStream outStr = InnerPrintStream.get();
        InnerPrintStream errStr = InnerPrintStream.getErr();
        Throwable t = new Throwable();
        testLevelOn(log, Log.Level.Fatal.getLevel() + 1);
        log.trace("trace test a");
        log.trace("trace test b", t);
        log.debug("debug test a");
        log.debug("debug test b", t);
        log.info("info test a");
        log.info("info test b", t);
        log.warn("warn test a");
        log.warn("warn test b", t);
        log.error("error test a");
        log.error("error test b", t);
        log.fatal("fatal test a");
        log.fatal("fatal test b", t);
        assertEquals(outStr.last.getLast(), "");
        assertEquals(errStr.last.getLast(), "");

        outStr.terminate();
        errStr.terminate();
    }
    public void testSimpleLogging() {
        InnerPrintStream str = InnerPrintStream.get();
        Log log = Logs.systemOutLog(Log.Level.Trace);

        testLevelOn(log, Log.Level.Trace.getLevel());
        log.trace("trace test a");
        assertTrue(str.last.getLast().indexOf("trace test a") >= 0);

        log.debug("debug test a");
        assertTrue(str.last.getLast().indexOf("debug test a") >= 0);

        log.info("info test a");
        assertTrue(str.last.getLast().indexOf("info test a") >= 0);

        log.warn("warn test a");
        assertTrue(str.last.getLast().indexOf("warn test a") >= 0);

        log.error("error test a");
        assertTrue(str.last.getLast().indexOf("error test a") >= 0);

        log.fatal("fatal test a");
        assertTrue(str.last.getLast().indexOf("fatal") >= 0);
        str.terminate();
    }

    public void testSimpleLoggingWithException() {
        InnerPrintStream str = InnerPrintStream.get();
        Log log = Logs.systemOutLog(Log.Level.Trace);
        Throwable t = new Throwable();
        int l=t.getStackTrace().length+1;
        testLevelOn(log, Log.Level.Trace.getLevel());
        log.trace("trace test a", t);
        assertTrue(str.getFromLast(l).indexOf("trace test a") >= 0);

        log.debug("debug test a",t);
        assertTrue(str.getFromLast(l).indexOf("debug test a") >= 0);

        log.info("info test a",t);
        assertTrue(str.getFromLast(l).indexOf("info test a") >= 0);

        log.warn("warn test a",t);
        assertTrue(str.getFromLast(l).indexOf("warn test a") >= 0);

        log.error("error test a",t);
        assertTrue(str.getFromLast(l).indexOf("error test a") >= 0);

        log.fatal("fatal test a",t);
        assertTrue(str.getFromLast(l).indexOf("fatal") >= 0);
        str.terminate();
    }
    public void testNullLogger() {
        InnerPrintStream.get();
        Log log = Logs.nullLog();
        testIgnoreLog(log);
    }

    private static class InnerPrintStream {
        PrintStream old;
        PrintStream p;
        boolean isErr;
        LinkedList<String> last = new LinkedList<String>();

        String getFromLast(int pos) {
            int size = last.size();
            return last.get(size - pos - 1);
        }
        static InnerPrintStream get() {
            InnerPrintStream ps = new InnerPrintStream();
            ps.p = new PrintStream(ps.new MyOutput());
            ps.old = System.out;
            ps.last.add("");
            System.setOut(ps.p);
            return ps;
        }
        static InnerPrintStream getErr() {
            InnerPrintStream ps = new InnerPrintStream();
            ps.p = new PrintStream(ps.new MyOutput());
            ps.old = System.err;
            ps.last.add("");
            System.setErr(ps.p);
            ps.isErr = true;
            return ps;
        }
        public void printString(String str) {
            last.add(str);
        }
        public void terminate() {
            if (isErr) {
                System.setErr(old);
            } else {
                System.setOut(old);
            }
        }

        private class MyOutput extends OutputStream {
            StringBuffer buf = new StringBuffer();

            public void write(int b) throws IOException {
                buf.append((char) b);
                // System.err.println(b);
                if (b == 10) {
                    printString(buf.toString());
                    buf = new StringBuffer();
                }
            }
        }
    }

    private void runMock(Log log, Throwable t) {
        log.debug("a");
        log.debug("b", t);
        log.error("c");
        log.error("d", t);
        log.fatal("e");
        log.fatal("f", t);
        log.info("g");
        log.info("h", t);
        log.trace("i");
        log.trace("j", t);
        log.warn("k");
        log.warn("l", t);
        testLevelOn(log, Log.Level.Trace.getLevel());
    }

    private void testLevelOn(Log log, int level) {
        assertEquals(level <= Log.Level.Trace.getLevel(), log.isTraceEnabled());
        assertEquals(level <= Log.Level.Debug.getLevel(), log.isDebugEnabled());
        assertEquals(level <= Log.Level.Info.getLevel(), log.isInfoEnabled());
        assertEquals(level <= Log.Level.Warn.getLevel(), log.isWarnEnabled());
        assertEquals(level <= Log.Level.Error.getLevel(), log.isErrorEnabled());
        assertEquals(level <= Log.Level.Fatal.getLevel(), log.isFatalEnabled());
    }
}
