package org.codehaus.cake.test.tck;

import org.codehaus.cake.container.ContainerConfiguration;
import org.codehaus.cake.test.tck.service.executors.WorkerService;
import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;

public abstract class TckRunner extends CompositeRunner {

    /**
     * If the file test-tck/src/main/resources/defaulttestclass exists. We will try to open it and
     * read which cache implementation should be tested by default.
     * <p>
     * This is very usefull if you just want to run a subset of the tests in an IDE.
     */

    public TckRunner(Class<?> clazz, Class<? extends ContainerConfiguration> configuration)
            throws InitializationError {
        super(clazz.getSimpleName() + " TCK");
        TckUtil.containerImplementation = clazz.getAnnotation(TckImplementationSpecifier.class)
                .value();
        TckUtil.configuration = configuration;
        
        try {
            add(new JUnit4ClassRunner(clazz));
        } catch (InitializationError e) {
            //ignore, no valid tests in clazz
        }
        
        try {
            initialize();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    protected void initialize() throws Exception {
        add(new JUnit4ClassRunner(WorkerService.class));
    }
}
