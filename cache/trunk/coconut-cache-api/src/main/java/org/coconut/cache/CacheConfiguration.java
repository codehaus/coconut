/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.servicemanager.CacheServiceManagerConfiguration;
import org.coconut.cache.service.statistics.CacheStatisticsConfiguration;
import org.coconut.cache.service.worker.CacheWorkerConfiguration;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.cache.spi.CacheSPI;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;
import org.coconut.core.Logger;

/**
 * This class is the primary class used for representing the configuration of a cache. All
 * general-purpose <tt>Cache</tt> implementation classes should have a constructor with
 * a single argument taking a CacheConfiguration.
 * <p>
 * <b>Usage Examples.</b> The following creates a new cache with the name <I>MyCache</I>.
 * The cache can hold a maximum of 1000 elements and uses a least-recently-used policy to
 * determine which elements to evict when the specified maximum size has been reached.
 * Elements will automatically expire after having been in the cache for 1 hour. Finally,
 * the cache and all of its services are registered as a mbean with the
 * {@link java.lang.management.ManagementFactory#getPlatformMBeanServer platform
 * <tt>MBeanServer</tt>} using the name of the cache.
 * 
 * <pre>
 * CacheConfiguration&lt;String, Integer&gt; cc = CacheConfiguration.create();
 * cc.setName(&quot;MyCache&quot;);
 * cc.eviction().setPolicy(Policies.newLRU()).setMaximumSize(1000);
 * cc.expiration().setDefaultTimeToLive(60 * 60, TimeUnit.SECONDS);
 * cc.management().setEnabled(true);
 * Cache&lt;String, Integer&gt; instance = cc.newInstance(SynchronizedCache.class);
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @see $HeadURL:
 *      https://svn.codehaus.org/coconut/cache/trunk/coconut-cache-api/src/main/java/org/coconut/cache/CacheServices.java $
 * @param <K>
 *            the type of keys that should be maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
@NotThreadSafe
public final class CacheConfiguration<K, V> {

    /** A list of all default service configuration types. */
    private final static List<Class<? extends AbstractCacheServiceConfiguration>> DEFAULTS = Arrays
            .asList(CacheEventConfiguration.class, CacheManagementConfiguration.class,
                    CacheStatisticsConfiguration.class, CacheLoadingConfiguration.class,
                    CacheExpirationConfiguration.class, CacheExceptionHandlingConfiguration.class,
                    CacheWorkerConfiguration.class, CacheServiceManagerConfiguration.class,
                    CacheEvictionConfiguration.class);

    /** A Map of additional properties. */
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /** The clock that should be used for timing. */
    private Clock clock = Clock.DEFAULT_CLOCK;

    /** The default logger for the cache. */
    private Logger defaultLogger;

    /** A collection of instantiated service configuration objects. */
    private final Collection<AbstractCacheServiceConfiguration> list = new ArrayList<AbstractCacheServiceConfiguration>();

    /** The name of the cache. */
    private String name;

    /**
     * Creates a new Configuration with default settings. CacheConfiguration instances
     * should be created using the {@link #create()} or {@link #create(String)} method.
     */
    public CacheConfiguration() {
        for (Class<? extends AbstractCacheServiceConfiguration> c : DEFAULTS) {
            try {
                AbstractCacheServiceConfiguration a = c.newInstance();
                addConfiguration(a);
                CacheSPI.initializeConfiguration(a, this);
            } catch (Exception e) {
                throw new CacheException(CacheSPI.HIGHLY_IRREGULAR, e);
            }
        }
    }

    /**
     * Creates a new Configuration with default settings and the additional configuration
     * types specified as parameter.
     * 
     * @param additionalConfigurationTypes
     *            the additional configuration types
     */
    protected CacheConfiguration(
            Collection<Class<? extends AbstractCacheServiceConfiguration>> additionalConfigurationTypes) {
        this();
        if (additionalConfigurationTypes == null) {
            throw new NullPointerException("additionalConfigurationTypes is null");
        }
        for (Class<? extends AbstractCacheServiceConfiguration> c : DEFAULTS) {
            if (c == null) {
                throw new NullPointerException("collection of service types contained a null");
            }
            try {
                AbstractCacheServiceConfiguration a = c.newInstance();
                addConfiguration(a);
                CacheSPI.initializeConfiguration(a, this);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Configuration type could not be registered [class = " + c + "]", e);
            }
        }
    }

    /**
     * Adds an instantiated configuration object.
     * 
     * @param <T>
     *            the type of configuration added
     * @param conf
     *            the configuration object that should be registered
     * @return the specified configuration object
     * @throws NullPointerException
     *             if the specified configuration object is null
     * @throws IllegalArgumentException
     *             if another configuration of the same type is already registered
     */
    public <T extends AbstractCacheServiceConfiguration> T addConfiguration(T conf) {
        if (conf == null) {
            throw new NullPointerException("conf is null");
        }
        for (AbstractCacheServiceConfiguration c : list) {
            if (c.getClass().equals(conf.getClass())) {
                throw new IllegalArgumentException("A configuration object of type "
                        + conf.getClass() + " has already been registered");
            }
        }
        list.add(conf);
        return conf;
    }

    /**
     * Returns a configuration object that can be used to control what types of events are
     * raised when state changes in the cache.
     * 
     * @return a CacheEventConfiguration
     */
    public CacheEventConfiguration event() {
        return getConfiguration(CacheEventConfiguration.class);
    }

    /**
     * Returns a service that can be used to configure maximum size, replacement policies,
     * and more settings for the cache.
     * 
     * @return a CacheEvictionConfiguration
     */
    public CacheEvictionConfiguration<K, V> eviction() {
        return getConfiguration(CacheEvictionConfiguration.class);
    }

    /**
     * Returns a configuration object that can be used to control how exceptions are
     * handled in the cache.
     * 
     * @return a CacheExceptionHandlingConfiguration
     */
    public CacheExceptionHandlingConfiguration<K, V> exceptionHandling() {
        return getConfiguration(CacheExceptionHandlingConfiguration.class);
    }

    /**
     * Returns a configuration object that can be used to control how and when elements in
     * the cache expires.
     * 
     * @return a CacheExpirationConfiguration
     */
    public CacheExpirationConfiguration<K, V> expiration() {
        return getConfiguration(CacheExpirationConfiguration.class);
    }

    /**
     * Returns a collection of all service configuration objects.
     * 
     * @return a collection of all service configuration objects
     */
    public Collection<AbstractCacheServiceConfiguration<K, V>> getAllConfigurations() {
        return (Collection) new ArrayList<AbstractCacheServiceConfiguration>(list);
    }

    /**
     * Returns the {@link org.coconut.core.Clock} that the cache should use.
     * 
     * @return the Clock that the cache should use
     * @see #setClock(Clock)
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Returns the default logger configured for this cache or <tt>null</tt> if no
     * default logger has been configured.
     * 
     * @return the default logger configured for this cache or null if no default logger
     *         has been configured
     * @see #setDefaultLogger(Logger)
     */
    public Logger getDefaultLogger() {
        return defaultLogger;
    }

    /**
     * Returns the name of the cache.
     * 
     * @return the name of the cache, or <tt>null</tt> if no name has been set.
     * @see #setName(String)
     * @see Cache#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a map of additional properties for the cache.
     * 
     * @return a map of additional properties for the cache.
     * @see #setProperty(String, Object)
     */
    public Map<String, ?> getProperties() {
        return Collections.unmodifiableMap(new HashMap<String, Object>(additionalProperties));
    }

    /**
     * Returns the property value for the specified key or <tt>null</tt> if no such
     * property exists. A <tt>null</tt> can also indicate that the key was explicitly
     * mapped to <tt>null</tt>.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @return the value of the property or <tt>null</tt> if no such property exists
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public Object getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * Returns the property value for the specified key or the specified default value if
     * no such property exists. A property does not exists if it is mapped to
     * <tt>null</tt> either explicitly or because no such entry exists.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @param defaultValue
     *            the default value to return if the property does not exist
     * @return the value of the property or the specified default value if the property
     *         does not exist
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public Object getProperty(String key, Object defaultValue) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        Object result = additionalProperties.get(key);
        return result == null ? defaultValue : result;
    }

    /**
     * Returns a configuration object that can be used to control how loading is done in
     * the cache.
     * 
     * @return a CacheLoadingConfiguration
     */
    public CacheLoadingConfiguration<K, V> loading() {
        return getConfiguration(CacheLoadingConfiguration.class);
    }

    /**
     * Returns a configuration object that can be used to control how services are
     * remotely managed.
     * 
     * @return a CacheManagementConfiguration
     */
    public CacheManagementConfiguration management() {
        return getConfiguration(CacheManagementConfiguration.class);
    }

    /**
     * Creates a new cache instance of the specified type using this configuration.
     * <p>
     * The behavior of this operation is undefined if this configuration is modified while
     * the operation is in progress.
     * 
     * @param cacheType
     *            the type of cache that should be created
     * @return a new Cache instance
     * @throws IllegalArgumentException
     *             if a cache of the specified type could not be created
     * @throws NullPointerException
     *             if the specified type is <tt>null</tt>
     * @param <T>
     *            the type of cache to create
     */
    public <T extends Cache<K, V>> T newCacheInstance(Class<? extends Cache> cacheType) {
        if (cacheType == null) {
            throw new NullPointerException("type is null");
        }

        final Constructor<T> c;
        try {
            c = (Constructor<T>) cacheType.getDeclaredConstructor(CacheConfiguration.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, no public contructor "
                            + "taking a single CacheConfiguration instance for the specified class [class = "
                            + cacheType + "]", e);
        }
        try {
            return c.newInstance(this);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, specified clazz [class = " + cacheType
                            + "] is an interface or an abstract class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create instance of " + cacheType, e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Constructor threw exception", e.getCause());
        }
    }

    /**
     * Returns a configuration object that can be used to add additional services to the
     * cache.
     * 
     * @return a CacheManagementConfiguration
     */
    public CacheServiceManagerConfiguration serviceManager() {
        return getConfiguration(CacheServiceManagerConfiguration.class);
    }

    /**
     * Sets the {@link org.coconut.core.Clock} that the cache should use. Normally users
     * should not need to set this, only if they want to provide another timing mechanism
     * then the built-in {@link java.lang.System#currentTimeMillis()} and
     * {@link java.lang.System#nanoTime()}. For example, a custom NTP protocol.
     * <p>
     * This method is also usefull for tests that rely on exact timing of events
     * 
     * @param clock
     *            the Clock that the cache should use
     * @return this configuration
     * @throws NullPointerException
     *             if the specified clock is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setClock(Clock clock) {
        if (clock == null) {
            throw new NullPointerException("clock is null");
        }
        this.clock = clock;
        return this;
    }

    /**
     * Sets the default loggger for this cache. If for some reason the cache or one of its
     * services needs to notify users of some kind of events this logger should be used.
     * Some services might allow to set a special logger. For example, for logging timing
     * informations, auditting, ... etc. In this case this special logger will take
     * precedence over this specified logger when logging for the service.
     * <p>
     * All available caches in Coconut Cache strives to be very conservertive about what
     * is logged, log as little as possible. That is, we actually recommend running with
     * log level set at {@link Logger.Level#Info} even in production.
     * 
     * @param logger
     *            the logger to use
     * @return this configuration
     * @see #getDefaultLogger()
     */
    public CacheConfiguration<K, V> setDefaultLogger(Logger logger) {
        this.defaultLogger = logger;
        return this;
    }

    /**
     * Sets the name of the cache. The name should be unique among other configured
     * caches. The name must consists only of alphanumeric characters and '_' or '-'.
     * <p>
     * If no name is set in the configuration, any cache implementation must generate a
     * name for the cache. How exactly the name is generated is implementation specific.
     * But the recommended way is to use {@link UUID#randomUUID()} to generate a pseudo
     * random name.
     * 
     * @param name
     *            the name of the cache
     * @return this configuration
     * @throws IllegalArgumentException
     *             if the specified name is the empty string or if the name contains other
     *             characters then alphanumeric characters and '_' or '-'
     * @see #getName()
     * @see Cache#getName()
     */
    public CacheConfiguration<K, V> setName(String name) {
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
     * Some cache implementations might allow additional properties to be set then those
     * defined by this class. This method can be used to set these additional properties.
     * 
     * @param key
     *            the key of the property
     * @param value
     *            the value of the property
     * @return this configuration
     * @see #getProperties()
     * @see #getProperty(String)
     * @see #getProperty(String, Object)
     * @throws NullPointerException
     *             if the specified key is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setProperty(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        additionalProperties.put(key, value);
        return this;
    }

    /**
     * Returns a XML-based string representation of this configuration. This xml-based
     * string can used as input to {@link #loadConfigurationFrom(InputStream)} or to
     * create a similar configuration.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();
        try {
            new XmlConfigurator().write(this, sos);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "An xml-based representation of this cache could not be created", e);
        }
        return new String(sos.toByteArray());
    }

    /**
     * Returns a configuration object that can be used to control how asynchronous tasks
     * are run within a cache.
     * 
     * @return a CacheThreadingConfiguration
     */
    public CacheWorkerConfiguration worker() {
        return getConfiguration(CacheWorkerConfiguration.class);
    }

    /**
     * Returns a configuration object of the specified type.
     * 
     * @param c
     *            the type of the configuration
     * @return a configuration objects of the specified type
     * @throws IllegalStateException
     *             if no configuration object of the specified type exists
     * @param <T>
     *            the type of the configuration
     */
    protected <T extends AbstractCacheServiceConfiguration> T getConfiguration(Class<T> c) {
        for (AbstractCacheServiceConfiguration o : list) {
            if (o.getClass().equals(c)) {
                return (T) o;
            }
        }
        throw new IllegalStateException("Unknown service configuration [ type = " + c + "]");
    }

    /**
     * Creates a new CacheConfiguration with default settings.
     * 
     * @return a new CacheConfiguration with default settings
     * @param <K>
     *            the type of keys that should be maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheConfiguration<K, V> create() {
        return new CacheConfiguration<K, V>();
    }

    /**
     * Creates a new CacheConfiguration with default settings and the specified name as
     * the name of the cache .
     * 
     * @param name
     *            the name of the cache
     * @return a new CacheConfiguration with the specified name
     * @param <K>
     *            the type of keys that should be maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheConfiguration<K, V> create(String name) {
        CacheConfiguration<K, V> conf = new CacheConfiguration<K, V>();
        conf.setName(name);
        return conf;
    }

    /**
     * Loads a XML-based cache configuration from the specified input stream, and
     * instantiates a new cache on the basis of the configuration.
     * 
     * @param xmlDoc
     *            an InputStream where the configuration can be read from
     * @return and cache instance reflecting the specified configuration
     * @throws Exception
     *             the cache could not be constructed for some reason
     * @param <K>
     *            the type of keys that should be maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> Cache<K, V> loadCacheFrom(InputStream xmlDoc) throws Exception {
        CacheConfiguration<K, V> conf = loadConfigurationFrom(xmlDoc);
        return conf.newCacheInstance((Class) Class.forName(conf.getProperty(
                XmlConfigurator.CACHE_INSTANCE_TYPE).toString()));
    }

    /**
     * Reads the XML-based configuration from the specified InputStream and returns a new
     * populated CacheConfiguration.
     * 
     * @param xmlDoc
     *            an InputStream where the configuration can be read from
     * @return a CacheConfiguration reflecting the xml configuration
     * @throws Exception
     *             some issue prevented the CacheConfiguration from being read and
     *             populated
     * @param <K>
     *            the type of keys that should be maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheConfiguration<K, V> loadConfigurationFrom(InputStream xmlDoc)
            throws Exception {
        CacheConfiguration<K, V> conf = CacheConfiguration.create();
        new XmlConfigurator().readInto(conf, xmlDoc);
        return conf;
    }
}
