package org.coconut.cache.tck.service.event;

import org.coconut.core.Logger.Level;
import org.coconut.operations.Ops.Procedure;
import org.coconut.test.throwables.RuntimeException1;
import org.junit.Test;

public class EventBusFailed extends AbstractEventTestBundle {

    @Test
    public void testname() throws Exception {
        init(conf.exceptionHandling().setExceptionHandler(exceptionHandler));
        event().subscribe(new Procedure() {
            public void apply(Object t) {
                throw RuntimeException1.INSTANCE;
            }
        });
        c.put(1, "A");
        exceptionHandler.eat(RuntimeException1.INSTANCE, Level.Fatal);
    }
}
