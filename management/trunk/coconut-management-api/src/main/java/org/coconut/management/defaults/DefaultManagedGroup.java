/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.coconut.management.JmxRegistrant;
import org.coconut.management.ManagedGroup;
import org.coconut.management.spi.Named;
import org.coconut.management.spi.NumberDynamicBean;
import org.coconut.management.spi.SelfConfigure;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultManagedGroup implements ManagedGroup {
    private final static Pattern PATTERN = Pattern
            .compile("[\\da-zA-Z\\x5F\\x2D]*(\\x2E([\\da-z\\x5F\\x2D])+)*");

    private final List<Object> apms = new ArrayList<Object>();

    private String description;

    private final Map<String, DefaultManagedGroup> groups = new HashMap<String, DefaultManagedGroup>();

    private final String name;

    private final DefaultManagedGroup parent;

    private boolean doRegister;

    private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    public DefaultManagedGroup(String name, String description, boolean register) {
        this(name, description, null, register);
    }

    private DefaultManagedGroup(String name, String description,
            DefaultManagedGroup parent, boolean register) {
        checkName(name);
        if (description == null) {
            throw new NullPointerException("description, parent, register is null");
        }
        this.name = name;
        this.parent = parent;
        this.description = description;
        this.doRegister = register;
    }

    /**
     * @param e
     * @return
     * @see java.util.Collection#add(java.lang.Object)
     */
    public synchronized ManagedGroup add(Object e) {
        apms.add(e);
        return this;
    }

    public synchronized ManagedGroup getParent() {
        return parent;
    }

//    /**
//     * @see org.coconut.apm.next.ApmGroup#addAsGroup(org.coconut.core.Named)
//     */
//    public synchronized ManagedGroup addAsGroup(Named name) {
//        ManagedGroup gm = addGroup(name.getName(), "No description");
//        gm.add(name);
//        return gm;
//    }

    public synchronized ManagedGroup addGroup(String name, String description) {
        return addGroup(name, description, true);
    }

    /**
     * @see org.coconut.apm.ApmGroup#addGroup(java.lang.String)
     */
    public synchronized ManagedGroup addGroup(String name, String description,
            boolean register) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (!Pattern.matches("[\\da-zA-Z\\x5F\\x2D]*", name)) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        } else if (groups.containsKey(name)) {
            throw new IllegalArgumentException("already a group defined with name "
                    + name);
        }
        DefaultManagedGroup dg = new DefaultManagedGroup(name, description, this,
                register);
        groups.put(name, dg);
        return dg;
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#getAll()
     */
    public synchronized Collection<?> getAll() {
        // TODO Auto-generated method stub
        return new ArrayList(apms);
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#getDescription()
     */
    public synchronized String getDescription() {
        return description;
    }

    /**
     * @see org.coconut.apm.ApmGroup#getGroups()
     */
    public synchronized Collection<ManagedGroup> getGroups() {
        return new ArrayList<ManagedGroup>(groups.values());
    }

    public synchronized String getName() {
        return name;
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#register(java.lang.String)
     */
    public synchronized void registerGroup(String objectName) throws JMException {
        // We could use a cool syntes such as org.coconut.cache:name=$1,type=$2,
        // ..
        // and then do a replacement on $1, $2

        String nextLevel = objectName;
        NumberDynamicBean bean = new NumberDynamicBean(getDescription());
        int index = objectName.indexOf("$" + getLevel());
        if (index > 0) {
            objectName = objectName.replace("$" + getLevel(), this.name);
            nextLevel = objectName;
            objectName = objectName.substring(0, index);
        }

        // deregister allready registered?
        if (doRegister) {
            for (Object o : apms) {
                register(bean, o);
            }
            bean.register(server, objectName);
        }
        for (ManagedGroup gm : groups.values()) {
            gm.registerGroup(nextLevel);
        }
    }

    /**
     * @see org.coconut.management.ManagedGroup#register(org.coconut.management.JmxNamer)
     */
    public synchronized void registerAll(JmxRegistrant namer) throws JMException {
        NumberDynamicBean bean = new NumberDynamicBean(getDescription());
        if (doRegister) {
            ObjectName name = namer.getName(this);
            if (name == null) {
                throw new IllegalArgumentException("namer returns null for objectname");
            }
            for (Object o : apms) {
                register(bean, o);
            }
            bean.register(server, name);
        }
        for (ManagedGroup gm : groups.values()) {
            namer.registerChild(gm);
        }
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#remove()
     */
    public synchronized void remove() {
        // TODO Auto-generated method stub

    }

    public synchronized void setMbeanServer(MBeanServer server) {
        this.server = server;
    }

    /**
     * @see org.coconut.apm.ApmGroup#unregister()
     */
    public synchronized void unregister() throws JMException {
        // TODO Auto-generated method stub

    }

    private void checkName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (!PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        }
    }

    private int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    void register(NumberDynamicBean bean, Object o) {
        if (o instanceof SelfConfigure) {
            ((SelfConfigure) o).configure(bean);
        } else {
            bean.add(o);
        }
    }

}
