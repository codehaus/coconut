package org.codehaus.cake.internal.service.spi;

import java.util.UUID;

import org.codehaus.cake.container.Container;
import org.codehaus.cake.container.ContainerConfiguration;
import org.codehaus.cake.internal.UseInternals;

@UseInternals
public class ContainerInfo {

    private final Class<? extends Container> clazz;
    private final String containerName;

    public ContainerInfo(Class clazz, ContainerConfiguration configuration) {
        String name = configuration.getName();
        if (name == null) {
            containerName = UUID.randomUUID().toString();
        } else {
            containerName = name;
        }
        this.clazz = clazz;
    }

    public String getContainerName() {
        return containerName;
    }

    public Class<? extends Container> getContainerType() {
        return clazz;
    }

    public String getContainerTypeName() {
        return getContainerType().getSimpleName();
    }

    public String getDefaultJMXDomain() {
        return getContainerType().getPackage().getName();
    }
}
