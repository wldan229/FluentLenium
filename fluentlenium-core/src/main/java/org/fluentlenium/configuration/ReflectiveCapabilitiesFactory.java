package org.fluentlenium.configuration;

import org.fluentlenium.utils.ReflectionUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link CapabilitiesFactory} that create {@link Capabilities} instances using reflection.
 */
public class ReflectiveCapabilitiesFactory implements CapabilitiesFactory, FactoryNames, ReflectiveFactory {
    private final String name;
    private final Object[] args;
    private final String capabilitiesClassName;
    private Class<? extends Capabilities> capabilitiesClass;
    private boolean available;

    public ReflectiveCapabilitiesFactory(final String name, final String capabilitiesClassName, final Object... args) {
        this.name = name;
        this.capabilitiesClassName = capabilitiesClassName;
        this.args = args;
        try {
            this.capabilitiesClass = (Class<? extends Capabilities>) Class.forName(capabilitiesClassName);
            this.available = Capabilities.class.isAssignableFrom(this.capabilitiesClass);
        } catch (final ClassNotFoundException e) {
            this.available = false;
        }
    }

    public ReflectiveCapabilitiesFactory(final String name, final Class<? extends Capabilities> capabilitiesClass,
            final Object... args) {
        this.name = name;
        this.capabilitiesClass = capabilitiesClass;
        this.args = args;
        this.capabilitiesClassName = capabilitiesClass.getName();
        this.available = Capabilities.class.isAssignableFrom(this.capabilitiesClass);
    }

    public Class<? extends Capabilities> getCapabilitiesClass() {
        return capabilitiesClass;
    }

    public boolean isAvailable() {
        return available;
    }

    protected DesiredCapabilities newDefaultCapabilities() {
        return null;
    }

    @Override
    public Capabilities newCapabilities() {
        if (!available) {
            throw new ConfigurationException("Capabilities " + capabilitiesClassName + " is not available.");
        }

        try {
            return ReflectionUtils.newInstance(capabilitiesClass, args);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ConfigurationException("Can't create new Capabilities instance", e);
        }
    }

    public String[] getNames() {
        final List<String> names = new ArrayList<>(Arrays.asList(name));
        if (capabilitiesClass != null) {
            names.add(capabilitiesClass.getName());
            names.add(capabilitiesClass.getSimpleName());
        }
        return names.toArray(new String[names.size()]);
    }
}
