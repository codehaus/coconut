package org.coconut.operations;

import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.coconut.test.TestUtil.assertNotSerializable;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.coconut.operations.Ops.Procedure;
import org.coconut.test.SystemErrOutHelper;
import org.coconut.test.SystemOutCatcher;
import org.coconut.test.TestUtil;
import org.jmock.Expectations;
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
    public void queueOffer() {
        final Queue q = context.mock(Queue.class);
        context.checking(new Expectations() {
            {
                one(q).offer(0);
            }
        });
        Procedure processor = Procedures.queueOffer(q);
        processor.apply(0);
        assertNotSerializable(processor);
        assertIsSerializable(Procedures.queueOffer(new ArrayBlockingQueue(1)));
    }

    @Test(expected = NullPointerException.class)
    public void collectionAddNPE() {
        Procedures.collectionAdd(null);
    }

    @Test
    public void collectionAdd() {
        final Collection q = context.mock(Collection.class);
        context.checking(new Expectations() {
            {
                one(q).add(0);
            }
        });
        Procedure processor = Procedures.collectionAdd(q);
        processor.apply(0);
        assertNotSerializable(processor);
        assertIsSerializable(Procedures.collectionAdd(new ArrayBlockingQueue(1)));
    }

    @Test(expected = NullPointerException.class)
    public void queueOfferNPE() {
        Procedures.queueOffer(null);
    }


    @Test
    public void systemOutPrint() {
        SystemOutCatcher str = SystemOutCatcher.get();
        try {
            Procedure eh = Procedures.systemOutPrint();
            eh.apply(234);
            assertTrue(str.toString().equals("234"));
        } finally {
            str.terminate();
        }
        assertIsSerializable(Procedures.SYS_OUT_PRINT);
        assertSame(Procedures.SYS_OUT_PRINT, Procedures.systemOutPrint());
        assertSame(Procedures.SYS_OUT_PRINT, TestUtil.serializeAndUnserialize(Procedures
                .systemOutPrint()));
    }

    @Test
    public void systemOutPrintln() {
        SystemOutCatcher str = SystemOutCatcher.get();
        try {
            Procedure eh = Procedures.systemOutPrintln();
            eh.apply(234);
            assertTrue(str.toString().equals("234"+TestUtil.LINE_SEPARATOR));
        } finally {
            str.terminate();
        }
        assertIsSerializable(Procedures.SYS_OUT_PRINTLN);
        assertSame(Procedures.SYS_OUT_PRINTLN, Procedures.systemOutPrintln());
        assertSame(Procedures.SYS_OUT_PRINTLN, TestUtil.serializeAndUnserialize(Procedures
                .systemOutPrintln()));
    }

    @Test
    public void noop() {
        Procedure<Integer> p = Procedures.noop();
        p.apply(null);
        p.apply(1);
        p.toString(); // does not fail
        assertIsSerializable(Procedures.NOOP);
        assertSame(Procedures.NOOP, Procedures.noop());
        assertSame(p, TestUtil.serializeAndUnserialize(p));
    }

}
