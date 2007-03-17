/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.threading.CacheThreadingConfiguration;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.cache.spi.ConfigurationValidator;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.core.Clock;

/**
 * This class is the primary class used for representing the configuration of a
 * cache. All general-purpose <tt>Cache</tt> implementation classes should
 * have a constructor with a single argument taking a CacheConfiguration.
 * <p>
 * <b>Usage Examples.</b> The following creates a new cache with the name
 * MyCache. The cache has a maximum of 1000 elements and uses a
 * least-recently-used policy to determine which elements to evict when the
 * capacity has been reached. Elements are also marked as expired after not
 * having been request in 1 hour. Finally the cache is registered as a mbean
 * with the platform mbean server using the name of the cache.
 * 
 * <pre>
 * CacheConfiguration&lt;String, Integer&gt; cc = newConf();
 * cc.setName(&quot;MyCache&quot;);
 * cc.eviction().setPolicy(Policies.newLRU()).setMaximumSize(1000);
 * cc.expiration().setDefaultTimeout(60 * 60, TimeUnit.SECONDS);
 * cc.jmx().setRegister(true);
 * Cache&lt;String, Integer&gt; instance = cc.newInstance(Select_A_Cache_Impl);
 * </pre>
 * 
 * <p>
 * In addition to the general methods available on CacheConfiguration This class
 * is using a number of inner classes to . This The following categories exist
 * <dl>
 * <dt>{@link Backend}</dt>
 * <dd>Handles storage and loading of entries from cache loaders and cache
 * stores</dd>
 * <dt>{@link Eviction}</dt>
 * <dd>Handles maximum size of the cache and what entries will be evicted</dd>
 * <dt>{@link Expiration}</dt>
 * <dd>Handles expired items and timeout settings for entries</dd>
 * <dt>{@link JMX}</dt>
 * <dd>Handles JMX registration of the cache</dd>
 * <dt>{@link Statistics}</dt>
 * <dd>Handles various statistics related issues</dd>
 * <dt>{@link Threading}</dt>
 * <dd>Handles the usage of threads within the cache</dd>
 * </dl>
 * <p>
 * A few point on the naming of methods. Methods postfixed with <tt>Hint</tt>
 * are merely hits to implementation on how to .... and can as such be ignored
 * by any implementation. They are only used to assist an implementation in
 * choosing performance optimal configuration.
 * <p>
 * If Extension of this class is possible with the following TODO what about
 * extensions of this class?? could be usefull for special purpose caches, if so
 * we should change some of the static methods to allow overriding.
 * <p>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@NotThreadSafe
public class CacheConfiguration<K, V> implements Cloneable {

    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private CacheErrorHandler<K, V> errorHandler = new CacheErrorHandler<K, V>();

    private Map<? extends K, ? extends V> initialMap;

    private ArrayList<AbstractCacheServiceConfiguration> list = new ArrayList<AbstractCacheServiceConfiguration>();

    private String name = UUID.randomUUID().toString();

    private Clock timingStrategy = Clock.DEFAULT_CLOCK;

    /**
     * Creates a new Configuration with default settings. CacheConfiguration
     * instances should be created using the {@link #newConf()} method.
     */
    CacheConfiguration() {
        // package private for now, might change at a later time.
    }

    public AbstractCacheServiceConfiguration<K, V> addService(
            AbstractCacheServiceConfiguration conf) {
        if (conf == null) {
            throw new NullPointerException("conf is null");
        }
        for (AbstractCacheServiceConfiguration c : list) {
            Class type = conf.getServiceInterface();
            if (c.getServiceInterface().isAssignableFrom(type)) {

            } else if (type.isAssignableFrom(c.getClass())) {

            }
        }
        list.add(conf);
        ConfigurationValidator.initializeConfiguration(conf, this);
        return conf;
    }

    public <T extends AbstractCacheServiceConfiguration> T addService(Class<T> conf) {
        AbstractCacheServiceConfiguration acsc;
        try {
            acsc = conf.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        return (T) addService(acsc);
    }

    /**
     * Returns a new Eviction object.
     */
    public CacheEvictionConfiguration eviction() {
        return lazyCreate(CacheEvictionConfiguration.class);
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


    /**
     * Returns the log configured for the cache.
     * 
     * @return the log configured for the cache, or <tt>null</tt> if no log is
     *         configured
     * @see #setLog(Log)
     */
    public CacheErrorHandler<K, V> getErrorHandler() {
        return errorHandler;
    }

    /**
     * Returns a map whose mappings are to be placed in the cache as initial
     * entries or <code>null</code> if no map has been specified.
     * 
     * @see #setInitialMap(Map)
     */
    public Map<? extends K, ? extends V> getInitialMap() {
        return initialMap;
    }

    /**
     * Returns the name of the cache.
     * 
     * @return the name of the cache, or <tt>null</tt> if no name has been
     *         set.
     * @see #setName(String)
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a map of any additional properties that is defined. This map is a
     * copy of the current properties.
     * 
     * @see #setProperty(String, Object)
     */
    public Map<String, ?> getProperties() {
        return Collections.unmodifiableMap(new HashMap<String, Object>(
                additionalProperties));
    }

    /**
     * Returns the property value for the specified key or <tt>null</tt> if no
     * such property exists. A <tt>null</tt> can also indicate that the key
     * was explicitly mapped to <tt>null</tt>.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @return the value of the property or <tt>null</tt> if no such property
     *         exists
     * @throws NullPointerException
     *             if key is <tt>null</tt>
     */
    public Object getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * Returns the property value for the specified key or the specified default
     * value if no such property exists. A property does not exists if it is
     * mapped to <tt>null</tt> either explicitly or because no such entry
     * exists.
     * 
     * @param key
     *            the key for which to retrieve the value
     * @param defaultValue
     *            the default value to return if the property does not exist
     * @return the value of the property or the specified default value if the
     *         property does not exist
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
     * @param name2
     */
    public <T extends AbstractCacheServiceConfiguration> T getServiceConfiguration(
            Class<T> service) {
        for (AbstractCacheServiceConfiguration o : list) {
            if (o.getClass().equals(service)) {
                return (T) o;
            }
        }
        return null;
    }

    /**
     * @param name2
     */
    public List<AbstractCacheServiceConfiguration> getServices() {
        return new ArrayList<AbstractCacheServiceConfiguration>(list);
    }

    /**
     * Creates a new cache instance of the specified type using this
     * configuration.
     * 
     * @param clazz
     *            the clazz the type of cache that should be created
     * @return a new Cache instance
     * @throws IllegalArgumentException
     *             if specified class does not contain a public constructor
     *             taking a single CacheConfiguration argument
     * @throws NullPointerException
     *             if the specified class is <tt>null</tt>
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

    public <T extends Cache<K, V>> T newInstanceAndStart(
            Class<? extends AbstractCache> clazz) {
        AbstractCache t = newInstance(clazz);
        t.preStart();
        return (T) t;
    }

    public CacheExpirationConfiguration<K, V> serviceExpiration() {
        return lazyCreate(CacheExpirationConfiguration.class);
    }

    public CacheLoadingConfiguration<K, V> serviceLoading() {
        return lazyCreate(CacheLoadingConfiguration.class);
    }

    public CacheManagementConfiguration<K, V> serviceManagement() {
        return lazyCreate(CacheManagementConfiguration.class);
    }

    public CacheThreadingConfiguration serviceThreading() {
        return lazyCreate(CacheThreadingConfiguration.class);
    }

    /**
     * Sets the {@link org.coconut.core.Clock} that the cache should use.
     * Normally users should not need to set this, only if they want to provide
     * another timing mechanism then the built-in
     * {@link java.lang.System#currentTimeMillis()}. For example, a custom NTP
     * protocol.
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
     * Sets the log that the cache should use for logging anomalies and errors.
     * If no log is set the cache will redirect all output to dev/null
     * 
     * @param log
     *            the log to use
     * @return this configuration
     * @see org.coconut.core.Logs
     * @see org.coconut.core.Log
     * @throws NullPointerException
     *             if log is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setErrorHandler(CacheErrorHandler<K, V> errorHandler) {
        if (errorHandler == null) {
            throw new NullPointerException("errorHandler is null");
        }
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Sets a map whose mappings are to be placed in the Cache as initial
     * entries.
     * 
     * @param map
     *            the map to set
     * @return this configuration
     * @see #getInitialMap()
     * @throws NullPointerException
     *             if map is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setInitialMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        initialMap = map;
        return this;
    }

    /**
     * Sets the name of the cache. The name should be unique among other
     * configured caches. Allthough not a strict requirement it is advised that
     * only alphanumeric characters are used in the name.
     * <p>
     * If no name is set, most cache implementation will generate an unique name
     * to reference the cache. How exactly this name is generated is
     * implementation specific.
     * 
     * @param name
     *            the name of the cache
     * @return this configuration
     * @see #getName()
     * @throws NullPointerException
     *             if name is <tt>null</tt>
     */
    public CacheConfiguration<K, V> setName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (name.equals("")) {
            throw new IllegalArgumentException(
                    "Cannot specify the empty string as a name");
        } else if (!Pattern.matches("[\\da-zA-Z\\x5F\\x2D]+", name)) {
            throw new IllegalArgumentException(
                    "not a valid name, must only contain alphanumeric characters and '_' or '-', was "
                            + name);
        }
        this.name = name;
        return this;
    }
    /**
     * Some cache implementations might allow additional properties to be set
     * then those defined by this class. This method can be used to set these
     * additional properties.
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();
        try {
            XmlConfigurator.getInstance().to(this, sos);
        } catch (Exception e) {
            throw new CacheException("This is highly irregular, please report", e);
        }
        return new String(sos.toByteArray());

    }

    private <T extends AbstractCacheServiceConfiguration> T lazyCreate(Class<T> c) {
        T service = getServiceConfiguration(c);
        if (service == null) {
            addService(c);
            service = getServiceConfiguration(c);
        }
        return service;
    }

    public static <K, V> CacheConfiguration<K, V> create() {
        return new CacheConfiguration<K, V>();
    }

    public static <K, V> CacheConfiguration<K, V> create(InputStream is) throws Exception {
        return XmlConfigurator.getInstance().from(is);
    }

    public static <K, V> CacheConfiguration<K, V> create(String name) {
        CacheConfiguration<K, V> conf = new CacheConfiguration<K, V>();
        conf.setName(name);
        return conf;
    }


    public static <K, V> Cache<K, V> createAndInstantiate(InputStream is)
            throws Exception {
        CacheConfiguration<K, V> conf = create(is);
        return conf.newInstance((Class) Class.forName(conf.getProperty(
                XmlConfigurator.CACHE_INSTANCE_TYPE).toString()));
    }

    public static <K, V> AbstractCache<K, V> createInstantiateAndStart(InputStream is)
            throws Exception {
        CacheConfiguration<K, V> conf = create(is);
        AbstractCache cc = (AbstractCache) conf.newInstance((Class) Class.forName(conf
                .getProperty(XmlConfigurator.CACHE_INSTANCE_TYPE).toString()));
        cc.preStart();
        return cc;
    }
}
