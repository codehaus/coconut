/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import java.lang.reflect.Method;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Managements {

    public static <T> T narrow(ManagedGroup group, Class<? extends T> c) {
        return null;
    }

    public static Object wrap(Object o, String name, String description, Method m,
            Object... parameters) {
        return null;
    }

    public static ManagedGroupVisitor register(MBeanServer server, String domain,
            String... levels) {
        if (server == null) {
            throw new NullPointerException("server is null");
        } else if (domain == null) {
            throw new NullPointerException("domain is null");
        }
        return new GroupVisitor(server, domain, levels);
    }

    static class GroupVisitor implements ManagedGroupVisitor {
        private final MBeanServer server;

        private final String domain;

        private final String[] levels;

        GroupVisitor(MBeanServer server, String domain, String... levels) {
            this.server = server;
            this.domain = domain;
            this.levels = levels.clone();
        }

        public void visitManagedGroup(ManagedGroup mg) throws JMException {
            String prefix = domain + ":" + levels[0] + "=" + mg.getName();
            visitManagedGroup(mg, prefix, 0);
        }

        private void visitManagedGroup(ManagedGroup mg, String prefix, int level)
                throws JMException {
            ObjectName on = new ObjectName(prefix);
            if (mg.getObjects().size() > 0) {
                mg.register(server, on);
            }
            for (ManagedGroup group : mg.getChildren()) {
                prefix += "," + levels[level + 1] + "=" + group.getName();
                visitManagedGroup(group, prefix, level + 1);
            }
        }

        public void visitManagedObject(ManagedGroup group, Object o) throws JMException {}

    }
}
