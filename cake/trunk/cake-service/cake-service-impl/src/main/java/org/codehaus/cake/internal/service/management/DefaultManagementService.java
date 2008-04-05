/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.internal.service.management;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.codehaus.cake.container.lifecycle.Stoppable;
import org.codehaus.cake.internal.container.Composer;
import org.codehaus.cake.internal.service.debug.InternalDebugService;
import org.codehaus.cake.internal.service.spi.CompositeService;
import org.codehaus.cake.internal.service.spi.ContainerInfo;
import org.codehaus.cake.service.management.ManagementConfiguration;
import org.codehaus.cake.util.management.DefaultManagedGroup;
import org.codehaus.cake.util.management.Manageable;
import org.codehaus.cake.util.management.ManagedGroup;
import org.codehaus.cake.util.management.ManagedVisitor;
import org.codehaus.cake.util.management.Managements;

/**
 * The default implementation of the {@link MapManagementService} interface. All methods exposed
 * through the DefaultManagementService interface can be invoked in a thread safe manner.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is made to
 * the compatibility of this class between different releases.
 * <p>
 * This is class is thread-safe.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DefaultCacheManagementService.java 537 2007-12-30 19:21:20Z kasper $
 */
public class DefaultManagementService extends DefaultManagedGroup implements CompositeService{

    /** Whether or not this service has been shutdown. */
    private volatile boolean isShutdown;

    /** Used to register all services. */
    private ManagedVisitor<?> registrant;

    private final String containerType;
    private final InternalDebugService debugService;
    
    /**
     * Creates a new AbstractManagementService.
     * 
     * @param conf
     *            the configuration of the Management service
     * @param name
     *            the name of the cache
     */
    public DefaultManagementService(ManagementConfiguration conf, ContainerInfo containerInfo,
            InternalDebugService debugService) {
        super(containerInfo.getContainerName(), "This group contains all managed services");
        this.debugService = debugService;
        containerType = containerInfo.getContainerTypeName();
        /* Set Registrant */
        registrant = conf.getRegistrant();
        if (registrant == null) {
            MBeanServer server = conf.getMBeanServer();
            if (server == null) {
                server = ManagementFactory.getPlatformMBeanServer();
            }
            String domain = conf.getDomain();
            if (domain == null) {
                domain = containerInfo.getDefaultJMXDomain();
            }
            registrant = Managements.hierarchicalRegistrant(server, domain, "name", "service",
                    "group");
        }
    }

    public void register(Composer composer,  List objects) throws JMException {
        debugService.debug("  Manageable.manage()");
        ManagedGroup group = Managements.delegatedManagedGroup(this);
        for (Object o : objects) {
            if (o instanceof Manageable) {
                Manageable m = (Manageable) o;
                debugService.debug("  Managing " + m);
                m.manage(group);
            } 
        }

        try {
            registrant.traverse(group);
        } finally {
            registrant = null;
        }
    }

    @Override
    protected void beforeMutableOperation() {
        if (isShutdown) {
            throw new IllegalStateException(containerType + " has been shutdown");
        }
    }

    /** {@inheritDoc} */
    public synchronized Collection<?> getChildServices() {
        return Collections.singleton(registrant);
    }

    /** {@inheritDoc} */
    @Stoppable
    public synchronized void stop() throws JMException {
        try {
            // TODO we should log any exceptions returned by traverse
            Managements.unregister().traverse(this);
        } finally {
            isShutdown = true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Management Service";
    }
}
