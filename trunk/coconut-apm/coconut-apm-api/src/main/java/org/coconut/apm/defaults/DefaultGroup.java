/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.coconut.apm.Apm;
import org.coconut.apm.ApmGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultGroup implements ApmGroup {
    private final static Pattern PATTERN = Pattern
            .compile("[\\da-z\\x5F]*(\\x2E([\\da-z\\x5F])+)*");

    private final String name;

    private final Map<String, Apm> apms = new HashMap<String, Apm>();

    private final Map<String, DefaultGroup> groups = new HashMap<String, DefaultGroup>();

    private final DefaultGroup parent;

    public DefaultGroup(String name) {
        this(name, null);
    }

    private DefaultGroup(String name, DefaultGroup parent) {
        checkName(name);
        this.name = name;
        this.parent = parent;
    }

    private void checkName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (!PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        }
    }

    public String getName() {
        return name;
    }

    /**
     * @see org.coconut.apm.ApmGroup#addGroup(java.lang.String)
     */
    public synchronized ApmGroup addGroup(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (!Pattern.matches("[\\da-z\\x5F]*", name)) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        } else if (groups.containsKey(name)) {
            throw new IllegalArgumentException("already a group defined with name "
                    + name);
        }
        DefaultGroup dg = new DefaultGroup(this.name + "." + name, this);
        groups.put(name, dg);
        return dg;
    }

    /**
     * @see org.coconut.apm.ApmGroup#addSampler(org.coconut.apm.Apm, long,
     *      java.util.concurrent.TimeUnit)
     */
    public <T extends Apm> T addSampler(T r, long time, TimeUnit unit) {
        throw new UnsupportedOperationException();
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
     * @see org.coconut.apm.ApmGroup#removeFromParentGroup()
     */
    public synchronized void removeFromParentGroup() {
        if (parent == null) {
            throw new IllegalStateException("cannot remove a group with no parent");
        }
    }

    /**
     * @param e
     * @return
     * @see java.util.Collection#add(java.lang.Object)
     */
    public synchronized boolean add(Apm e) {
        String name = e.getName();
        if (name == null) {
            throw new NullPointerException("name of apm is null");
        } else if (!Pattern.matches("[\\da-z\\x5F]*", name)) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        } else if (apms.containsKey(name)) {
            throw new IllegalArgumentException("already a apm defined with name " + name);
        }
        return false;
        // return getApm().add(e);
    }

    private Collection<Apm> getApm() {
        return null;
    }

    /**
     * @param c
     * @return
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends Apm> c) {
        return getApm().addAll(c);
    }

    /**
     * @see java.util.Collection#clear()
     */
    public void clear() {
        getApm().clear();
    }

    /**
     * @param o
     * @return
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return getApm().contains(o);
    }

    /**
     * @param c
     * @return
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return getApm().containsAll(c);
    }

    /**
     * @param o
     * @return
     * @see java.util.Collection#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return getApm().equals(o);
    }

    /**
     * @return
     * @see java.util.Collection#hashCode()
     */
    public int hashCode() {
        return getApm().hashCode();
    }

    /**
     * @return
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty() {
        return getApm().isEmpty();
    }

    /**
     * @return
     * @see java.util.Collection#iterator()
     */
    public Iterator<Apm> iterator() {
        return getApm().iterator();
    }

    /**
     * @param o
     * @return
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        return getApm().remove(o);
    }

    /**
     * @param c
     * @return
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        return getApm().removeAll(c);
    }

    /**
     * @param c
     * @return
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        return getApm().retainAll(c);
    }

    /**
     * @return
     * @see java.util.Collection#size()
     */
    public int size() {
        return getApm().size();
    }

    /**
     * @return
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray() {
        return getApm().toArray();
    }

    /**
     * @param <T>
     * @param a
     * @return
     * @see java.util.Collection#toArray(T[])
     */
    public <T> T[] toArray(T[] a) {
        return getApm().toArray(a);
    }

}
