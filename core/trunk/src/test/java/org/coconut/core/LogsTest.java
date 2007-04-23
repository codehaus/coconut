/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.impl.NoOpLog;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.coconut.core.Log.Level;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test of different loggers.
 * <p>
 * The tests that tests commons logging are a bit fragile. Just stay away from
 * commons logging it is a serious PITA.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@RunWith(JMock.class)
public class LogsTest {

	Mockery context = new JUnit4Mockery();

	@Test
	public void testToHandler() {
		final org.apache.commons.logging.Log log = context
				.mock(org.apache.commons.logging.Log.class);
		final Throwable t = new Throwable();
		context.checking(new Expectations() {
			{
				one(log).debug("a");
				one(log).debug("b", t);
				one(log).error("c");
				one(log).error("d", t);
				one(log).fatal("e");
				one(log).fatal("f", t);
				one(log).info("g");
				one(log).info("h", t);
				one(log).trace("i");
				one(log).trace("j", t);
				one(log).warn("k");
				one(log).warn("l", t);
				one(log).isDebugEnabled();
				will(returnValue(true));
				one(log).isErrorEnabled();
				will(returnValue(true));
				one(log).isFatalEnabled();
				will(returnValue(true));
				one(log).isInfoEnabled();
				will(returnValue(true));
				one(log).isTraceEnabled();
				will(returnValue(true));
				one(log).isWarnEnabled();
				will(returnValue(true));
			}
		});
		runMock(Logs.Commons.from(log), t);
	}

	@Test
	public void testCommonsLoggingName() {

		Log l = Logs.Commons.from(new Jdk14Logger("foobar"));
		assertEquals("foobar", Logs.getName(l));
		l = Logs.Commons.from(new Log4JLogger(LogManager.getLogger("foobar2")));
		assertEquals("foobar2", Logs.getName(l));
		l = Logs.Commons.from(new NoOpLog("dkdkd"));
		assertNull(Logs.getName(l));
	}

	@Test
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

	@Test
	public void testCommonsLogging2() {
		Log l = Logs.Commons.from("asv");
		org.apache.commons.logging.Log ll = Logs.Commons.getAsCommonsLogger(l);
		assertTrue(ll instanceof SimpleLog);
		assertTrue(Logs.Commons.isCommonsLogger(l));
		assertFalse(Logs.Commons.isCommonsLogger(Logs.systemErrLog(Level.Error)));
		try {
			Logs.Commons.getAsCommonsLogger(Logs.systemErrLog(Level.Error));
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException iea) {}
	}

	@Test
	public void testJDKLogging() {
		java.util.logging.Handler[] handlers = java.util.logging.Logger.getLogger("")
				.getHandlers();
		for (int index = 0; index < handlers.length; index++) {
			handlers[index].setLevel(java.util.logging.Level.FINEST);
		}
		java.util.logging.Logger l = java.util.logging.Logger.getLogger("FooLogger");
		l.setLevel(java.util.logging.Level.FINEST);
		Log log = Logs.JDK.from(l);
		assertEquals("FooLogger", Logs.getName(log));
		testLevelOn(log, Log.Level.Trace.getLevel());

		l.setLevel(java.util.logging.Level.OFF);
		testLevelOn(log, Log.Level.Fatal.getLevel() + 1);
	}

	public void testJDKLogging2() {
		Log l = Logs.JDK.from("asv1");
		java.util.logging.Logger ll = Logs.JDK.getAsJDKLogger(l);
		assertEquals("asv1", ll.getName());
		assertTrue(Logs.JDK.isJDKLogger(l));
		assertFalse(Logs.JDK.isJDKLogger(Logs.systemErrLog(Level.Error)));

		l = Logs.JDK.from("asv1".getClass());
		ll = Logs.JDK.getAsJDKLogger(l);
		assertEquals("asv1".getClass().getCanonicalName(), ll.getName());
		try {
			Logs.JDK.getAsJDKLogger(Logs.systemErrLog(Level.Error));
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException iea) {}
	}

	@Test
	public void testJDKNoLogging() {
		java.util.logging.Handler[] handlers = java.util.logging.Logger.getLogger("")
				.getHandlers();
		for (int index = 0; index < handlers.length; index++) {
			handlers[index].setLevel(java.util.logging.Level.FINEST);
		}
		java.util.logging.Logger l = java.util.logging.Logger.getLogger("FooLogger");
		l.setLevel(java.util.logging.Level.OFF);
		Log log = Logs.JDK.from(l);
		testIgnoreLog(log);
	}

	@Test
	public void testLog4J() {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();

		logger.setLevel(org.apache.log4j.Level.ALL);

		Log log = Logs.Log4j.from(logger);

		testLevelOn(log, Log.Level.Trace.getLevel());

		logger.setLevel(org.apache.log4j.Level.OFF);
		testLevelOn(log, Log.Level.Fatal.getLevel() + 1);

		assertEquals("foobar", Logs.getName(Logs.Log4j.from(LogManager
				.getLogger("foobar"))));
	}

	@Test
	public void testLog4JLogging2() {
		Log l = Logs.Log4j.from("asv1");
		Logger ll = Logs.Log4j.getAsLog4jLogger(l);
		assertEquals("asv1", ll.getName());
		assertTrue(Logs.Log4j.isLog4jLogger(l));
		assertFalse(Logs.Log4j.isLog4jLogger(Logs.systemErrLog(Level.Error)));

		l = Logs.Log4j.from("asv1".getClass());
		ll = Logs.Log4j.getAsLog4jLogger(l);
		assertEquals("asv1".getClass().getCanonicalName(), ll.getName());
		try {
			Logs.Log4j.getAsLog4jLogger(Logs.systemErrLog(Level.Error));
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException iea) {}
	}

	@Test
	public void testLog4jNoLogging() {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();

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

	@Test
	public void testSimpleLogging() {
		InnerPrintStream str = InnerPrintStream.get();
		Log log = Logs.systemOutLog(Log.Level.Trace);

		assertEquals("simple", Logs.getName(log));
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

	@Test
	public void testSimpleLoggingWithException() {
		InnerPrintStream str = InnerPrintStream.get();
		Log log = Logs.systemOutLog(Log.Level.Trace);
		Throwable t = new Throwable();
		int l = t.getStackTrace().length + 1;
		testLevelOn(log, Log.Level.Trace.getLevel());
		log.trace("trace test a", t);
		assertTrue(str.getFromLast(l).indexOf("trace test a") >= 0);

		log.debug("debug test a", t);
		assertTrue(str.getFromLast(l).indexOf("debug test a") >= 0);

		log.info("info test a", t);
		assertTrue(str.getFromLast(l).indexOf("info test a") >= 0);

		log.warn("warn test a", t);
		assertTrue(str.getFromLast(l).indexOf("warn test a") >= 0);

		log.error("error test a", t);
		assertTrue(str.getFromLast(l).indexOf("error test a") >= 0);

		log.fatal("fatal test a", t);
		assertTrue(str.getFromLast(l).indexOf("fatal") >= 0);
		str.terminate();
	}

	@Test
	public void testNullLogger() {
		InnerPrintStream.get();
		Log log = Logs.nullLog();
		testIgnoreLog(log);
	}

	@Test
	public void testStaticMethods() {
		assertNull(Logs.getName(context.mock(Log.class)));
		try {
			Logs.printStreamLog(Level.Error, null);
			fail("Should throw NullPointerException");
		} catch (NullPointerException npe) {}
	}

	static class InnerPrintStream {
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
