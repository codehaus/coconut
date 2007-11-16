package org.coconut.cache.tck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.coconut.cache.spi.CacheServiceSupport;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Request;
import org.junit.runners.Suite.SuiteClasses;

public class ServiceSuite extends TestClassRunner {

    /**
     * Internal use only.
     */
    public ServiceSuite(Class<?> klass) throws InitializationError {
        this(klass, getAnnotatedClasses(klass));
    }

    /**
     * Internal use only.
     */
    public ServiceSuite(Class<?> klass, Class[] annotatedClasses) throws InitializationError {
        super(klass, Request.classes(klass.getName(), annotatedClasses).getRunner());
    }

    private static Class[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
        SuiteClasses annotation = klass.getAnnotation(SuiteClasses.class);
        if (annotation == null)
            throw new InitializationError(String.format(
                    "class '%s' must have a SuiteClasses annotation", klass.getName()));
        List<Class> l = Arrays.asList(CacheTCKRunner.tt.getAnnotation(CacheServiceSupport.class)
                .value());
        ArrayList<Class> classes = new ArrayList<Class>(Arrays.asList(annotation.value()));
        for (Iterator<Class> iterator = classes.iterator(); iterator.hasNext();) {
            Class<?> c = iterator.next();
            RequireService r = c.getAnnotation(RequireService.class);
            if (r != null) {
                for (Class<?> cl : r.value()) {
                    if (!l.contains(cl)) {
                        iterator.remove();
                    }
                }
            }
        }
        return classes.toArray(new Class[0]);
    }
}
