/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.jsr166y.ops;

import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import jsr166y.forkjoin.Ops.Procedure;

import org.codehaus.cake.jsr166y.ops.Procedures;
import org.codehaus.cake.test.util.SystemOutCatcher;
import org.codehaus.cake.test.util.TestUtil;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class ProceduresTest {
    Mockery context = new JUnit4Mockery();

    @Test
    public void systemOutPrint() {
        SystemOutCatcher str = SystemOutCatcher.get();
        try {
            Procedure eh = Procedures.systemOutPrint();
            eh.op(234);
            assertTrue(str.toString().equals("234"));
        } finally {
            str.terminate();
        }
        assertIsSerializable(Procedures.SYS_OUT_PRINT_PROCEDURE);
        assertSame(Procedures.SYS_OUT_PRINT_PROCEDURE, Procedures.systemOutPrint());
        assertSame(Procedures.SYS_OUT_PRINT_PROCEDURE, TestUtil.serializeAndUnserialize(Procedures
                .systemOutPrint()));
    }

    @Test
    public void systemOutPrintln() {
        SystemOutCatcher str = SystemOutCatcher.get();
        try {
            Procedure eh = Procedures.systemOutPrintln();
            eh.op(234);
            assertTrue(str.toString().equals("234"+TestUtil.LINE_SEPARATOR));
        } finally {
            str.terminate();
        }
        assertIsSerializable(Procedures.SYS_OUT_PRINTLN_PROCEDURE);
        assertSame(Procedures.SYS_OUT_PRINTLN_PROCEDURE, Procedures.systemOutPrintln());
        assertSame(Procedures.SYS_OUT_PRINTLN_PROCEDURE, TestUtil.serializeAndUnserialize(Procedures
                .systemOutPrintln()));
    }

    @Test
    public void noop() {
        Procedure<Integer> p = Procedures.ignore();
        p.op(null);
        p.op(1);
        p.toString(); // does not fail
        assertIsSerializable(Procedures.IGNORE_PROCEDURE);
        assertSame(Procedures.IGNORE_PROCEDURE, Procedures.ignore());
        assertSame(p, TestUtil.serializeAndUnserialize(p));
    }

}
