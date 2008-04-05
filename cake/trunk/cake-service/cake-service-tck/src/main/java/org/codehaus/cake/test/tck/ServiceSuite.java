/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.test.tck;

import org.junit.internal.runners.InitializationError;
import org.junit.runners.Suite;

@SuppressWarnings("deprecation")
public class ServiceSuite extends Suite {

    public ServiceSuite(Class<?> klass) throws InitializationError {
        super(klass);
        // TODO Auto-generated constructor stub
    }

/*
    public ServiceSuite(Class<?> klass)  {
        this(klass, getAnnotatedClasses(klass));
    }

    public ServiceSuite(Class<?> klass, Class[] annotatedClasses) throws InitializationError {
        super(klass, Request.classes(klass.getName(), annotatedClasses).getRunner());
    }
    private static Class[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
        SuiteClasses annotation = klass.getAnnotation(SuiteClasses.class);
        if (annotation == null)
            throw new InitializationError(String.format(
                    "class '%s' must have a SuiteClasses annotation", klass.getName()));
        List<Class> l = new ArrayList(Arrays.asList(CacheTCKRunner.tt.getAnnotation(SupportedServices.class)
                .value()));
        ArrayList<Class> classes = new ArrayList<Class>(Arrays.asList(annotation.value()));
        if (CacheTCKRunner.tt.getAnnotation(NotThreadSafe.class) != null) {
            l.add(NotThreadSafe.class);
        } else { // assume its ThreadSafe
            l.add(ThreadSafe.class);
        }
        for (Iterator<Class> iterator = classes.iterator(); iterator.hasNext();) {
            Class<?> c = iterator.next();
            RequireService r = c.getAnnotation(RequireService.class);
            if (r != null) {
                for (Class<?> cl : r.value()) {
                    //xor?
                    if (r.isAvailable() && !l.contains(cl)) {
                        iterator.remove();
                    }
                    if (!r.isAvailable() && l.contains(cl)) {
                        iterator.remove();
                    }
                }
            }
        }
        return classes.toArray(new Class[0]);
    }
    */
}
