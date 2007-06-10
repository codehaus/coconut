/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractManagedGroup implements ManagedGroup {
	final static Pattern NAME_PATTERN = Pattern
			.compile("[\\da-zA-Z\\x5F\\x2D]*(\\x2E([\\da-z\\x5F\\x2D])+)*");

	private final String description;

	private final ConcurrentHashMap<String, AbstractManagedGroup> map = new ConcurrentHashMap<String, AbstractManagedGroup>();

	private final String name;

	private ObjectName objectName;

	private AbstractManagedGroup parent;

	private MBeanServer server;

	AbstractManagedGroup(String name, String description) {
		if (name == null) {
			throw new NullPointerException("name is null");
		} else if (name.length() == 0) {
			throw new IllegalArgumentException("cannot specify the empty string as name");
		} else if (!NAME_PATTERN.matcher(name).matches()) {
			throw new IllegalArgumentException("not a valid name, was " + name);
		}
		this.name = name;
		this.description = description;
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#getChildren()
     */
	public Collection<ManagedGroup> getChildren() {
		return new ArrayList<ManagedGroup>(map.values());
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#getDescription()
     */
	public String getDescription() {
		return description;
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#getName()
     */
	public String getName() {
		return name;
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#getObjectName()
     */
	public synchronized ObjectName getObjectName() {
		return objectName;
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#getParent()
     */
	public synchronized ManagedGroup getParent() {
		return parent;
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#getServer()
     */
	public synchronized MBeanServer getServer() {
		return server;
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#isRegistered()
     */
	public boolean isRegistered() {
		return getObjectName() != null;
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#register(javax.management.MBeanServer,
     *      javax.management.ObjectName)
     */
	public synchronized void register(MBeanServer service, ObjectName name)
			throws JMException {
		service.registerMBean(this, name);
		this.server = service;
		this.objectName = name;
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#remove()
     */
	public synchronized void remove() {
		if (parent != null) {
			parent.map.remove(getName());
			parent = null;
		}
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#unregister()
     */
	public synchronized void unregister() throws JMException {
		server.unregisterMBean(objectName);
		objectName = null;
		server = null;
	}

	/**
     * @see org.coconut.jmx.ManagedGroup#addNewGroup(java.lang.String,
     *      java.lang.String)
     */
	synchronized ManagedGroup addNewGroup(AbstractManagedGroup group) {
		if (map.containsKey(group.getName())) {
			throw new IllegalArgumentException();
		}
		map.put(group.getName(), group);
		group.parent = this;
		return group;
	}

}