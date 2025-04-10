package com.companyname.config.props;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class ConfigLoader {
    private static final String LOADER_CONFIG_FILE_NAME = "config/configLoader.properties";
    private static Properties configProps;

    private ConfigLoader() {
    }

    public static Properties config() {
        if (configProps == null) {
            configProps = new Properties();
            loadPropertiesFromResource(configProps, LOADER_CONFIG_FILE_NAME);
        }
        return configProps;

    }

    public static InputStream loadResourceAsStream(String path) {
        return ConfigLoader.class.getClassLoader().getResourceAsStream(path);
    }

    public static <T> T getClassInstance(Class<T> type, Class<? extends T> defaultImplementation, String propertyName) {
        String className = config().getProperty(propertyName);
        className = className != null && !className.isEmpty() ? className : defaultImplementation.getName();
        return instantiateByClassName(className, type);
    }

    private static <T> T instantiateByClassName(String className, Class<T> type) {
        try {
            return type.cast(Class.forName(className).getDeclaredConstructor().newInstance());
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException("Cannot create instance of " + className, e);
        }
    }

    public static Boolean loadPropertiesFromResource(Properties props, String path) {
        InputStream is = loadResourceAsStream(path);
        boolean result = false;
        try {
            if (is != null) {
                props.load(is);
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
