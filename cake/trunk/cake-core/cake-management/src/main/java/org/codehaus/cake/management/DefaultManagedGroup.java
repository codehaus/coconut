/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The default implementation of {@link ManagedGroup}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DefaultManagedGroup.java 510 2007-12-12 08:52:55Z kasper $
 */
public class DefaultManagedGroup extends AbstractManagedGroup {

    /** A map of all attributes. */
    private final Map<String, AbstractManagedAttribute> attributes = new ConcurrentHashMap<String, AbstractManagedAttribute>();

    /** A map of all operations. */
    private final Map<OperationKey, AbstractManagedOperation> ops = new ConcurrentHashMap<OperationKey, AbstractManagedOperation>();

    /** A set of the objects that have been registered with this group. */
    private final Set<Object> os = new CopyOnWriteArraySet<Object>();

    DefaultManagedGroup(DefaultManagedGroup parent, String name, String description) {
        super(parent, name, description);
    }

    /**
     * Creates a new DefaultManagedGroup with the specified name and description.
     * 
     * @param name
     *            the name of the group
     * @param description
     *            the description of the group
     * @throws NullPointerException
     *             if the specified name or description is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified name does not follow the naming standard of managed groups
     */
    public DefaultManagedGroup(String name, String description) {
        super(name, description);
    }

    /** {@inheritDoc} */
    public synchronized ManagedGroup add(Object o) {
        if (o == null) {
            throw new NullPointerException("o is null");
        }
        mainLock.lock();
        try {
            beforeMutableOperationInner();
            if (isRegistered()) {
                throw new IllegalStateException("Cannot add objects when this group is registered");
            }
            BeanInfo bi;
            try {
                bi = Introspector.getBeanInfo(o.getClass());
            } catch (java.beans.IntrospectionException e) {
                // /CLOVER:OFF
                throw new IllegalArgumentException(e); // cannot happen
                // /CLOVER:ON
            }
            attributes.putAll(DefaultManagedAttribute.fromPropertyDescriptors(bi
                    .getPropertyDescriptors(), o));
            ops.putAll(DefaultManagedOperation.fromMethodDescriptors(bi.getMethodDescriptors(), o));
            os.add(o);
            return this;
        } finally {
            mainLock.unlock();
        }
    }

    /** {@inheritDoc} */
    public ManagedGroup addChild(String name, String description) {
        mainLock.lock();
        try {
            beforeMutableOperationInner();
            return new DefaultManagedGroup(this, name, description);
        } finally {
            mainLock.unlock();
        }
    }

    /** {@inheritDoc} */
    public Collection<?> getObjects() {
        return new ArrayList(os);
    }

    /** {@inheritDoc} */
    Object getRegistrant() {
        return ManagementUtil.from(getName(), getDescription(), attributes, ops);
    }
}
