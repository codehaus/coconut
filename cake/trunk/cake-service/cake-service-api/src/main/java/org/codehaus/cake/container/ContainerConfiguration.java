/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.container;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.codehaus.cake.management.Manageable;
import org.codehaus.cake.management.ManagedGroup;
import org.codehaus.cake.util.Clock;
import org.codehaus.cake.util.Logger;
import org.codehaus.cake.util.Loggers.Commons;
import org.codehaus.cake.util.Loggers.JDK;
import org.codehaus.cake.util.Loggers.Log4j;

/**
 * This class is the primary class used for representing the configuration of a container. All
 * general-purpose <tt>Container</tt> implementation classes should have a constructor with a
 * single argument taking a class extending ContainerConfiguration.
 * <p>
 * This class is not meant to be directly instantiated, instead it should be overriden with a
 * configuration object for a concrete container type.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ContainerConfiguration.java 559 2008-01-09 16:28:27Z kasper $
 * @param <T>
 *            The type of container instantiated from this configuration
 */
public abstract class ContainerConfiguration<T> {
    /** A Map of additional properties. */
    private final Map<String, String> additionalProperties = new HashMap<String, String>();

    /** The default clock. */
    private Clock clock = Clock.DEFAULT_CLOCK;

    /** A collection of instantiated service configuration objects. */
    private final Map<Object, Object> configurations = new HashMap<Object, Object>();

    /** The default logger. */
    private Logger defaultLogger;

    /** The name of the container. */
    private String name;

    /** Additional configuration objects. */
    private final Map<Object, Boolean> registeredServices = new LinkedHashMap<Object, Boolean>();

    /** The type of container that should be created. */
    private Class<? extends T> type;

    /**
     * Adds an instantiated configuration object.
     * 
     * @param <T>
     *            the type of configuration added
     * @param configuration
     *            the configuration object that should be registered
     * @return the specified configuration object
     * @throws NullPointerException
     *             if the specified configuration object is null
     * @throws IllegalArgumentException
     *             if another configuration of the same type is already registered
     */
    public <U> ContainerConfiguration<T> addConfiguration(U configuration) {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        }
        if (configurations.containsKey(configuration.getClass())) {
            throw new IllegalArgumentException("A configuration of type "
                    + configuration.getClass() + " has already been added");
        }
        configurations.put(configuration.getClass(), configuration);
        return this;
    }

    /**
     * Registers a object for the container. Only objects of type {@link MapLifecycle} or
     * {@link Manageable}, are valid. If the object is of type {@link MapLifecycle} the container
     * will invoke the respectic lifecycle methods on the object. If the object is of type
     * {@link Manageable} and management is enabled for the container (see
     * {@link MapManagementConfiguration#setEnabled(boolean)}). It can be registered with a
     * {@link ManagedGroup}.
     * <p>
     * Attaches the specified instance to the service map of the container. This object can then
     * later be retrived by calling {@link org.coconut.map.Container#getService(Class)}.
     * 
     * <pre>
     * ContainerServiceManagerConfiguration csmc;
     * csmc.add(&quot;fooboo&quot;);
     * 
     * ...later..
     * Container&lt;?,?&gt; c;
     * assert &quot;fooboo&quot; = c.getService(String.class);
     * </pre>
     * 
     * If the specified key conflicts with the key-type of any of the build in service an exception
     * will be thrown when the container is constructed.
     * 
     * @param o
     *            the object to register
     * @return this configuration
     * @throws IllegalArgumentException
     *             in case of an argument of invalid type or if the object has already been
     *             registered.
     */
    public ContainerConfiguration addService(Object o) {
        if (o == null) {
            throw new NullPointerException("o is null");
        } else if (registeredServices.containsKey(o)) {
            throw new IllegalArgumentException("Object has already been registered");
        }
        registeredServices.put(o, false);
        return this;
    }

    /**
     * Returns a collection of all service configuration objects.
     * 
     * @return a collection of all service configuration objects
     */
    public Set<Object> getConfigurations() {
        return new HashSet<Object>(configurations.values());
    }

    /**
     * Returns the {@link org.codehaus.cake.util.Clock} that the container should use.
     * 
     * @return the Clock that the container should use
     * @see #setClock(Clock)
     */
    public final Clock getClock() {
        return clock;
    }

    /**
     * Returns the default logger configured for this container or <tt>null</tt> if no default
     * logger has been configured.
     * 
     * @return the default logger configured for this container or null if no default logger has
     *         been configured
     * @see #setDefaultLogger(Logger)
     */
    public Logger getDefaultLogger() {
        return defaultLogger;
    }

    /**
     * Returns the name of the container.
     * 
     * @return the name of the container, or <tt>null</tt> if no name has been set.
     * @see #setName(String)
     * @see Container#getName()
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the objects that have been registered through {@link #add(Object)}.
     * 
     * @return the objects that have been registered
     */
    public List<Object> getServices() {
        return new ArrayList(registeredServices.keySet());
    }

    /**
     * Returns a map of additional properties for the container.
     * 
     * @return a map of additional properties for the container.
     * @see #setProperty(String, String)
     */
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(new HashMap<String, String>(additionalProperties));
    }

    /**
     * Returns the property value for the specified key or <tt>null</tt> if no such property
     * exists. A <tt>null</tt> can also indicate that the key was explicitly mapped to
     * <tt>null</tt>.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @return the value of the property or <tt>null</tt> if no such property exists
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * Returns the property value for the specified key or the specified default value if no such
     * property exists. A property does not exists if it is mapped to <tt>null</tt> either
     * explicitly or because no such entry exists.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @param defaultValue
     *            the default value to return if the property does not exist
     * @return the value of the property or the specified default value if the property does not
     *         exist
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public String getProperty(String key, String defaultValue) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        String result = additionalProperties.get(key);
        return result == null ? defaultValue : result;
    }

    /**
     * Returns the type of container set by {@link #setContainerType(Class)}.
     * 
     * @return the type of container set by {@link #setContainerType(Class)} or <code>null</code>
     *         if no type has been set
     */
    public Class<? extends T> getType() {
        return type;
    }

    /**
     * Creates a new Container of the type set using {@link #setContainerType(Class)} from this
     * configuration.
     * 
     * @return the newly created Container
     * @throws IllegalArgumentException
     *             if a container of the specified type could not be created
     * @throws IllegalStateException
     *             if no container has been set using {@link #setContainerType(Class)}
     */
    public T newInstance() {
        Class<? extends T> type = getType();
        if (type == null) {
            throw new IllegalStateException("no type has been set, using #setType");
        }
        return newInstance(type);
    }

    /**
     * Creates a new container instance of the specified type using this configuration.
     * <p>
     * The behavior of this operation is undefined if this configuration is modified while the
     * operation is in progress.
     * 
     * @param containerType
     *            the type of container that should be created
     * @return a new Container instance
     * @throws IllegalArgumentException
     *             if a container of the specified type could not be created
     * @throws NullPointerException
     *             if the specified type is <tt>null</tt>
     * @param <T>
     *            the type of container to create
     */
    public <S extends T> S newInstance(Class<S> type) {
        if (type == null) {
            throw new NullPointerException("type is null");
        }
        final Constructor<T> c;
        try {
            c = (Constructor<T>) type.getDeclaredConstructor(getClass());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Could not create container instance, no public contructor "
                            + "taking a single ContainerConfiguration instance for the specified class [class = "
                            + type + "]", e);
        }
        try {
            return (S) c.newInstance(this);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Could not create container instance, specified clazz [class = " + type
                            + "] is an interface or an abstract class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create instance of " + type, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalArgumentException("Constructor threw exception", cause);
        }
    }

    /**
     * Sets the {@link org.codehaus.cake.util.Clock} that the container should use. Normally users
     * should not need to set this, only if they want to provide another timing mechanism then the
     * built-in {@link java.lang.System#currentTimeMillis()} and {@link java.lang.System#nanoTime()}.
     * For example, a custom NTP protocol.
     * <p>
     * This method is also useful for tests that rely on exact timing of events.
     * 
     * @param clock
     *            the Clock that the container should use
     * @return this configuration
     * @throws NullPointerException
     *             if the specified clock is <tt>null</tt>
     */
    public ContainerConfiguration setClock(Clock clock) {
        if (clock == null) {
            throw new NullPointerException("clock is null");
        }
        this.clock = clock;
        return this;
    }

    /**
     * Sets the default loggger for this container. If for some reason the container or one of its
     * services needs to notify users of some kind of events this logger should be used. Some
     * services might allow to set a special logger. For example, for logging timing informations,
     * auditting, ... etc. In this case this special logger will take precedence over this specified
     * logger when logging for the service.
     * <p>
     * All available containers in Coconut Container strives to be very conservertive about what is
     * logged, log as little as possible. That is, we actually recommend running with log level set
     * at {@link org.codehaus.cake.util.Logger.Level#Info} even in production.
     * 
     * @param logger
     *            the logger to use
     * @return this configuration
     * @see #getDefaultLogger()
     * @see JDK
     * @see Commons
     * @see Log4j
     */
    public ContainerConfiguration setDefaultLogger(Logger logger) {
        this.defaultLogger = logger;
        return this;
    }

    /**
     * Sets the name of the container. The name should be unique among other configured containers.
     * The name must consists only of alphanumeric characters and '_' or '-'.
     * <p>
     * If no name is set in the configuration, any container implementation must generate a name for
     * the container. How exactly the name is generated is implementation specific. But the
     * recommended way is to use {@link UUID#randomUUID()} or a similar mechanism to generate a
     * random name.
     * 
     * @param name
     *            the name of the container
     * @return this configuration
     * @throws IllegalArgumentException
     *             if the specified name is the empty string or if the name contains other
     *             characters then alphanumeric characters and '_' or '-'
     * @see #getName()
     * @see Container#getName()
     */
    public ContainerConfiguration setName(String name) {
        if ("".equals(name)) {
            throw new IllegalArgumentException("cannot set the empty string as name");
        } else if (name != null) {
            if (!Pattern.matches("[\\da-zA-Z\\x5F\\x2D]+", name)) {
                throw new IllegalArgumentException(
                        "not a valid name, must only contain alphanumeric characters and '_' or '-', was "
                                + name);
            }
        }
        this.name = name;
        return this;
    }

    /**
     * Some container implementations might allow additional properties to be set then those defined
     * by this class. This method can be used to set these additional properties.
     * 
     * @param key
     *            the key of the property
     * @param value
     *            the value of the property
     * @return this configuration
     * @see #getProperties()
     * @see #getProperty(String)
     * @see #getProperty(String, String)
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    public ContainerConfiguration setProperty(String key, String value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        additionalProperties.put(key, value);
        return this;
    }

    /**
     * Sets the type of container that should be created when calling
     * {@link #newContainerInstance()}.
     * 
     * @param containerType
     *            the type of container
     * @return this configuration
     */
    public ContainerConfiguration setType(Class<? extends T> type) {
        this.type = type;
        return this;
    }

    /**
     * Returns a XML-based string representation of this configuration. This xml-based string can
     * used as input to {@link #loadConfigurationFrom(InputStream)} or to create a similar
     * configuration.
     * 
     * @return a XML-based string representation of this configuration
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();
        try {
            // new XmlConfigurator().write(this, sos);
        } catch (Exception e) {
            PrintStream ps = new PrintStream(sos);
            ps.println("An xml-based representation of this container could not be created");
            e.printStackTrace(ps);
        }
        return new String(sos.toByteArray());
    }

    /**
     * Returns a configuration object of the specified type.
     * 
     * @param configurationType
     *            the type of the configuration
     * @return a configuration objects of the specified type
     * @throws IllegalArgumentException
     *             if no configuration object of the specified type exists
     * @param <U>
     *            the type of the configuration
     */
    protected <U> U getConfigurationOfType(Class<U> configurationType) {
        Object o = configurations.get(configurationType);
        if (o == null) {
            throw new IllegalArgumentException("Unknown service configuration [ type = "
                    + configurationType + "]");
        }
        return (U) o;
    }
}
