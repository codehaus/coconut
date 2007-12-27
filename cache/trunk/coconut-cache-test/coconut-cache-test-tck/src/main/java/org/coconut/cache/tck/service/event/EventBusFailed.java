package org.coconut.cache.tck.service.event;

import org.coconut.cache.test.service.exceptionhandling.TestExceptionHandler;
import org.coconut.operations.Ops.Procedure;
import org.coconut.test.throwables.RuntimeException1;
import org.junit.Test;

public class EventBusFailed extends AbstractEventTestBundle {

    @Test
    public void testname() throws Exception {
        TestExceptionHandler teh=new TestExceptionHandler();
        conf.exceptionHandling().setExceptionHandler(teh);
        init();
        event().subscribe(new Procedure() {
            public void apply(Object t) {
                throw new RuntimeException1();
            }
        });
        c.put(1, "A");
        assertSame(c, teh.getC());
        assertTrue(teh.getCause() instanceof RuntimeException1);
    }
}
