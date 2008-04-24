package org.codehaus.cake.service.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.forkjoin.ForkJoinExecutor;

public class ExecutorsManagerBuilder {

    public static ExecutorsManager specialExecutor(final ExecutorsManager manager,
            final Object service, final ExecutorService executor) {
        return new ExecutorManagerDecorator() {
            @Override
            public ExecutorService getExecutorService(Object ser, AttributeMap attributes) {
                if (ser.equals(service)) {
                    return executor;
                }
                return super.getExecutorService(service, attributes);
            }
        };
    }

    public static class ExecutorManagerDecorator extends ExecutorsManager {
        private ExecutorsManager delegate;

        @Override
        public ExecutorService getExecutorService(Object service, AttributeMap attributes) {
            return delegate.getExecutorService(service, attributes);
        }


        @Override
        public ForkJoinExecutor getForkJoinExecutor(Object service, AttributeMap attributes) {
            return delegate.getForkJoinExecutor(service, attributes);
        }

        @Override
        public ScheduledExecutorService getScheduledExecutorService(Object service,
                AttributeMap attributes) {
            return delegate.getScheduledExecutorService(service, attributes);
        }
    }
}
