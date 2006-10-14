/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.management.MBeanServer;

import org.coconut.apm.Apm;
import org.coconut.apm.ApmGroup;
import org.coconut.apm.spi.NumberDynamicBean;
import org.coconut.core.Named;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultApmGroup implements ApmGroup {
    private final static Pattern PATTERN = Pattern
            .compile("[\\da-zA-Z\\x5F]*(\\x2E([\\da-z\\x5F])+)*");

    private final String name;

    private String description;

    private final List<Object> apms = new ArrayList<Object>();

    private final Map<String, DefaultApmGroup> groups = new HashMap<String, DefaultApmGroup>();

    private final DefaultApmGroup parent;

    private boolean register;

    private String pattern;
    public DefaultApmGroup(String name, boolean register) {
        this(name, null, register);
    }

    private DefaultApmGroup(String name, DefaultApmGroup parent, boolean register) {
        checkName(name);
        this.name = name;
        this.parent = parent;
        this.register = register;
    }

    private void checkName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (!PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        }
    }

    public static ApmGroup newRoot(String name) {
        return new DefaultApmGroup(name, false);
    }

    public static ApmGroup newRoot(String name, MBeanServer server) {
        DefaultApmGroup d = new DefaultApmGroup(name, false);
        d.setMbeanServer(server);
        return d;
    }

    public String getName() {
        return name;
    }

    MBeanServer server;

    public synchronized void setMbeanServer(MBeanServer server) {
        this.server = server;
    }

    public ApmGroup addGroup(String name) {
        return addGroup(name, true);
    }

    /**
     * @see org.coconut.apm.ApmGroup#addGroup(java.lang.String)
     */
    public synchronized ApmGroup addGroup(String name, boolean register) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (!Pattern.matches("[\\da-zA-Z\\x5F]*", name)) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        } else if (groups.containsKey(name)) {
            throw new IllegalArgumentException("already a group defined with name "
                    + name);
        }
        DefaultApmGroup dg = new DefaultApmGroup(name, this, register);
        groups.put(name, dg);
        return dg;
    }

    /**
     * @see org.coconut.apm.ApmGroup#getGroups()
     */
    public synchronized Collection<ApmGroup> getGroups() {
        return new ArrayList<ApmGroup>(groups.values());
    }

    /**
     * @see org.coconut.apm.ApmGroup#getParentGroup()
     */
    public synchronized ApmGroup getParentGroup() {
        return parent;
    }

    /**
     * @param e
     * @return
     * @see java.util.Collection#add(java.lang.Object)
     */
    public synchronized ApmGroup add(Object e) {
        apms.add(e);
        return this;
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#addAsGroup(org.coconut.core.Named)
     */
    public ApmGroup addAsGroup(Named name) {
        ApmGroup gm = addGroup(name.getName());
        gm.add(name);
        return gm;
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#getAll()
     */
    public Collection<?> getAll() {
        // TODO Auto-generated method stub
        return null;
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
     * @see org.coconut.apm.next.ApmGroup#setDescription(java.lang.String)
     */
    public synchronized void setDescription(String name) {
        this.description = name;
    }

    /**
     * @see org.coconut.apm.next.ApmGroup#register(java.lang.String)
     */
    public synchronized void register(String name) throws Exception {
        // We could use a cool syntes such as org.coconut.cache:name=$1,type=$2,
        // ..
        // and then do a replacement on $1, $2
        String nextLevel = name;
        NumberDynamicBean bean = new NumberDynamicBean(getDescription());
        int index = name.indexOf("$" + getLevel());
        if (index > 0) {
            name = name.replace("$" + getLevel(), this.name);
            nextLevel = name;
            name = name.substring(0, index);
        }

        // deregister allready registered?
        if (register) {
            for (Object o : apms) {
                if (o instanceof Apm) {
                    Apm p = (Apm) o;
                    p.configureJMX(bean);
                } else {
                    bean.add(o);
                }
            }
            bean.register(server, name);
        }
        for (ApmGroup gm : groups.values()) {
            gm.register(nextLevel);
        }
    }

    private int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    /**
     * @see org.coconut.apm.ApmGroup#add(java.lang.Runnable, long, java.util.concurrent.TimeUnit)
     */
    public <T extends Runnable> ApmGroup add(T r, long time, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }
}
