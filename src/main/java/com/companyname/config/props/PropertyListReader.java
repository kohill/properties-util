package com.companyname.config.props;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PropertyListReader {
    private static final Logger LOG = LogManager.getLogger(PropertyListReader.class);
    private static final String LOADER_CLASS = "property.loader.class";
    private static final String DEFAULT_NAME = "default";
    private static final PropertyLoader loader;
    private static final java.util.Properties initialPropsStore = new java.util.Properties();

    static {
        loader = ConfigLoader.getClassInstance(PropertyLoader.class, PropertyDefaultLoader.class, LOADER_CLASS);
        initialPropsStore.putAll(loader.load());
    }

    private final Map<String, java.util.Properties> propsStoreMap = new HashMap<>();
    private final Map<String, PropertyActions> actionsMap = new HashMap<>();


    private PropertyListReader() {
        Set<String> listNames = initialPropsStore.stringPropertyNames().stream().filter(s -> s.contains(Constant.ENV_NAME)).collect(Collectors.toSet());
        if (!listNames.isEmpty()) {
            if (!listNames.contains(Constant.ENV_NAME_DEFAULT)) {
                propsStoreMap.put(DEFAULT_NAME, loader.parseVariables(initialPropsStore));
            }
            Pattern dp = Pattern.compile("env\\.name\\.(.*)");
            listNames.stream().forEach(s -> {
                Matcher m = dp.matcher(s);
                if (m.find()) {
                    String propsListKey = m.group(1);
                    java.util.Properties propsList = initialPropsStore;
                    propsList.setProperty(Constant.ENV_NAME, initialPropsStore.getProperty(s));
                    propsList = loader.parseVariables(propsList);
                    propsStoreMap.put(propsListKey, propsList);
                }
            });
        } else {
            propsStoreMap.put(DEFAULT_NAME, initialPropsStore);
        }
        LOG.debug("Loaded Properties List:");
        propsStoreMap.forEach((o, o2) -> {
            LOG.debug("{} List:", o);
            o2.forEach((k, v) -> LOG.debug("{}={}", k, v));
        });
        propsStoreMap.forEach((s, properties) -> {
            actionsMap.put(s, new PropertyActions(s));
        });
    }

    public static PropertyActions get() {
        return get(DEFAULT_NAME);
    }

    public static PropertyActions get(String propsList) {
        return InstanceHolder.HOLDER_INSTANCE.actionsMap.get(propsList);
    }


    private static class InstanceHolder {
        public static final PropertyListReader HOLDER_INSTANCE = new PropertyListReader();
    }

    public class PropertyActions {
        private final java.util.Properties propsStore;
        private final String listName;

        private PropertyActions(String listName) {
            this.listName = listName;
            propsStore = propsStoreMap.get(this.listName);
        }

        public java.util.Properties getAllProperties() {
            java.util.Properties newProps = new java.util.Properties();
            newProps.putAll(propsStore);
            return newProps;
        }

        public boolean isDefined(String propertyName) {
            return propsStore.containsKey(propertyName);
        }

        public String getProperty(String propertyName) {
            return propsStore.getProperty(propertyName, "");
        }

        public String getProperty(String propertyName, String defaultValue) {
            return propsStore.getProperty(propertyName, defaultValue);
        }

        public boolean getProperty(String propertyName, boolean defaultValue) {
            String value = getProperty(propertyName);
            return StringUtils.isNotBlank(value) ? Boolean.parseBoolean(value) : defaultValue;
        }

        public int getProperty(String propertyName, int defaultValue) {
            String value = getProperty(propertyName);
            return StringUtils.isBlank(value) ? defaultValue : Integer.parseInt(value);
        }

        public long getProperty(String propertyName, long defaultValue) {
            String value = getProperty(propertyName);
            return StringUtils.isBlank(value) ? defaultValue : Long.parseLong(value);
        }

        public String getPropertyOrThrow(String propertyName) {
            String prop = getProperty(propertyName);
            if (StringUtils.isBlank(prop)) {
                throw new IllegalStateException("Required property " + propertyName + " is empty or undefined");
            } else {
                return prop;
            }
        }
    }
}
