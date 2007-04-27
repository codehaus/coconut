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
import org.coconut.core.Logger.Level;
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
public class LoggersTest {

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
		runMock(Loggers.Commons.from(log), t);
	}

	@Test
	public void testCommonsLoggingName() {

		Logger l = Loggers.Commons.from(new Jdk14Logger("foobar"));
		assertEquals("foobar", Loggers.getName(l));
		l = Loggers.Commons.from(new Log4JLogger(LogManager.getLogger("foobar2")));
		assertEquals("foobar2", Loggers.getName(l));
		l = Loggers.Commons.from(new NoOpLog("dkdkd"));
		assertNull(Loggers.getName(l));
	}

	@Test
	public void testCommonsCacheLogging() {
		InnerPrintStream str = InnerPrintStream.getErr();

		System.setProperty("org.apache.commons.logging.Log", SimpleLog.class.getName());
		System.setProperty("org.apache.commons.logging.simplelog.showlogname", "true");
		System.setProperty("org.apache.commons.logging.simplelog.showShortLogname",
				"false");
		Logger l = Loggers.Commons.from(LoggersTest.class);
		l.error("test error");
		assertTrue(str.last.getLast().indexOf(LoggersTest.class.getName()) >= 0);
		assertTrue(str.last.getLast().indexOf("test error") >= 0);
		str.terminate();
	}

	@Test
	public void testCommonsLogging2() {
		Logger l = Loggers.Commons.from("asv");
		org.apache.commons.logging.Log ll = Loggers.Commons.getAsCommonsLogger(l);
		assertTrue(ll instanceof SimpleLog);
		assertTrue(Loggers.Commons.isCommonsLogger(l));
		assertFalse(Loggers.Commons.isCommonsLogger(Loggers.systemErrLog(Level.Error)));
		try {
			Loggers.Commons.getAsCommonsLogger(Loggers.systemErrLog(Level.Error));
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
		Logger log = Loggers.JDK.from(l);
		assertEquals("FooLogger", Loggers.getName(log));
		testLevelOn(log, Logger.Level.Trace.getLevel());

		l.setLevel(java.util.logging.Level.OFF);
		testLevelOn(log, Logger.Level.Fatal.getLevel() + 1);
	}

	public void testJDKLogging2() {
		Logger l = Loggers.JDK.from("asv1");
		java.util.logging.Logger ll = Loggers.JDK.getAsJDKLogger(l);
		assertEquals("asv1", ll.getName());
		assertTrue(Loggers.JDK.isJDKLogger(l));
		assertFalse(Loggers.JDK.isJDKLogger(Loggers.systemErrLog(Level.Error)));

		l = Loggers.JDK.from("asv1".getClass());
		ll = Loggers.JDK.getAsJDKLogger(l);
		assertEquals("asv1".getClass().getCanonicalName(), ll.getName());
		try {
			Loggers.JDK.getAsJDKLogger(Loggers.systemErrLog(Level.Error));
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
		Logger log = Loggers.JDK.from(l);
		testIgnoreLog(log);
	}

	@Test
	public void testLog4J() {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();

		logger.setLevel(org.apache.log4j.Level.ALL);

		Logger log = Loggers.Log4j.from(logger);

		testLevelOn(log, Logger.Level.Trace.getLevel());

		logger.setLevel(org.apache.log4j.Level.OFF);
		testLevelOn(log, Logger.Level.Fatal.getLevel() + 1);

		assertEquals("foobar", Loggers.getName(Loggers.Log4j.from(LogManager
				.getLogger("foobar"))));
	}

	@Test
	public void testLog4JLogging2() {
		Logger l = Loggers.Log4j.from("asv1");
		org.apache.log4j.Logger ll = Loggers.Log4j.getAsLog4jLogger(l);
		assertEquals("asv1", ll.getName());
		assertTrue(Loggers.Log4j.isLog4jLogger(l));
		assertFalse(Loggers.Log4j.isLog4jLogger(Loggers.systemErrLog(Level.Error)));

		l = Loggers.Log4j.from("asv1".getClass());
		ll = Loggers.Log4j.getAsLog4jLogger(l);
		assertEquals("asv1".getClass().getCanonicalName(), ll.getName());
		try {
			Loggers.Log4j.getAsLog4jLogger(Loggers.systemErrLog(Level.Error));
			fail("Should throw IllegalArgumentException");
		} catch (IllegalArgumentException iea) {}
	}

	@Test
	public void testLog4jNoLogging() {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();

		logger.setLevel(org.apache.log4j.Level.OFF);
		Logger log = Loggers.Log4j.from(logger);
		testIgnoreLog(log);
	}

	public void testIgnoreLog(Logger log) {
		InnerPrintStream outStr = InnerPrintStream.get();
		InnerPrintStream errStr = InnerPrintStream.getErr();
		Throwable t = new Throwable();
		testLevelOn(log, Logger.Level.Fatal.getLevel() + 1);
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
		Logger log = Loggers.systemOutLog(Logger.Level.Trace);

		assertEquals("simple", Loggers.getName(log));
		testLevelOn(log, Logger.Level.Trace.getLevel());
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
		Logger log = Loggers.systemOutLog(Logger.Level.Trace);
		Throwable t = new Throwable();
		int l = t.getStackTrace().length + 1;
		testLevelOn(log, Logger.Level.Trace.getLevel());
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
		Logger log = Loggers.nullLog();
		testIgnoreLog(log);
	}

	@Test
	public void testStaticMethods() {
		assertNull(Loggers.getName(context.mock(Logger.class)));
		try {
			Loggers.printStreamLog(Level.Error, null);
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

	private void runMock(Logger log, Throwable t) {
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
		testLevelOn(log, Logger.Level.Trace.getLevel());
	}

	private void testLevelOn(Logger log, int level) {
		assertEquals(level <= Logger.Level.Trace.getLevel(), log.isTraceEnabled());
		assertEquals(level <= Logger.Level.Debug.getLevel(), log.isDebugEnabled());
		assertEquals(level <= Logger.Level.Info.getLevel(), log.isInfoEnabled());
		assertEquals(level <= Logger.Level.Warn.getLevel(), log.isWarnEnabled());
		assertEquals(level <= Logger.Level.Error.getLevel(), log.isErrorEnabled());
		assertEquals(level <= Logger.Level.Fatal.getLevel(), log.isFatalEnabled());
	}
}
