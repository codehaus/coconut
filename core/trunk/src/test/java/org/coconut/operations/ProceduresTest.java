package org.coconut.operations;

import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.coconut.test.TestUtil.assertNotSerializable;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.coconut.operations.Ops.Procedure;
import org.coconut.test.SystemErrOutHelper;
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

    @Test(expected = NullPointerException.class)
    public void toPrintStreamNPE() {
        Procedures.toPrintStream(null);
    }

    @Test(expected = NullPointerException.class)
    public void toPrintStreamSafeNPE() {
        Procedures.toPrintStreamSafe(null);
    }

    @Test
    public void toSystemOut() {
        SystemErrOutHelper str = SystemErrOutHelper.get();
        try {
            Procedure eh = Procedures.systemOutPrintln();
            eh.apply(234);
            assertTrue(str.getFromLast(0).startsWith("234"));
        } finally {
            str.terminate();
        }
    }

    @Test
    public void toSystemOutSafe() {
        SystemErrOutHelper str = SystemErrOutHelper.get();
        try {
            Procedure eh = Procedures.toSystemOutSafe();
            eh.apply(234);
            assertTrue(str.getFromLast(0).startsWith("234"));
        } finally {
            str.terminate();
        }
    }

    @Test
    public void toPrintStream() {
        toSystemOut();// hack
    }

    @Test
    public void toPrintStreamSafe() {
        toSystemOutSafe();// hack
    }
}
