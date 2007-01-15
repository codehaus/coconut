/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.management.MBeanServer;

import org.coconut.core.Named;
import org.coconut.management.JmxRegistrant;
import org.coconut.management.ManagedGroup;
import org.coconut.management.spi.NumberDynamicBean;
import org.coconut.management.spi.SelfConfigure;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultManagedGroup implements ManagedGroup {
    private final static Pattern PATTERN = Pattern
            .compile("[\\da-zA-Z\\x5F]*(\\x2E([\\da-z\\x5F])+)*");

    private final String name;

    private String description;

    private final List<Object> apms = new ArrayList<Object>();

    private final Map<String, DefaultManagedGroup> groups = new HashMap<String, DefaultManagedGroup>();

    private final DefaultManagedGroup parent;

    private boolean register;

    private String pattern;

    public DefaultManagedGroup(String name, boolean register) {
        this(name, null, register);
    }

    private DefaultManagedGroup(String name, DefaultManagedGroup parent, boolean register) {
        checkName(name);
        this.name = name;
        this.parent = parent;
        this.register = register;
    }

    private void checkName(String name) {
        if (true) {
            return;
        }
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (!PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        }
    }

    public String getName() {
        return name;
    }

    MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    public synchronized void setMbeanServer(MBeanServer server) {
        this.server = server;
    }

    public ManagedGroup addGroup(String name, String description) {
        return addGroup(name, description, true);
    }

    /**
     * @see org.coconut.apm.ApmGroup#addGroup(java.lang.String)
     */
    public synchronized ManagedGroup addGroup(String name, String description,
            boolean register) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (!Pattern.matches("[\\da-zA-Z\\x5F]*", name)) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        } else if (groups.containsKey(name)) {
            throw new IllegalArgumentException("already a group defined with name "
                    + name);
        }
        DefaultManagedGroup dg = new DefaultManagedGroup(name, this, register);
        dg.description = description;
        groups.put(name, dg);
        return dg;
    }

    /**
     * @see org.coconut.apm.ApmGroup#getGroups()
     */
    public synchronized Collection<ManagedGroup> getGroups() {
        return new ArrayList<ManagedGroup>(groups.values());
    }

    /**
     * @see org.coconut.apm.ApmGroup#getParentGroup()
     */
    public synchronized ManagedGroup getParentGroup() {
        return parent;
    }

    /**
     * @param e
     * @return
     * @see java.util.Collection#add(java.lang.Object)
     */
    public synchronized <T> T add(T e) {
        apms.add(e);
        return e;
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#addAsGroup(org.coconut.core.Named)
     */
    public ManagedGroup addAsGroup(Named name) {
        ManagedGroup gm = addGroup(name.getName(), "No description");
        gm.add(name);
        return gm;
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#getAll()
     */
    public synchronized Collection<?> getAll() {
        // TODO Auto-generated method stub
        return new ArrayList(apms);
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#remove()
     */
    public void remove() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.coconut.apm.next.ApmGroup#getDescription()
     */
    public synchronized String getDescription() {
        return description;
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#register(java.lang.String)
     */
    public synchronized void register(String objectName) throws Exception {
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
        if (register) {
            for (Object o : apms) {
                register(bean, o);
            }
            bean.register(server, objectName);
        }
        for (ManagedGroup gm : groups.values()) {
            gm.register(nextLevel);
        }
    }

    /**
     * @see org.coconut.management.ManagedGroup#register(org.coconut.management.JmxNamer)
     */
    public void registerAll(JmxRegistrant namer) throws Exception {
        NumberDynamicBean bean = new NumberDynamicBean(getDescription());
        if (register) {
            for (Object o : apms) {
                register(bean, o);
            }
            bean.register(server, namer.getName());
        }
        for (ManagedGroup gm : groups.values()) {
            namer.registerChild(gm);
        }
    }

    void register(NumberDynamicBean bean, Object o) {
        if (o instanceof SelfConfigure) {
            ((SelfConfigure) o).configure(bean);
        } else {
            bean.add(o);
        }
    }

    private int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * @see org.coconut.apm.ApmGroup#add(java.lang.Runnable, long,
     *      j33316011ava.util.concurrent.TimeUnit)
     */
    public <T extends Runnable> T add(T r, long time, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.coconut.apm.ApmGroup#unregister()
     */
    public void unregister() throws Exception {
        // TODO Auto-generated method stub

    }

}
