/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Various management utility functions.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Managements {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private Managements() {}

    // /CLOVER:ON
    public static ManagedVisitor hierarchicalRegistrant(MBeanServer server, String domain,
            String... levels) {
        return new HierarchicalRegistrant(server, domain, levels);
    }

    static class HierarchicalRegistrant implements ManagedVisitor {
        /** The MBeanServer to register with. */
        private final MBeanServer server;

        /** The base domain to register at. */
        private final String domain;

        private final String[] levels;

        HierarchicalRegistrant(MBeanServer server, String domain, String... levels) {
            if (server == null) {
                throw new NullPointerException("server is null");
            } else if (domain == null) {
                throw new NullPointerException("domain is null");
            } else if (levels == null) {
                throw new NullPointerException("levels is null");
            }
            for (String level : levels) {
                if (level == null) {
                    throw new NullPointerException("levels contained a null");
                }
            }
            this.server = server;
            this.domain = domain;
            this.levels = levels.clone();
        }

        /** {@inheritDoc} */
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
                String p = prefix + "," + levels[level + 1] + "=" + group.getName();
                visitManagedGroup(group, p, level + 1);
            }
        }

        // /CLOVER:OFF
        /** {@inheritDoc} */
        public void visitManagedObject(Object o) throws JMException {}
        // /CLOVER:ON
    }
}
