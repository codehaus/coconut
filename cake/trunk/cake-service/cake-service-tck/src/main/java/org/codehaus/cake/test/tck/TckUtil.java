package org.codehaus.cake.test.tck;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.codehaus.cake.container.Container;
import org.codehaus.cake.container.ContainerConfiguration;

public class TckUtil {

    static Class<? extends Container> containerImplementation;
    static Class<? extends ContainerConfiguration> configuration;

    static {
        try {
            InputStream is = TckRunner.class.getClassLoader().getResourceAsStream("defaulttestclass");
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                containerImplementation = (Class) Class.forName(p.getProperty("container"));
                configuration = (Class) Class.forName(p.getProperty("configuration"));
            }
        } catch (ClassNotFoundException e) {
            // ignore, user has not defined a class
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static Container newContainer(ContainerConfiguration configuration) {
        try {
            return (Container) configuration.newInstance(containerImplementation);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static ContainerConfiguration newConfiguration() {
        try {
            return configuration.newInstance();
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
