/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.statistics.CacheStatisticsConfiguration;
import org.coconut.cache.service.threading.CacheExecutorConfiguration;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.cache.spi.ConfigurationValidator;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;
import org.coconut.core.Logger;

/**
 * This class is the primary class used for representing the configuration of a cache. All
 * general-purpose <tt>Cache</tt> implementation classes should have a constructor with
 * a single argument taking a CacheConfiguration.
 * <p>
 * <b>Usage Examples.</b> The following creates a new cache with the name MyCache. The
 * cache has a maximum of 1000 elements and uses a least-recently-used policy to determine
 * which elements to evict when the specified maximum size has been reached. Elements will
 * automatically expired after having been in the cache for 1 hour. Finally, the cache is
 * registered as a mbean with the platform mbean server using the name of the cache.
 * 
 * <pre>
 * CacheConfiguration&lt;String, Integer&gt; cc = newConf();
 * cc.setName(&quot;MyCache&quot;);
 * cc.eviction().setPolicy(Policies.newLRU()).setMaximumSize(1000);
 * cc.expiration().setDefaultTimeout(60 * 60, TimeUnit.SECONDS);
 * cc.management().setRegister(true);
 * Cache&lt;String, Integer&gt; instance = cc.newInstance(Select_A_Cache_Impl);
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys that should be maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
@NotThreadSafe
public final class CacheConfiguration<K, V> {

    public final static List<Class<? extends AbstractCacheServiceConfiguration>> DEFAULT_SERVICES;

    /** A Map of additional properties. */
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /** The default logger for the cache. */
    private Logger defaultLogger;

    private final List<AbstractCacheServiceConfiguration> list = new ArrayList<AbstractCacheServiceConfiguration>();

    private final Collection<Class<? extends AbstractCacheServiceConfiguration<K, V>>> serviceTypes;

    /** The name of the cache. */
    private String name;

    /** The clock that should be used for timing. */
    private Clock timingStrategy = Clock.DEFAULT_CLOCK;

    static {
        List services = new ArrayList();
        services.add(CacheEventConfiguration.class);
        services.add(CacheManagementConfiguration.class);
        services.add(CacheStatisticsConfiguration.class);
        services.add(CacheLoadingConfiguration.class);
        services.add(CacheExpirationConfiguration.class);
        services.add(CacheExceptionHandlingConfiguration.class);
        services.add(CacheExecutorConfiguration.class);
        services.add(CacheEvictionConfiguration.class);
        DEFAULT_SERVICES = Collections.unmodifiableList(services);
    }

    /**
     * Creates a new Configuration with default settings. CacheConfiguration instances
     * should be created using the {@link #newConf()} method.
     */
    public CacheConfiguration() {
        // this(DEFAULT_SERVICES);
        serviceTypes = new ArrayList<Class<? extends AbstractCacheServiceConfiguration<K, V>>>(
                (Collection) DEFAULT_SERVICES);
        for (Class c : serviceTypes) {
            getConfiguration(c);
        }
    }

    /**
     * @param <T>
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
// if (conf.getClass().getName().contains("Event")) {
// System.out.println("adding " + conf.getClass());
// new Exception().printStackTrace();
// }
// for (AbstractCacheServiceConfiguration c : list) {
// if (c.getClass().isAssignableFrom(conf.getClass())
// || conf.getClass().isAssignableFrom(c.getClass())) {
// throw new IllegalArgumentException(
// "A service of the specified type is already registered (type = "
// + conf.getClass());
// }
// }
        list.add(conf);

        return conf;
    }

    public Collection<Class<? extends AbstractCacheServiceConfiguration<K, V>>> getServiceTypes() {
        return (Collection) DEFAULT_SERVICES;
    }

    /**
     * Returns the {@link org.coconut.core.Clock} that the cache should use.
     * 
     * @return the Clock that the cache should use
     * @see #setClock(Clock)
     */
    public Clock getClock() {
        return timingStrategy;
    }

    public <T extends AbstractCacheServiceConfiguration> T getConfiguration(
            Class<T> serviceConfigurationType) {
        T t = getConfigurationOfType(serviceConfigurationType);
        if (t == null) {
            t = lazyCreate(serviceConfigurationType);
            if (t == null) {
                throw new IllegalArgumentException(
                        "Unknown service configuration, type ="
                                + serviceConfigurationType);
            }
        }
        return t;
    }

    /**
     * Returns a list of all registered configuration objects.
     * 
     * @return a list of all registered configuration objects
     */
    public List<AbstractCacheServiceConfiguration> getConfigurations() {
        return new ArrayList<AbstractCacheServiceConfiguration>(list);
    }

    /**
     * Returns the default log configured for this cache or <tt>null</tt> if no default
     * log has been configured.
     * 
     * @return the default log configured for this cache or null if no default log has
     *         been configured
     */
    public Logger getDefaultLog() {
        return defaultLogger;
    }

    /**
     * Returns the name of the cache.
     * 
     * @return the name of the cache, or <tt>null</tt> if no name has been set.
     * @see #setName(String)
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
        return Collections.unmodifiableMap(new HashMap<String, Object>(
                additionalProperties));
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
     * Creates a new cache instance of the specified type using this configuration.
     * 
     * @param type
     *            the type of cache that should be created
     * @return a new Cache instance
     * @throws IllegalArgumentException
     *             if specified class does not contain a public constructor taking a
     *             single CacheConfiguration argument
     * @throws NullPointerException
     *             if the specified class is <tt>null</tt> *
     * @param <T>
     *            the type of cache to create
     */
    public <T extends Cache<K, V>> T newInstance(Class<? extends Cache> type)
            throws IllegalArgumentException {
        if (type == null) {
            throw new NullPointerException("clazz is null");
        }
        T cache = null;
        Constructor<T> c = null;
        try {
            c = (Constructor<T>) type.getDeclaredConstructor(CacheConfiguration.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, no public contructor taking a single CacheConfiguration instance",
                    e);
        }
        try {
            cache = c.newInstance(this);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, specified clazz " + type
                            + ") is an interface or abstract class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create instance of " + type, e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Constructor threw exception", e);
        }
        return cache;
    }

    /**
     * Returns a configuration object that can be used to control what types of events are
     * raised whenever something interesting happens in the cache.
     * 
     * @return a CacheEventConfiguration
     */
    public CacheEventConfiguration event() {
        return lazyCreate(CacheEventConfiguration.class);
    }

    /**
     * Returns a service that can be used to configure maximum size, replacement policies,
     * and more for the cache.
     * 
     * @return a CacheEvictionConfiguration
     */
    public CacheEvictionConfiguration<K, V> eviction() {
        return lazyCreate(CacheEvictionConfiguration.class);
    }

    /**
     * Returns a configuration object that can be used to control how and when elements in
     * the cache expires.
     * 
     * @return a CacheExpirationConfiguration
     */
    public CacheExpirationConfiguration<K, V> expiration() {
        return lazyCreate(CacheExpirationConfiguration.class);
    }

    /**
     * Returns a configuration object that can be used to control how loading is done in
     * the cache.
     * 
     * @return a CacheLoadingConfiguration
     */
    public CacheLoadingConfiguration<K, V> loading() {
        return lazyCreate(CacheLoadingConfiguration.class);
    }

    /**
     * Returns a configuration object that can be used to control how services are
     * remotely managed.
     * 
     * @return a CacheManagementConfiguration
     */
    public CacheManagementConfiguration management() {
        return lazyCreate(CacheManagementConfiguration.class);
    }

    /**
     * Returns a configuration object that can be used to control how all the various
     * threads running with coconut.
     * 
     * @return a CacheThreadingConfiguration
     */
    public CacheExecutorConfiguration<K, V> threading() {
        return lazyCreate(CacheExecutorConfiguration.class);
    }

    /**
     * Sets the {@link org.coconut.core.Clock} that the cache should use. Normally users
     * should not need to set this, only if they want to provide another timing mechanism
     * then the built-in {@link java.lang.System#currentTimeMillis()}. For example, a
     * custom NTP protocol.
     * <p>
     * 
     * @param timingStrategy
     *            the Timer to use
     * @return this configuration
     * @throws NullPointerException
     *             if the supplied timingStrategy is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setClock(Clock timingStrategy) {
        if (timingStrategy == null) {
            throw new NullPointerException("timingStrategy is null");
        }
        this.timingStrategy = timingStrategy;
        return this;
    }

    /**
     * Sets the default loggger for this cache. If for some reason the cache or one of its
     * services needs to notify users of some kind of event this logger should be used. a
     * service needs to log information and no special logger has been set for the
     * service. This log should be used.
     * <p>
     * Coconut Caches strives to be very conservertive about what is logged. That is, if
     * it is not log as little as possible. That is, We actually advise running with log
     * at {@link Logger.Level.INFO} level even in production.
     * 
     * @param logger
     *            the log to use
     * @return this configuration
     * @see #getDefaultLog()
     */
    public CacheConfiguration<K, V> setDefaultLog(Logger logger) {
        this.defaultLogger = logger;
        return this;
    }

    /**
     * Sets the name of the cache. The name should be unique among other configured
     * caches. The name must consists only of alphanumeric characters and '_' or '-'.
     * <p>
     * If no name is set in the configuration, any cache implementation must generate an
     * (unique) name for the cache. How exactly the name is generated is implementation
     * specific.
     * 
     * @param name
     *            the name of the cache
     * @return this configuration
     * @see #getName()
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
     *             if key is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setProperty(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        additionalProperties.put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();
        try {
            new XmlConfigurator().write(this, sos);
        } catch (Exception e) {
            throw new CacheException("This is highly irregular, please report", e);
        }
        return new String(sos.toByteArray());

    }

    /**
     * Returns a configuration object of the specified type.
     * 
     * @param <T>
     *            the type of the configuration object
     * @param configurationType
     *            the type of the configuration object
     * @return an instance of the specified configuration object type
     */
    private <T extends AbstractCacheServiceConfiguration> T createConfiguration(
            Class<T> configurationType) {
        AbstractCacheServiceConfiguration acsc;
        try {
            acsc = configurationType.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) addConfiguration(acsc);
    }

    private <T extends AbstractCacheServiceConfiguration> T getConfigurationOfType(
            Class<T> serviceConfigurationType) {
        for (AbstractCacheServiceConfiguration o : list) {
            if (o.getClass().equals(serviceConfigurationType)) {
                return (T) o;
            }
        }
        return null;
    }

    protected <T extends AbstractCacheServiceConfiguration> T lazyCreate(Class<T> c) {
        T conf = getConfigurationOfType(c);
        if (conf == null) {
            createConfiguration(c);
            conf = getConfiguration(c);
        }
        // bit of a hack
        ConfigurationValidator.initializeConfiguration(conf, this);
        return conf;
    }

    /**
     * Creates a new CacheConfiguration with default settings.
     * 
     * @return a new CacheConfiguration
     * @param <K>
     *            the type of keys that should be maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheConfiguration<K, V> create() {
        return new CacheConfiguration<K, V>();
    }

    /**
     * Creates a new CacheConfiguration with the specified name.
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
     * Loads a cache configuration from the provided stream, and instantiates a new cache
     * on the basis of the configuration.
     * 
     * @param xmlDoc
     *            an InputStream where the configuration can be read from
     * @return and cache instance reflecting the specified configuration
     * @throws Exception
     *             the cache could not be constructed for some reason *
     * @param <K>
     *            the type of keys that should be maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> Cache<K, V> createCache(InputStream xmlDoc) throws Exception {
        CacheConfiguration<K, V> conf = createConfiguration(xmlDoc);
        return conf.newInstance((Class) Class.forName(conf.getProperty(
                XmlConfigurator.CACHE_INSTANCE_TYPE).toString()));
    }

    /**
     * Reads the XML configuration from the specified InputStream and returns a new
     * populated CacheConfiguration.
     * 
     * @param xmlDoc
     *            an InputStream where the configuration can be read from
     * @return a CacheConfiguration reflecting the xml configuration
     * @throws Exception
     *             some Exception prevented the CacheConfiguration from being populated
     * @param <K>
     *            the type of keys that should be maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> CacheConfiguration<K, V> createConfiguration(InputStream xmlDoc)
            throws Exception {
        CacheConfiguration<K, V> conf = CacheConfiguration.create();
        new XmlConfigurator().read(conf, xmlDoc);
        return conf;
    }
}
