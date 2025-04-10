package com.companyname.config.props;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class PropertyReader {
    private static final Logger LOG = LogManager.getLogger(PropertyReader.class);
    private static final String LOADER_CLASS = "property.loader.class";
    private static Properties propsStore;

    static {
        propsStore = new Properties();
        PropertyLoader loader = ConfigLoader.getClassInstance(PropertyLoader.class, PropertyDefaultLoader.class, LOADER_CLASS);
        propsStore = loader.parseVariables(loader.load());
        LOG.debug("Loaded Properties List:");
        propsStore.forEach((o, o2) -> LOG.debug("{}={}", o, o2));
    }

    private PropertyReader() {
    }

    public static Properties getAllProperties() {
        Properties newProps = new Properties();
        newProps.putAll(propsStore);
        return newProps;
    }

    public static boolean isDefined(String propertyName) {
        return propsStore.containsKey(propertyName);
    }

    public static String getProperty(String propertyName) {
        return propsStore.getProperty(propertyName, "");
    }

    public static String getProperty(String propertyName, String defaultValue) {
        return propsStore.getProperty(propertyName, defaultValue);
    }

    public static boolean getProperty(String propertyName, boolean defaultValue) {
        String value = getProperty(propertyName);
        return StringUtils.isNotBlank(value) ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static int getProperty(String propertyName, int defaultValue) {
        String value = getProperty(propertyName);
        return StringUtils.isBlank(value) ? defaultValue : Integer.parseInt(value);
    }

    public static long getProperty(String propertyName, long defaultValue) {
        String value = getProperty(propertyName);
        return StringUtils.isBlank(value) ? defaultValue : Long.parseLong(value);
    }

    public static String getPropertyOrThrow(String propertyName) {
        String prop = getProperty(propertyName);
        if (StringUtils.isBlank(prop)) {
            throw new IllegalStateException("Required property " + propertyName + " is empty or undefined");
        } else {
            return prop;
        }
    }

    /**
     * Gets Property from the map and sets it as a System Property.
     *
     * @param propertyName Name of the property.
     **/
    public void setPropertyAsSystem(String propertyName) {
        System.setProperty(propertyName, getProperty(propertyName));
    }
}
